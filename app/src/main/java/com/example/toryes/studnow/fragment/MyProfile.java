package com.example.toryes.studnow.fragment;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.toryes.studnow.R;
import com.example.toryes.studnow.Utils.Constants;
import com.example.toryes.studnow.Utils.GetFilePathFromDevice;
import com.example.toryes.studnow.Utils.Prefrence;
import com.example.toryes.studnow.Utils.ProgressDialogBox;
import com.example.toryes.studnow.Utils.ProgressDialogVerfication;
import com.example.toryes.studnow.Utils.SharedPrefUtil;
import com.example.toryes.studnow.activity.HomeActivity;
import com.example.toryes.studnow.activity.SignupActvity;
import com.example.toryes.studnow.bin.LoginCredentials;
import com.example.toryes.studnow.bin.User;
import com.example.toryes.studnow.service.LocationTracker;
import com.facebook.login.LoginManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;
import cz.msebera.android.httpclient.message.BasicHeader;
import cz.msebera.android.httpclient.protocol.HTTP;
import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by TORYES on 7/1/2017.
 */

public class MyProfile extends Fragment implements Constants{
    AppCompatButton btnJoinUs;
    EditText edtFirstName,edtLastName,edtPhone,edtEmail,edtPass,edtrepass;
    String longitude,latitude,img_Decodable_Str,userid,downloadUrl,phoneNumber;
    CircleImageView imageView,btnSelectImage;
    int PICK_IMAGE_REQUEST=101;
    LoginCredentials loginCredentials;
    AlertDialog b;
    EditText edt;
    FirebaseUser firebaseUser;

    PhoneAuthCredential mAuthoCeredential;
    PhoneAuthProvider.ForceResendingToken mResendToken;
    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.my_profile_fragment,null,false);
         loginCredentials = Prefrence.getLoginCeredential(getActivity());
        initView(view);
        LocationTracker locationTracker=new LocationTracker(getActivity());
        if(locationTracker.canGetLocation()) {
            latitude = locationTracker.getLatitude() + "";
            longitude = locationTracker.getLongitude() + "";
        }
        return view;
    }

    public void initView(View view){
        getActivity().setTitle("My Profile");
        edtEmail= (EditText) view.findViewById(R.id.edtEmail);
        edtFirstName= (EditText) view.findViewById(R.id.edtFirstName);
        edtLastName= (EditText) view.findViewById(R.id.edtLastName);
        edtPhone= (EditText) view.findViewById(R.id.edtPhone);
        edtPass= (EditText) view.findViewById(R.id.edtPass);
        edtrepass= (EditText) view.findViewById(R.id.edtRePass);
        imageView= (CircleImageView) view.findViewById(R.id.profilePic);
//        edIdentity= (EditText) view.findViewById(R.id.edtId);
        btnSelectImage= (CircleImageView) view.findViewById(R.id.btnSelectImage);
        // fetching data from saved object
         loginCredentials=Prefrence.getLoginCeredential(getActivity());
        edtEmail.setText(loginCredentials.getEmail());
        edtFirstName.setText(loginCredentials.getFirst_name());
        edtLastName.setText(loginCredentials.getLast_name());
        edtPhone.setText(loginCredentials.getPhone());
        edtPass.setText(loginCredentials.getPassword());
        edtrepass.setText(loginCredentials.getPassword());
        userid=loginCredentials.getUser_id();
        Glide.with(this).load(Prefrence.getString(getActivity(),"imageUrl")).diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true).into(imageView);
        btnJoinUs= (AppCompatButton) view.findViewById(R.id.btnJoinUs);
        btnJoinUs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //updating profile

                if (edtEmail.length()>0&&edtFirstName.length()>0&&edtPhone.length()>0&&edtPass.length()>0&&edtrepass.length()>0) {
                    if (!edtPass.getText().toString().matches(edtrepass.getText().toString()))
                        Toast.makeText(getActivity(), "Password didn't matched", Toast.LENGTH_SHORT).show();
                   else if (!isValidEmail(edtEmail.getText().toString()))
                        Toast.makeText(getActivity(), "Please enter valid email address", Toast.LENGTH_SHORT).show();

                  else   if (edtPass.length()<6)
                        Toast.makeText(getActivity(), "Password should be atleast 6 character", Toast.LENGTH_SHORT).show();
                   else if (edtPass.getText().toString().matches(edtrepass.getText().toString())&& isValidEmail(edtEmail.getText().toString())&&edtPass.length()>=6){
//                        callUpdateProfileApi();
                    if (loginCredentials.getPhone().matches(edtPhone.getText().toString())) {
                        updateUserIdPass();
                    }
                    else {
                        showEdittextialog();
                    }
                }}
                else
                    Toast.makeText(getActivity(), "Please enter all the field.", Toast.LENGTH_SHORT).show();

