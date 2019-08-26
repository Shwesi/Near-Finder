package com.example.toryes.studnow.adapter;

import android.content.Context;
import android.os.Build;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.toryes.studnow.R;
import com.example.toryes.studnow.bin.Message;

import java.util.ArrayList;

public class MessagesAdapter extends ArrayAdapter<Message> {
    Context context;
    String sender;
    public   MessagesAdapter(ArrayList<Message> messages, Context context,String sender){
            super(context, R.layout.message, R.id.message, messages);
            this.context=context;
        this.sender=sender;
        }
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            convertView = super.getView(position, convertView, parent);
            Message message = getItem(position);

            TextView nameView = (TextView)convertView.findViewById(R.id.message);
            nameView.setText(message.getText());
            TextView tvDate = (TextView)convertView.findViewById(R.id.tvDate);
            tvDate.setText(message.getDate().toString());
            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams)nameView.getLayoutParams();

            int sdk = Build.VERSION.SDK_INT;
            if (message.getSender().equals(sender)){
                if (sdk >= Build.VERSION_CODES.JELLY_BEAN) {
                    convertView.setBackground(context.getResources().getDrawable(R.drawable.chat_reciver_9));
                } else{
                    convertView.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.chat_reciver_9));
                }
                layoutParams.gravity = Gravity.RIGHT;
            }else{
                if (sdk >= Build.VERSION_CODES.JELLY_BEAN) {
                    convertView.setBackground(context.getResources().getDrawable(R.drawable.sender_9));
                } else{
                    convertView.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.sender_9));
                }
                layoutParams.gravity = Gravity.LEFT;
            }

            nameView.setLayoutParams(layoutParams);


            return convertView;
        }
    }