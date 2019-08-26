package com.example.toryes.studnow.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.toryes.studnow.R;
import com.example.toryes.studnow.Utils.Constants;
import com.example.toryes.studnow.Utils.Prefrence;
import com.example.toryes.studnow.Utils.ProgressDialogBox;
import com.example.toryes.studnow.Utils.ProgressDialogVerfication;
import com.example.toryes.studnow.Utils.SharedPrefUtil;
import com.example.toryes.studnow.bin.LoginCredentials;

import com.example.toryes.studnow.bin.User;
import com.example.toryes.studnow.chat.core.login.LoginContract;
import com.example.toryes.studnow.chat.core.login.LoginPresenter;
import com.example.toryes.studnow.service.LocationTracker;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;
import cz.msebera.android.httpclient.message.BasicHeader;
import cz.msebera.android.httpclient.protocol.HTTP;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.content.ContentValues.TAG;

public class LoginActvity extends AppCompatActivity implements Constants,View.OnClickListener, LoginContract.View,Prefrence.SavedSucees {
    EditText edtMail, edtPass;
    EditText edt;
    Button btnLogin;
    String  longitude, latitude;
    TextView tvSignUp,tvForgotPass;
    String username ;
    String password ;
    Button btnGmail,btnFb;
    private LoginPresenter mLoginPresenter;
    private GoogleSignInClient mGoogleSignInClient;
    private static final String TAG = "GoogleActivity";
    private static final int RC_SIGN_IN = 9001;
    private CallbackManager mCallbackManager;
    LoginButton loginButton;
    String phoneNumber;
    private FirebaseAuth mAuth;
    AlertDialog b;
    String mVerificationId;
    PhoneAuthCredential mAuthoCeredential;
    String acess_token;
    FirebaseUser user;
    PhoneAuthProvider.ForceResendingToken mResendToken;
    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_login_actvity);

        new Prefrence(this);
        PackageInfo info;
        try {
            info = getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md;
                md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                String something = new String(Base64.encode(md.digest(), 0));
                //String something = new String(Base64.encodeBytes(md.digest()));
                Log.e("hash key", something);
            }
        } catch (PackageManager.NameNotFoundException e1) {
            Log.e("name not found", e1.toString());
        } catch (NoSuchAlgorithmException e) {
            Log.e("no such an algorithm", e.toString());
        } catch (Exception e) {
            Log.e("exception", e.toString());
        }
        init();
        if (Prefrence.getBoolen(LoginActvity.this,LOGIN_SUCCES)){
            startActivity(new Intent(LoginActvity.this,HomeActivity.class));
            finish();
        }
        edtMail = (EditText) findViewById(R.id.edtEmail);
        edtPass = (EditText) findViewById(R.id.edtPass);
        btnLogin = (Button) findViewById(R.id.btnLogin);
        tvSignUp = (TextView) findViewById(R.id.tvSignUp);
        btnFb= (Button) findViewById(R.id.btnFb);
        btnGmail= (Button) findViewById(R.id.btnGmail);
        tvForgotPass = (TextView) findViewById(R.id.tvForgotPass);
        btnLogin.setOnClickListener(this);
        tvSignUp.setOnClickListener(this);
        btnGmail.setOnClickListener(this);
        btnFb.setOnClickListener(this);
        tvForgotPass.setOnClickListener(this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        //locationtracker class fetch the current longitude and lattitude
        LocationTracker locationTracker=new LocationTracker(LoginActvity.this);
        if(locationTracker.canGetLocation()){
            latitude=locationTracker.getLatitude()+"";
            longitude=locationTracker.getLongitude()+"";
        }
phoneValidate();
        initGmail();
        initFb();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tvSignUp:
                startActivity(new Intent(LoginActvity.this,SignupActvity.class));
                break;
            case R.id.tvForgotPass:
                startActivity(new Intent(LoginActvity.this,ResetPassword.class));
                break;
            case R.id.btnLogin:
                if (edtMail.length()>0&&edtPass.length()>0) {
                    username = edtMail.getText().toString().trim();
                    password = edtPass.getText().toString().trim();
// login with firebase
                    loginWithFcm();
                }
                else
                    Toast.makeText(LoginActvity.this,"Please enter your ceredential",Toast.LENGTH_SHORT).show();
                break;
            case R.id.btnFb:
                ProgressDialogBox.showProgressDialog(LoginActvity.this);
                loginButton.performClick();
                break;
            case R.id.btnGmail:
                ProgressDialogBox.showProgressDialog(LoginActvity.this);
                signIn();
                break;
        }
    }


    @Override
    public void onLoginSuccess(String message) {

        Log.e("str1",message);
        Prefrence.saveBollen(LoginActvity.this,"native",true);
        Toast.makeText(LoginActvity.this, "Logged in successfully", Toast.LENGTH_SHORT).show();
        ProgressDialogBox.disMissDailog();

//        finish();
    }


    @Override
    public void onLoginFailure(String message) {
        ProgressDialogBox.disMissDailog();
        showDailog(message);

    }

    public void initGmail(){

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        mAuth = FirebaseAuth.getInstance();
    }

    public void initFb(){

        mCallbackManager = CallbackManager.Factory.create();
         loginButton = (LoginButton) findViewById(R.id.button_facebook_login);
        loginButton.setReadPermissions("email", "public_profile");
        loginButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d(TAG, "facebook:onSuccess:" + loginResult);
                acess_token=  loginResult.getAccessToken().getUserId();
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                Log.d(TAG, "facebook:onCancel");
                ProgressDialogBox.disMissDailog();
                // [START_EXCLUDE]
                updateUI(null);
                // [END_EXCLUDE]
            }

            @Override
            public void onError(FacebookException error) {
                Log.d(TAG, "facebook:onError", error);
                // [START_EXCLUDE]
                updateUI(null);
                ProgressDialogBox.disMissDailog();
                // [END_EXCLUDE]
            }
        });
        // [END initialize_fblogin]
    }

    private void init() {


        File folder = new File(Environment.getExternalStorageDirectory() +
                File.separator + "Android/data/com.shwesi.tracker");
              if (!folder.exists()) {
           folder.mkdirs();
        }
        mLoginPresenter = new LoginPresenter(this);

    }
    public void loginWithFcm(){
        String emailId = edtMail.getText().toString().trim();
        String password = edtPass.getText().toString().trim();
        ProgressDialogBox.showProgressDialog(LoginActvity.this);

        mLoginPresenter.login(LoginActvity.this, emailId, password);

    }
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
//        updateUI(currentUser);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e);
                // [START_EXCLUDE]
                updateUI(null);
                // [END_EXCLUDE]
            }
        }
        else
            mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }


    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());
        // [START_EXCLUDE silent]