//
            }
        });
        phoneValidate();
        btnSelectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                browseImage();
            }
        });
    }



    public void showEdittextialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
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
                                    getActivity(),               // Activity (for callback binding)
                                    mCallbacks);
                            ProgressDialogVerfication.showProgressDialog(getActivity());
                            dialog.dismiss();
                        }
                        else
                        {
                            Toast.makeText(getActivity()," Country Code and phone Number can't be blank",Toast.LENGTH_SHORT).show();
                        }
                    }

                });
            }
        });
        b.setCancelable(false);
        b.show();
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == getActivity().RESULT_OK && data != null && data.getData() != null) {

            img_Decodable_Str= GetFilePathFromDevice.getPath(getActivity(), data.getData());
            uploadFile(Uri.fromFile(new File(img_Decodable_Str)),edtEmail.getText().toString());
            File f = new File(img_Decodable_Str);
            Glide.with(getActivity()).load(img_Decodable_Str).into(imageView);



        }
    }
    public final  boolean isValidEmail(CharSequence target) {
        return !TextUtils.isEmpty(target) && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

public void updateUserIdPass(){
    final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    ProgressDialogBox.showProgressDialog(getActivity());

// Get auth credentials from the user for re-authentication. The example below shows
// email and password credentials but there are multiple possible providers,
// such as GoogleAuthProvider or FacebookAuthProvider.
    AuthCredential credential = EmailAuthProvider
            .getCredential(loginCredentials.getEmail(), loginCredentials.getPassword());

// Prompt the user to re-provide their sign-in credentials
    user.reauthenticate(credential)
            .addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    user.updateEmail(edtEmail.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Log.e("TAG", "Password email");
                                user.updatePassword(edtPass.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Log.e("TAG", "Password updated");
                                        } else {
                                            Log.e("TAG", "Error password not updated");
                                        }
                                    }
                                });
                            } else {
                                Log.e("TAG", "Error password not email");
                                ProgressDialogBox.disMissDailog();
                            }
                        }
                    });
                    //if user id and password updated then updating user profile
                    updateProfile();
                }

            });
}

    private void updateProfile() {
        final LoginCredentials loginCredentials = Prefrence.getLoginCeredential(getActivity());
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();

        final User user1 = new User();
        user1.email = edtEmail.getText().toString();
        user1.password =  edtPass.getText().toString();
        user1.firstname = edtFirstName.getText().toString();
        user1.lastname = edtLastName.getText().toString();
        user1.lat = latitude + "";
        user1.lon = longitude + "";
        user1.phoneNumber = edtPhone.getText().toString();
        if (downloadUrl!=null)
        user1.profilePic=downloadUrl;
        else
            user1.profilePic=loginCredentials.getImage();
        Map<String, Object> postValues = user1.toUpdateMap();
        database.child(Constants.ARG_USERS).child(loginCredentials.getUseridentificationNo()).updateChildren(postValues).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(getActivity(),"Profile Updated Successfully",Toast.LENGTH_SHORT).show();
                    LoginCredentials loginCredentials1 = new LoginCredentials();


                    loginCredentials1.setFirst_name(user1.firstname);
                    loginCredentials1.setLast_name(user1.lastname);
                    loginCredentials1.setEmail(user1.email);
                    loginCredentials1.setPhone(user1.phoneNumber);
                    loginCredentials1.setPassword(user1.password);
                    loginCredentials1.setImage(user1.profilePic);
                    loginCredentials1.setLongitude(longitude + "");
                    loginCredentials1.setLatitude(latitude + "");
                    loginCredentials1.setUseridentificationNo(loginCredentials.getUseridentificationNo());
                    Prefrence.saveLoginUpdateCeredential(getActivity(),loginCredentials1);
                    ProgressDialogBox.disMissDailog();
                }
                else {
                    Toast.makeText(getActivity(),"Please try again",Toast.LENGTH_SHORT).show();
                }


            }
        });
    }


    private void uploadFile(Uri filePath,String filename) {
        ProgressDialogBox.showProgressDialog(getActivity());
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageReference = storage.getReferenceFromUrl("gs://tracking-6b462.appspot.com");
        if (filePath != null) {
            //displaying a progress dialog while upload is going on


            StorageReference riversRef = storageReference.child("profile/"+filename);
            riversRef.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            //if the upload is successfull
                            //hiding the progress dialog
                            Prefrence.saveString(getActivity(),"imageUrl",taskSnapshot.getDownloadUrl().toString());

                            Prefrence.saveString(getActivity(),"phone",edtPhone.getText().toString());
                            downloadUrl=taskSnapshot.getDownloadUrl().toString();
                            updateprofileimageDownloadUri();


                            //and displaying a success toast

//                            Toast.makeText(getApplicationContext(), "File Uploaded ", Toast.LENGTH_LONG).show();
                            Log.e("message","fileuploaded");
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            //if the upload is not successfull
                            //hiding the progress dialog

                            //and displaying error message
                            ProgressDialogBox.disMissDailog();
                            Toast.makeText(getActivity(), exception.getMessage(), Toast.LENGTH_LONG).show();
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

    public void updateprofileimageDownloadUri(){
        final LoginCredentials loginCredentials = Prefrence.getLoginCeredential(getActivity());
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();

        final User user1 = new User();
            user1.profilePic=downloadUrl;
        Glide.with(this).load(Prefrence.getString(getActivity(),"imageUrl")).diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true).into(HomeActivity.imageView);

        Map<String, Object> postValues = user1.toImageUpdateMap();
        database.child(Constants.ARG_USERS).child(loginCredentials.getUseridentificationNo()).updateChildren(postValues).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
//                            mOnUserDatabaseListener.onSuccess(context.getString(R.string.user_successfully_added));
                    ProgressDialogBox.disMissDailog();
                }


            }
        });
    }



    public void phoneValidate(){
        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {

                mAuthoCeredential=phoneAuthCredential;
                Toast.makeText(getActivity(),"Verification done SucessFully",Toast.LENGTH_LONG).show();
                ProgressDialogVerfication.disMissDailog();
                updateUserIdPass();
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
}
