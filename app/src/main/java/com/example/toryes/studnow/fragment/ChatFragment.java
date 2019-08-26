package com.example.toryes.studnow.fragment;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import com.bumptech.glide.Glide;
import com.example.toryes.studnow.FirebaseChatMainApp;
import com.example.toryes.studnow.R;
import com.example.toryes.studnow.Utils.Constants;
import com.example.toryes.studnow.Utils.GetFilePathFromDevice;
import com.example.toryes.studnow.Utils.MessageDataSource;
import com.example.toryes.studnow.Utils.Prefrence;
import com.example.toryes.studnow.Utils.SharedPrefUtil;
import com.example.toryes.studnow.activity.SignupActvity;
import com.example.toryes.studnow.adapter.ChatRecyclerAdapter;
import com.example.toryes.studnow.adapter.MessagesAdapter;
import com.example.toryes.studnow.bin.Chat;
import com.example.toryes.studnow.bin.LoginCredentials;
import com.example.toryes.studnow.bin.Message;
import com.example.toryes.studnow.bin.User;
import com.example.toryes.studnow.chat.core.chat.ChatContract;
import com.example.toryes.studnow.chat.core.chat.ChatPresenter;
import com.example.toryes.studnow.events.PushNotificationEvent;
import com.example.toryes.studnow.helper.ChatDatabaseHelper;
import com.example.toryes.studnow.helper.DeletedChatHelper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import hani.momanii.supernova_emoji_library.Actions.EmojIconActions;
import hani.momanii.supernova_emoji_library.Helper.EmojiconEditText;

import static android.content.ContentValues.TAG;

/**
 * Created by TORYES on 7/12/2017.
 */

