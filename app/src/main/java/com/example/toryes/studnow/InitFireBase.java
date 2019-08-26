package com.example.toryes.studnow;

import android.app.Application;

import com.example.toryes.studnow.fcm.FcmNotificationBuilder;
import com.firebase.client.Firebase;

/**
 * Created by TORYES on 7/12/2017.
 */

public class InitFireBase extends Application {

    private static final String TAG = "FireBase";


    @Override
    public void onCreate() {
        super.onCreate();
        Firebase.setAndroidContext(this);
        FcmNotificationBuilder.initialize();


    }}