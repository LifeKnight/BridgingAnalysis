package com.lifeknight.bridginganalysis.mod;

import com.lifeknight.bridginganalysis.gui.hud.HudText;
import com.lifeknight.bridginganalysis.utilities.Logger;
import com.lifeknight.bridginganalysis.variables.LifeKnightBoolean;
import com.lifeknight.bridginganalysis.variables.LifeKnightInteger;
import com.lifeknight.bridginganalysis.variables.LifeKnightVariable;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MovingObjectPosition;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.lifeknight.bridginganalysis.gui.hud.HudTextRenderer.doRender;
import static net.minecraft.util.EnumChatFormatting.*;
import static net.minecraft.util.MovingObjectPosition.MovingObjectType.BLOCK;

@net.minecraftforge.fml.common.Mod(modid = Mod.modID, name = Mod.modName, version = Mod.modVersion, clientSideOnly = true)
public class Mod {
	public static final String
			modName = "BridgingAnalysis",
			modVersion = "0.1",
			modID = "bridginganalysis";
	public static final EnumChatFormatting modColor = DARK_GREEN;
	public static final ExecutorService THREAD_POOL = Executors.newCachedThreadPool(new LifeKnightThreadFactory());
	public static boolean openGui = false;
	public static GuiScreen guiToOpen;
	public static final ArrayList<LifeKnightVariable> variables = new ArrayList<>();
	public static final LifeKnightBoolean runMod = new LifeKnightBoolean("Mod", "Main", true);
	public static final LifeKnightBoolean automaticSessions = new LifeKnightBoolean("AutomaticSessions", "Settings", false);
	public static final LifeKnightBoolean showStatus = new LifeKnightBoolean("ShowStatus", "Settings", true);
	public static final LifeKnightBoolean automaticallyEndAfterThreshold = new LifeKnightBoolean("EndAfterThreshold", "Settings", false);
	public static final LifeKnightInteger sessionThreshold = new LifeKnightInteger("SessionThreshold", "Settings", 30, 10, 300);
	public static final LifeKnightBoolean omitSessionsUnderThreshold = new LifeKnightBoolean("OmitSessionsUnderThreshold", "Settings", true);
	public static final LifeKnightInteger omitThreshold = new LifeKnightInteger("OmitThreshold", "Settings", 5, 1, 30);
	public static final LifeKnightInteger decimalCount = new LifeKnightInteger("DecimalCount", "Settings", 2, 0, 8);
	public static KeyBinding toggleSessionKeyBinding = new KeyBinding("Toggle bridging analysis session", 0x19, modName);
	public static boolean sessionIsRunning = false, blockCanBePlaced = false;
	public static ArrayList<BridgingAnalysis> analyses = new ArrayList<>();
	public static Logger sessionLogger;
	public static Config config;

	@EventHandler
	public void init(FMLInitializationEvent initEvent) {
		ClientRegistry.registerKeyBinding(toggleSessionKeyBinding);
		MinecraftForge.EVENT_BUS.register(this);
		ClientCommandHandler.instance.registerCommand(new ModCommand());

		config = new Config();

		sessionLogger = new Logger("BridgingSessions", new File("logs/lifeknight/bridgingsessions"));

		getSessionsFromLogs();

		new HudText() {

			@Override
			public boolean isVisible() {
				return sessionIsRunning && showStatus.getValue();
			}

			@Override
			public String getTextToDisplay() {
				return GREEN + "ACTIVE" + GOLD + ": " + AQUA + analyses.get(analyses.size() - 1).detectBridgeType();
			}

			@Override
			public int getXCoordinate() {
				return 10;
			}

			@Override
			public int getYCoordinate() {
				return 5;
			}
		};
	}

	@SubscribeEvent
	public void onTick(TickEvent.ClientTickEvent event) {
		if (openGui) {
			Minecraft.getMinecraft().displayGuiScreen(guiToOpen);
			openGui = false;
		}

		if (!Minecraft.getMinecraft().inGameHasFocus || !runMod.getValue()) {
			if (sessionIsRunning) {
				analyses.get(analyses.size() - 1).end();
			}
		} else if (sessionIsRunning) {
			BridgingAnalysis bridgingAnalysis = analyses.get(analyses.size() - 1);

			bridgingAnalysis.onTick();

			if (automaticSessions.getValue() && (Minecraft.getMinecraft().thePlayer.motionY < -0.5 || Minecraft.getMinecraft().thePlayer.getLookVec().yCoord * -90 < 70)) {
				bridgingAnalysis.end();
			}
		}
		MovingObjectPosition movingObjectPosition = Minecraft.getMinecraft().objectMouseOver;

		if (movingObjectPosition != null) {
			if (movingObjectPosition.typeOfHit == BLOCK && !movingObjectPosition.sideHit.getName().equals("up") && !movingObjectPosition.sideHit.getName().equals("down")) {
				if (Minecraft.getMinecraft().thePlayer.getHeldItem() != null && Minecraft.getMinecraft().thePlayer.getHeldItem().getItem() instanceof ItemBlock) {
					blockCanBePlaced = true;
				}
			} else {
				blockCanBePlaced = false;
			}
		}
	}

	@SubscribeEvent
	public void onRenderTick(TickEvent.RenderTickEvent event) {
		if (Minecraft.getMinecraft().inGameHasFocus) {
			doRender();
		}
	}

	@SubscribeEvent
	public void onKeyTyped(InputEvent.KeyInputEvent event) {
		if (Keyboard.isKeyDown(toggleSessionKeyBinding.getKeyCode()) && Minecraft.getMinecraft().inGameHasFocus) {
			if (sessionIsRunning) {
				analyses.get(analyses.size() - 1).end();
			} else {
				startNewAnalysis();
			}
		}
	}

	@SubscribeEvent
	public void onMousePressed(InputEvent.MouseInputEvent event) {
		if (Mouse.getEventButton() == 1 && Minecraft.getMinecraft().inGameHasFocus) {
		    if (blockCanBePlaced) {
		        onBlockPlacement();
            }
			if (sessionIsRunning) {
				analyses.get(analyses.size() - 1).incrementRightClicks();
			}
		}
	}

	public void onBlockPlacement() {
        if (sessionIsRunning) {
            analyses.get(analyses.size() - 1).onBlockPlacement();
        } else if (automaticSessions.getValue() && Keyboard.isKeyDown(0x1F) && Minecraft.getMinecraft().thePlayer.getLookVec().yCoord * -90 > 75) {
            startNewAnalysis();
        }
    }

	@SubscribeEvent
	public void onJump(LivingEvent.LivingJumpEvent event) {
		try {
			if (event.entity.getUniqueID() == Minecraft.getMinecraft().thePlayer.getUniqueID() && sessionIsRunning && analyses.size() != 0) {
				analyses.get(analyses.size() - 1).onJump();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void startNewAnalysis() {
		if (analyses.size() != 0 && sessionIsRunning) {
			analyses.get(analyses.size() - 1).end();
		}
		new BridgingAnalysis().activate();
}
	public static void openGui(GuiScreen guiScreen) {
		guiToOpen = guiScreen;
		openGui = true;
	}

	private void getSessionsFromLogs() {
		for (String log: sessionLogger.getLogs()) {
			THREAD_POOL.submit(() -> {
				Scanner scanner = new Scanner(log);

				String line;
				while (scanner.hasNextLine()) {
					line = scanner.nextLine();
					if (!line.contains("New logger created")) {
						BridgingAnalysis.interpretBridgingAnalysisFromJson(line);
					}
				}
			});
		}
	}
}