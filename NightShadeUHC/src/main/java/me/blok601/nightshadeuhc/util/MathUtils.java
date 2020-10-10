package me.blok601.nightshadeuhc.util;

import org.bukkit.Location;
import org.bukkit.World;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by Blok on 6/23/2018.
 */
public class MathUtils {

    public static String formatDoubleToPercet(double percent){
        DecimalFormat df = new DecimalFormat("##.#");
        return df.format(percent);
    }

    public static int getRand(int i, int i1){
        ThreadLocalRandom x = ThreadLocalRandom.current();
        return x.nextInt((i1 - i) + i);
    }

    public static double getPercentage(double input, double outOf){
        double percentage = input / outOf;
        return Math.ceil(percentage * 100);
    }

    public static ArrayList<Location> getCircle(Location center, double radius, int amount) {
        World world = center.getWorld();
        double increment = (2 * Math.PI) / amount;
        ArrayList<Location> locations = new ArrayList<>();
        for (int i = 0; i < amount; i++) {
            double angle = i * increment;
            double x = center.getX() + (radius * Math.cos(angle)); //basic trig
            double z = center.getZ() + (radius * Math.sin(angle)); //basic trig
            locations.add(new Location(world, x, center.getY(), z)); //center will be player location
        }
        return locations;
    }

    public static boolean isInt(String s){
        try{
            Integer.parseInt(s);
            return true;
        }catch(Exception e){
            return false;
        }
    }

    public static boolean getChance(double c) {
        Random random = new Random();
        double r = random.nextDouble();
        return r * 100 < c;
    }

    public static float calculateXPBuff(double initial) {
        //1.25 buff
        double add = (initial / 10F);
        return (float) (initial + add);

    }

    public static boolean isBetween(int max, int min, int val){
        return min < val && val < max;
    }

    public static double cummulativeListDouble(Collection<Double> numbers){
        double i = 0;
        for (double n :  numbers){
            i +=n;
        }

        return i;
    }

    public static int cummulativeList(Collection<Integer> numbers){
        int i = 0;
        for (int n :  numbers){
            i +=n;
        }

        return i;
    }

}