public class ChatFragment extends AppCompatActivity implements View.OnClickListener,ChatContract.View,ChatRecyclerAdapter.OnLongPressListener,ChatRecyclerAdapter.OnItemClick,
        Constants{

    public static final String USER_EXTRA = "USER";
    Menu menu;
    ArrayList<Chat> deletedArraylist;
    View itemView;
    public static final String TAG = "ChatActivity";
    ChatRecyclerAdapter mChatRecyclerAdapter;
    private ArrayList<Message> mMessages;
    private MessagesAdapter mAdapter;
//    private String mRecipient,mSender;
    public RecyclerView mRecyclerViewChat;
    private Date mLastMessageDate = new Date();
    private String mConvoId;
    private MessageDataSource.MessagesListener mListener;
    EmojiconEditText newMessageView;
    ChatPresenter mChatPresenter;
    ImageView imageView;
    int PICK_IMAGE_REQUEST=101;
    String img_Decodable_Str;
    StorageReference storageReference;
    ProgressBar progressBar;
    ImageView imgPhoto,emojiButton;
    EmojIconActions emojIcon;
    ChatDatabaseHelper chatDatabaseHelper;

    String receiver ;
    String receiverUid ;
    String sender ;
    String senderUid;
    String receiverFirebaseToken ;
    LoginCredentials loginCredentials;
    boolean typingStarted=false;
    Toolbar toolbar;
    TextView lstseen;
    ArrayList<Chat> clearChat;
//    Emoji

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_fragment);
        View view=findViewById(R.id.root_view);
        toolbar= (Toolbar) findViewById(R.id.toolbar);
         lstseen= (TextView) toolbar.findViewById(R.id.lastseen);
        TextView username= (TextView) toolbar.findViewById(R.id.username);
        username.setText(getIntent().getStringExtra(KEY_FIRST_NAME));
        setSupportActionBar(toolbar);
        loginCredentials = Prefrence.getLoginCeredential(ChatFragment.this);
        chatDatabaseHelper=new ChatDatabaseHelper(ChatFragment.this);
        init();
        FirebaseStorage storage = FirebaseStorage.getInstance();
        storageReference = storage.getReferenceFromUrl("gs://tracking-6b462.appspot.com/");
        imageView= (ImageView) findViewById(R.id.emoji_btn);
        imgPhoto= (ImageView)findViewById(R.id.imageView);
        progressBar= (ProgressBar) findViewById(R.id.progressBar);
        emojiButton=(ImageView)findViewById(R.id.emoji_btn1);
        imageView.setOnClickListener(this);
        mRecyclerViewChat = (RecyclerView) findViewById(R.id.recycler_view_chat);
        newMessageView = (EmojiconEditText)findViewById(R.id.new_message);
        mMessages = new ArrayList<>();
        if ( getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(ContextCompat.getColor(ChatFragment.this, R.color.colorPrimaryDark));
        FloatingActionButton sendMessage = (FloatingActionButton) findViewById(R.id.send_message);
        sendMessage.setOnClickListener(this);
        emojIcon = new EmojIconActions(ChatFragment.this,view , newMessageView, emojiButton);
        emojIcon.ShowEmojIcon();
        emojIcon.setKeyboardListener(new EmojIconActions.KeyboardListener() {
            @Override
            public void onKeyboardOpen() {
                Log.e("Keyboard", "open");
            }

            @Override
            public void onKeyboardClose() {
                Log.e("Keyboard", "close");
            }
        });
        newMessageView.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            public void afterTextChanged(Editable s) {
                if (!TextUtils.isEmpty(s.toString()) && s.toString().trim().length() == 1) {
                    //Log.i(TAG, “typing started event…”);
                    typingStarted = true;
//                    updateTypingStatus();
                    //send typing started status
                } else if (s.toString().trim().length() == 0 && typingStarted) {
                    //Log.i(TAG, “typing stopped event…”);
                    typingStarted = false;
//                    updateTypingStatus();
                    //send typing stopped status
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.send_message:
                if (newMessageView.length()>0) {
                    sendMessage(newMessageView.getText().toString());
                    newMessageView.setText("");
                }
                break;
            case R.id.emoji_btn:
                browseImage();
                break;
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {

            img_Decodable_Str= GetFilePathFromDevice.getPath(ChatFragment.this, data.getData());
//            Glide.with(SignupActvity.this).load(img_Decodable_Str).into(profilePic);
            String filename= img_Decodable_Str.substring(img_Decodable_Str.lastIndexOf("/")+1);
            uploadFile(Uri.fromFile(new File(img_Decodable_Str)),filename);
            imgPhoto.setVisibility(View.VISIBLE);
            Picasso.with(ChatFragment.this)
                    .load(img_Decodable_Str)
                    .into(imgPhoto);
//            Glide.with(ChatFragment.this).load(img_Decodable_Str).into(imgPhoto);
            progressBar.setVisibility(View.VISIBLE);

        }
    }

    public void browseImage(){
        Intent intent = new Intent();
// Show only images, no videos or anything else
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
// Always show the chooser (if there are multiple options available)
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    private void init() {
//        setTitle(getIntent().getStringExtra(KEY_FIRST_NAME));
        mChatPresenter = new ChatPresenter(this);
        mChatPresenter.getMessage(FirebaseAuth.getInstance().getCurrentUser().getUid(),
                getIntent().getStringExtra(ARG_RECEIVER_UID),ChatFragment.this);
        getSeen(getIntent().getStringExtra(ARG_RECEIVER_UID));
        scheduleOnline();

    }

    private void sendMessage(String message) {
        LoginCredentials loginCredentials = Prefrence.getLoginCeredential(ChatFragment.this);

//        String message = newMessageView.getText().toString();
         receiver =   getIntent().getStringExtra(ARG_RECEIVER);
         receiverUid =   getIntent().getStringExtra(ARG_RECEIVER_UID);
         sender = FirebaseAuth.getInstance().getCurrentUser().getEmail();
         senderUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
         receiverFirebaseToken =   getIntent().getStringExtra(Constants.ARG_FIREBASE_TOKEN);
        Chat chat = new Chat(sender,
                receiver,
                senderUid,
                receiverUid,
                message,
                System.currentTimeMillis(),
                loginCredentials.getFirst_name(),"","false");
        mChatPresenter.sendMessage(getApplicationContext(),
                chat,
                receiverFirebaseToken);
    }
//    @Override
//    public void onMessageAdded(Message message) {
//        mMessages.add(message);
//        mAdapter.notifyDataSetChanged();
//    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mRecyclerViewChat.setAdapter(null);

//        MessageDataSource.stop(mListener);
    }
    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }
    @Override
    public void onResume() {
        super.onResume();
        FirebaseChatMainApp.setChatActivityOpen(true);
        getSeen(getIntent().getStringExtra(ARG_RECEIVER_UID));
    }

    @Override
    public void onPause() {
        super.onPause();
        FirebaseChatMainApp.setChatActivityOpen(false);
        Prefrence.saveString(ChatFragment.this,KEY_EMAIL,"admin");
        getSeen(getIntent().getStringExtra(ARG_RECEIVER_UID));
    }
    @Override
    public void onSendMessageSuccess() {



//        Toast.makeText(getActivity(), "Message sent", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onSendMessageFailure(String message) {
        Toast.makeText(ChatFragment.this, "Message not sent", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onGetMessagesSuccess(Chat chat) {

        if (mChatRecyclerAdapter == null||clearChat.size()==0) {
            mChatRecyclerAdapter = new ChatRecyclerAdapter(new ArrayList<Chat>(),getApplicationContext());
            clearChat=new ArrayList<Chat>();
            mRecyclerViewChat.setAdapter(mChatRecyclerAdapter);
        }
            clearChat.add(chat);
            mChatRecyclerAdapter.add(chat);
            mChatRecyclerAdapter.setLongClickLisetener(this);
            mChatRecyclerAdapter.setClickListener(this);
            mRecyclerViewChat.smoothScrollToPosition(mChatRecyclerAdapter.getItemCount() - 1);

    }

    @Override
    public void onGetMessagesFailure(String message) {

    }
    @Subscribe
    public void onPushNotificationEvent(PushNotificationEvent pushNotificationEvent) {
        if (mChatRecyclerAdapter == null || mChatRecyclerAdapter.getItemCount() == 0) {
            mChatPresenter.getMessage(FirebaseAuth.getInstance().getCurrentUser().getUid(),
                    pushNotificationEvent.getUid(),ChatFragment.this);
        }
    }

    private void uploadFile(Uri filePath,String filename) {
        //if there is a file to upload

        if (filePath != null) {


            StorageReference riversRef = storageReference.child("images/"+filename);
            riversRef.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                           sendMessage(taskSnapshot.getDownloadUrl().toString());
                            //and displaying a success toast
                            imgPhoto.setVisibility(View.GONE);
                            //if the upload is successfull
                            //hiding the progress dialog
                            progressBar.setVisibility(View.GONE);
//                            Toast.makeText(getApplicationContext(), "File Uploaded ", Toast.LENGTH_LONG).show();
                            Log.e("message","fileuploaded");
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            //if the upload is not successfull
                            //hiding the progress dialog
                            progressBar.setVisibility(View.GONE);
                            //and displaying error message
                            Toast.makeText(getApplicationContext(), exception.getMessage(), Toast.LENGTH_LONG).show();
                            Log.e("message",exception.getMessage());
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            //calculating progress percentage
                            double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();

                            //displaying percentage in progress dialog
//                            progressDialog.setMessage("Uploaded " + ((int) progress) + "%...");
                        }
                    });
        }
        //if there is not any file
        else {
            //you can display an error toast
        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == android.R.id.home)
            super.onBackPressed();
            if (id==R.id.action_search){
                if (ChatRecyclerAdapter.deletedChat.size()>0)
            for (int i=0;i<ChatRecyclerAdapter.deletedChat.size();i++) {
                deleteChat(ChatRecyclerAdapter.deletedChat.get(i), i);
            mChatRecyclerAdapter.remove((ChatRecyclerAdapter.deletedChat.get(i)));
            mChatRecyclerAdapter.notifyDataSetChanged();

                }
                ChatRecyclerAdapter.deletedChat.clear();
                item.setVisible(false);

            }
            else if (id==R.id.action_clear){
                if (clearChat==null)
                    clearChat=new ArrayList<>();
                if (clearChat.size()>0) {
                    for (int i = 0; i < clearChat.size(); i++) {
                        Chat chat = clearChat.get(i);
                        deleteChat(chat, i);
                        mChatRecyclerAdapter.remove((clearChat.get(i)));
                        mChatRecyclerAdapter.notifyDataSetChanged();


                    }
                    mRecyclerViewChat.setAdapter(null);
                    clearChat.clear();
                    mChatRecyclerAdapter = new ChatRecyclerAdapter(clearChat, ChatFragment.this);
                    mRecyclerViewChat.setAdapter(mChatRecyclerAdapter);
                }
            }
//            mChatRecyclerAdapter.notifyDataSetChanged();
            return true;

    }

    @Override
    public void onLongItemclik(View view, int pos,List<Chat> pushkey) {
        MenuItem item = menu.findItem(R.id.action_search);
        item.setVisible(true);

    }
    private void deleteChat(final Chat chat,int pos) {
        final String del;
        if (chat.deleted.matches("false"))
            del=loginCredentials.getEmail();
        else
            del="both";

        final String room_type_1 =FirebaseAuth.getInstance().getCurrentUser().getUid() + "_" + getIntent().getStringExtra(ARG_RECEIVER_UID);
        final String room_type_2 = getIntent().getStringExtra(ARG_RECEIVER_UID) + "_" + FirebaseAuth.getInstance().getCurrentUser().getUid();
        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.child(Constants.ARG_CHAT_ROOMS).getRef().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(room_type_1)) {
                    Log.e(TAG, "getMessageFromFirebaseUser: " + room_type_1 + " exists");

                    FirebaseDatabase.getInstance()
                            .getReference()
                            .child(ARG_CHAT_ROOMS)
                            .child(room_type_1)
                            .child(chat.pushId)
                            .child("deleted")
                            .setValue(del);

                } else if (dataSnapshot.hasChild(room_type_2)) {
                    FirebaseDatabase.getInstance()
                            .getReference()
                            .child(ARG_CHAT_ROOMS)
                            .child(room_type_2)
                            .child(chat.pushId)
                            .child("deleted")
                            .setValue(del);

                } else {
                    Log.e(TAG, "getMessageFromFirebaseUser: no such room available");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
//                mOnGetMessagesListener.onGetMessagesFailure("Unable to get message: " + databaseError.getMessage());
            }
        });
        mChatRecyclerAdapter.remove(chat);
        mChatRecyclerAdapter.notifyDataSetChanged();


    }

    public void updateTypingStatus(){
        FirebaseAuth.getInstance()
                .signInWithEmailAndPassword(loginCredentials.getEmail(), loginCredentials.getPassword())
                .addOnCompleteListener(ChatFragment.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "performFirebaseLogin:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (task.isSuccessful()) {
//                            mOnLoginListener.onSuccess(task.getResult().toString());
                            updateTyping(task.getResult().getUser().getUid(),
                                    new SharedPrefUtil(getApplicationContext()).getString(Constants.ARG_FIREBASE_TOKEN, null),typingStarted);
                        }
                    }
                });
    }

    private void updateTyping(String uid, String token,boolean typingStarted) {
        FirebaseDatabase.getInstance()
                .getReference()
                .child(ARG_USERS)
                .child(uid)
                .child("typing")
                .setValue(typingStarted);
        getStatus();

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {


        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_setting, menu);
        this.menu=menu;
        MenuItem item = menu.findItem(R.id.action_search);
        item.setVisible(false);
        return true;
    }


    public void getStatus(){
        final String room_type_1 = senderUid + "_" + receiverUid;
        final String room_type_2 = receiverUid + "_" + senderUid;

        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.child(Constants.ARG_USERS).getRef().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
//                if (dataSnapshot.hasChild(room_type_1)) {
                    Log.e(TAG, "getMessageFromFirebaseUser: " + room_type_1 + " exists");
                    FirebaseDatabase.getInstance()
                            .getReference()
                            .child(Constants.ARG_USERS)
                            .child(Prefrence.getString(ChatFragment.this,"uid")).
                            addChildEventListener(new ChildEventListener() {

                        @Override
                        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                            dataSnapshot.getValue();
                            Log.e("value",dataSnapshot.getValue()+"");

                        }

                        @Override
                        public void onChildChanged(DataSnapshot dataSnapshot, String s) {

//                            if (!loginCredentials.getEmail().matches(chat_my.))
                        }

                        @Override
                        public void onChildRemoved(DataSnapshot dataSnapshot) {

                        }

                        @Override
                        public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
//                            mOnGetMessagesListener.onGetMessagesFailure("Unable to get message: " + databaseError.getMessage());
                        }
                    });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
