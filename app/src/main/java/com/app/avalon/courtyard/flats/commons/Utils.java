package com.app.avalon.courtyard.flats.commons;

import android.app.ProgressDialog;
import android.content.Context;

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
 * Created by Lalit.Poptani on 9/26/2016.
 */

public class Utils {

    private static final String TAG = Utils.class.getSimpleName();

    private static ProgressDialog progressDialog;

    public static void showProgressDialog(Context context, String message) {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(context);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setCancelable(false);
            progressDialog.setMessage(message);
        }

        progressDialog.show();
    }

    public static void hideProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
            progressDialog = null;
        }
    }

    public static void setFireBasePersistenceData(){
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }

    private static DatabaseReference getFirebaseDatabaseReference() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference reference = database.getReference();
        return reference;
    }

    public static void getBlockInfo() {
        //Query query = getFirebaseDatabaseReference().orderByChild("Block");
        //query.addChildEventListener(new ChildEventListener() {
        getFirebaseDatabaseReference().addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if(dataSnapshot.getValue() != null){
                    AppLogs.e(TAG, dataSnapshot.getKey());
                    AppLogs.e(TAG, String.valueOf(dataSnapshot.getValue()));
                    if(!dataSnapshot.getKey().equalsIgnoreCase("aboutMe")){
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

    public static void getAboutMeInfo(){
        getFirebaseDatabaseReference().child("aboutMe").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if(dataSnapshot.getValue() != null){
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
