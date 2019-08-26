package com.example.toryes.studnow.chat.core.login;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;


import com.example.toryes.studnow.Utils.Constants;
import com.example.toryes.studnow.Utils.Prefrence;
import com.example.toryes.studnow.Utils.ProgressDialogBox;
import com.example.toryes.studnow.Utils.SharedPrefUtil;
import com.example.toryes.studnow.activity.LoginActvity;
import com.example.toryes.studnow.activity.SignupActvity;
import com.example.toryes.studnow.bin.LoginCredentials;
import com.example.toryes.studnow.bin.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.Locale;

import static android.content.ContentValues.TAG;

/**
 * Author: Kartik Sharma
 * Created on: 8/28/2016 , 11:10 AM
 * Project: FirebaseChat
 */

public class LoginInteractor implements LoginContract.Interactor,Constants {
    private LoginContract.OnLoginListener mOnLoginListener;

    public LoginInteractor(LoginContract.OnLoginListener onLoginListener) {
        this.mOnLoginListener = onLoginListener;
    }

    @Override
    public void
    performFirebaseLogin(final Activity activity, final String email, String password) {
        FirebaseAuth.getInstance()
                .signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(activity, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "performFirebaseLogin:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (task.isSuccessful()) {
                            if (task.getResult().getUser().isEmailVerified()) {
                                mOnLoginListener.onSuccess(task.getResult().toString());
                                getSeen(activity.getApplicationContext(), task.getResult().getUser().getUid());

                                //updating firebase token of user and last_seen status
                                updateFirebaseToken(task.getResult().getUser().getUid(),
                                        new SharedPrefUtil(activity.getApplicationContext()).getString(Constants.ARG_FIREBASE_TOKEN, null));
                            }
                            else {
                                Toast.makeText(activity,"Please Verify Your Email",Toast.LENGTH_SHORT).show();
                                ProgressDialogBox.disMissDailog();
                            }
                        } else {
                            mOnLoginListener.onFailure(task.getException().getMessage());
                        }
                    }
                });
    }

    private void updateFirebaseToken(String uid, String token) {
//        User user=new User();
//        user.firebaseToken=token;
//        user.emailVerified=true;
//        user.last_seen=convertTimestamp();
        FirebaseDatabase.getInstance()
                .getReference()
                .child(ARG_USERS)
                .child(uid).child("firebaseToken")
                .setValue(token);
        //updating last seen
        FirebaseDatabase.getInstance()
                .getReference()
                .child(ARG_USERS)
                .child(uid).child("last_seen").setValue(convertTimestamp());
        FirebaseDatabase.getInstance()
                .getReference()
                .child(ARG_USERS)
                .child(uid).child("emailVerified").setValue(true);

    }

    public String convertTimestamp(){
        Date d = new Date();
        Log.e("date",d.toString());
        SimpleDateFormat formatter = new SimpleDateFormat("hh:mm a", Locale.US);

        // Create a calendar object that will convert the date and time value in milliseconds to date.
        Calendar calendar = Calendar.getInstance();
        return calendar.getTimeInMillis()+"";

    }
    public void getSeen(final Context con, String uid){
        FirebaseDatabase.getInstance().getReference().child(ARG_USERS).child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterator<DataSnapshot> dataSnapshots = dataSnapshot.getChildren().iterator();
                User user1 = dataSnapshot.getValue(User.class);
                LoginCredentials loginCredentials=new LoginCredentials();
//                            loginCredentials.setUser_id(jsonObject.getString(KEY_USER_ID));
//                            loginCredentials.setUseridentificationNo(jsonObject.getString(KEY_UNIQUE_ID));
                loginCredentials.setFirst_name(user1.firstname);
                loginCredentials.setLast_name(user1.lastname);
                loginCredentials.setEmail( user1.email);
                loginCredentials.setPhone(user1.phoneNumber);
                loginCredentials.setPassword(user1.password);
                loginCredentials.setImage(user1.profilePic);
                Prefrence.saveString(con,"imageUrl",user1.profilePic);
                loginCredentials.setLongitude(user1.lon);
                loginCredentials.setLatitude(user1.lat);
                loginCredentials.setUseridentificationNo(user1.uid);
                Log.e("userr",user1.firstname+" "+ user1.profilePic);
                loginCredentials.setFirebasetoken( new SharedPrefUtil(con).getString(Constants.ARG_FIREBASE_TOKEN, null));
                Prefrence.saveLoginCeredential(con, loginCredentials);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


}
