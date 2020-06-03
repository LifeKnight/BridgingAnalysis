package com.lifeknight.bridginganalysis.mod;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.lifeknight.bridginganalysis.utilities.Misc;
import com.lifeknight.bridginganalysis.utilities.Stopwatch;
import com.lifeknight.bridginganalysis.utilities.Text;
import net.minecraft.client.Minecraft;
import net.minecraft.util.MovingObjectPosition;

import java.util.ArrayList;

import static com.lifeknight.bridginganalysis.mod.Mod.*;
import static net.minecraft.util.MovingObjectPosition.MovingObjectType.BLOCK;

public class BridgingAnalysis {
    private long totalMilliseconds = 0;
    private int blocksPlacedCount = 0;
    private int ticksSpentShifting = 0;
    private int ticksSpentWaiting = 0;
    private int rightClickCount = 0;
    private int jumpCount = 0;
    private double startX = 0;
    private double startY = 0;
    private double startZ = 0;
    private String time = "";
    private String date = "";
    private String serverIp = "";
    private boolean jumpFlag = false;
    private ArrayList<Integer> elevationTimes = new ArrayList<>();
    private ArrayList<Integer> shiftTicks = new ArrayList<>();
    private ArrayList<Integer> waitTicks = new ArrayList<>();
    private final Stopwatch stopwatch = new Stopwatch();
    private final Stopwatch elevationStopwatch = new Stopwatch();

    public BridgingAnalysis() {
        analyses.add(this);
    }

    public void activate() {
        startX = Minecraft.getMinecraft().thePlayer.posX;
        startY = Minecraft.getMinecraft().thePlayer.posY;
        startZ = Minecraft.getMinecraft().thePlayer.posZ;
        time = Misc.getCurrentTime();
        date = Misc.getCurrentDate();
        serverIp = Minecraft.getMinecraft().isSingleplayer() ? "Singleplayer" : Minecraft.getMinecraft().getCurrentServerData().serverIP;
        stopwatch.start();
        sessionIsRunning = true;
    }

    public void incrementRightClicks() {
        rightClickCount++;
    }

    public String getTimeElapsedString() {
        return stopwatch.getFormattedTime();
    }

