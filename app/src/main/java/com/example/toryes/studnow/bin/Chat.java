package com.example.toryes.studnow.bin;

import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Author: Kartik Sharma
 * Created on: 9/4/2016 , 12:43 PM
 * Project: FirebaseChat
 */

@IgnoreExtraProperties
public class Chat {
    public String sender;
    public String receiver;
    public String senderUid;
    public String receiverUid;
    public String message;
    public long timestamp;
    public String userName;
    public String pushId;
    public String deleted;
//    public String senderDel;
//    public String receiverDel;
//    public String pushKey;

    public Chat() {
    }

    public Chat(String sender, String receiver, String senderUid, String receiverUid, String message, long timestamp,String userName,String pushId,String deleted) {
        this.sender = sender;
        this.receiver = receiver;
        this.senderUid = senderUid;
        this.receiverUid = receiverUid;
        this.message = message;
        this.timestamp = timestamp;
        this.userName=userName;
        this.pushId=pushId;
        this.deleted=deleted;
//        this.receiverDel=recieverDel;
//        this.messageId=messageId;
    }
}
