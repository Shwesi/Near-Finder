package com.example.toryes.studnow.fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.toryes.studnow.R;
import com.example.toryes.studnow.Utils.Constants;
import com.example.toryes.studnow.Utils.ItemClickSupport;
import com.example.toryes.studnow.Utils.Prefrence;
import com.example.toryes.studnow.Utils.ProgressDialogBox;
import com.example.toryes.studnow.adapter.AllUserAdapter;
import com.example.toryes.studnow.bin.LoginCredentials;
import com.example.toryes.studnow.bin.NearByUser;
import com.example.toryes.studnow.bin.User;
import com.example.toryes.studnow.bin.Users;
import com.example.toryes.studnow.chat.core.users.get.all.GetUsersContract;
import com.example.toryes.studnow.chat.core.users.get.all.GetUsersPresenter;
import com.example.toryes.studnow.helper.DatabaseHelper;
import com.example.toryes.studnow.service.LocationTracker;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import cz.msebera.android.httpclient.Header;

/**
 * Created by TORYES on 7/3/2017.
 */

public class AllUserFragment extends Fragment implements Constants,GetUsersContract.View, ItemClickSupport.OnItemClickListener{
    RecyclerView recyclerView;
  public static   AllUserAdapter allUserAdapter;
    String user_id;
    ArrayList<NearByUser> arrayList;
    List<User> users;
    DatabaseHelper databaseHelper;
    ////chat_my
    public static final String ARG_TYPE = "type";
    public static final String TYPE_CHATS = "type_chats";
    public static final String TYPE_ALL = "type_all";
    GetUsersPresenter mGetUsersPresenter;
    String longitude,latitude;
    ArrayList<String> StoreContacts ;

    Cursor cursor ;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.all_user_fragment,null,false);
        LocationTracker locationTracker=new LocationTracker(getActivity());

        if(locationTracker.canGetLocation()) {
            latitude = locationTracker.getLatitude() + "";
            longitude = locationTracker.getLongitude() + "";
//            Toast.makeText(SignupActvity.this, locationTracker.getLongitude() + "" + locationTracker.getLatitude(), Toast.LENGTH_SHORT).show();
        }
        getActivity().setTitle(getActivity().getResources().getString(R.string.friends));
        Prefrence.saveBollen(getActivity(),"fcmregistration",true);