//        showProgressDialog();

        // [END_EXCLUDE]

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
//                            Toast.makeText(LoginActvity.this, "Login SucessFully",
//                                    Toast.LENGTH_SHORT).show();
                           user = mAuth.getCurrentUser();
                            FirebaseDatabase.getInstance().getReference().child(ARG_USERS).child(user.getUid()).
                                    addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            if (dataSnapshot.exists()){
                                            User user1 = dataSnapshot.getValue(User.class);
                                                if (dataSnapshot.hasChild("phoneNumber")) {
                                                    if (user1.phoneNumber.matches("")) {

                                                        showEdittextialog(user);
                                                    } else {

                                                        phoneNumber = user1.phoneNumber;
                                                        updateUI(user);
                                                    }
                                                }
                                                else {
                                                    showEdittextialog(user);
                                                }
                                        }
                                        else {
                                                showEdittextialog(user);
                                            }

                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }
                                    });


                        } else {
                            ProgressDialogBox.disMissDailog();

                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            showDailog(task.getException().getMessage());
//                            Toast.makeText(LoginActvity.this, "Authentication failed.",
//                                    Toast.LENGTH_SHORT).show();
//                            updateUI(null);
                        }
                        ProgressDialogBox.disMissDailog();
                        // [START_EXCLUDE]
