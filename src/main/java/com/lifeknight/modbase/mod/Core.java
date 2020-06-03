package com.lifeknight.modbase.mod;

import com.lifeknight.modbase.gui.hud.HudText;
import com.lifeknight.modbase.utilities.Chat;
import com.lifeknight.modbase.utilities.Text;
import com.lifeknight.modbase.variables.LifeKnightBoolean;
import com.lifeknight.modbase.variables.LifeKnightInteger;
import com.lifeknight.modbase.variables.LifeKnightVariable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.settings.KeyBinding;
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
import net.minecraftforge.fml.common.network.FMLNetworkEvent;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.lifeknight.modbase.gui.hud.HudTextRenderer.doRender;
import static net.minecraft.util.EnumChatFormatting.*;

@net.minecraftforge.fml.common.Mod(modid = Core.modID, name = Core.modName, version = Core.modVersion, clientSideOnly = true)
public class Core {
	public static final String
			modName = "BridgingAnalysis",
			modVersion = "0.1",
			modID = "bridginganalysis";
	public static final EnumChatFormatting modColor = DARK_GREEN;
	public static final ExecutorService THREAD_POOL = Executors.newCachedThreadPool(new LifeKnightThreadFactory());
	public static boolean onHypixel = false, openGui = false;
	public static GuiScreen guiToOpen;
	public static final ArrayList<LifeKnightVariable> variables = new ArrayList<>();
	public static final LifeKnightBoolean runMod = new LifeKnightBoolean("Mod", "Main", true);
	public static final LifeKnightBoolean automaticStart = new LifeKnightBoolean("AutomaticStart", "Settings", false);
	public static KeyBinding toggleSessionKeyBinding = new KeyBinding("Toggle bridging analysis session", 0x19, modName);
	;
	public static final LifeKnightInteger toggleSessionKeyBind = new LifeKnightInteger("ToggleSessionKeyBind", "Movement", 0x19);
	public static boolean sessionIsRunning = false;
	public static ArrayList<BridgingAnalysis> analyses = new ArrayList<>();
	public static Config config;

