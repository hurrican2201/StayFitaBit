package com.lidago.stayfitabit.firebase;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.lidago.stayfitabit.ActivityType;
import com.lidago.stayfitabit.Args;
import com.lidago.stayfitabit.event.FirebaseData;
import com.lidago.stayfitabit.history.HistoryModel;
import com.lidago.stayfitabit.history.HistoryViewAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Created on 23.06.2016.
 */
public class FirebaseClient {

    private static FirebaseClient instance = null;
    private Firebase userRef;
    private Firebase runningRef;
    private Firebase pushUpsRef;
    private FirebaseData firebaseData;
    private User user;
    private List<HistoryModel> allItems;
    private List<HistoryModel> runItems;
    private List<HistoryModel> pushUpItems;
    private HistoryViewAdapter adapter;
    private boolean isFirebaseListenerSet = false;

    public static FirebaseClient getInstance() {
        if(instance == null) {
            instance = new FirebaseClient();
        }
        return instance;
    }

    private FirebaseClient() {
        allItems = new ArrayList<HistoryModel>();
        pushUpItems = new ArrayList<HistoryModel>();
        runItems = new ArrayList<HistoryModel>();
        firebaseData = new FirebaseData();
        setupFirebase();
    }

    public Firebase getUserRef() {
        return userRef;
    }

    public Firebase getRunningRef() {
        return runningRef;
    }

    public Firebase getPushUpsRef() {
        return pushUpsRef;
    }

    public FirebaseData getFirebaseData() {
        return firebaseData;
    }

    public User getUser() {
        return user;
    }

    public List<HistoryModel> getAllItems() {
        return allItems;
    }

    public List<HistoryModel> getRunItems() {
        return runItems;
    }

    public List<HistoryModel> getPushUpItems() {
        return pushUpItems;
    }

    public void setAdapter(HistoryViewAdapter adapter) {
        this.adapter = adapter;
        adapter.notifyDataSetChanged();
    }

    private void setupFirebase() {
        userRef = new Firebase(Args.USERS_URL);
        runningRef = new Firebase(Args.RUNNING_URL);
        pushUpsRef = new Firebase(Args.PUSHUPS_URL);
    }

    public void setListener() {
        userRef.child(getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                user = dataSnapshot.getValue(User.class);
                firebaseData.setDataReceived(Args.USER);
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

        pushUpsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                firebaseData.setDataReceived(Args.PUSHUPS);
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

        runningRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                firebaseData.setDataReceived(Args.RUN);
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

        runningRef.orderByChild("uid").equalTo(getUid()).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Running run = dataSnapshot.getValue(Running.class);
                HistoryModel model = createHistoryModelObject(run);
                allItems.add(model);
                runItems.add(model);
                Collections.sort(allItems);
                Collections.sort(runItems);
                if(adapter != null)
                    adapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

        pushUpsRef.orderByChild("uid").equalTo(getUid()).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                PushUps push = dataSnapshot.getValue(PushUps.class);
                HistoryModel model = createHistoryModelObject(push);
                allItems.add(model);
                pushUpItems.add(model);
                Collections.sort(allItems);
                Collections.sort(pushUpItems);
                if(adapter != null)
                    adapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
        isFirebaseListenerSet = true;
    }

    public void createUser(String email, String password, Firebase.ValueResultHandler<Map<String, Object>> handler) {
        userRef.createUser(email, password, handler);
    }

    public boolean isLoggedIn() {
        if(userRef.getAuth() == null) {
            return false;
        }
        else
            return true;
    }

    public boolean isFirebaseListenerSet() {
        return isFirebaseListenerSet;
    }

    public void logout() {
        userRef.unauth();
    }

    public String getUid() {
        return userRef.getAuth().getUid();
    }

    public void saveToFirebase(Running run) {
        runningRef.push().setValue(run);
    }

    public void saveToFirebase(PushUps pushUps) {
        pushUpsRef.push().setValue(pushUps);
    }

    public void saveToFirebase(User user) {
        userRef.child(getUid()).setValue(user);
    }

    public void sortLists() {
        Collections.sort(allItems);
        Collections.sort(runItems);
        Collections.sort(pushUpItems);
    }

    public void sortListsReverse() {
        Collections.reverse(allItems);
        Collections.reverse(runItems);
        Collections.reverse(pushUpItems);
    }

    private HistoryModel createHistoryModelObject(Running run) {
        return new HistoryModel(ActivityType.RUN, run.getStartTime(), run.getValue(), (run.getEndTime().getTime()-run.getStartTime().getTime()), run.getLocationList());
    }

    private HistoryModel createHistoryModelObject(PushUps push) {
        return new HistoryModel(ActivityType.PUSHUPS, push.getStartTime(), push.getValue(), (push.getEndTime().getTime()-push.getStartTime().getTime()), push.getWorkout());
    }

    public float getTotalDistance() {
        int distance = 0;
        for(HistoryModel run:runItems) {
            distance += run.getValue();
        }
        return (distance/1000);
    }

    public long getTotalDuration() {
        long duration = 0;
        for(HistoryModel run:runItems) {
            duration += run.getDuration();
        }
        return duration;
    }

    public int getTotalPushUps() {
        int value = 0;
        for(HistoryModel pushUps:pushUpItems) {
            value += pushUps.getValue();
        }
        return value;
    }

    public int getTotalCalories() {
        int calories;

        if((user.getGender())==Gender.MALE) {
            double runCalories = user.getWeight() * Args.MENMET * getTotalDuration()/3600000;
            double pushUpCalories = getTotalPushUps();
            calories = (int)runCalories + (int)pushUpCalories;
        }else if ((user.getGender())==Gender.FEMALE) {
            double runCalories = user.getWeight() * Args.WOMENMET * getTotalDuration()/3600000;
            double pushUpCalories = getTotalPushUps();
            calories = (int)runCalories + (int)pushUpCalories;
        }
        else {
            calories = 0;
        }

        return calories;
    }

    public boolean isUserInitialize() {
        if(user == null || user.getAge() == 0 || user.getWeight() == 0 || user.getSize() == 0) {
            user = new User(Gender.MALE);
            user.setForename("");
            user.setName("");
            return false;
        }
        else
            return true;
    }
}
