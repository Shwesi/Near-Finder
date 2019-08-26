package com.example.toryes.studnow.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.toryes.studnow.R;
import com.example.toryes.studnow.Utils.Constants;
import com.example.toryes.studnow.Utils.ProgressDialogBox;
import com.example.toryes.studnow.bin.LoginCredentials;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;
import cz.msebera.android.httpclient.message.BasicHeader;
import cz.msebera.android.httpclient.protocol.HTTP;

public class ResetPassword extends AppCompatActivity implements Constants {
    EditText edtemail;
    Button btnReset;
    private FirebaseAuth auth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        auth = FirebaseAuth.getInstance();
        edtemail= (EditText) findViewById(R.id.edtEmail);
        btnReset= (Button) findViewById(R.id.btnResetPass);
        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edtemail.length()>0) {
                    // check if email is valid
                    if (isValidEmail(edtemail.getText().toString())) {
//                        callResetAPi(edtemail.getText().toString());
                        ProgressDialogBox.showProgressDialog(ResetPassword.this);
                        //call password reset api of firebase
                        fireBaseRestPass();
                    }
                    else
                        Toast.makeText(ResetPassword.this,"Please enter Valid email id",Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(ResetPassword.this,"Please enter Valid email id",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void fireBaseRestPass(){
        auth.sendPasswordResetEmail(edtemail.getText().toString())
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            ProgressDialogBox.disMissDailog();
                            // showing dialog box
                            showDailog("We have sent you  instructions to reset your password on email ! ");


                        } else {
                            Toast.makeText(ResetPassword.this, "Failed to send reset email!", Toast.LENGTH_SHORT).show();
                        }

//                        progressBar.setVisibility(View.GONE);
                    }
                });
    }


    public final static boolean isValidEmail(CharSequence target) {
        return !TextUtils.isEmpty(target) && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }



    public void showDailog(String message){
        AlertDialog.Builder builder1 = new AlertDialog.Builder(ResetPassword.this);
        builder1.setMessage(message);
        builder1.setCancelable(true);

        builder1.setPositiveButton(
                "Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        startActivity(new Intent(ResetPassword.this,LoginActvity.class));
                        finish();
                        dialog.cancel();

                    }
                });

        AlertDialog alert11 = builder1.create();
        alert11.show();
    }
}
