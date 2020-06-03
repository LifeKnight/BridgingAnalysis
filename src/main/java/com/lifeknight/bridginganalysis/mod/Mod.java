package com.lifeknight.bridginganalysis.mod;

import com.lifeknight.bridginganalysis.gui.hud.HudText;
import com.lifeknight.bridginganalysis.utilities.Chat;
import com.lifeknight.bridginganalysis.utilities.Logger;
import com.lifeknight.bridginganalysis.utilities.Misc;
import com.lifeknight.bridginganalysis.utilities.Text;
import com.lifeknight.bridginganalysis.variables.LifeKnightBoolean;
import com.lifeknight.bridginganalysis.variables.LifeKnightInteger;
import com.lifeknight.bridginganalysis.variables.LifeKnightVariable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.EnumChatFormatting;
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
import net.minecraftforge.fml.common.network.FMLNetworkEvent;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.lifeknight.bridginganalysis.gui.hud.HudTextRenderer.doRender;
import static net.minecraft.util.EnumChatFormatting.*;

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
	public static final LifeKnightBoolean automaticStart = new LifeKnightBoolean("AutomaticStart", "Settings", false);
	public static final LifeKnightBoolean showStatus = new LifeKnightBoolean("ShowStatus", "Settings", true);
	public static KeyBinding toggleSessionKeyBinding = new KeyBinding("Toggle bridging analysis session", 0x19, modName);
	public static final LifeKnightInteger toggleSessionKeyBind = new LifeKnightInteger("ToggleSessionKeyBind", "Movement", 0x19);
	public static boolean sessionIsRunning = false;
	public static ArrayList<BridgingAnalysis> analyses = new ArrayList<>();
	public static Logger sessionLogger;
	public static Config config;

	@EventHandler
	public void init(FMLInitializationEvent initEvent) {
		ClientRegistry.registerKeyBinding(toggleSessionKeyBinding);
		MinecraftForge.EVENT_BUS.register(this);
		ClientCommandHandler.instance.registerCommand(new ModCommand());

		variables.remove(toggleSessionKeyBind);
		config = new Config();

		sessionLogger = new Logger("BridgingSessions", new File("logs/lifeknight/bridgingsessions"));

		getSessionsFromLogs();

		new HudText() {

			@Override
			public boolean isVisible() {
				return sessionIsRunning;
			}

			@Override
			public String getTextToDisplay() {
				return GREEN + "ACTIVE";
			}

			@Override
			public int getXCoordinate() {
				return Misc.getScaledWidth(10);
			}

			@Override
			public int getYCoordinate() {
				return Misc.getScaledHeight(5);
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

			if (automaticStart.getValue() && (bridgingAnalysis.getDistanceTraveledVertically() < -1 || Minecraft.getMinecraft().thePlayer.getLookVec().yCoord * -90 < 70)) {
				bridgingAnalysis.end();
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
			if (sessionIsRunning) {
				analyses.get(analyses.size() - 1).incrementRightClicks();
			}
		}

	}

	@SubscribeEvent
	public void onBlockPlacement(BlockEvent.PlaceEvent event) {
		if (event.player.getUniqueID() == Minecraft.getMinecraft().thePlayer.getUniqueID()) {
			if (sessionIsRunning) {
				analyses.get(analyses.size() - 1).onBlockPlacement();
			} else if (automaticStart.getValue() && Keyboard.isKeyDown(0x1F) && (Keyboard.isKeyDown(0x1E) || Keyboard.isKeyDown(0x20)) && Minecraft.getMinecraft().thePlayer.getLookVec().yCoord * -90 > 75) {
				startNewAnalysis();
			}
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
			Scanner scanner = new Scanner(log);

			String line;
			while (scanner.hasNextLine()) {
				line = scanner.nextLine();
				if (!line.contains("New logger created")) {
					BridgingAnalysis.interpretBridgingAnalysisFromJson(line);
				}
			}
		}
	}
}