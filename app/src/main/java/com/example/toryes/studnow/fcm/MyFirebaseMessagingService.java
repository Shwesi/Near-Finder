package com.example.toryes.studnow.fcm;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;


import com.example.toryes.studnow.FirebaseChatMainApp;
import com.example.toryes.studnow.R;
import com.example.toryes.studnow.Utils.Constants;
import com.example.toryes.studnow.Utils.Prefrence;
import com.example.toryes.studnow.activity.HomeActivity;
import com.example.toryes.studnow.adapter.ChatRecyclerAdapter;
import com.example.toryes.studnow.events.PushNotificationEvent;
import com.example.toryes.studnow.fragment.AllUserFragment;
import com.example.toryes.studnow.helper.DatabaseHelper;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class MyFirebaseMessagingService extends FirebaseMessagingService implements Constants {

    private static final String TAG = "MyFirebaseMsgService";
    int count=0;
    public static final String URL_REGEX = "^((https?|ftp)://|(www|ftp)\\.)?[a-z0-9-]+(\\.[a-z0-9-]+)+([/?].*)?$";
    /**
     * Called when message is received.
     *
     * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
     */
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        if (Prefrence.getInt(this,"count")!=0){
            count=Prefrence.getInt(this,"count");
        }

        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.d(TAG, "From: " + remoteMessage.getFrom());

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());

            String title = remoteMessage.getData().get("username");
            String message = remoteMessage.getData().get("text");
            String username = remoteMessage.getData().get("title");
            String uid = remoteMessage.getData().get("uid");
            String fcmToken = remoteMessage.getData().get("fcm_token");
            if (!Prefrence.getString(getApplicationContext(),KEY_EMAIL).matches(username)) {
                DatabaseHelper databaseHelper = new DatabaseHelper(this);
                databaseHelper.insertMessage(username,message);

                if (!FirebaseChatMainApp.isChatActivityOpen()){
                    count++;
                    sendNotification(title,
                            message,
                            username,
                            uid,
                            fcmToken);

                }
            }
            else {
            // Don't show notification if chat_my activity is open.
            if (!FirebaseChatMainApp.isChatActivityOpen()) {
                count++;
                sendNotification(title,
                        message,
                        username,
                        uid,
                        fcmToken);
                Prefrence.saveInt(this,"count",count);
            } else {
                EventBus.getDefault().post(new PushNotificationEvent(title,
                        message,
                        username,
                        uid,
                        fcmToken));
            }
        }}
    }

    /**
     * Create and show a simple notification containing the received FCM message.
     */
    private void sendNotification(String title,
                                  String message,
                                  String receiver,
                                  String receiverUid,
                                  String firebaseToken) {
        Intent intent = new Intent(this, HomeActivity.class);
        intent.putExtra(ARG_RECEIVER, receiver);
        intent.putExtra(ARG_RECEIVER_UID, receiverUid);
        intent.putExtra(ARG_FIREBASE_TOKEN, firebaseToken);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_ONE_SHOT);
if (isContainUrl(message))
    message="Sent a File";
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this).setColor(this.getResources().getColor(R.color.colorPrimary))
                .setSmallIcon(R.drawable.notification_icon)
                .setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0, notificationBuilder.build());
    }
    public boolean isContainUrl(String message){
        boolean val =false;
        Pattern p = Pattern.compile(URL_REGEX);
        Matcher m = p.matcher(message);//replace with string to compare
        if(m.find()) {
            val=true;
        }
        return val;
    }
}