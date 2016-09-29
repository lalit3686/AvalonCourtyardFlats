package com.app.avalon.courtyard.flats.commons;

import com.app.avalon.courtyard.flats.application.MyApplication;
import com.app.avalon.courtyard.flats.beans.AboutMeDetails;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.LinkedList;

/**
 * Created by Lalit.Poptani on 9/29/2016.
 */

public class FirebaseUtils {

    private static final String TAG = FirebaseUtils.class.getSimpleName();
    private static DatabaseReference databaseReference;

    private static DatabaseReference getFirebaseDatabaseReference() {
        if(databaseReference == null){
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            database.setPersistenceEnabled(true);
            databaseReference = database.getReference();
        }
        return databaseReference;
    }

    public static void getBlockInfo() {
        //Query query = getFirebaseDatabaseReference().orderByChild("Block");
        //query.addChildEventListener(new ChildEventListener() {
        getFirebaseDatabaseReference().child(AppConstants.FIREBASE_NODE_OWNERS).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if (dataSnapshot.getValue() != null) {
                    AppLogs.e(TAG, dataSnapshot.getKey());
                    AppLogs.e(TAG, String.valueOf(dataSnapshot.getValue()));
                    if (!dataSnapshot.getKey().equalsIgnoreCase(AppConstants.FIREBASE_NODE_ABOUT_ME)) {
                        LinkedList<Object> event = new LinkedList<Object>();
                        event.add(AppConstants.MESSAGE_ADDED);
                        event.add(dataSnapshot);
                        MyApplication.getEventBusInstance().post(event);
                    }
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                LinkedList<Object> event = new LinkedList<Object>();
                event.add(AppConstants.MESSAGE_UPDATED);
                event.add(dataSnapshot);
                MyApplication.getEventBusInstance().post(event);
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                LinkedList<Object> event = new LinkedList<Object>();
                event.add(AppConstants.MESSAGE_DELETED);
                event.add(dataSnapshot);
                MyApplication.getEventBusInstance().post(event);
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    public static void getAboutMeInfo() {
        getFirebaseDatabaseReference().child(AppConstants.FIREBASE_NODE_ABOUT_ME).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot.getValue() != null) {
                    AppLogs.e(TAG, dataSnapshot.getKey());
                    AppLogs.e(TAG, dataSnapshot.getValue().toString());
                    AboutMeDetails aboutMeDetails = dataSnapshot.getValue(AboutMeDetails.class);
                    MyApplication.getEventBusInstance().post(aboutMeDetails);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
