package me.blok601.nightshadeuhc.util;

import me.blok601.nightshadeuhc.UHC;
import me.blok601.nightshadeuhc.manager.GameManager;
import org.bukkit.scheduler.BukkitRunnable;
import org.joda.time.Period;
import org.joda.time.format.PeriodFormatter;
import org.joda.time.format.PeriodFormatterBuilder;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class TimeUtils {

    public static void start(final String time) {

        SimpleDateFormat dateFormatGmt = new SimpleDateFormat("HH:mm");
        dateFormatGmt.setTimeZone(TimeZone.getTimeZone("GMT"));


        new BukkitRunnable() {

            @Override
            public void run() {
                String f = dateFormatGmt.format(new Date());
                //System.out.println(f);
                if (f.equalsIgnoreCase(time)) {
                    GameManager.get().setWhitelistEnabled(false);
                    cancel();
                }
            }
        }.runTaskTimerAsynchronously(UHC.get(), 0, 20);
    }

    public static String formatSecondsToTime(int secondTime, String color) {
        PeriodFormatter formatter = new PeriodFormatterBuilder()
                .appendMonths()
                .appendSeparator(color + "mo")
                .appendWeeks()
                .appendSeparator(color + "w")
                .appendDays()
                .appendSeparator(color + "d")
                .appendHours()
                .appendSeparator(color + "h")
                .appendMinutes()
                .appendSeparator(color + "m")
                .printZeroAlways()
                .appendSeconds()
                .appendSuffix(color + "s").toFormatter();

        return formatter.print(Period.seconds(secondTime).normalizedStandard());
    }

    public static boolean isTime(String input) {
        String[] array;
        if (input.contains("m")) {
            array = input.split("m");
            String number = array[0];
            return MathUtils.isInt(number);
        } else if (input.contains("mins")) {
            array = input.split("mins");
            String number = array[0];
            return MathUtils.isInt(number);
        } else if (input.contains("minutes")) {
            array = input.split("minutes");
            String number = array[0];
            return MathUtils.isInt(number);
        }

        return false;
    }

    public static String parseAsInt(String time) {
        String[] array;
        if (time.contains("m")) {
            array = time.split("m");
            return array[0];
        } else if (time.contains("mins")) {
            array = time.split("mins");
            return array[0];
        } else if (time.contains("minutes")) {
            array = time.split("minutes");
            return array[0];
        }

        return null;
    }

    public static int parseTimeAsSeconds(String input) {
        //Input would be
        String[] array;
        if (input.contains("m")) {
            array = input.split("m");
            String number = array[0];
            if (MathUtils.isInt(number)) {
                int i = Integer.parseInt(number);
                return i * 60;
            }
        } else if (input.contains("mins")) {
            array = input.split("mins");
            String number = array[0];
            if (MathUtils.isInt(number)) {
                int i = Integer.parseInt(number);
                return i * 60;
            }
        } else if (input.contains("minutes")) {
            array = input.split("minutes");
            String number = array[0];
            if (MathUtils.isInt(number)) {
                int i = Integer.parseInt(number);
                return i * 60;
            }
        }

        return 0;
    }

    public static String formatSeconds(int seconds) {
        int mins = seconds / 60;
        int s = seconds % 60;

        return mins + " minutes and " + s + " seconds";
    }

    public static String formatSecondsToMinutesSeconds(int seconds) {
        int mins = seconds / 60;
        int s = seconds % 60;

        return mins + ":" + (s < 10 ? s + "0" : s);
    }
}
