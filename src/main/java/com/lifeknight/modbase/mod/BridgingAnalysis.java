package com.lifeknight.modbase.mod;

import com.lifeknight.modbase.utilities.Misc;
import com.lifeknight.modbase.utilities.Stopwatch;
import com.lifeknight.modbase.utilities.Text;
import net.minecraft.client.Minecraft;
import net.minecraft.util.MovingObjectPosition;

import java.util.ArrayList;

import static com.lifeknight.modbase.mod.Core.analyses;
import static com.lifeknight.modbase.mod.Core.sessionIsRunning;
import static net.minecraft.util.MovingObjectPosition.MovingObjectType.BLOCK;

public class BridgingAnalysis {
    private int blocksPlaced = 0;
    private int ticksSpentShifting = 0;
    private int ticksSpentWaiting = 0;
    private int rightClicks = 0;
    private int jumpCount = 0;
    private final double startX;
    private final double startY;
    private final double startZ;
    private final String time;
    private final String date;
    private boolean jumpFlag = false;
    private final ArrayList<Long> elevationTimes = new ArrayList<>();
    private final Stopwatch stopwatch = new Stopwatch();
    private final Stopwatch elevationStopwatch = new Stopwatch();
    
    public BridgingAnalysis() {
        analyses.add(this);
        startX = Minecraft.getMinecraft().thePlayer.posX;
        startY = Minecraft.getMinecraft().thePlayer.posY;
        startZ = Minecraft.getMinecraft().thePlayer.posZ;
        time = Misc.getCurrentTime();
        date = Misc.getCurrentDate();
        stopwatch.start();
    }

    public void incrementRightClicks() {
        rightClicks++;
    }

    public void incrementTicksSpentShifting() {
        ticksSpentShifting++;
    }

    public String getTimeElapsedString() {
        return stopwatch.getFormattedTime();
    }

    public int getBlocksPlaced() {
        return blocksPlaced;
    }

    public double getDistanceTraveledHorizontally() {
        double x2 = Minecraft.getMinecraft().thePlayer.posX;
        double z2 = Minecraft.getMinecraft().thePlayer.posZ;
        return Math.sqrt(Math.pow(x2 - startX, 2) + Math.pow(z2 - startZ, 2));
    }

    public double getDistanceTraveledVertically() {
        return Minecraft.getMinecraft().thePlayer.posY - startY;
    }

    public double getDistanceTraveled() {
        double x = Minecraft.getMinecraft().thePlayer.posX;
        double y = Minecraft.getMinecraft().thePlayer.posY;
        double z = Minecraft.getMinecraft().thePlayer.posZ;
        return Math.sqrt(Math.pow(x - startX, 2) + Math.pow(y - startY, 2) + Math.pow(z - startZ, 2));
    }

    public int getTicksSpentShifting() {
        return ticksSpentShifting;
    }

    public int getTicksSpentWaiting() {
        return ticksSpentWaiting;
    }

    public int getRightClicks() {
        return (int) Math.ceil(rightClicks / 2F);
    }

    public double getAveragePlacementSpeed() {
        return (100 * (double) blocksPlaced / getTotalMilliseconds());
    }

    public double averageMovementSpeed() {
        return (1000 * getDistanceTraveledHorizontally() / getTotalMilliseconds());
    }

    public double getAverageElevationTime() {
        if (elevationTimes.size() != 0) {
            long totalMilliseconds = 0L;

            for (Long time : elevationTimes) {
                totalMilliseconds += time;
            }

            return (totalMilliseconds / (double) elevationTimes.size()) / 1000;
        }  else {
            return 0;
        }
    }

    public long getTotalMilliseconds() {
        if (stopwatch.getTotalMilliseconds() == 0) {
            return 1;
        } else {
            return stopwatch.getTotalMilliseconds();
        }
    }

    public int getJumpCount() {
        return jumpCount / 2;
    }

    public void end() {
        sessionIsRunning = false;
        stopwatch.stop();
        elevationStopwatch.stop();
    }

    public String getInformation() {
        return "Blocks Placed: " + blocksPlaced +
                "Right Clicks: " + getRightClicks() +
                "Ticks Spent Shifting: " + ticksSpentShifting +
                "Jumps: " + getJumpCount() +
                "Distance Travelled: " + Text.shortenDouble(getDistanceTraveled(), 1) +
                "Horizontal Distance Travelled: " + Text.shortenDouble(getDistanceTraveledHorizontally(), 1) +
                "Vertical Distance Travelled: " + Text.shortenDouble(getDistanceTraveledVertically(), 1) +
                "Time Elapsed: " + getTimeElapsedString();
    }

    public String getTime() {
        return time;
    }

    public String getDate() {
        return date;
    }

    public void onJump() {
        jumpCount++;
        jumpFlag = true;
        elevationStopwatch.start();
    }

    public void onBlockPlacement() {
        blocksPlaced++;

        if (jumpFlag && Minecraft.getMinecraft().thePlayer.posY == Math.floor(Minecraft.getMinecraft().thePlayer.posY)) {
            jumpFlag = false;
            elevationTimes.add(elevationStopwatch.getTotalMilliseconds());
            elevationStopwatch.pause();

        }
    }

    public long getElevationTime() {
        return elevationStopwatch.getTotalMilliseconds();
    }

    public void onTick() {
        if (Minecraft.getMinecraft().thePlayer.isSneaking()) {
            ticksSpentShifting++;
        }

        MovingObjectPosition movingObjectPosition = Minecraft.getMinecraft().objectMouseOver;

        if (movingObjectPosition.typeOfHit == BLOCK && !movingObjectPosition.sideHit.getName().equals("up")) {
            ticksSpentWaiting++;
        }
    }

}
