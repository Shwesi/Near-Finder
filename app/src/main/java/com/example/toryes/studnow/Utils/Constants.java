package com.example.toryes.studnow.Utils;

import android.widget.AutoCompleteTextView;

/**
 * Created by TORYES on 6/30/2017.
 */

public interface Constants  {
    String BASE_URL="http://www.troyesitservices.com/nearme/";
    String LOGIN_OPERATION = "login";
    String LOGIN_SUCCES="login_success";
    String KEY_USER_ID="user_id";
    String KEY_FIRST_NAME="first_name";
    String KEY_lAST_NAME="last_name";
    String KEY_EMAIL="email";
    String KEY_PHONE="phone";
    String KEY_IMAGE="image";
    String KEY_LONGITUDE="longitude";
    String KEY_LATITUDE="latitude";
    String KEY_DISTANCE="distance";
    String KEY_DISTANCE_UNIT="distance_unit";
    String KEY_PASSWORD="password";
    String KEY_SESSION_TOKEN="session_token";
    String KEY_PLACE="place";
    String KEY_MESSAGE="message";
    String KEY_PUSH_ID="push_id";
    String KEY_UNIQUE_ID="unique_id";
    String TOPIC_GLOBAL = "global";
    String ARG_USERS = "friends";
    String KEY_OTHER_CHAT_IMAGE="other_chat";
   String ARG_RECEIVER = "receiver";
    String ARG_RECEIVER_UID = "receiver_uid";
     String ARG_CHAT_ROOMS = "chat_rooms";
     String ARG_FIREBASE_TOKEN = "firebaseToken";
    String ARG_FRIENDS = "friends";
     String ARG_UID = "uid";
    // broadcast receiver intent filters
    String REGISTRATION_COMPLETE = "registrationComplete";
    String PUSH_NOTIFICATION = "pushNotification";

    // id to handle the notification in the notification tray
    int NOTIFICATION_ID = 100;
    int NOTIFICATION_ID_BIG_IMAGE = 101;

    String SHARED_PREF = "ah_firebase";
    String DATABASE_NAME = "chat_my.db";
    String DATABASE_NAME_CHAT = "chat_storage.db";
    String DATABASE_NAME_DELETED = "deleted_storage.db";
    String TABLE_NAME = "chat_my";
    String COLUMN_ID = "id";
     String COLUMN_EMAIL = "email";
     String COLUMN_MESSAGE = "message";
    String distance="distance";
    String lat="lat";
    String lon="lon";
    String phone="phone";
    String KEY_UID="uid";
    String KEY_TOKEN="firebaseToken";
    String KEY_SENDER="sender";
    String KEY_RECEIVER="reciever";
    String KEY_SID="senderUid";
    String KEY_RID="recieverUid";
    String KEY_TIME="chatTime";
    String KEY_USERNAME="userName";

}