    public int getBlocksPlacedCount() {
        return blocksPlacedCount;
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

    public int getRightClickCount() {
        return (int) Math.ceil(rightClickCount / 2F);
    }

    public double getAveragePlacementSpeed() {
        return (100 * (double) blocksPlacedCount / getTotalMilliseconds());
    }

    public double averageMovementSpeed() {
        return (1000 * getDistanceTraveledHorizontally() / getTotalMilliseconds());
    }

    public double getAverageElevationTime() {
        if (elevationTimes.size() != 0) {
            long totalMilliseconds = 0L;

            for (int time: elevationTimes) {
                totalMilliseconds += time;
            }

            return (totalMilliseconds / (double) elevationTimes.size()) / 1000;
        } else {
            return 0;
        }
    }

    public long getTotalMilliseconds() {
        if (totalMilliseconds == 0) {
            return 1;
        } else {
            return totalMilliseconds;
        }
    }

    public int getJumpCount() {
        return jumpCount / 2;
    }

    public void end() {
        sessionIsRunning = false;
        stopwatch.stop();
        elevationStopwatch.stop();

        sessionLogger.plainLog(toString());
    }

    public void onJump() {
        jumpCount++;
        jumpFlag = true;
        elevationStopwatch.start();
    }

    public void onBlockPlacement() {
        blocksPlacedCount++;

        if (jumpFlag && Minecraft.getMinecraft().thePlayer.posY == Math.floor(Minecraft.getMinecraft().thePlayer.posY)) {
            jumpFlag = false;
            elevationTimes.add((int) elevationStopwatch.getTotalMilliseconds());
            elevationStopwatch.pause();
        }
    }

    public long getElevationTime() {
        return elevationStopwatch.getTotalMilliseconds();
    }

    public void onTick() {
        totalMilliseconds = stopwatch.getTotalMilliseconds();
        if (Minecraft.getMinecraft().thePlayer.isSneaking()) {
            ticksSpentShifting++;
        } else if (ticksSpentShifting != 0){
            shiftTicks.add(ticksSpentShifting);
            ticksSpentShifting = 0;
        }

        MovingObjectPosition movingObjectPosition = Minecraft.getMinecraft().objectMouseOver;

        if (movingObjectPosition.typeOfHit == BLOCK && !movingObjectPosition.sideHit.getName().equals("up")) {
            ticksSpentWaiting++;
        } else if (ticksSpentWaiting != 0) {
            waitTicks.add(ticksSpentWaiting);
            ticksSpentWaiting = 0;
        }
    }

    public static BridgingAnalysis interpretBridgingAnalysisFromJson(String JsonAsString) {
        try {
            JsonObject bridgingAnalysisAsJson = new JsonParser().parse(JsonAsString).getAsJsonObject();
            BridgingAnalysis result = new BridgingAnalysis();

            JsonObject miscellaneous = bridgingAnalysisAsJson.get("miscellaneous").getAsJsonObject();

            result.serverIp = miscellaneous.get("server").getAsString();
            result.time = miscellaneous.get("time").getAsString();
            result.date = miscellaneous.get("date").getAsString();

            JsonObject counts = bridgingAnalysisAsJson.get("counts").getAsJsonObject();

            result.blocksPlacedCount = counts.get("blocksPlaced").getAsInt();
            result.ticksSpentShifting = counts.get("ticksSpentShifting").getAsInt();
            result.ticksSpentWaiting = counts.get("ticksSpentWaiting").getAsInt();
            result.rightClickCount = counts.get("rightClicks").getAsInt();
            result.jumpCount = counts.get("jumps").getAsInt();
            result.totalMilliseconds = counts.get("totalMilliseconds").getAsInt();

            JsonObject arrays = bridgingAnalysisAsJson.get("arrays").getAsJsonObject();

            result.elevationTimes = Text.fromCSVToIntegerArrayList(arrays.get("elevationTimes").getAsString());
            result.shiftTicks = Text.fromCSVToIntegerArrayList(arrays.get("shiftTicks").getAsString());
            result.waitTicks = Text.fromCSVToIntegerArrayList(arrays.get("waitTicks").getAsString());

            JsonObject startPosition = bridgingAnalysisAsJson.get("startPosition").getAsJsonObject();

            result.startX = startPosition.get("positionX").getAsDouble();
            result.startY = startPosition.get("positionY").getAsDouble();
            result.startZ = startPosition.get("positionZ").getAsDouble();

            return result;

        } catch (Exception e) {
            return new BridgingAnalysis();
        }
    }

    public String toString() {
        JsonObject bridgingAnalysisAsJson = new JsonObject();

        JsonObject miscellaneous = new JsonObject();

        miscellaneous.addProperty("server", serverIp);
        miscellaneous.addProperty("time", time);
        miscellaneous.addProperty("date", date);

        bridgingAnalysisAsJson.add("miscellaneous", miscellaneous);

        JsonObject counts = new JsonObject();

        counts.addProperty("blocksPlaced", blocksPlacedCount);
        counts.addProperty("ticksSpentShifting", ticksSpentShifting);
        counts.addProperty("ticksSpentWaiting", ticksSpentShifting);
        counts.addProperty("rightClicks", rightClickCount);
        counts.addProperty("jumps", jumpCount);
        counts.addProperty("totalMilliseconds", stopwatch.getTotalMilliseconds());

        bridgingAnalysisAsJson.add("counts", counts);

        JsonObject arrays = new JsonObject();

        arrays.addProperty("elevationTimes", Text.toCSV(elevationTimes));
        arrays.addProperty("shiftTicks", Text.toCSV(shiftTicks));
        arrays.addProperty("waitTicks", Text.toCSV(waitTicks));

        bridgingAnalysisAsJson.add("arrays", arrays);

        JsonObject startPosition = new JsonObject();

        startPosition.addProperty("positionX", startX);
        startPosition.addProperty("positionY", startY);
        startPosition.addProperty("positionZ", startZ);

        bridgingAnalysisAsJson.add("startPosition", startPosition);

        return bridgingAnalysisAsJson.toString();
    }
}
