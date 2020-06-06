package com.lifeknight.bridginganalysis.utilities;

import com.sun.javafx.geom.Vec2d;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Vec3;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ThreadLocalRandom;

public class Misc {
	public static int height = 0;
	public static int width = 0;
	public static String getCurrentDate() {
		return new SimpleDateFormat("MM/dd/yyyy").format(new Date());
	}

	public static String getCurrentTime() {
		return new SimpleDateFormat("hh:mm:ss a").format(new Date());
	}

	public static int scale(int toScale) {
		switch (Minecraft.getMinecraft().gameSettings.guiScale) {
			case 1: {
				return toScale * 2;
			}
			case 2: {
				return toScale;
			}
			default: {
				return (int) (toScale / 1.5);
			}
		}
	}

	public static int get2ndPanelCenter() {
		return getScaledHeight(300) + (width - getScaledWidth(300)) / 2;
	}

	public static int getSupposedWidth() {
		int i = Minecraft.getMinecraft().gameSettings.guiScale == 0 ? 3 : Minecraft.getMinecraft().gameSettings.guiScale;
		return 1920 / i;
	}

	public static int getSupposedHeight() {
		int i = Minecraft.getMinecraft().gameSettings.guiScale == 0 ? 3 : Minecraft.getMinecraft().gameSettings.guiScale;
		return 1080 / i;
	}

	public static int getScaledWidth(int widthIn) {
		return scale((int) (widthIn * ((double) width / getSupposedWidth())));
	}

	public static int getScaledHeight(int heightIn) {
		return scale((int) (heightIn * ((double) height / getSupposedHeight())));
	}

	public static int getGameWidth() {
		int i = Minecraft.getMinecraft().gameSettings.guiScale == 0 ? 3 : Minecraft.getMinecraft().gameSettings.guiScale;
		return Minecraft.getMinecraft().displayWidth / i;
	}

	public static int getGameHeight() {
		int i = Minecraft.getMinecraft().gameSettings.guiScale == 0 ? 3 : Minecraft.getMinecraft().gameSettings.guiScale;
		return Minecraft.getMinecraft().displayHeight / i;
	}

	public static Vec2d getReversedLookVector() {
		Vec3 vec3In = Minecraft.getMinecraft().thePlayer.getLookVec();
		double k = 0.017453292;

		double x = vec3In.xCoord;
		double y = vec3In.yCoord;

		double pitch = Math.acos(y) / k;

		double yaw = -(Math.acos(x/y) + Math.PI) / k;

		return new Vec2d(yaw, pitch);
	}
	
	public static int scaleFrom1080pWidth(int widthIn) {
		int i;
		if (Minecraft.getMinecraft().gameSettings.guiScale == 0 || Minecraft.getMinecraft().gameSettings.guiScale == 3) {
			i = widthIn / 3;
		} else if (Minecraft.getMinecraft().gameSettings.guiScale == 2) {
			i = widthIn / 2;
		} else {
			i = widthIn;
		}
		return (int) (i * (getGameWidth() / (double) getSupposedWidth()));
	}

	public static int scaleFrom1080pHeight(int widthIn) {
		int i;
		if (Minecraft.getMinecraft().gameSettings.guiScale == 0 || Minecraft.getMinecraft().gameSettings.guiScale == 3) {
			i = widthIn / 3;
		} else if (Minecraft.getMinecraft().gameSettings.guiScale == 2) {
			i = widthIn / 2;
		} else {
			i = widthIn;
		}
		return (int) (i * (getGameHeight() / (double) getSupposedHeight()));
	}

	public static int scaleTo1080pWidth(int widthIn) {
		int i;
		if (Minecraft.getMinecraft().gameSettings.guiScale == 0 || Minecraft.getMinecraft().gameSettings.guiScale == 3) {
			i = widthIn * 3;
		} else if (Minecraft.getMinecraft().gameSettings.guiScale == 2) {
			i = widthIn * 2;
		} else {
			i = widthIn;
		}
		return (int) (i * (getSupposedWidth() / (double) getGameWidth()));
	}

	public static int scaleTo1080pHeight(int heightIn) {
		int i;
		if (Minecraft.getMinecraft().gameSettings.guiScale == 0 || Minecraft.getMinecraft().gameSettings.guiScale == 3) {
			i = heightIn * 3;
		} else if (Minecraft.getMinecraft().gameSettings.guiScale == 2) {
			i = heightIn * 2;
		} else {
			i = heightIn;
		}
		return (int) (i * (getSupposedHeight() / (double) getGameHeight()));
	}
}
