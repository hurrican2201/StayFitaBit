package com.lidago.stayfitabit.pushups;

/**
 * Created on 25.06.2016.
 */
public class PushUpsWorkout {
    private int level;
    private int day;
    private int set1;
    private int set2;
    private int set3;
    private int set4;
    private int set5;

    public PushUpsWorkout() {
        // empty constructor required for Firebase
    }

    public PushUpsWorkout(int level, int day, int set1, int set2, int set3, int set4, int set5) {
        this.level = level;
        this.day = day;
        this.set1 = set1;
        this.set2 = set2;
        this.set3 = set3;
        this.set4 = set4;
        this.set5 = set5;
    }

    public int getLevel() {
        return level;
    }

    public int getDay() {
        return day;
    }

    public int getSet1() {
        return set1;
    }

    public int getSet2() {
        return set2;
    }

    public int getSet3() {
        return set3;
    }

    public int getSet4() {
        return set4;
    }

    public int getSet5() {
        return set5;
    }
}
