package com.example.toryes.studnow.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.toryes.studnow.R;
import com.example.toryes.studnow.Utils.Constants;
import com.example.toryes.studnow.Utils.Prefrence;
import com.example.toryes.studnow.Utils.ProgressDialogVerfication;
import com.example.toryes.studnow.bin.LoginCredentials;
import com.example.toryes.studnow.service.LocationTracker;
import com.facebook.login.LoginManager;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.FirebaseDatabase;

import java.util.concurrent.TimeUnit;

/**
 * Created by TORYES on 11/21/2017.
 */

public class UpdateNumber extends Fragment implements Constants {
    Button btnUpdate;
    EditText edtPhone,edtCountry;
    PhoneAuthCredential mAuthoCeredential;
    PhoneAuthProvider.ForceResendingToken mResendToken;
    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    LoginCredentials loginCredentials;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.update_number,null,false);
        loginCredentials=Prefrence.getLoginCeredential(getActivity());
        edtCountry= (EditText) view.findViewById(R.id.edtCountrycode);
        edtPhone= (EditText) view.findViewById(R.id.edtPhone);
        btnUpdate= (Button) view.findViewById(R.id.btnJoinUs);
        phoneValidate();
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edtPhone.length()>0&&edtCountry.length()>0) {

                    //do something with edt.getText().toString();
                    PhoneAuthProvider.getInstance().verifyPhoneNumber(
                            "+" +edtCountry.getText().toString()+ edtPhone.getText().toString(),        // Phone number to verify
                            60,                 // Timeout duration
                            TimeUnit.SECONDS,   // Unit of timeout
                            getActivity(),               // Activity (for callback binding)
                            mCallbacks);
                    ProgressDialogVerfication.showProgressDialog(getActivity());

                }
                else
                {
                    Toast.makeText(getActivity()," Country Code and phone Number can't be blank",Toast.LENGTH_SHORT).show();
                }
            }

        });
        return view;
    }


    public void phoneValidate(){
        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {

                mAuthoCeredential=phoneAuthCredential;
                Toast.makeText(getActivity(),"Verification done SucessFully",Toast.LENGTH_LONG).show();
                updateNumber();
                ProgressDialogVerfication.disMissDailog();

//                updateUI(user);

            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                Toast.makeText(getActivity(),"verification fail"+ e.getMessage().toString(),Toast.LENGTH_LONG).show();
                LoginManager.getInstance().logOut();
                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    // Invalid request
                    // [START_EXCLUDE]
                    Toast.makeText(getActivity(),"invalid mob no",Toast.LENGTH_LONG).show();
                    // [END_EXCLUDE]
                } else if (e instanceof FirebaseTooManyRequestsException) {
                    // The SMS quota for the project has been exceeded
                    // [START_EXCLUDE]
                    Toast.makeText(getActivity(),"quota over" ,Toast.LENGTH_LONG).show();
                    // [END_EXCLUDE]
                }
            }

            @Override
            public void onCodeAutoRetrievalTimeOut(String s) {
                super.onCodeAutoRetrievalTimeOut(s);
                Toast.makeText(getActivity(),"Phone Number not detected in your device",Toast.LENGTH_LONG).show();

                ProgressDialogVerfication.disMissDailog();
            }

            @Override
            public void onCodeSent(String verificationId,
                                   PhoneAuthProvider.ForceResendingToken token) {


            }
        };

    }

    public void updateNumber(){


            FirebaseDatabase.getInstance()
                    .getReference()
                    .child(ARG_USERS)
                    .child(loginCredentials.getUseridentificationNo()).child("phoneNumber").setValue(edtPhone.getText().toString());



    }
}



