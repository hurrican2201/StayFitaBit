package com.lidago.stayfitabit.event;

import com.lidago.stayfitabit.Args;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created on 29.06.2016.
 */
public class FirebaseData {

    private boolean user = false;
    private boolean pushups = false;
    private boolean run = false;
    private List listeners = new ArrayList();

    public synchronized void addFirebaseDataEventListener(FirebaseDataEventListener listener)  {
        listeners.add(listener);
    }
    public synchronized void removeFirebaseDataEventListener(FirebaseDataEventListener listener)   {
        listeners.remove(listener);
    }

    private synchronized void fireAllDataReceivedEvent() {
        FirebaseDataEvent event = new FirebaseDataEvent(this);
        Iterator i = listeners.iterator();
        while(i.hasNext())  {
            ((FirebaseDataEventListener) i.next()).allDataReceived(event);
        }
    }

    public void setDataReceived(String type) {
        if(type == Args.USER) {
            user = true;
        }
        else if(type == Args.PUSHUPS) {
            pushups = true;
        }
        else if(type == Args.RUN) {
            run = true;
        }

        if(user && pushups && run)
            fireAllDataReceivedEvent();
    }
}
