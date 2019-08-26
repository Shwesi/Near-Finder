package com.example.toryes.studnow.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.toryes.studnow.Utils.Constants;
import com.example.toryes.studnow.Utils.Prefrence;
import com.example.toryes.studnow.bin.LoginCredentials;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by TORYES on 11/14/2017.
 */

public class AppKilled extends Service implements Constants {
    LoginCredentials loginCredentials;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }

    private void updateLastSeen(String uid, String value) {

        FirebaseDatabase.getInstance()
                .getReference()
                .child(ARG_USERS)
                .child(uid).child("last_seen").setValue(value);
        Prefrence.saveString(this,"last_seen",value);


    }
    public String convertTimestamp(){
        Date d = new Date();
        Log.e("date",d.toString());
        SimpleDateFormat formatter = new SimpleDateFormat("hh:mm a");

        // Create a calendar object that will convert the date and time value in milliseconds to date.
        Calendar calendar = Calendar.getInstance();
        return calendar.getTimeInMillis()+"";

    }

    public void onTaskRemoved(Intent rootIntent) {

        //unregister listeners
        //do any other cleanup if required
        loginCredentials=Prefrence.getLoginCeredential(this);
        if (loginCredentials!=null)
            updateLastSeen(loginCredentials.getUseridentificationNo(),convertTimestamp());
        //stop service
        stopSelf();
    }

    }

