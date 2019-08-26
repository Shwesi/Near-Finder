package com.example.toryes.studnow.helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.toryes.studnow.Utils.Constants;
import com.example.toryes.studnow.bin.Chat;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by TORYES on 10/12/2017.
 */

public class ChatHelper  extends SQLiteOpenHelper implements Constants {

    private HashMap hp;

    public ChatHelper(Context context) {
        super(context, DATABASE_NAME_CHAT , null, 2);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // TODO Auto-generated method stub
        db.execSQL(
                "create table chatidstorage " +
                        "(id integer primary key,senderUid text, pushkey text)"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub
        db.execSQL("DROP TABLE IF EXISTS chatidstorage");
        onCreate(db);
    }

    public boolean insertMessage (String sId,String pushId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(KEY_SID, sId);
        contentValues.put(KEY_PUSH_ID, pushId);
        long ab=  db.insert("chatidstorage", null, contentValues);
        if (ab>0)
            Log.e("msg","sucess");
        return true;
    }

    public ArrayList<String> getChat(String sender){
        ArrayList<String> chatArrayList=new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String pushId = null;
        Cursor cursor = db.rawQuery("select * from chatidstorage where senderUid" +
                "='"+sender+"'",null);


        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
//                Chat chat_my=new Chat();
               pushId=cursor.getString(cursor.getColumnIndex(KEY_PUSH_ID));
                chatArrayList.add(pushId);
//                chat_my.receiver = cursor.getString(cursor.getColumnIndex(KEY_RECEIVER));
//                chat_my.senderUid=cursor.getString(cursor.getColumnIndex(KEY_SID));
//                chat_my.receiverUid = cursor.getString(cursor.getColumnIndex(KEY_RID));
//                chat_my.timestamp=cursor.getInt(cursor.getColumnIndex(KEY_TIME));
//                chat_my.userName = cursor.getString(cursor.getColumnIndex(KEY_USERNAME));
//                chat_my.message= cursor.getString(cursor.getColumnIndex(KEY_MESSAGE));
//                chatArrayList.add(chat_my);
//                cursor.moveToNext();
            }
        }
        Log.e("array",chatArrayList.toString());
        return chatArrayList;
    }
    public Cursor getData(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from chatidstorage where id="+id+"", null );
        return res;
    }

    public int numberOfRows(){
        SQLiteDatabase db = this.getReadableDatabase();
        int numRows = (int) DatabaseUtils.queryNumEntries(db, TABLE_NAME);
        return numRows;
    }

    public boolean updateContact (Integer id, String name, String phone, String email, String street,String place) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("name", name);
        contentValues.put("phone", phone);
        contentValues.put("email", email);
        contentValues.put("street", street);
        contentValues.put("place", place);
        db.update("chatidstorage", contentValues, "id = ? ", new String[] { Integer.toString(id) } );
        return true;
    }

    public void deleteContact (String email) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME,
                "email = ? ",
                new String[] { email });
    }
    public int countMessage(String email){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "SELECT * FROM chat_my WHERE email = '" +email+"'", null );
        int count=res.getCount();
        return count;
    }
//    public ArrayList<String> getAllCotacts() {
//        ArrayList<String> array_list = new ArrayList<String>();
//
//        //hp = new HashMap();
//        SQLiteDatabase db = this.getReadableDatabase();
//        Cursor res =  db.rawQuery( "select * from contacts", null );
//        res.moveToFirst();
//
//        while(res.isAfterLast() == false){
//            array_list.add(res.getString(res.getColumnIndex(CONTACTS_COLUMN_NAME)));
//            res.moveToNext();
//        }
//        return array_list;
//    }
}