//                        hideProgressDialog();
                        // [END_EXCLUDE]
                    }
                });
    }
    // [END auth_with_google]

    // [START signin]
    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }
    public String getdefaultImagePAth(){
        Bitmap bitMap = BitmapFactory.decodeResource(getResources(),R.drawable.user);

        File mFile1 = Environment.getExternalStorageDirectory();

        String fileName ="user.png";

        File mFile2 = new File(mFile1,fileName);
        try {
            FileOutputStream outStream;

            outStream = new FileOutputStream(mFile2);

            bitMap.compress(Bitmap.CompressFormat.PNG, 100, outStream);

            outStream.flush();

            outStream.close();

        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        String sdPath = mFile1.getAbsolutePath().toString()+"/"+fileName;
        return sdPath;
    }

    private void updateUI(FirebaseUser user1) {

        Prefrence.saveBollen(LoginActvity.this,"native",false);
//        hideProgressDialog();
        if (user1 != null) {
           User user=new User();
            if (user1.getEmail()!=null)
            user.email=user1.getEmail();
            else
                user.email=acess_token;
            if (user1.getPhoneNumber()!=null)
            user.phoneNumber=user1.getPhoneNumber();
            else
                user.phoneNumber=phoneNumber;
            if (user1.getPhotoUrl()!=null)
                user.profilePic=user1.getPhotoUrl().toString();
            else
                user.profilePic=getdefaultImagePAth();
            user.firstname=user1.getDisplayName();
            user.lastname="";
            user.password="";
            user.emailVerified=true;
            user.last_seen="Online";
            user.firebaseToken= new SharedPrefUtil(getApplicationContext()).getString(Constants.ARG_FIREBASE_TOKEN, null);
            user.uid=user1.getUid();
            user.lat=latitude;
            user.lon=longitude;
            DatabaseReference database = FirebaseDatabase.getInstance().getReference();
            database.child(Constants.ARG_USERS)
                    .child(user1.getUid())
                    .setValue(user);
            LoginCredentials loginCredentials=new LoginCredentials();
//                            loginCredentials.setUser_id(jsonObject.getString(KEY_USER_ID));
//                            loginCredentials.setUseridentificationNo(jsonObject.getString(KEY_UNIQUE_ID));
            loginCredentials.setFirst_name(user.firstname);
            loginCredentials.setLast_name(user.lastname);
            loginCredentials.setEmail( user.email);
            loginCredentials.setPhone(user.phoneNumber);
            loginCredentials.setPassword(user.password);
            loginCredentials.setImage(user.profilePic);
            Prefrence.saveString(LoginActvity.this,"imageUrl",user.profilePic);
            loginCredentials.setLongitude(user.lon);
            loginCredentials.setLatitude(user.lat);
            loginCredentials.setUseridentificationNo(user.uid);
            loginCredentials.setFirebasetoken( new SharedPrefUtil(LoginActvity.this).getString(Constants.ARG_FIREBASE_TOKEN, null));
            Prefrence.saveLoginCeredential(LoginActvity.this, loginCredentials);

        }

    }


    private void handleFacebookAccessToken(AccessToken token) {
        Log.d(TAG, "handleFacebookAccessToken:" + token);
        // [START_EXCLUDE silent]
//        showProgressDialog();
        // [END_EXCLUDE]

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
//                            ProgressDialogBox.showProgressDialog(LoginActvity.this);
//                            Toast.makeText(LoginActvity.this, "Login SucessFully",
//                                    Toast.LENGTH_SHORT).show();
                             user = mAuth.getCurrentUser();

                            FirebaseDatabase.getInstance().getReference().child(ARG_USERS).child(user.getUid()).
                                    addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            if (dataSnapshot.exists()){
                                                User user1 = dataSnapshot.getValue(User.class);
                                                if (dataSnapshot.hasChild("phoneNumber")){
                                                if (user1.phoneNumber.matches("")) {

                                                    showEdittextialog(user);
                                                }
                                                else {

                                                    phoneNumber=user1.phoneNumber;
                                                    updateUI(user);
                                                }}
                                                else {
                                                    showEdittextialog(user);
                                                }

                                            }
                                            else {
                                                showEdittextialog(user);
                                            }

                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }
                                    });

//                            showEdittextialog(user);



//                            updateUI(user);
                        } else {

                            LoginManager.getInstance().logOut();
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            showDailog( task.getException().getMessage().toString());
//                            Toast.makeText(LoginActvity.this, task.getException().getMessage().toString(),
//                                    Toast.LENGTH_LONG).show();
//                            updateUI(null);
                        }
                        ProgressDialogBox.disMissDailog();
                        // [START_EXCLUDE]
