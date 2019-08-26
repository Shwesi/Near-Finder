package com.example.toryes.studnow.helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import com.example.toryes.studnow.Utils.Constants;
import com.example.toryes.studnow.bin.Chat;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by TORYES on 10/9/2017.
 */

public class ChatDatabaseHelper extends SQLiteOpenHelper implements Constants {

    private HashMap hp;

    public ChatDatabaseHelper(Context context) {
        super(context, DATABASE_NAME_CHAT , null, 2);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // TODO Auto-generated method stub
        db.execSQL(
                "create table chat_my " +
                        "(id integer primary key,sender text, reciever text,senderUid text, recieverUid text,chatTime text, userName text,message text,push_id text)"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub
        db.execSQL("DROP TABLE IF EXISTS contacts");
        onCreate(db);
    }

    public boolean insertMessage (String sender, String receiver,String sId,String rID,long time,String username,String message,String pushid) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(KEY_SENDER, sender);
        contentValues.put(KEY_RECEIVER, receiver);
        contentValues.put(KEY_SID, sId);
        contentValues.put(KEY_RID, rID);
        contentValues.put(KEY_TIME, time);
        contentValues.put(KEY_USERNAME, username);
        contentValues.put(KEY_MESSAGE,message);
        contentValues.put(KEY_PUSH_ID,pushid);
      long ab=  db.insert(TABLE_NAME, null, contentValues);
        if (ab>0)
            Log.e("msg","sucess");
        return true;
    }

    public ArrayList<Chat> getChat(String sender){
        ArrayList<Chat> chatArrayList=new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor  cursor = db.rawQuery("select * from "+TABLE_NAME +" where sender='"+sender+"' OR reciever='"+sender+"'",null);


        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                Chat chat=new Chat();
                chat.sender=cursor.getString(cursor.getColumnIndex(KEY_SENDER));
                chat.receiver = cursor.getString(cursor.getColumnIndex(KEY_RECEIVER));
                chat.senderUid=cursor.getString(cursor.getColumnIndex(KEY_SID));
                chat.receiverUid = cursor.getString(cursor.getColumnIndex(KEY_RID));
                chat.timestamp=cursor.getInt(cursor.getColumnIndex(KEY_TIME));
                chat.userName = cursor.getString(cursor.getColumnIndex(KEY_USERNAME));
                chat.message= cursor.getString(cursor.getColumnIndex(KEY_MESSAGE));
                chatArrayList.add(chat);
                cursor.moveToNext();
            }
        }
        Log.e("array",chatArrayList.toString());
        return chatArrayList;
    }

    public Chat getChatDetail(String sender){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor  cursor = db.rawQuery("select * from "+TABLE_NAME +" where sender='"+sender+"' OR reciever='"+sender+"'",null);
        Chat chat = null;

        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                 chat=new Chat();
                chat.sender=cursor.getString(cursor.getColumnIndex(KEY_SENDER));
                chat.receiver = cursor.getString(cursor.getColumnIndex(KEY_RECEIVER));
                chat.senderUid=cursor.getString(cursor.getColumnIndex(KEY_SID));
                chat.receiverUid = cursor.getString(cursor.getColumnIndex(KEY_RID));
                chat.timestamp=cursor.getInt(cursor.getColumnIndex(KEY_TIME));
                chat.userName = cursor.getString(cursor.getColumnIndex(KEY_USERNAME));
                chat.message= cursor.getString(cursor.getColumnIndex(KEY_MESSAGE));
                cursor.moveToNext();
            }
        }

        return chat;
    }
    public Cursor getData(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from contacts where id="+id+"", null );
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
        db.update("contacts", contentValues, "id = ? ", new String[] { Integer.toString(id) } );
        return true;
    }

    public void deleteContact (String pushId,String message) {
        SQLiteDatabase db = this.getWritableDatabase();
//       long ab= db.delete(TABLE_NAME,
//                KEY_MESSAGE + "='"+message+"' AND " + KEY_SENDER + "='"+sender+"'",
//                null);
        long ab= db.delete(TABLE_NAME,
                KEY_PUSH_ID + "=?" ,
                new String[] {pushId});
        if (ab>0)
            Log.e("ab","succees");
//        db.delete(TABLE_NAME,
//                "email = ? ",
//                new String[] { sender });
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