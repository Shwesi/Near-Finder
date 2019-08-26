package com.example.toryes.studnow.Utils;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.toryes.studnow.R;
import com.jpardogo.android.googleprogressbar.library.FoldingCirclesDrawable;
import com.jpardogo.android.googleprogressbar.library.GoogleProgressBar;

/**
 * Created by TORYES on 11/20/2017.
 */

public class ProgressDialogVerfication {
    public static ProgressBar dialog;
    static Dialog dialog1;
    public static void showProgressDialog(final Context context){
//        LayoutInflater layoutInflater= (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//        View view=layoutInflater.inflate(R.layout.progress_dialog_layout,null);
//        dialog = (GoogleProgressBar) view.findViewById(R.id.google_progress);
        dialog1 = new  Dialog(context);
//        dialog1.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog1.setTitle("Verifying Your phone number...");
        dialog1.setContentView(R.layout.verify_progress_bar);
        dialog1.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog= (ProgressBar) dialog1.findViewById(R.id.progressBar);
        TextView textView= (TextView) dialog1.findViewById(R.id.textView);
        textView.setText("Verifying Your phone number...");
        dialog1.setCancelable(false);

        dialog1.show();
//        dialog=new GoogleProgressBar(context);
//        dialog.setIndeterminate(false);
//        dialog.setCancelable(false);
//        dialog.show();




    }
    public static void disMissDailog(){
//    hideClick(dialog);
        dialog1.dismiss();
        dialog.setVisibility(View.GONE);
    }

//    public static void hideClick(View view) {
//        dialog.hide();
//        // or avi.smoothToHide();
//    }
//
//    public static void showClick(View view) {
//        dialog.show();
//        // or avi.smoothToShow();
//    }
}

