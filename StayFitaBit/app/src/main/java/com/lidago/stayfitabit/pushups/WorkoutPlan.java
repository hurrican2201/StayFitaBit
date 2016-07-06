package com.lidago.stayfitabit.pushups;

import java.util.ArrayList;
import java.util.List;

/**
 * Created on 24.06.2016.
 */
public class WorkoutPlan {

    private static WorkoutPlan instance = null;
    private List<PushUpsWorkout> workoutPlan;

    public static WorkoutPlan getInstance() {
        if(instance == null) {
            instance = new WorkoutPlan();
        }
        return instance;
    }

    private WorkoutPlan() {
        workoutPlan = new ArrayList<PushUpsWorkout>();
        // level 1, week 1
        workoutPlan.add(new PushUpsWorkout(1,1,2,3,2,2,3));
        workoutPlan.add(new PushUpsWorkout(1,2,3,4,2,3,4));
        workoutPlan.add(new PushUpsWorkout(1,3,4,5,4,4,5));
        // level 1, week 2
        workoutPlan.add(new PushUpsWorkout(1,4,4,6,4,4,6));
        workoutPlan.add(new PushUpsWorkout(1,5,5,6,4,4,7));
        workoutPlan.add(new PushUpsWorkout(1,6,5,7,5,5,8));
        // level 2, week 1
        workoutPlan.add(new PushUpsWorkout(2,1,6,6,4,4,5));
        workoutPlan.add(new PushUpsWorkout(2,2,6,8,6,6,7));
        workoutPlan.add(new PushUpsWorkout(2,3,8,10,7,7,10));
        // level 2, week 2
        workoutPlan.add(new PushUpsWorkout(2,4,9,11,8,8,11));
        workoutPlan.add(new PushUpsWorkout(2,5,10,12,9,9,13));
        workoutPlan.add(new PushUpsWorkout(2,6,12,13,10,10,15));
        // level 3, week 1
        workoutPlan.add(new PushUpsWorkout(3,1,10,12,7,7,9));
        workoutPlan.add(new PushUpsWorkout(3,2,10,12,8,8,12));
        workoutPlan.add(new PushUpsWorkout(3,3,11,15,9,9,13));
        // level 3, week 2
        workoutPlan.add(new PushUpsWorkout(3,4,14,14,10,10,15));
        workoutPlan.add(new PushUpsWorkout(3,5,14,16,12,12,17));
        workoutPlan.add(new PushUpsWorkout(3,6,16,17,14,14,20));
        // level 4, week 1
        workoutPlan.add(new PushUpsWorkout(4,1,10,12,7,7,9));
        workoutPlan.add(new PushUpsWorkout(4,2,10,12,8,8,12));
        workoutPlan.add(new PushUpsWorkout(4,3,11,13,9,9,13));
        // level 4, week 2
        workoutPlan.add(new PushUpsWorkout(4,4,12,14,11,10,16));
        workoutPlan.add(new PushUpsWorkout(4,5,14,16,12,12,18));
        workoutPlan.add(new PushUpsWorkout(4,6,16,18,13,13,20));
        // level 5, week 1
        workoutPlan.add(new PushUpsWorkout(5,1,12,17,13,13,17));
        workoutPlan.add(new PushUpsWorkout(5,2,14,19,14,14,25));
        workoutPlan.add(new PushUpsWorkout(5,3,16,21,15,15,21));
        // level 5, week 2
        workoutPlan.add(new PushUpsWorkout(5,4,18,22,16,16,25));
        workoutPlan.add(new PushUpsWorkout(5,5,20,25,20,20,28));
        workoutPlan.add(new PushUpsWorkout(5,6,23,28,23,23,33));
        // level 6, week 1
        workoutPlan.add(new PushUpsWorkout(6,1,14,18,14,14,20));
        workoutPlan.add(new PushUpsWorkout(6,2,20,25,15,15,25));
        workoutPlan.add(new PushUpsWorkout(6,3,22,30,20,20,28));
        // level 6, week 2
        workoutPlan.add(new PushUpsWorkout(6,4,21,25,21,21,32));
        workoutPlan.add(new PushUpsWorkout(6,5,25,29,25,25,36));
        workoutPlan.add(new PushUpsWorkout(6,6,29,33,29,29,40));
    }

    public List<PushUpsWorkout> getWorkoutPlan() {
        return workoutPlan;
    }
}
