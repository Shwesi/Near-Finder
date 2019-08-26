package com.example.toryes.studnow.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.toryes.studnow.FirebaseChatMainApp;
import com.example.toryes.studnow.R;
import com.example.toryes.studnow.Utils.Constants;
import com.example.toryes.studnow.Utils.NotificationUtils;
import com.example.toryes.studnow.Utils.Prefrence;
import com.example.toryes.studnow.bin.LoginCredentials;
import com.example.toryes.studnow.bin.Members;
import com.example.toryes.studnow.fragment.AboutFragment;
import com.example.toryes.studnow.fragment.AllUserFragment;
import com.example.toryes.studnow.fragment.MapFragment;
import com.example.toryes.studnow.fragment.MyProfile;
import com.example.toryes.studnow.fragment.PrivacyFragment;
import com.example.toryes.studnow.fragment.UpdateNumber;
import com.example.toryes.studnow.service.AppKilled;
import com.example.toryes.studnow.service.LocationTracker;
import com.facebook.login.LoginManager;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,Constants {
    private static final String TAG = MainActivity.class.getSimpleName();
    private BroadcastReceiver mRegistrationBroadcastReceiver;
    private TextView txtRegId, txtMessage;
    AdView mAdView;
    public static CircleImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        startService(new Intent(HomeActivity.this, AppKilled.class));
//        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        mAdView = (AdView) findViewById(R.id.adView);
        // showing add
        showAdd();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        //setting icontintlist null to show ny own icon from drawable
        navigationView.setItemIconTintList(null);
        if (!Prefrence.getBoolen(HomeActivity.this,"native")) {
            Menu menu = navigationView.getMenu();
            MenuItem target = menu.findItem(R.id.nav_profile);
            MenuItem targetUpdateNum = menu.findItem(R.id.nav_update_profile);
            targetUpdateNum.setVisible(true);
            target.setVisible(false);
        }
        else
        {
            Menu menu = navigationView.getMenu();
            MenuItem target = menu.findItem(R.id.nav_profile);
            MenuItem targetUpdateNum = menu.findItem(R.id.nav_update_profile);
            targetUpdateNum.setVisible(false);
            target.setVisible(true);
        }
        // getting header of drawer
        View header=navigationView.getHeaderView(0);
        TextView tv= (TextView) header.findViewById(R.id.header_name);
         imageView= (CircleImageView) header.findViewById(R.id.profilePic);
        // getting name and profile image from login ceredentail
        LoginCredentials loginCredentials= Prefrence.getLoginCeredential(HomeActivity.this);
        if (loginCredentials!=null)
        tv.setText(loginCredentials.getFirst_name()+" "+loginCredentials.getLast_name());
        else{
            Prefrence.saveBollen(HomeActivity.this,LOGIN_SUCCES,false);
            startActivity(new Intent(HomeActivity.this,LoginActvity.class));
            finish();
        }

        Glide.with(this).load(Prefrence.getString(HomeActivity.this,"imageUrl")).diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true).into(imageView);
