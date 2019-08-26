package com.example.toryes.studnow.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.toryes.studnow.R;
import com.example.toryes.studnow.Utils.Constants;
import com.example.toryes.studnow.Utils.Prefrence;
import com.example.toryes.studnow.activity.HomeActivity;
import com.example.toryes.studnow.bin.Chat;
import com.example.toryes.studnow.bin.LoginCredentials;
import com.example.toryes.studnow.fragment.ChatFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements Constants {
    private static final int VIEW_TYPE_ME = 1;
    private static final int VIEW_TYPE_OTHER = 2;
    public static OnLongPressListener onLongPressListener;
    OnItemClick onItemClick;
    public  boolean multiselect=false;
    private List<Chat> mChats;
    public static List<Chat> deletedChat=new ArrayList<>();
    static long time;
    Context context;
    public static final String URL_REGEX = "^((https?|ftp)://|(www|ftp)\\.)?[a-z0-9-]+(\\.[a-z0-9-]+)+([/?].*)?$";

    public ChatRecyclerAdapter(List<Chat> chats, Context context) {
        mChats = chats;
        this.context=context;

    }

    public void add(Chat chat) {
        mChats.add(chat);
        notifyItemInserted(mChats.size() - 1);

    }
    public List<Chat> getChat(){
        return mChats;
    }
public void remove(Chat chat){
    mChats.remove(chat);
    notifyItemInserted(mChats.size() - 1);
    multiselect=false;
//    onLongPressListener=null;


}
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        RecyclerView.ViewHolder viewHolder = null;
        switch (viewType) {
            case VIEW_TYPE_ME:
                View viewChatMine = layoutInflater.inflate(R.layout.item_chat_other, parent, false);
                viewHolder = new MyChatViewHolder(viewChatMine);
                break;
            case VIEW_TYPE_OTHER:
                View viewChatOther = layoutInflater.inflate(R.layout.item_chat_mine, parent, false);
                viewHolder = new OtherChatViewHolder(viewChatOther);
                break;
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (TextUtils.equals(mChats.get(position).senderUid,
                FirebaseAuth.getInstance().getCurrentUser().getUid())) {
            configureOtherChatViewHolder((OtherChatViewHolder) holder, position);
        } else {
            configureMyChatViewHolder ((MyChatViewHolder) holder, position);
        }
    }

    private void configureMyChatViewHolder(MyChatViewHolder myChatViewHolder, int position) {
        Chat chat = mChats.get(position);

            myChatViewHolder.itemView.setActivated(false);
        myChatViewHolder.itemView.setBackgroundColor(context.getResources().getColor(android.R.color.transparent));
//        String alphabet = chat_my.sender.substring(0, 1);
        LoginCredentials loginCredentials=Prefrence.getLoginCeredential(context);
    if (isContainUrl(chat.message)) {
        myChatViewHolder.imageView.setVisibility(View.VISIBLE);
        myChatViewHolder.txtChatMessage.setVisibility(View.INVISIBLE);
        myChatViewHolder.linearLayout.setVisibility(View.GONE);
        Picasso.with(context)
                .load(chat.message)
                .into(myChatViewHolder.imageView);
        Glide.with(context).load(Prefrence.getString(context,KEY_OTHER_CHAT_IMAGE)).into(myChatViewHolder.profile);
//        Glide.with(context).load(chat_my.message).into(myChatViewHolder.imageView);
//        Glide.with(context).load(loginCredentials.getImage()).into(myChatViewHolder.profile);
    }
        else {
        myChatViewHolder.imageView.setVisibility(View.GONE);
        myChatViewHolder.txtChatMessage.setVisibility(View.VISIBLE);
        myChatViewHolder.txtChatMessage.setText(chat.message);
        myChatViewHolder.timestamp.setText(convertTimestamp(chat.timestamp));
        myChatViewHolder.linearLayout.setVisibility(View.VISIBLE);
        time=chat.timestamp;
//        myChatViewHolder.txtUserAlphabet.setText(alphabet);
//        Glide.with(context).load(loginCredentials.getImage()).into(myChatViewHolder.profile);
        Glide.with(context).load(Prefrence.getString(context,KEY_OTHER_CHAT_IMAGE)).into(myChatViewHolder.profile);
    }
        Glide.with(context).load(Prefrence.getString(context,KEY_OTHER_CHAT_IMAGE)).into(myChatViewHolder.profile);
//        Picasso.with(context)
//                .load(loginCredentials.getImage())
//                .into(myChatViewHolder.profile);
//        Glide.with(context).load(loginCredentials.getImage()).into(myChatViewHolder.profile);

    }

    private void configureOtherChatViewHolder(OtherChatViewHolder otherChatViewHolder, int position) {
        Chat chat = mChats.get(position);
        LoginCredentials loginCredentials=Prefrence.getLoginCeredential(context);
            otherChatViewHolder.itemView.setActivated(false);
        otherChatViewHolder.itemView.setBackgroundColor(context.getResources().getColor(android.R.color.transparent));
//        String alphabet = chat_my.sender.substring(0, 1);
        if (isContainUrl(chat.message)) {

            Picasso.with(context)
                    .load(chat.message)
                    .into(otherChatViewHolder.imageView);
            Glide.with(context).load(chat.message).into(otherChatViewHolder.imageView);
            otherChatViewHolder.imageView.setVisibility(View.VISIBLE);
            otherChatViewHolder.txtChatMessage.setVisibility(View.INVISIBLE);
            otherChatViewHolder.linearLayout.setVisibility(View.GONE);
            Glide.with(context).load(loginCredentials.getImage()).into(otherChatViewHolder.profile);
//            Glide.with(context).load(Prefrence.getString(context,KEY_OTHER_CHAT_IMAGE)).into(otherChatViewHolder.profile);
        }
        else {
            otherChatViewHolder.txtChatMessage.setText(chat.message);
            otherChatViewHolder.txtChatMessage.setVisibility(View.VISIBLE);
            otherChatViewHolder.linearLayout.setVisibility(View.VISIBLE);
//            otherChatViewHolder.txtUserAlphabet.setText(alphabet);
            otherChatViewHolder.imageView.setVisibility(View.GONE);
            otherChatViewHolder.timestamp.setText(convertTimestamp(chat.timestamp));

            time=chat.timestamp;
            Glide.with(context).load(loginCredentials.getImage()).into(otherChatViewHolder.profile);
//            Glide.with(context).load(Prefrence.getString(context,KEY_OTHER_CHAT_IMAGE)).into(otherChatViewHolder.profile);
        }
        Glide.with(context).load(loginCredentials.getImage()).into(otherChatViewHolder.profile);
//        Glide.with(context).load(Prefrence.getString(context,KEY_OTHER_CHAT_IMAGE)).into(otherChatViewHolder.profile);
//        Picasso.with(context)
//                .load(Prefrence.getString(context,KEY_OTHER_CHAT_IMAGE))
//                .into(otherChatViewHolder.profile);
    }

    @Override
    public int getItemCount() {
        if (mChats != null) {
            return mChats.size();
        }
        return 0;
    }

    @Override
    public int getItemViewType(int position) {
        if (TextUtils.equals(mChats.get(position).senderUid,
                FirebaseAuth.getInstance().getCurrentUser().getUid())) {
            return VIEW_TYPE_OTHER;
        } else {
            return VIEW_TYPE_ME;
        }
    }

    private  class MyChatViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener,View.OnClickListener {
        private TextView txtChatMessage,timestamp;
        private ImageView imageView;
        private CircleImageView profile;
        private LinearLayout linearLayout;
        public MyChatViewHolder(View itemView) {
            super(itemView);
            txtChatMessage = (TextView) itemView.findViewById(R.id.text_view_chat_message);
            timestamp=(TextView) itemView.findViewById(R.id.timestamp);
//            txtUserAlphabet = (TextView) itemView.findViewById(R.id.text_view_user_alphabet);
            imageView= (ImageView) itemView.findViewById(R.id.imageView);
            profile= (CircleImageView) itemView.findViewById(R.id.profilePic);
            linearLayout= (LinearLayout) itemView.findViewById(R.id.linearlayout);
            itemView.setOnLongClickListener(this);
            itemView.setOnClickListener(this);

        }



        @Override
        public boolean onLongClick(View v) {
            if (onLongPressListener!=null) {
                itemView.setBackgroundColor(context.getResources().getColor(R.color.list_bg));
                itemView.setActivated(true);
                multiselect=true;
                Chat chat=mChats.get(getAdapterPosition());
                deletedChat.add(chat);
                onLongPressListener.onLongItemclik(v, getAdapterPosition(), deletedChat);
                itemView.setLongClickable(false);
            }
            return true;
        }

        @Override
        public void onClick(View v) {
            Chat chat=mChats.get(getAdapterPosition());
            if (multiselect &&!itemView.isActivated()) {
                itemView.setBackgroundColor(context.getResources().getColor(R.color.list_bg));
                itemView.setActivated(true);

                deletedChat.add(chat);
            }
            else {
                itemView.setBackgroundColor(context.getResources().getColor(android.R.color.transparent));
                itemView.setActivated(false);

                deletedChat.remove(chat);
                if (deletedChat.size()==0) {
                    multiselect = false;
                    itemView.setLongClickable(true);
                    itemView.setActivated(false);
                }
            }
            onItemClick.onItemClick(deletedChat,itemView);

        }
    }

    private  class OtherChatViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener,View.OnClickListener {
        private TextView txtChatMessage,timestamp;
        private ImageView imageView;
        private CircleImageView profile;
        private LinearLayout linearLayout;
        public OtherChatViewHolder(View itemView) {
            super(itemView);
            txtChatMessage = (TextView) itemView.findViewById(R.id.text_view_chat_message);
            timestamp=(TextView) itemView.findViewById(R.id.timestamp);
            linearLayout= (LinearLayout) itemView.findViewById(R.id.linearlayout);
//            txtUserAlphabet = (TextView) itemView.findViewById(R.id.text_view_user_alphabet);
            imageView= (ImageView) itemView.findViewById(R.id.imageView);
            profile= (CircleImageView) itemView.findViewById(R.id.profilePic);
            itemView.setOnLongClickListener(this);
            itemView.setOnClickListener(this);
        }



        @Override
        public boolean onLongClick(View v) {
            if (onLongPressListener!=null) {
                itemView.setBackgroundColor(context.getResources().getColor(R.color.list_bg));
                itemView.setActivated(true);
                multiselect=true;
                Chat chat=mChats.get(getAdapterPosition());
                deletedChat.add(chat);
                onLongPressListener.onLongItemclik(v, getAdapterPosition(), deletedChat);
                itemView.setLongClickable(false);
            }
            return true;
        }

        @Override
        public void onClick(View v) {
            Chat chat=mChats.get(getAdapterPosition());
            if (multiselect &&!itemView.isActivated()) {
                itemView.setBackgroundColor(context.getResources().getColor(R.color.list_bg));
                itemView.setActivated(true);

                deletedChat.add(chat);
            }
            else {
                itemView.setBackgroundColor(context.getResources().getColor(android.R.color.transparent));
                itemView.setActivated(false);

                deletedChat.remove(chat);
                if (deletedChat.size()==0) {
                    multiselect = false;
                    itemView.setLongClickable(true);
                    itemView.setActivated(false);
                }
            }
            onItemClick.onItemClick(deletedChat,itemView);
        }
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

public void setLongClickLisetener(OnLongPressListener onLongPressListener){
    this.onLongPressListener=onLongPressListener;
}
public void setClickListener(OnItemClick onItemClick){
    this.onItemClick=onItemClick;
}
    public String convertTimestamp(long timestamp){
        Date d = new Date(timestamp);
        Log.e("date",d.toString());
        SimpleDateFormat formatter = new SimpleDateFormat("hh:mm a");

        // Create a calendar object that will convert the date and time value in milliseconds to date.
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timestamp);
        return formatter.format(calendar.getTime());

    }
    public interface OnLongPressListener{
        void onLongItemclik(View view,int pos,List<Chat> timestamp);
    }
    public interface OnItemClick{
        void onItemClick(List<Chat> timestamp,View itemview);
    }
}