//                mOnGetMessagesListener.onGetMessagesFailure("Unable to get message: " + databaseError.getMessage());
            }
        });
    }


    public void getSeen(String receiverUid){
        FirebaseDatabase.getInstance().getReference().child(ARG_USERS).child(receiverUid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterator<DataSnapshot> dataSnapshots = dataSnapshot.getChildren().iterator();
                    User user = dataSnapshot.getValue(User.class);
                if (!user.last_seen.matches("Online"))
                    convertDate(user.last_seen);
                else
                    lstseen.setText(user.last_seen);
                Prefrence.saveString(ChatFragment.this,"last_seen",user.last_seen);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }



    public void scheduleOnline() {
        final Handler handler=new Handler();
        final int FIVE_SECONDS = 1000;
        handler.postDelayed(new Runnable() {
            public void run() {
                getSeen(getIntent().getStringExtra(ARG_RECEIVER_UID));    // this method will contain your almost-finished HTTP calls
                handler.postDelayed(this, FIVE_SECONDS);
            }
        }, FIVE_SECONDS);
    }

    @Override
    public void onBackPressed() {

        finish();
        super.onBackPressed();
    }


    @Override
    public void onItemClick(List<Chat> timestamp,View itemview) {
        MenuItem item1 = menu.findItem(R.id.action_clear);
        item1.setVisible(true);
        if (timestamp.size()==0&&!itemview.isActivated()){
            MenuItem item = menu.findItem(R.id.action_search);
            item.setVisible(false);
        }
    }

    public void convertDate(String date){
        Calendar c1 = Calendar.getInstance(); // today
        c1.add(Calendar.DAY_OF_YEAR, -1); // yesterday
        Calendar c2 = Calendar.getInstance();
        // your date
        c2.setTimeInMillis(Long.parseLong(date));
//        SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy");
//        try {
//            Date mDate = sdf.parse(date);
//            c2.setTime(mDate);
//            long timeInMilliseconds = mDate.getTime();
//            System.out.println("Date in milli :: " + timeInMilliseconds);
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
        Log.e("dateMy",c1.get(Calendar.YEAR)+"  "+c2.get(Calendar.YEAR)+"   "+c1.get(Calendar.DAY_OF_YEAR)+"  "+c2.get(Calendar.DAY_OF_YEAR));
        if (c1.get(Calendar.YEAR) == c2.get(Calendar.YEAR)
                && c1.get(Calendar.DAY_OF_YEAR) == c2.get(Calendar.DAY_OF_YEAR)) {
            lstseen.setText("Last Seen Yesterday");
        }
        else if (c1.get(Calendar.YEAR) <= c2.get(Calendar.YEAR)
                && c1.get(Calendar.DAY_OF_YEAR) > c2.get(Calendar.DAY_OF_YEAR)){
            lstseen.setText("Last Seen "+convertTimestampToprevDate(Long.parseLong(date)));
        }
        else {
            lstseen.setText("Last Seen "+convertTimestamp(Long.parseLong(date)));
        }
    }

    public String convertTimestamp(long time){
        SimpleDateFormat formatter = new SimpleDateFormat("hh:mm a", Locale.US);
        // Create a calendar object that will convert the date and time value in milliseconds to date.
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time);
//        calendar.add(Calendar.DAY_OF_YEAR, -1);
        return formatter.format(calendar.getTime());

    }
    public String convertTimestampToprevDate(long time){
        SimpleDateFormat formatter = new SimpleDateFormat("dd MMM yyyy hh:mm a",Locale.US);
        Date date = new Date(time);
        // Create a calendar object that will convert the date and time value in milliseconds to date.
//        Calendar calendar = Calendar.getInstance();
//        calendar.setTimeInMillis(time);
        return formatter.format(date);

    }
}
