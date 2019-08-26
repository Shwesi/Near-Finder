package com.example.toryes.studnow.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.text.TextUtils;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.toryes.studnow.R;
import com.example.toryes.studnow.Utils.Constants;
import com.example.toryes.studnow.Utils.GetFilePathFromDevice;

import com.example.toryes.studnow.Utils.Prefrence;
import com.example.toryes.studnow.Utils.ProgressDialogBox;
import com.example.toryes.studnow.Utils.ProgressDialogVerfication;
import com.example.toryes.studnow.Utils.SharedPrefUtil;
import com.example.toryes.studnow.bin.LoginCredentials;
import com.example.toryes.studnow.bin.User;
import com.example.toryes.studnow.chat.core.registration.RegisterContract;
import com.example.toryes.studnow.chat.core.registration.RegisterPresenter;
import com.example.toryes.studnow.chat.core.users.add.AddUserContract;
import com.example.toryes.studnow.chat.core.users.add.AddUserPresenter;
import com.example.toryes.studnow.service.LocationTracker;
import com.facebook.login.LoginManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;
import cz.msebera.android.httpclient.message.BasicHeader;
import cz.msebera.android.httpclient.protocol.HTTP;
import de.hdodenhof.circleimageview.CircleImageView;

import static android.R.attr.bitmap;

public class SignupActvity extends AppCompatActivity implements Constants,View.OnClickListener, RegisterContract.View, AddUserContract.View {
    AppCompatButton btnJoinUs;
    EditText edtFirstName,edtLastName,edtPhone,edtEmail,edtPass,edtrepass;
    String longitude,latitude,phoneNumber;
    TextView tvLogin;
    CircleImageView profilePic,btnSelectImage;
    int PICK_IMAGE_REQUEST=101;
    String  img_Decodable_Str;
    private RegisterPresenter mRegisterPresenter;
    private AddUserPresenter mAddUserPresenter;
    User user;
    LoginCredentials loginCredentials;
    AlertDialog b;
    EditText edt;
    FirebaseUser firebaseUser;

