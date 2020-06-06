package com.lifeknight.bridginganalysis.mod;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import static com.lifeknight.bridginganalysis.mod.Core.modColor;
import static com.lifeknight.bridginganalysis.mod.Core.modName;
import static net.minecraft.util.EnumChatFormatting.RED;

public class Utilities {
    public static final ArrayList<String> queuedMessages = new ArrayList<>();

    public static ArrayList<String> returnStartingEntries(ArrayList<String> arrayList, String input) {
        ArrayList<String> result = new ArrayList<>();
        if (!input.equals("") && arrayList != null) {
            for (String element: arrayList) {
                try {
                    if (element.toLowerCase().startsWith(input.toLowerCase())) {
                        result.add(element);
                    }
                } catch (Exception ignored) {

                }
            }
        } else {
            result.addAll(arrayList);
        }
        return result;
    }

    public static String shortenDouble(double value, int decimalDigits) {
        String afterDecimal = String.valueOf(value).substring(String.valueOf(value).indexOf(".") + 1);

        if (decimalDigits == 0) {
            return String.valueOf(Math.round(value));
        }
        if (afterDecimal.length() <= decimalDigits) {
            return String.valueOf(value);
        } else {
            return String.valueOf(value).substring(0, String.valueOf(value).indexOf(".") + decimalDigits + 1);
        }
    }

    public static String toCSV(ArrayList objects) {
        StringBuilder stringBuilder = new StringBuilder();

        for (Object object: objects) {
            stringBuilder.append(object).append(",");
        }

        return stringBuilder.toString();
    }

    public static ArrayList<Integer> fromCSVToIntegerArrayList(String CSV) {
        try {
            String[] integers = CSV.split(",");
            ArrayList<Integer> result = new ArrayList<>();

            for (String integer: integers) {
                if (!integer.isEmpty()) {
                    result.add(Integer.parseInt(integer));
                }
            }
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    public static String formatTimeFromMilliseconds(long milliseconds) {
        long days;
        long hours;
        long minutes;
        long seconds;
        long millisecondsLeft = milliseconds;
        days = millisecondsLeft / 86400000;
        millisecondsLeft %= 86400000;
        hours = millisecondsLeft / 3600000;
        millisecondsLeft %= 3600000;
        minutes = millisecondsLeft / 60000;
        millisecondsLeft %= 60000;
        seconds = millisecondsLeft / 1000;
        millisecondsLeft %= 1000;

        StringBuilder result = new StringBuilder();

        if (days > 0) {
            result.append(days).append(":");
            result.append(appendTime(hours)).append(":");
        } else {
            result.append(hours).append(":");
        }

        result.append(appendTime(minutes)).append(":");

        result.append(appendTime(seconds)).append(".");

        result.append(formatMilliseconds(millisecondsLeft));

        return result.toString();
    }

    private static String appendTime(long timeValue) {
        StringBuilder result = new StringBuilder();
        if (timeValue > 9) {
            result.append(timeValue);
        } else {
            result.append("0").append(timeValue);
        }
        return result.toString();
    }

    private static String formatMilliseconds(long milliseconds) {
        String asString = String.valueOf(milliseconds);

        if (asString.length() == 1) {
            return "00" + milliseconds;
        } else if (asString.length() == 2) {
            return "0" + milliseconds;
        }
        return asString;
    }

    public static void addChatMessage(String msg) {
        if (Minecraft.getMinecraft().theWorld != null) {
            Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText(modColor + "" + EnumChatFormatting.BOLD + modName + " > " + EnumChatFormatting.RESET + msg));
        } else {
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    addChatMessage(msg);
                }
            }, 100L);
        }
    }

    public static void addErrorMessage(String msg) {
        addChatMessage(RED + msg);
    }

    public static void queueChatMessageForConnection(String msg) {
        queuedMessages.add(msg);
    }
}
