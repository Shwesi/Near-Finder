package com.example.toryes.studnow;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.multidex.MultiDex;
import android.util.Log;

import com.example.toryes.studnow.Utils.Constants;
import com.example.toryes.studnow.Utils.Prefrence;
import com.example.toryes.studnow.activity.Splash;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class FirebaseChatMainApp extends Application implements Application.ActivityLifecycleCallbacks,Constants {
    private static boolean sIsChatActivityOpen = false;
    private static FirebaseDatabase mDatabase;

    IntentFilter intentFilter;
    public static boolean isChatActivityOpen() {
        return sIsChatActivityOpen;
    }

    public static void setChatActivityOpen(boolean isChatActivityOpen) {
        FirebaseChatMainApp.sIsChatActivityOpen = isChatActivityOpen;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        registerActivityLifecycleCallbacks(this);




//        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
//        FirebaseDatabase.getInstance().getReference().keepSynced(true);
//        if (mDatabase == null) {
//            mDatabase = FirebaseDatabase.getInstance();
//            mDatabase.setPersistenceEnabled(true);
//            // ...
//        }
    }
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(base);
    }


    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        Log.e("start","start");
    }

    @Override
    public void onActivityStarted(Activity activity) {
        Log.e("start1","start");
    }

    @Override
    public void onActivityResumed(Activity activity) {
        Log.e("start2","start");
//        registerReceiver(myBroadcastReceiver,intentFilter);
    }

    @Override
    public void onActivityPaused(Activity activity) {
        Log.e("start3","start");


    }

    @Override
    public void onActivityStopped(Activity activity) {
        Log.e("start4","start");
        if (!isAppForground(getApplicationContext())&& Splash.permision&&Prefrence.getBoolen(this,"fcmregistration"))
            updateLatSeen(getApplicationContext(),FirebaseAuth.getInstance().getCurrentUser().getUid(),convertTimestamp());
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
        Log.e("start5","start");
    }

    @Override
    public void onActivityDestroyed(Activity activity) {
//        unregisterReceiver(myBroadcastReceiver);
        Log.e("start6","start");
    }
    public String convertTimestamp(){
        Date d = new Date();
        Log.e("date",d.toString());
        SimpleDateFormat formatter = new SimpleDateFormat("hh:mm a");

        // Create a calendar object that will convert the date and time value in milliseconds to date.
        Calendar calendar = Calendar.getInstance();
        return calendar.getTimeInMillis()+"";

    }
    private void updateLatSeen(Context context,String uid, String value) {
//        FirebaseDatabase.getInstance()
//                .getReference()
//                .child(ARG_USERS)
//                .child(uid)
//                .child(ARG_FIREBASE_TOKEN)
//                .setValue(token);
        FirebaseDatabase.getInstance()
                .getReference()
                .child(ARG_USERS)
                .child(uid).child("last_seen").setValue(value);
        Prefrence.saveString(context,"last_seen",value);


    }
    public boolean isAppForground(Context mContext) {

        ActivityManager am = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> tasks = am.getRunningTasks(1);
        if (!tasks.isEmpty()) {
            ComponentName topActivity = tasks.get(0).topActivity;
            if (!topActivity.getPackageName().equals(mContext.getPackageName())) {
                return false;
            }
        }

        return true;
    }
}
