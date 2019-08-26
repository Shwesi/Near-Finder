package com.example.toryes.studnow.chat.core.users.add;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;


import com.example.toryes.studnow.R;
import com.example.toryes.studnow.Utils.Constants;
import com.example.toryes.studnow.Utils.Prefrence;
import com.example.toryes.studnow.Utils.SharedPrefUtil;
import com.example.toryes.studnow.activity.SignupActvity;
import com.example.toryes.studnow.bin.LoginCredentials;
import com.example.toryes.studnow.bin.User;
import com.example.toryes.studnow.bin.Users;
import com.example.toryes.studnow.service.LocationTracker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Author: Kartik Sharma
 * Created on: 9/2/2016 , 10:08 PM
 * Project: FirebaseChat
 */

public class AddUserInteractor implements AddUserContract.Interactor,Constants {
    private AddUserContract.OnUserDatabaseListener mOnUserDatabaseListener;
     String latitude = "0.0",longitude="0.0";

    public AddUserInteractor(AddUserContract.OnUserDatabaseListener onUserDatabaseListener) {
        this.mOnUserDatabaseListener = onUserDatabaseListener;
    }

    @Override
    public void addUserToDatabase(final Context context, final FirebaseUser firebaseUser, final User user1) {
        LocationTracker locationTracker=new LocationTracker(context);

        if(locationTracker.canGetLocation()) {
            latitude = locationTracker.getLatitude() + "";
            longitude = locationTracker.getLongitude() + "";
//            Toast.makeText(SignupActvity.this, locationTracker.getLongitude() + "" + locationTracker.getLatitude(), Toast.LENGTH_SHORT).show();
        }
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        User user = new User(firebaseUser.getUid(),
                firebaseUser.getEmail(),
                new SharedPrefUtil(context).getString(ARG_FIREBASE_TOKEN),convertTimestamp(), user1.phoneNumber,user1.profilePic,latitude,longitude,user1.firstname,user1.lastname,user1.password,user1.emailVerified);
                database.child(Constants.ARG_USERS)
                .child(firebaseUser.getUid())
                .setValue(user)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            mOnUserDatabaseListener.onSuccess(context.getString(R.string.user_successfully_added));
                          LoginCredentials  loginCredentials=new LoginCredentials();
                            loginCredentials.setFirst_name(user1.firstname);
                            loginCredentials.setLast_name(user1.lastname);
                            loginCredentials.setEmail( firebaseUser.getEmail());
                            loginCredentials.setPhone(user1.phoneNumber);
                            loginCredentials.setPassword(user1.password);
                            loginCredentials.setImage(user1.profilePic);
                            loginCredentials.setLongitude(longitude);
                            loginCredentials.setLatitude(latitude);
                            loginCredentials.setFirebasetoken(new SharedPrefUtil(context).getString(ARG_FIREBASE_TOKEN));
                        } else {
                            mOnUserDatabaseListener.onFailure(context.getString(R.string.user_unable_to_add));
                        }
                    }
                });
    }
    public String convertTimestamp(){
        Date d = new Date();
        Log.e("date",d.toString());
        SimpleDateFormat formatter = new SimpleDateFormat("hh:mm a");

        // Create a calendar object that will convert the date and time value in milliseconds to date.
        Calendar calendar = Calendar.getInstance();
        return calendar.getTimeInMillis()+"";

    }
}