//        GetContactsIntoArrayList();
        Prefrence.saveString(getActivity(),KEY_EMAIL,"admin");
        arrayList=new ArrayList<>();
        users=new ArrayList<>();
         databaseHelper=new DatabaseHelper(getActivity());
        Log.e("numof",databaseHelper.numberOfRows()+"");
        //chat_my
        init();
        if (Prefrence.getInt(getActivity(),"count")!=0)
            Log.e("mycoutn",Prefrence.getInt(getActivity(),"count")+"");
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerview);
        // call recyclerview adapter
        allUserAdapter = new AllUserAdapter(arrayList,getActivity(),users);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        //setting adapter to recyclerview
        recyclerView.setAdapter(allUserAdapter);
        // fetching data from saved object loginCredentials
        LoginCredentials loginCredentials = Prefrence.getLoginCeredential(getActivity());
        user_id=loginCredentials.getUser_id();
        Log.e("mail", loginCredentials.getEmail() + "" + loginCredentials.getFirst_name());
        ProgressDialogBox.showProgressDialog(getActivity());
        ItemClickSupport.addTo(recyclerView)
        .setOnItemClickListener(this);
        // updating last seen status to online
        updateLastSeen( FirebaseAuth.getInstance().getCurrentUser().getUid(),"Online");
        scheduleOnline();
        return view;

    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    private void init() {
        mGetUsersPresenter = new GetUsersPresenter(getActivity(),this);
        //fetching all the registered user from firebase
        getUsers();
    }
    private void getUsers() {

            mGetUsersPresenter.getAllUsers();
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_search) {
            Intent bundle=new Intent(getActivity(),MapFragment.class);
            bundle.putParcelableArrayListExtra("near_by_user", arrayList);
            SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
            SharedPreferences.Editor editor = sharedPrefs.edit();
            Gson gson = new Gson();

            String json = gson.toJson(arrayList);

            editor.putString("near_by_user", json);
            editor.commit();
            startActivity(bundle);
            return true;
        }
        else if (id == R.id.action_invite){
            shareWithFriends();
            return true;

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.menu_map, menu);
        MenuItem item = menu.findItem(R.id.action_search);
    }

    @Override
    public void onGetAllUsersSuccess(List<User> users) {
        this.users=users;
        for (int i=0;i<users.size();i++) {
            users.get(i).setDistance(Double.parseDouble(distance(Double.parseDouble(latitude),Double.parseDouble(longitude),Double.parseDouble(users.get(i).lat),Double.parseDouble(users.get(i).lon))));
            NearByUser nearByUser = new NearByUser();
            nearByUser.setFirstname(users.get(i).firstname);
            nearByUser.setLastname(users.get(i).lastname);
            nearByUser.setEmail(users.get(i).email);
            nearByUser.setPhone(users.get(i).phoneNumber);
            nearByUser.setImage(users.get(i).profilePic);
            nearByUser.setLongitude(users.get(i).lon);
            nearByUser.setLatitude(users.get(i).lat);
            nearByUser.setDistanceunit("km");
            nearByUser.setStatus(users.get(i).last_seen);
            Log.e("status",users.get(i).last_seen);
            nearByUser.setDistance(distance(Double.parseDouble(latitude),Double.parseDouble(longitude),Double.parseDouble(users.get(i).lat),Double.parseDouble(users.get(i).lon))+"");
            arrayList.add(nearByUser);
        }


//        Log.e("users",users.get(0).firstname);
        ProgressDialogBox.disMissDailog();
        allUserAdapter = new AllUserAdapter(arrayList,getActivity(),users);
        recyclerView.setAdapter(allUserAdapter);
        allUserAdapter.notifyDataSetChanged();
        Collections.sort(users);
        User user=new User();
        user.firstname="Invite Friends";
        users.add(user);
        allUserAdapter.notifyDataSetChanged();
    }

    @Override
    public void onGetAllUsersFailure(String message) {
        Toast.makeText(getActivity(), "Error: " + message, Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onGetChatUsersSuccess(List<User> users) {

    }

    @Override
    public void onGetChatUsersFailure(String message) {

    }

    @Override
    public void onItemClicked(RecyclerView recyclerView, int position, View v) {
        if (position!=allUserAdapter.getItemCount()-1) {
            User nearByUser = users.get(position);
            databaseHelper.deleteContact(nearByUser.email);
            allUserAdapter.notifyDataSetChanged();
            Prefrence.saveString(getActivity(), KEY_OTHER_CHAT_IMAGE, nearByUser.profilePic);
            Prefrence.saveString(getActivity(), KEY_EMAIL, allUserAdapter.getUser(position).email);
            Prefrence.saveString(getActivity(), "uid", nearByUser.uid);
            Intent in = new Intent(getActivity(), ChatFragment.class);
            in.putExtra(ARG_RECEIVER, nearByUser.email);
            in.putExtra("last_seen", allUserAdapter.getUser(position).last_seen);
            in.putExtra(ARG_RECEIVER_UID, nearByUser.uid);
            in.putExtra(ARG_FIREBASE_TOKEN, nearByUser.firebaseToken);
            in.putExtra(KEY_FIRST_NAME, nearByUser.firstname);
            startActivity(in);
        }
        else {
shareWithFriends();
        }
    }


    public void shareWithFriends(){
        try {
            Intent i = new Intent(Intent.ACTION_SEND);
            i.setType("text/plain");
            i.putExtra(Intent.EXTRA_SUBJECT, "NearMe");
            String sAux = "\nLet me recommend you this application\n\n";
            sAux = sAux + "https://play.google.com/store/apps/details?id=toryes.nearme \n\n";
            i.putExtra(Intent.EXTRA_TEXT, sAux);
            startActivity(Intent.createChooser(i, "choose one"));
        } catch(Exception e) {
            //e.toString();
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        allUserAdapter.notifyDataSetChanged();
        updateLastSeen( FirebaseAuth.getInstance().getCurrentUser().getUid(),"Online");
    }
    @Override
    public void onPause() {
        super.onPause();
        allUserAdapter.notifyDataSetChanged();
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        updateLastSeen( FirebaseAuth.getInstance().getCurrentUser().getUid(),convertTimestamp());
    }

    private void updateLastSeen(String uid, String value) {

        FirebaseDatabase.getInstance()
                .getReference()
                .child(ARG_USERS)
                .child(uid).child("last_seen").setValue(value);
        Prefrence.saveString(getActivity(),"last_seen",value);
       allUserAdapter.notifyDataSetChanged();

    }

    public String convertTimestamp(){
        Date d = new Date();
        Log.e("date",d.toString());
        SimpleDateFormat formatter = new SimpleDateFormat("hh:mm a");

        // Create a calendar object that will convert the date and time value in milliseconds to date.
        Calendar calendar = Calendar.getInstance();
        return calendar.getTimeInMillis()+"";

    }

    private String distance(double lat1, double lon1, double lat2, double lon2) {
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1))
                * Math.sin(deg2rad(lat2))
                + Math.cos(deg2rad(lat1))
                * Math.cos(deg2rad(lat2))
                * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;
//        double earthRadius = 6371000; //meters
//        double dLat = Math.toRadians(lat2-lat1);
//        double dLng = Math.toRadians(lon2-lon1);
//        double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
//                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
//                        Math.sin(dLng/2) * Math.sin(dLng/2);
//        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
//        float dist = (float) (earthRadius * c)/1000;
        DecimalFormat decimalFormat=new DecimalFormat("##.####",new DecimalFormatSymbols(Locale.US));

        return decimalFormat.format(dist);
    }

    private double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    private double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
    }
    public void scheduleOnline() {
        final Handler handler=new Handler();
        final int FIVE_SECONDS = 10000;
        handler.postDelayed(new Runnable() {
            public void run() {
                getUsers();
                allUserAdapter.notifyDataSetChanged();
                 // this method will contain your almost-finished HTTP calls
                handler.postDelayed(this, FIVE_SECONDS);
            }
        }, FIVE_SECONDS);
    }


    public void GetContactsIntoArrayList(){

        cursor = getActivity().getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,null, null, null);

        while (cursor.moveToNext()) {

         String   name = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));

           String phonenumber = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

            Log.e("phone",name + " "  + ":" + " " + phonenumber);
        }

        cursor.close();

    }
}