//        Picasso.with(HomeActivity.this)
//                .load(Prefrence.getString(HomeActivity.this,"imageUrl"))
//                .into(imageView);

        Fragment fragment=new DashBoard();
        FragmentManager fm=getSupportFragmentManager();
        FragmentTransaction ft=fm.beginTransaction();
        ft.replace(R.id.container,fragment).commit();
        setTitle(getResources().getString(R.string.find_near));
        //register receiver here to receive push notification
        registerBraoadcastReceiver();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //updating last seen
        updateLatSeen(FirebaseAuth.getInstance().getCurrentUser().getUid(),convertTimestamp());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);

        return true;
    }
    public String convertTimestamp(){
        Date d = new Date();
        Log.e("date",d.toString());
        SimpleDateFormat formatter = new SimpleDateFormat("hh:mm a");

        // Create a calendar object that will convert the date and time value in milliseconds to date.
        Calendar calendar = Calendar.getInstance();
        return calendar.getTimeInMillis()+"";

    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();


        Fragment fragment=null;
        FragmentManager fm=getSupportFragmentManager();
        FragmentTransaction ft=fm.beginTransaction();
        Bundle bundle=new Bundle();
        String placeArr[]=getResources().getStringArray(R.array.place_type);
        if (id == R.id.nav_profile) {
            if(fragment != null)
                getSupportFragmentManager().beginTransaction().remove(fragment).commit();
           fragment=new MyProfile();
            ft.replace(R.id.container,fragment).commit();
            setTitle(getResources().getString(R.string.my_profile));
            // Handle the camera action
        }
        if (id == R.id.nav_update_profile) {
            if(fragment != null)
                getSupportFragmentManager().beginTransaction().remove(fragment).commit();
            fragment=new UpdateNumber();
            ft.replace(R.id.container,fragment).commit();
            setTitle(getResources().getString(R.string.updaet_number));
            // Handle the camera action
        }
        else if (id == R.id.nav_friend) {
            if(fragment != null)
                getSupportFragmentManager().beginTransaction().remove(fragment).commit();
            fragment=new AllUserFragment();
            ft.replace(R.id.container,fragment).commit();
            setTitle(getResources().getString(R.string.friends));

        }
         else if (id == R.id.nav_find_nearme) {
            fragment=new DashBoard();
            ft.replace(R.id.container,fragment).commit();
            setTitle(getResources().getString(R.string.find_near));

        }
        else if (id == R.id.nav_policy) {
            fragment=new PrivacyFragment();
            ft.replace(R.id.container,fragment).commit();

        }
 else if (id == R.id.nav_about) {

            fragment=new AboutFragment();
            ft.replace(R.id.container,fragment).commit();

        }
        else if (id == R.id.nav_more) {
            moreApp();
        } else if (id == R.id.nav_rate) {
            rateUs();
        }

        else if (id == R.id.nav_web) {
            website();
        }
//        else if (id == R.id.nav_restaurant) {
//            bundle.putString(KEY_PLACE,placeArr[3]);
//            fragment=new MapFragment();
//            fragment.setArguments(bundle);
//            ft.replace(R.id.container,fragment).commit();
//
//        }
//        else if (id == R.id.nav_airport) {
//            bundle.putString(KEY_PLACE,placeArr[4]);
//            fragment=new MapFragment();
//            fragment.setArguments(bundle);
//            ft.replace(R.id.container,fragment).commit();
//
//        } else if (id == R.id.nav_more) {
////            bundle.putString(KEY_PLACE,placeArr[5]);
//            fragment=new MapFragment();
////            fragment.setArguments(bundle);
//            ft.replace(R.id.container,fragment).commit();
//
//        }
 else if (id == R.id.nav_logout
                ) {
            Prefrence.clearPref(HomeActivity.this);
            LoginManager.getInstance().logOut();

            startActivity(new Intent(HomeActivity.this,LoginActvity.class));
            finish();

        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    public void registerBraoadcastReceiver(){
        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                // checking for type intent filter
                if (intent.getAction().equals(REGISTRATION_COMPLETE)) {
                    // gcm successfully registered
                    // now subscribe to `global` topic to receive app wide notifications
                    FirebaseMessaging.getInstance().subscribeToTopic(TOPIC_GLOBAL);

                    Prefrence.displayFirebaseRegId(HomeActivity.this);

                } else if (intent.getAction().equals(PUSH_NOTIFICATION)) {
                    // new push notification is received

                    String message = intent.getStringExtra("message");

                    Toast.makeText(getApplicationContext(), "Push notification: " + message, Toast.LENGTH_LONG).show();

//                    txtMessage.setText(message);
                }
            }
        };

        Prefrence.displayFirebaseRegId(HomeActivity.this);
    }


    @Override
    protected void onResume() {
        super.onResume();
        FirebaseChatMainApp.setChatActivityOpen(true);
        // register GCM registration complete receiver
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(REGISTRATION_COMPLETE));

        // register new push message receiver
        // by doing this, the activity will be notified each time a new message arrives
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(PUSH_NOTIFICATION));

        // clear the notification area when the app is opened
        NotificationUtils.clearNotifications(getApplicationContext());
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        FirebaseChatMainApp.setChatActivityOpen(false);
        super.onPause();
    }

    public void showAdd() {
            AdRequest adRequest = new AdRequest.Builder()
                    .build();
            mAdView.loadAd(adRequest);
        }

    private void updateLatSeen(String uid, String value) {
//        FirebaseDatabase.getInstance()
//                .getReference()
//                .child(ARG_USERS)
//                .child(uid)
//                .child(ARG_FIREBASE_TOKEN)
//                .setValue(token);
        FirebaseDatabase.getInstance()
                .getReference()
                .child(ARG_USERS)
                .child(uid).child("last_seen").setValue(value);
        Prefrence.saveString(HomeActivity.this,"last_seen",value);


    }

    public void rateUs(){
        final String appPackageName = getPackageName(); // getPackageName() from Context or Activity object
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
        } catch (android.content.ActivityNotFoundException anfe) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
        }
    }

    public void moreApp(){
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/developer?id=Troyes%20IT%20Services%20Pvt.%20Ltd&hl=en")));

    }
    public void website(){
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.troyesitservices.com/")));
    }
}