	@EventHandler
	public void init(FMLInitializationEvent initEvent) {
		ClientRegistry.registerKeyBinding(toggleSessionKeyBinding);
		MinecraftForge.EVENT_BUS.register(this);
		ClientCommandHandler.instance.registerCommand(new ModCommand());

		variables.remove(toggleSessionKeyBind);
		config = new Config();

		new HudText() {
			@Override
			public boolean isVisible() {
				return sessionIsRunning;
			}

			@Override
			public String getTextToDisplay() {
				if (sessionIsRunning) {
					return "Elevation time: " + analyses.get(analyses.size() - 1).getElevationTime();
				}
				return null;
			}

			@Override
			public int getXCoordinate() {
				return 0;
			}

			@Override
			public int getYCoordinate() {
				return 0;
			}
		};
		new HudText() {
			@Override
			public boolean isVisible() {
				return sessionIsRunning;
			}

			@Override
			public String getTextToDisplay() {
				if (sessionIsRunning) {
					return "Right clicks: " + analyses.get(analyses.size() - 1).getRightClicks();
				}
				return null;
			}

			@Override
			public int getXCoordinate() {
				return 0;
			}

			@Override
			public int getYCoordinate() {
				return 20;
			}
		};
		new HudText() {
			@Override
			public boolean isVisible() {
				return sessionIsRunning;
			}

			@Override
			public String getTextToDisplay() {
				if (sessionIsRunning) {
					return "Ticks spent shifting: " + String.valueOf(analyses.get(analyses.size() - 1).getTicksSpentShifting());
				}
				return null;
			}

			@Override
			public int getXCoordinate() {
				return 0;
			}

			@Override
			public int getYCoordinate() {
				return 40;
			}
		};
		new HudText() {
			@Override
			public boolean isVisible() {
				return sessionIsRunning;
			}

			@Override
			public String getTextToDisplay() {
				if (sessionIsRunning) {
					return "Ticks spent waiting: " + analyses.get(analyses.size() - 1).getTicksSpentWaiting();
				}
				return null;
			}

			@Override
			public int getXCoordinate() {
				return 0;
			}

			@Override
			public int getYCoordinate() {
				return 60;
			}
		};
		new HudText() {
			@Override
			public boolean isVisible() {
				return sessionIsRunning;
			}

			@Override
			public String getTextToDisplay() {
				if (sessionIsRunning) {
					return "Distance travelled: " + Text.shortenDouble(analyses.get(analyses.size() - 1).getDistanceTraveled(), 1);
				}
				return null;
			}

			@Override
			public int getXCoordinate() {
				return 0;
			}

			@Override
			public int getYCoordinate() {
				return 80;
			}
		};
		new HudText() {
			@Override
			public boolean isVisible() {
				return sessionIsRunning;
			}

			@Override
			public String getTextToDisplay() {
				if (sessionIsRunning) {
					return "Distanced travelled horizontally: " + Text.shortenDouble(analyses.get(analyses.size() - 1).getDistanceTraveledHorizontally(), 1);
				}
				return null;
			}

			@Override
			public int getXCoordinate() {
				return 0;
			}

			@Override
			public int getYCoordinate() {
				return 100;
			}
		};
		new HudText() {
			@Override
			public boolean isVisible() {
				return sessionIsRunning;
			}

			@Override
			public String getTextToDisplay() {
				if (sessionIsRunning) {
					return "Distanced travelled vertically: " + Text.shortenDouble(analyses.get(analyses.size() - 1).getDistanceTraveledVertically(), 1);
				}
				return null;
			}

			@Override
			public int getXCoordinate() {
				return 0;
			}

			@Override
			public int getYCoordinate() {
				return 120;
			}
		};
		new HudText() {
			@Override
			public boolean isVisible() {
				return sessionIsRunning;
			}

			@Override
			public String getTextToDisplay() {
				if (sessionIsRunning) {
					return "Time elapsed: " + (analyses.get(analyses.size() - 1).getTimeElapsedString());
				}
				return null;
			}

			@Override
			public int getXCoordinate() {
				return 0;
			}

			@Override
			public int getYCoordinate() {
				return 140;
			}
		};
		new HudText() {

			@Override
			public boolean isVisible() {
				return sessionIsRunning;
			}

			@Override
			public String getTextToDisplay() {
				if (sessionIsRunning) {
					return "Blocks placed/s: " + Text.shortenDouble(analyses.get(analyses.size() - 1).getAveragePlacementSpeed(), 1);
				}
				return null;
			}

			@Override
			public int getXCoordinate() {
				return 0;
			}

			@Override
			public int getYCoordinate() {
				return 160;
			}
		};
		new HudText() {

			@Override
			public boolean isVisible() {
				return sessionIsRunning;
			}

			@Override
			public String getTextToDisplay() {
				if (sessionIsRunning) {
					return "Meters/s: " + Text.shortenDouble(analyses.get(analyses.size() - 1).averageMovementSpeed(), 2);
				}
				return null;
			}

			@Override
			public int getXCoordinate() {
				return 0;
			}

			@Override
			public int getYCoordinate() {
				return 180;
			}
		};
		new HudText() {

			@Override
			public boolean isVisible() {
				return sessionIsRunning;
			}

			@Override
			public String getTextToDisplay() {
				if (sessionIsRunning) {
					return "Average elevation time: " + Text.shortenDouble(analyses.get(analyses.size() - 1).getAverageElevationTime(), 2);
				}
				return null;
			}

			@Override
			public int getXCoordinate() {
				return 0;
			}

			@Override
			public int getYCoordinate() {
				return 200;
			}
		};
	}

	@SubscribeEvent
	public void onConnect(final FMLNetworkEvent.ClientConnectedToServerEvent event) {
		new Timer().schedule(new TimerTask() {
			@Override
			public void run() {
				if (Minecraft.getMinecraft().theWorld != null) {
					for (String msg : Chat.queuedMessages) {
						Chat.addChatMessage(msg);
					}
				}
				try {
					onHypixel = Minecraft.getMinecraft().getCurrentServerData().serverIP.toLowerCase().contains("hypixel.net");
				} catch (Exception ignored) {
				}
			}
		}, 2000);
	}

	@SubscribeEvent
	public void onTick(TickEvent.ClientTickEvent event) {
		if (openGui) {
			Minecraft.getMinecraft().displayGuiScreen(guiToOpen);
			openGui = false;
		}

		if (!Minecraft.getMinecraft().inGameHasFocus || !runMod.getValue()) {
			sessionIsRunning = false;
		} else if (sessionIsRunning) {
			BridgingAnalysis bridgingAnalysis = analyses.get(analyses.size() - 1);

			bridgingAnalysis.onTick();

			if (automaticStart.getValue() && (bridgingAnalysis.getDistanceTraveledVertically() < -1 || Minecraft.getMinecraft().thePlayer.getLookVec().yCoord * -90 < 70)) {
				bridgingAnalysis.end();
				sessionIsRunning = false;
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
			sessionIsRunning = !sessionIsRunning;

			if (sessionIsRunning) {
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

	public void startNewAnalysis() {
		if (analyses.size() != 0) {
			analyses.get(analyses.size() - 1).end();
		}
		sessionIsRunning = true;
		new BridgingAnalysis();
}
	public static void openGui(GuiScreen guiScreen) {
		guiToOpen = guiScreen;
		openGui = true;
	}
}