//                        hideProgressDialog();
                        // [END_EXCLUDE]
                    }
                });
    }
    public void showDailog(String message){
        AlertDialog.Builder builder1 = new AlertDialog.Builder(LoginActvity.this);
        builder1.setMessage(message);
        builder1.setCancelable(true);

        builder1.setPositiveButton(
                "Ok",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        dialog.cancel();

                    }
                });

        AlertDialog alert11 = builder1.create();
        alert11.show();
    }

    public void showEdittextialog(final FirebaseUser user) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.custom_dialog, null);
        dialogBuilder.setView(dialogView);
        final EditText edtCountryCode=(EditText) dialogView.findViewById(R.id.edtCountryCode);
        edt = (EditText) dialogView.findViewById(R.id.edittext);

        dialogBuilder.setTitle("Verify Your Phone Number");


        dialogBuilder.setPositiveButton("Verify",null);
        dialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                //pass
                dialog.dismiss();
                LoginManager.getInstance().logOut();
            }
        });
         b = dialogBuilder.create();
        b.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(final DialogInterface dialog) {
                Button button=b.getButton(AlertDialog.BUTTON_POSITIVE);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (edt.length()>0&&edtCountryCode.length()>0) {
                            phoneNumber=edt.getText().toString();
                            //do something with edt.getText().toString();
                            PhoneAuthProvider.getInstance().verifyPhoneNumber(
                                    "+" +edtCountryCode.getText().toString()+ edt.getText().toString(),        // Phone number to verify
                                    60,                 // Timeout duration
                                    TimeUnit.SECONDS,   // Unit of timeout
                                    LoginActvity.this,               // Activity (for callback binding)
                                    mCallbacks);
                            ProgressDialogVerfication.showProgressDialog(LoginActvity.this);
                            dialog.dismiss();
                        }
                        else
                        {
                            Toast.makeText(LoginActvity.this," Country Code and phone Number can't be blank",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
        b.setCancelable(false);
        b.show();
    }

    @Override
    public boolean saved(boolean value) {
       Log.e("booo",value+"");
        Intent myIntent=new Intent(LoginActvity.this,HomeActivity.class);
        startActivity(myIntent);
        finish();
        return true;
    }

    public void phoneValidate(){
        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {

                mAuthoCeredential=phoneAuthCredential;
                Toast.makeText(LoginActvity.this,"Verification done SucessFully",Toast.LENGTH_LONG).show();
                ProgressDialogVerfication.disMissDailog();
                updateUI(user);
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                Toast.makeText(LoginActvity.this,"verification fail"+ e.getMessage().toString(),Toast.LENGTH_LONG).show();
                LoginManager.getInstance().logOut();
                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    // Invalid request
                    // [START_EXCLUDE]
                    Toast.makeText(LoginActvity.this,"invalid mob no",Toast.LENGTH_LONG).show();
                    // [END_EXCLUDE]
                } else if (e instanceof FirebaseTooManyRequestsException) {
                    // The SMS quota for the project has been exceeded
                    // [START_EXCLUDE]
                    Toast.makeText(LoginActvity.this,"quota over" ,Toast.LENGTH_LONG).show();
                    // [END_EXCLUDE]
                }
            }

            @Override
            public void onCodeAutoRetrievalTimeOut(String s) {
                super.onCodeAutoRetrievalTimeOut(s);
                Toast.makeText(LoginActvity.this,"Phone Number not detected in your device ",Toast.LENGTH_LONG).show();
                LoginManager.getInstance().logOut();
                ProgressDialogVerfication.disMissDailog();
            }

            @Override
            public void onCodeSent(String verificationId,
                                   PhoneAuthProvider.ForceResendingToken token) {
                // The SMS verification code has been sent to the provided phone number, we
                // now need to ask the user to enter the code and then construct a credential
                // by combining the code with a verification ID.
                //Log.d(TAG, "onCodeSent:" + verificationId);
//                Toast.makeText(LoginActvity.this,"Verification code sent to mobile"+verificationId,Toast.LENGTH_LONG).show();
                // Save verification ID and resending token so we can use them later
                mVerificationId = verificationId;
//                showOtpEdit(mVerificationId);
//                ProgressDialogBox.disMissDailog();
//                mResendToken = token;
//                MobileNumber.setVisibility(View.GONE);
//                Submit.setVisibility(View.GONE);
//                Textview.setVisibility(View.GONE);
//                OTPButton.setVisibility(View.VISIBLE);
//                OTPEditview.setVisibility(View.VISIBLE);
//                Otp.setVisibility(View.VISIBLE);
                // ...
            }
        };

    }



}
