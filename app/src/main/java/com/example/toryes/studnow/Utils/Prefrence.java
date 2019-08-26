package com.example.toryes.studnow.Utils;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;

import com.example.toryes.studnow.activity.HomeActivity;
import com.example.toryes.studnow.activity.LoginActvity;
import com.example.toryes.studnow.bin.LoginCredentials;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * Created by TORYES on 7/3/2017.
 */

public class Prefrence implements Constants{
    public static SharedPreferences.Editor editor;
    public static   SharedPreferences preferences;
    public static SavedSucees savedSucees;
    public Prefrence(SavedSucees savedSucees){
        this.savedSucees=savedSucees;
    }
    public Prefrence(){

    }
    public static void clearPref(Context context){
        editor=context.getSharedPreferences(context.getPackageName(),Context.MODE_PRIVATE).edit();
        editor.clear().commit();

    }
    public static void saveBollen(Context context,String key,boolean value){
        editor=context.getSharedPreferences(context.getPackageName(),Context.MODE_PRIVATE).edit();
        editor.putBoolean(key,value).commit();
    }
    public static boolean getBoolen(Context context,String key){
        preferences=context.getSharedPreferences(context.getPackageName(),Context.MODE_PRIVATE);
        return preferences.getBoolean(key,false);
    }
    public static void saveString(Context context,String key,String value){
        editor=context.getSharedPreferences(context.getPackageName(),Context.MODE_PRIVATE).edit();
        editor.putString(key,value).commit();
    }
    public static String getString(Context context,String key){
        preferences=context.getSharedPreferences(context.getPackageName(),Context.MODE_PRIVATE);
        return preferences.getString(key,null);
    }
    public static void saveInt(Context context,String key,int value){
        editor=context.getSharedPreferences(context.getPackageName(),Context.MODE_PRIVATE).edit();
        editor.putInt(key,value).commit();
    }
    public static void saveLong(Context context,String key,long value){
        editor=context.getSharedPreferences(context.getPackageName(),Context.MODE_PRIVATE).edit();
        editor.putLong(key,value).commit();
    }
    public static long getLong(Context context,String key){
        preferences=context.getSharedPreferences(context.getPackageName(),Context.MODE_PRIVATE);
        return preferences.getLong(key,0);
    }
    public static int getInt(Context context,String key){
        preferences=context.getSharedPreferences(context.getPackageName(),Context.MODE_PRIVATE);
        return preferences.getInt(key,0);
    }
    public static Object loadSerializedObject(File f) {
        try {
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream(f));
            Object o = ois.readObject();
            return o;
        } catch (Exception ex) {
            Log.v("read error", ex.getMessage());
            ex.printStackTrace();
        }
        return null;
    }
    public static void saveLoginCeredential(Context context,LoginCredentials loginCredentials) {
        try {
            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(new File(Environment.getExternalStorageDirectory() + "/Android/data/toryes.nearme/login.bin"))); //Select where you wish to save the file...
            oos.writeObject(loginCredentials); // write the class as an 'object'
            oos.flush(); // flush the stream to insure all of the information was written to 'save_object.bin'
            oos.close();// close the stream
            Prefrence.saveBollen(context,LOGIN_SUCCES,true);
//            Intent myIntent=new Intent(context,HomeActivity.class);
//            myIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            context.startActivity(myIntent);
            savedSucees.saved(true);

        } catch (Exception ex) {
            Log.e("save error", ex.getMessage());
            savedSucees.saved(false);
            ex.printStackTrace();
        }
    }
    public static void saveLoginUpdateCeredential(Context context,LoginCredentials loginCredentials) {
        try {
            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(new File(Environment.getExternalStorageDirectory() + "/Android/data/toryes.nearme/login.bin"))); //Select where you wish to save the file...
            oos.writeObject(loginCredentials); // write the class as an 'object'
            oos.flush(); // flush the stream to insure all of the information was written to 'save_object.bin'
            oos.close();// close the stream
            Prefrence.saveBollen(context,LOGIN_SUCCES,true);
//            Intent myIntent=new Intent(context,HomeActivity.class);
//            myIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            context.startActivity(myIntent);
//            savedSucees.saved(true);

        } catch (Exception ex) {
            Log.e("save error", ex.getMessage());
//            savedSucees.saved(false);
            ex.printStackTrace();
        }
    }
    public static LoginCredentials getLoginCeredential(Context context){
        LoginCredentials loginCredentials = (LoginCredentials) Prefrence.loadSerializedObject(new File(Environment.getExternalStorageDirectory() + "/Android/data/toryes.nearme/login.bin"));
        return loginCredentials;
    }
    public static void storeRegIdInPref(Context context,String token) {
        SharedPreferences pref = context.getSharedPreferences(SHARED_PREF, 0);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("regId", token);
        editor.commit();
    }
    public static String displayFirebaseRegId(Context context) {
        SharedPreferences pref = context.getSharedPreferences(SHARED_PREF, 0);
        String regId = pref.getString("regId", null);

        Log.e("regid", "Firebase reg id: " + regId);

        if (!TextUtils.isEmpty(regId))
            Log.e("Firebase Reg Id: ","" + regId);
        else
            Log.e("Firebase Reg Id: ","Firebase Reg Id is not received yet!");
        return regId;
    }
    public interface SavedSucees{
        public boolean saved(boolean value);
    }
}
