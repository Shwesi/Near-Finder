package com.example.toryes.studnow.bin;

import java.util.Date;


/**
 * Created by Ashu on 24/11/15.
 */
public class Message {
    private String mText;
    private String mSender;
    private Date mDate;
    private String myGcmId;

    public void setMyGcmId(String myGcmId) {
        this.myGcmId = myGcmId;
    }

    public String getMyGcmId() {
        return myGcmId;
    }

    public Date getDate() {
        return mDate;
    }

    public void setDate(Date date) {
        mDate = date;
    }

    public String getText() {
        return mText;
    }

    public void setText(String text) {
        mText = text;
    }

    public String getSender() {
        return mSender;
    }

    public void setSender(String sender) {
        mSender = sender;
    }
}