    PhoneAuthCredential mAuthoCeredential;
    PhoneAuthProvider.ForceResendingToken mResendToken;
    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_actvity);
        // initliazing views
        initView();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //locationtracker class fetch the current longitude and lattitude
        LocationTracker locationTracker=new LocationTracker(SignupActvity.this);
        if(locationTracker.canGetLocation()) {
            latitude = locationTracker.getLatitude() + "";
            longitude = locationTracker.getLongitude() + "";
        }
    }

    public void initView(){
        tvLogin= (TextView) findViewById(R.id.textLogin);
        edtEmail= (EditText) findViewById(R.id.edtEmail);
        edtFirstName= (EditText) findViewById(R.id.edtFirstName);
        edtLastName= (EditText) findViewById(R.id.edtLastName);
        edtPhone= (EditText) findViewById(R.id.edtPhone);
        edtPass= (EditText) findViewById(R.id.edtPass);
        edtrepass= (EditText) findViewById(R.id.edtRePass);
        btnJoinUs= (AppCompatButton) findViewById(R.id.btnJoinUs);
        profilePic= (CircleImageView) findViewById(R.id.profilePic);
        btnSelectImage= (CircleImageView) findViewById(R.id.btnSelectImage);
        edtPass.setTypeface(Typeface.DEFAULT);
        edtPass.setTransformationMethod(new PasswordTransformationMethod());
        edtrepass.setTypeface(Typeface.DEFAULT);
        edtrepass.setTransformationMethod(new PasswordTransformationMethod());
        btnJoinUs.setOnClickListener(this);
        btnSelectImage.setOnClickListener(this);
        tvLogin.setOnClickListener(this);
        mRegisterPresenter = new RegisterPresenter(this);
        mAddUserPresenter = new AddUserPresenter(this);
        phoneValidate();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnJoinUs:
                if (edtEmail.length()>0&&edtFirstName.length()>0&&edtPhone.length()>0&&edtPass.length()>0&&edtrepass.length()>0) {
                    if (!edtPass.getText().toString().matches(edtrepass.getText().toString()))
                        Toast.makeText(SignupActvity.this, "Password didn't matched", Toast.LENGTH_SHORT).show();
                    if (!isValidEmail(edtEmail.getText().toString().trim()))
                        Toast.makeText(SignupActvity.this, "Please enter valid email address", Toast.LENGTH_SHORT).show();
//                    if (img_Decodable_Str==null) {
//                        Uri uri = Uri.parse("R.drawable.user");
//                        img_Decodable_Str = uri.getPath();
//                    }
//                        Toast.makeText(SignupActvity.this, "Please select image", Toast.LENGTH_SHORT).show();
                    if (edtPass.length()<6)
                        Toast.makeText(SignupActvity.this, "Password should be atleast 6 character", Toast.LENGTH_SHORT).show();
                    if (edtPass.getText().toString().matches(edtrepass.getText().toString())&& isValidEmail(edtEmail.getText().toString().trim())&&edtPass.length()>=6)
//                        callRegisterApi();
//                        onRegister();
                    // uploading profile image
                    if (img_Decodable_Str==null) {
                      img_Decodable_Str= getdefaultImagePAth();
                    }
                    else
                    uploadFile(Uri.fromFile(new File(img_Decodable_Str)),edtEmail.getText().toString().trim());


                }
                else
                    Toast.makeText(SignupActvity.this, "Please enter all the field.", Toast.LENGTH_SHORT).show();
                break;
            case R.id.btnSelectImage:
                browseImage();
                break;
            case R.id.textLogin:
                startActivity(new Intent(SignupActvity.this,LoginActvity.class));
                finish();
                break;
        }
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
    public void browseImage(){
        Intent intent = new Intent();
// Show only images, no videos or anything else
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
// Always show the chooser (if there are multiple options available)
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {

            img_Decodable_Str= GetFilePathFromDevice.getPath(this, data.getData());
            File f = new File(img_Decodable_Str);
//            Picasso.with(SignupActvity.this)
//                    .load(f)
//                    .into(profilePic);
            Glide.with(SignupActvity.this).load(img_Decodable_Str).into(profilePic);


        }
    }


    public final static boolean isValidEmail(CharSequence target) {
        return !TextUtils.isEmpty(target) && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }




    public void toastMsg(String message){
        Toast.makeText(SignupActvity.this,message,Toast.LENGTH_SHORT).show();
    }

    private void onRegister() {
        showEdittextialog(firebaseUser);
        ProgressDialogBox.disMissDailog();
    }

    @Override
    public void onRegistrationSuccess(FirebaseUser firebaseUser) {
        // after resgistrartion adding user to firebase
        mAddUserPresenter.addUser(getApplicationContext(), firebaseUser,user);
        ProgressDialogVerfication.disMissDailog();
//        Toast.makeText(SignupActvity.this,"Registration Successfull",Toast.LENGTH_SHORT).show();
        Log.e("ftokem",firebaseUser.getUid()+" "+new SharedPrefUtil(getApplicationContext()).getString(ARG_FIREBASE_TOKEN, null));
        showDailog("Registration Successfull Please Verify Your Email");

    }

    public void showEdittextialog(final FirebaseUser user) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.custom_dialog, null);
        dialogBuilder.setView(dialogView);
        final EditText edtCountryCode=(EditText) dialogView.findViewById(R.id.edtCountryCode);
        edt = (EditText) dialogView.findViewById(R.id.edittext);
        edt.setText(edtPhone.getText().toString());
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
                Button button = b.getButton(AlertDialog.BUTTON_POSITIVE);
                button.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        // TODO Do something
                        if (edt.length()>0&&edtCountryCode.length()>0) {
                            phoneNumber=edt.getText().toString();
                            //do something with edt.getText().toString();
                            PhoneAuthProvider.getInstance().verifyPhoneNumber(
                                    "+" +edtCountryCode.getText().toString()+ edt.getText().toString(),        // Phone number to verify
                                    60,                 // Timeout duration
                                    TimeUnit.SECONDS,   // Unit of timeout
                                    SignupActvity.this,               // Activity (for callback binding)
                                    mCallbacks);
                            ProgressDialogVerfication.showProgressDialog(SignupActvity.this);
                            dialog.dismiss();
                        }
                        else
                        {
                            Toast.makeText(SignupActvity.this," Country Code and phone Number can't be blank",Toast.LENGTH_SHORT).show();
                        }
                    }

                });
        }
        });
        b.setCancelable(false);
        b.show();
    }

    @Override
    public void onRegistrationFailure(String message) {
        ProgressDialogBox.disMissDailog();
        Toast.makeText(SignupActvity.this, "Registration failed!+\n" + message, Toast.LENGTH_LONG).show();

    }

    @Override
    public void onAddUserSuccess(String message) {
//        mProgressDialog.dismiss();
//        Toast.makeText(, message, Toast.LENGTH_SHORT).show();
//        UserListingActivity.startActivity(SignupActvity.this,
//                Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
    }

    @Override
    public void onAddUserFailure(String message) {
//        mProgressDialog.dismiss();
        Toast.makeText(SignupActvity.this, message, Toast.LENGTH_SHORT).show();
    }

    public void showDailog(String message){
        AlertDialog.Builder builder1 = new AlertDialog.Builder(SignupActvity.this);
        builder1.setMessage(message);
        builder1.setCancelable(true);

        builder1.setPositiveButton(
                "Ok",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                      startActivity(new Intent(SignupActvity.this,LoginActvity.class));
                        finish();
                        dialog.cancel();

                    }
                });

        AlertDialog alert11 = builder1.create();
        alert11.show();
    }



    private void uploadFile(Uri filePath,String filename) {
        //if there is a file to upload
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageReference = storage.getReferenceFromUrl("gs://tracking-6b462.appspot.com/");
        if (filePath != null) {
            ProgressDialogBox.showProgressDialog(SignupActvity.this);
            //displaying a progress dialog while upload is going on
            StorageReference riversRef = storageReference.child("profile/"+filename);
            riversRef.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            //if the upload is successfull
                            //hiding the progress dialog
                            Prefrence.saveString(SignupActvity.this,"imageUrl",taskSnapshot.getDownloadUrl().toString());
                            Prefrence.saveString(SignupActvity.this,"phone",edtPhone.getText().toString());
                            user=new User();
                            user.email=edtEmail.getText().toString().trim();
                            user.phoneNumber=edtPhone.getText().toString();
                            user.profilePic=taskSnapshot.getDownloadUrl().toString();
                            user.firstname=edtFirstName.getText().toString();
                            user.lastname=edtLastName.getText().toString();
                            user.password=edtPass.getText().toString();
                            user.emailVerified=false;
                            // register with fcm
                            onRegister();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            //if the upload is not successfull
                            //hiding the progress dialog
                            ProgressDialogBox.disMissDailog();

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


    public void phoneValidate(){
        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {

                mAuthoCeredential=phoneAuthCredential;
                Toast.makeText(SignupActvity.this,"Verification done SucessFully",Toast.LENGTH_LONG).show();

//                updateUI(user);
                mRegisterPresenter.register(SignupActvity.this, edtEmail.getText().toString().trim(), edtPass.getText().toString());
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                Toast.makeText(SignupActvity.this,"verification fail"+ e.getMessage().toString(),Toast.LENGTH_LONG).show();
                LoginManager.getInstance().logOut();
                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    // Invalid request
                    // [START_EXCLUDE]
                    Toast.makeText(SignupActvity.this,"invalid mob no",Toast.LENGTH_LONG).show();
                    // [END_EXCLUDE]
                } else if (e instanceof FirebaseTooManyRequestsException) {
                    // The SMS quota for the project has been exceeded
                    // [START_EXCLUDE]
                    Toast.makeText(SignupActvity.this,"quota over" ,Toast.LENGTH_LONG).show();
                    // [END_EXCLUDE]
                }
            }

            @Override
            public void onCodeAutoRetrievalTimeOut(String s) {
                super.onCodeAutoRetrievalTimeOut(s);
                Toast.makeText(SignupActvity.this,"Phone Number not detected in your device",Toast.LENGTH_LONG).show();
                ProgressDialogVerfication.disMissDailog();
            }

            @Override
            public void onCodeSent(String verificationId,
                                   PhoneAuthProvider.ForceResendingToken token) {


            }
        };

    }
}
