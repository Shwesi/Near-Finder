package com.example.toryes.studnow.chat.core.users.get.all;

import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Log;


import com.example.toryes.studnow.Utils.Constants;
import com.example.toryes.studnow.bin.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Author: Kartik Sharma
 * Created on: 9/2/2016 , 10:08 PM
 * Project: FirebaseChat
 */

public class GetUsersInteractor implements GetUsersContract.Interactor,Constants {
    private static final String TAG = "GetUsersInteractor";

    private GetUsersContract.OnGetAllUsersListener mOnGetAllUsersListener;
    Context context;
    ArrayList<String> contactArrayList,nameArrayList;
    public GetUsersInteractor(Context context,GetUsersContract.OnGetAllUsersListener onGetAllUsersListener) {
        this.mOnGetAllUsersListener = onGetAllUsersListener;
        this.context=context;
        contactArrayList=new ArrayList<>();
        nameArrayList=new ArrayList<>();
    }


    @Override
    public void getAllUsersFromFirebase() {
        Cursor cursor = context.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,null, null, null);

        while (cursor.moveToNext()) {

            String   name = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));

            String phonenumber = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            try {
                // phone must begin with '+'
                PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
                Phonenumber.PhoneNumber numberProto = phoneUtil.parse(phonenumber, "");
                int countryCode = numberProto.getCountryCode();
                long nationalNumber = numberProto.getNationalNumber();
                Log.i("code", "code " + countryCode);
                Log.i("code", "national number " + nationalNumber);
                phonenumber=nationalNumber+"";
            } catch (NumberParseException e) {
                System.err.println("NumberParseException was thrown: " + e.toString());
                if (phonenumber.startsWith("0")) {
                    phonenumber=phonenumber.replaceFirst("^0+(?!$)", "");

                }
            }

            if (!contactArrayList.contains(phonenumber)) {
                phonenumber=phonenumber.replace(" ","");
                contactArrayList.add(phonenumber+"");
                nameArrayList.add(name);
                Log.e("phone", name + " " + ":" + " " + phonenumber);
            }

        }

        cursor.close();
        FirebaseDatabase.getInstance().getReference().child(ARG_USERS).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterator<DataSnapshot> dataSnapshots = dataSnapshot.getChildren().iterator();
                List<User> users = new ArrayList<>();
                while (dataSnapshots.hasNext()) {
                    DataSnapshot dataSnapshotChild = dataSnapshots.next();
                    User user = dataSnapshotChild.getValue(User.class);
                    if (user.emailVerified&&contactArrayList.contains(user.phoneNumber)){
                        user.firstname=nameArrayList.get(contactArrayList.indexOf(user.phoneNumber));
                        user.lastname="";
                    if (!TextUtils.equals(user.uid, FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                        users.add(user);
                    }
                }
                }
                mOnGetAllUsersListener.onGetAllUsersSuccess(users);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                mOnGetAllUsersListener.onGetAllUsersFailure(databaseError.getMessage());
            }
        });
    }

    @Override
    public void getChatUsersFromFirebase() {
        /*FirebaseDatabase.getInstance().getReference().child(Constants.ARG_CHAT_ROOMS).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterator<DataSnapshot> dataSnapshots=dataSnapshot.getChildren().iterator();
                List<User> users=new ArrayList<>();
                while (dataSnapshots.hasNext()){
                    DataSnapshot dataSnapshotChild=dataSnapshots.next();
                    dataSnapshotChild.getRef().
                    Chat chat_my=dataSnapshotChild.getValue(Chat.class);
                    if(chat_my.)4
                    if(!TextUtils.equals(user.uid,FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                        users.add(user);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });*/
    }
}
