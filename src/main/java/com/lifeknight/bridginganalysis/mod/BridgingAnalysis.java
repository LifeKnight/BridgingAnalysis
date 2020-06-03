package com.lifeknight.bridginganalysis.mod;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.lifeknight.bridginganalysis.utilities.Misc;
import com.lifeknight.bridginganalysis.utilities.Stopwatch;
import com.lifeknight.bridginganalysis.utilities.Text;
import net.minecraft.client.Minecraft;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;

import java.util.ArrayList;

import static com.lifeknight.bridginganalysis.mod.Mod.*;
import static com.lifeknight.bridginganalysis.mod.Mod.sessionLogger;
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
    private double endX = 0;;
    private double endY = 0;
    private double endZ = 0;
    private String time = "";
    private String date = "";
    private String serverIp = "";
    private double averageXLook = 0;
    private double averageYLook = 0;
    private ArrayList<Integer> elevationTimes = new ArrayList<>();
    private ArrayList<Integer> shiftTicks = new ArrayList<>();
    private ArrayList<Integer> waitTicks = new ArrayList<>();
    private final ArrayList<Vec3> lookVectors = new ArrayList<>();
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

    public int getBlocksPlacedCount() {
        return blocksPlacedCount;
    }

    public int getJumpCount() {
        return jumpCount;
    }

    public double getStartX() {
        return startX;
    }

    public double getStartY() {
        return startY;
    }

    public double getStartZ() {
        return startZ;
    }

    public int getRightClickCount() {
        return (int) (rightClickCount / 2F);
    }

    public String getTime() {
        return time;
    }

    public String getDate() {
        return date;
    }

    public String getServerIp() {
        return serverIp;
    }

    public ArrayList<Integer> getElevationTimes() {
        return elevationTimes;
    }

    public ArrayList<Integer> getShiftTicks() {
        return shiftTicks;
    }

    public ArrayList<Integer> getWaitTicks() {
        return waitTicks;
    }

    public String getTimeElapsedString() {
        return stopwatch.getFormattedTime();
    }

    public double getDistanceTraveledHorizontally() {
        return Math.sqrt(Math.pow(endX - startX, 2) + Math.pow(endZ - startZ, 2));
    }

    public double getDistanceTraveledVertically() {
        return endY - startY;
    }

    public double getDistanceTraveled() {
        return Math.sqrt(Math.pow(endX - startX, 2) + Math.pow(endY - startY, 2) + Math.pow(endZ - startZ, 2));
    }

    public double getAveragePlacementSpeed() {
        return (1000 * blocksPlacedCount / (double) getTotalMilliseconds());
    }

    public double getAverageMovementSpeed() {
        return (1000 * getDistanceTraveled() / (double) getTotalMilliseconds());
    }

    public double getTotalSecondsSpentShifting() {
        double totalSecondsSpentShifting = 0;

        for (int ticksSpentShifting : shiftTicks) {
            totalSecondsSpentShifting += ticksSpentShifting * 0.025;
        }

        return totalSecondsSpentShifting;
    }

    public double getTotalSecondsSpentWaiting() {
        double totalSecondsSpentWaiting = 0;

        for (int ticksSpentWaiting : waitTicks) {
            totalSecondsSpentWaiting += ticksSpentWaiting * 0.025;
        }

        return totalSecondsSpentWaiting;
    }

    public double getTotalSecondsSpentElevating() {
        double totalSecondsSpentElevating = 0;

        for (int millisecondsSpentElevating : elevationTimes) {
            totalSecondsSpentElevating += millisecondsSpentElevating / 1000F;
        }

        return totalSecondsSpentElevating;
    }

    public double getAverageShiftingTime() {
        if (shiftTicks.size() != 0) {
            return (getTotalSecondsSpentShifting() / (double) shiftTicks.size());
        }
        return 0;
    }

    public double getAverageWaitingTime() {
        if (waitTicks.size() != 0) {
            return (getTotalSecondsSpentWaiting() / (double) waitTicks.size());
        }
        return 0;
    }

    public double getAverageElevationTime() {
        if (elevationTimes.size() != 0) {
            return (getTotalSecondsSpentElevating() / (double) elevationTimes.size());
        } else {
            return 0;
        }
    }
    
    public double getAverageXLook() {
        if (averageXLook == 0) {
            double sumOfXLooks = 0;
            
            for (Vec3 lookVector : lookVectors) {
                sumOfXLooks += lookVector.xCoord;
            }
            averageXLook =  -395.5097353389201 * sumOfXLooks / (double) lookVectors.size();
        }
        return averageXLook;
    }

    public double getAverageYLook() {
        if (averageYLook == 0) {
            double sumOfYLooks = 0;

            for (Vec3 lookVector : lookVectors) {
                sumOfYLooks += lookVector.yCoord;
            }
            averageYLook = -90 * sumOfYLooks / (double) lookVectors.size();
        }
        return averageYLook;
    }

    public double getAverageClicksPerSecond() {
        return 1000 * getRightClickCount() / (double) getTotalMilliseconds();
    }

    public int getWastedClicks() {
        return getRightClickCount() - blocksPlacedCount;
    }

    public long getTotalMilliseconds() {
        if (totalMilliseconds == 0) {
            return 1;
        } else {
            return totalMilliseconds;
        }
    }

    public void end() {
        sessionIsRunning = false;
        stopwatch.stop();
        elevationStopwatch.stop();

        if (!(omitSessionsUnderThreshold.getValue() && omitThreshold.getValue() > getTotalMilliseconds() / 1000F)) {
            sessionLogger.plainLog(toString());
        }
    }

    public void onJump() {
        jumpCount++;
        elevationStopwatch.start();
    }

    public void onBlockPlacement() {
        blocksPlacedCount++;

        if (Minecraft.getMinecraft().thePlayer.posY == Math.floor(Minecraft.getMinecraft().thePlayer.posY) && elevationStopwatch.isRunning()) {
            elevationTimes.add((int) elevationStopwatch.getTotalMilliseconds());
            elevationStopwatch.pause();
            elevationStopwatch.reset();
        }
    }

    public void onTick() {
        totalMilliseconds = stopwatch.getTotalMilliseconds();

        endX = Minecraft.getMinecraft().thePlayer.posX;
        endY = Minecraft.getMinecraft().thePlayer.posY;
        endZ = Minecraft.getMinecraft().thePlayer.posZ;

        if (automaticallyEndAfterThreshold.getValue() && getTotalMilliseconds() / 1000F >= sessionThreshold.getValue()) {
            end();
        } else {
            if (totalMilliseconds % 20 == 0) {
                lookVectors.add(Minecraft.getMinecraft().thePlayer.getLookVec());
            }
            if (Minecraft.getMinecraft().thePlayer.isSneaking()) {
                ticksSpentShifting++;
            } else if (ticksSpentShifting != 0) {
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
    }

    public static void interpretBridgingAnalysisFromJson(String JsonAsString) {
        try {
            JsonObject bridgingAnalysisAsJson = new JsonParser().parse(JsonAsString).getAsJsonObject();
            BridgingAnalysis result = new BridgingAnalysis();

            JsonObject miscellaneous = bridgingAnalysisAsJson.get("miscellaneous").getAsJsonObject();

            result.serverIp = miscellaneous.get("server").getAsString();
            result.time = miscellaneous.get("time").getAsString();
            result.date = miscellaneous.get("date").getAsString();
            result.averageXLook = miscellaneous.get("averageXLook").getAsDouble();
            result.averageYLook = miscellaneous.get("averageYLook").getAsDouble();

            JsonObject counts = bridgingAnalysisAsJson.get("counts").getAsJsonObject();

            result.blocksPlacedCount = counts.get("blocksPlaced").getAsInt();
            result.rightClickCount = counts.get("rightClicks").getAsInt();
            result.jumpCount = counts.get("jumps").getAsInt();
            result.totalMilliseconds = counts.get("totalMilliseconds").getAsInt();

            JsonObject arrays = bridgingAnalysisAsJson.get("arrays").getAsJsonObject();

            result.elevationTimes = Text.fromCSVToIntegerArrayList(arrays.get("elevationTimes").getAsString());
            result.shiftTicks = Text.fromCSVToIntegerArrayList(arrays.get("shiftTicks").getAsString());
            result.waitTicks = Text.fromCSVToIntegerArrayList(arrays.get("waitTicks").getAsString());

            JsonObject positions = bridgingAnalysisAsJson.get("positions").getAsJsonObject();

            result.startX = positions.get("startZ").getAsDouble();
            result.startY = positions.get("startY").getAsDouble();
            result.startZ = positions.get("startZ").getAsDouble();

            result.endX = positions.get("endX").getAsDouble();
            result.endY = positions.get("endY").getAsDouble();
            result.endZ = positions.get("endZ").getAsDouble();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public String toString() {
        JsonObject bridgingAnalysisAsJson = new JsonObject();

        JsonObject miscellaneous = new JsonObject();

        miscellaneous.addProperty("server", serverIp);
        miscellaneous.addProperty("time", time);
        miscellaneous.addProperty("date", date);
        miscellaneous.addProperty("averageXLook", getAverageXLook());
        miscellaneous.addProperty("averageYLook", getAverageYLook());

        bridgingAnalysisAsJson.add("miscellaneous", miscellaneous);

        JsonObject counts = new JsonObject();

        counts.addProperty("blocksPlaced", blocksPlacedCount);
        counts.addProperty("rightClicks", rightClickCount);
        counts.addProperty("jumps", jumpCount);
        counts.addProperty("totalMilliseconds", stopwatch.getTotalMilliseconds());

        bridgingAnalysisAsJson.add("counts", counts);

        JsonObject arrays = new JsonObject();

        arrays.addProperty("elevationTimes", Text.toCSV(elevationTimes));
        arrays.addProperty("shiftTicks", Text.toCSV(shiftTicks));
        arrays.addProperty("waitTicks", Text.toCSV(waitTicks));

        bridgingAnalysisAsJson.add("arrays", arrays);

        JsonObject positions = new JsonObject();

        positions.addProperty("startX", startX);
        positions.addProperty("startY", startY);
        positions.addProperty("startZ", startZ);

        positions.addProperty("endX", endX);
        positions.addProperty("endY", endY);
        positions.addProperty("endZ", endZ);

        bridgingAnalysisAsJson.add("positions", positions);

        return bridgingAnalysisAsJson.toString();
    }

    public String detectBridgeType() {
        String result = "";
        if (getDistanceTraveledVertically() / getDistanceTraveledHorizontally() > 1.25) {
            if (blocksPlacedCount / getDistanceTraveled() > 1.5) {
                result = "Diagonal ";
            }
            if (shiftTicks.size() / (double) blocksPlacedCount > 1.25) {
                result += "Shift-jitterbridging";
            } else {
                result += "Tallstacking";
            }
        } else if (getDistanceTraveledVertically() / getDistanceTraveledHorizontally() < 0.4) {
            if (blocksPlacedCount / getDistanceTraveled() > 1.5) {
                result = "Diagonal ";
            }
            if (shiftTicks.size() / (double) blocksPlacedCount < 0.0625) {
                if (1000 * getRightClickCount() / (double) getTotalMilliseconds() > 7) {
                    result += "Breezily/Godbridge";
                } else {
                    result += "Low CPS Breezily/Godbridge";
                }
            } else if (shiftTicks.size() / (double) blocksPlacedCount > 0.8) {
                result += "Speedbridging";
            } else {
                result += "Breezily/Godbridge-Shift";
            }
        } else if (getDistanceTraveledVertically() / getDistanceTraveledHorizontally() < 0.8) {
            if (blocksPlacedCount / getDistanceTraveled() > 1.75) {
                result = "Diagonal ";
            }
            if (shiftTicks.size() / (double) blocksPlacedCount < 0.25) {
                if (1000 * getRightClickCount() / (double) getTotalMilliseconds() > 7) {
                    result += "Jitterbridging";
                } else {
                    result += "Low CPS Jitterbridging";
                }
            } else {
                result += "Shift-jitterbridging";
            }
        } else {
            if (blocksPlacedCount / getDistanceTraveled() > 2) {
                result = "Diagonal ";
            }
            if (1000 * getRightClickCount() / (double) getTotalMilliseconds() > 7) {
                result += "Jitterstacking";
            } else {
                result += "Stacking";
            }
        }
        return result;
    }

    public static ArrayList<BridgingAnalysis> getAnalyses() {
        if (omitSessionsUnderThreshold.getValue()) {
            ArrayList<BridgingAnalysis> result = new ArrayList<>();

            for (BridgingAnalysis bridgingAnalysis : analyses) {
                if (bridgingAnalysis.getTotalMilliseconds() >= omitThreshold.getValue() * 1000) {
                    result.add(bridgingAnalysis);
                }
            }
            return result;
        }
        return analyses;
    }

}
