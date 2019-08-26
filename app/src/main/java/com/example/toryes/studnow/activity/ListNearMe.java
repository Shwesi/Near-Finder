package com.example.toryes.studnow.activity;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.toryes.studnow.R;
import com.example.toryes.studnow.Utils.Constants;
import com.example.toryes.studnow.adapter.ArrayAdapterSearchView;
import com.example.toryes.studnow.adapter.NearByItemAdapter;
import com.example.toryes.studnow.adapter.NearMeAdapter;
import com.example.toryes.studnow.bin.LoginCredentials;
import com.example.toryes.studnow.bin.Members;
import com.example.toryes.studnow.bin.NearByUser;
import com.example.toryes.studnow.bin.NearModel;
import com.example.toryes.studnow.fragment.MapFragment;
import com.example.toryes.studnow.placeapi.DataParser;
import com.example.toryes.studnow.placeapi.GetNearbyPlacesData;
import com.example.toryes.studnow.service.LocationTracker;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;
import cz.msebera.android.httpclient.message.BasicHeader;
import cz.msebera.android.httpclient.protocol.HTTP;


public class ListNearMe extends AppCompatActivity implements Constants,NearMeAdapter.OnCardClickListner{
    ArrayList<Members> arrayList=new ArrayList<>();
    private int PROXIMITY_RADIUS = 10000;
    double latitude=0,longitude=0;
    ArrayList<NearModel> nearByList;
    String place;
    private Hashtable<String, String> markers;
    Marker marker;
    LoginCredentials loginCredentials;
    RecyclerView recyclerView;
    NearMeAdapter nearMeAdapter;
    Spinner spinner;
    ArrayAdapter<String> spinnerArrayAdapter;
    ImageView imageView;
    AdView mAdView;
    TextView tv;
    String str[]={"Select Distance","In 1 Km Distance","In 2 Km Distance","In 3 Km Distance","In 4 Km Distance","In 5 Km Distance","In 6 Km Distance","In 7 Km Distance","In 8 Km Distance","In 9 Km Distance","In 10 Km Distance","In 15 Km Distance","In 20 Km Distance","In 25 Km Distance","In 30 Km Distance"};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_near_me);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        recyclerView= (RecyclerView) findViewById(R.id.recyclerview);
        mAdView = (AdView) findViewById(R.id.adView);
        //showing add
        showAdd();
        spinner= (Spinner) findViewById(R.id.spinner);
        imageView= (ImageView) findViewById(R.id.map);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setLayoutManager(new LinearLayoutManager(ListNearMe.this));
        // fecthing current longitude and lattitude
        LocationTracker locationTracker=new LocationTracker(ListNearMe.this);
        if(locationTracker.canGetLocation()){
            latitude=locationTracker.getLatitude();
            longitude=locationTracker.getLongitude();
            Log.e("loc",longitude+""+latitude);

        }

        Intent arguments = getIntent();
        String arr[]=getResources().getStringArray(R.array.place_type_name);
        place=arguments.getStringExtra(KEY_PLACE);
        if (place.contains("_")) {
            String title = place.replace("_", " ");
            for (int i=0;i<arr.length;i++)
                if (title.equalsIgnoreCase("gas station"))
                    setTitle("Petrol Pump"+" Near Me ");
                else  if (arr[i].equalsIgnoreCase(title))
//                title = title.substring(0,1).toUpperCase()+ title.substring(1).toLowerCase();
                    setTitle(arr[i]+" Near Me ");

        }
        else {
          String  place1 = place.substring(0,1).toUpperCase()+ place.substring(1).toLowerCase();
            setTitle(place1+" Near Me ");
        }

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(ListNearMe.this, MapFragment.class);
                        intent.putExtra(KEY_PLACE,place);

                        startActivity(intent);
            }
        });
        //calling near me api to get nearby places
//        nearMeAPi();
        //customizing spinner
        setSpinnerCategory();
//        showInterSitialAdd();
    }
    public void showAdd() {
        AdRequest adRequest = new AdRequest.Builder()
                .build();
        mAdView.loadAd(adRequest);
    }

    public void setSpinnerCategory(){
        // Initializing an ArrayAdapter
        spinnerArrayAdapter = new ArrayAdapter<String>(
                this,R.layout.spinner_item,R.id.textview,str){
            @Override
            public boolean isEnabled(int position){
                if(position == 0)
                {
                    // Disable the first item from Spinner
                    // First item will be use for hint
                    return false;
                }
                else
                {

                    return true;
                }
            }
            @Override
            public View getDropDownView(int position, View convertView,
                                        ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                tv = (TextView) view.findViewById(R.id.textview);
                if(position == 0){
                    // Set the hint text color gray
//                    tv.setTextColor(Color.GRAY);
                    tv.setVisibility(View.GONE);
                }
                else {
                    tv.setVisibility(View.VISIBLE);
                    tv.setTextColor(Color.BLACK);
                }
                return view;
            }
        };
        spinnerArrayAdapter.setDropDownViewResource(R.layout.spinner_item);
        spinner.setAdapter(spinnerArrayAdapter);
    spinner.setSelection(10);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                spinnerItem= (String) parent.getItemAtPosition(position);
//                spinnerPos=position;
//                Prefrence.saveString(AddTips.this,"cat_name",spinnerItem);
                // If user change the default selection
                // First item is disable and it is used for hint
                switch (position){
                    case 1:
                        PROXIMITY_RADIUS=1000;
                        nearMeAPi();
                        break;
                    case 2:
                        PROXIMITY_RADIUS=2000;
                        nearMeAPi();
                        break;
                    case 3:
                        PROXIMITY_RADIUS=3000;
                        nearMeAPi();
                        break;
                    case 4:
                        PROXIMITY_RADIUS=4000;
                        nearMeAPi();
                        break;
                    case 5:
                        PROXIMITY_RADIUS=5000;
                        nearMeAPi();
                        break;
                    case 6:
                        PROXIMITY_RADIUS=6000;
                        nearMeAPi();
                        break;
                    case 7:
                        PROXIMITY_RADIUS=7000;
                        nearMeAPi();
                        break;
                    case 8:
                        PROXIMITY_RADIUS=8000;
                        nearMeAPi();
                        break;
                    case 9:
                        PROXIMITY_RADIUS=9000;
                        nearMeAPi();
                        break;
                    case 10:
                        PROXIMITY_RADIUS=10000;
                        nearMeAPi();
                        break;
                    case 11:
                        PROXIMITY_RADIUS=15000;
                        nearMeAPi();
                        break;
                    case 12:
                        PROXIMITY_RADIUS=20000;
                        nearMeAPi();
                        break;
                    case 13:
                        PROXIMITY_RADIUS=25000;
                        nearMeAPi();
                        break;
                    case 14:
                        PROXIMITY_RADIUS=30000;
                        nearMeAPi();
                        break;


                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    private String getUrl(double latitude, double longitude, String nearbyPlace,String keyword) {
        nearByList=new ArrayList<>();
        nearMeAdapter=new NearMeAdapter(nearByList,ListNearMe.this);
        recyclerView.setAdapter(nearMeAdapter);
        if (nearbyPlace.contains(" ")) {
            nearbyPlace=nearbyPlace.replace(" ","_");
        }

        StringBuilder googlePlacesUrl = new StringBuilder("https://maps.googleapis.com/maps/api/place/textsearch/json?");
        googlePlacesUrl.append("query="+getCityName());
        googlePlacesUrl.append("&sensor=true");
        googlePlacesUrl.append("&location=" + latitude + "," + longitude);
        googlePlacesUrl.append("&radius=" + PROXIMITY_RADIUS);
        googlePlacesUrl.append("&type=" + nearbyPlace);
        googlePlacesUrl.append("&key=" + getResources().getString(R.string.google_api_key_default));
        Log.d("getUrl", googlePlacesUrl.toString());
        return (googlePlacesUrl.toString());
    }

    
    public String getCityName(){
        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(this, Locale.getDefault());
        String city = null;
        try {
            addresses = geocoder.getFromLocation(latitude, longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
             city = addresses.get(0).getAdminArea();
            city=city.replace(" ","%20");
//          city = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
//             city = addresses.get(0).getLocality();
        } catch (IOException e) {
            e.printStackTrace();
        }

       
        return city;
    }

    public void nearMeAPi(){
        AsyncHttpClient client = new AsyncHttpClient();
        client.setTimeout(30000);
        //post data in json format
        // getUrl() making the url to get nearby resultr
        RequestParams loginCerendentail = new RequestParams();
        client.post(ListNearMe.this, getUrl(latitude,longitude,place,"airport"),loginCerendentail, new AsyncHttpResponseHandler() {
            @Override
            public void onStart() {
                // showing progress bar

            }

            @Override
            public void onFinish() {

            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    String str = new String(responseBody, "UTF-8");
                    Log.e("str", str);
                    //parsing nearby data
                    parseNearData(str);

                    //parse the response from server

                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
//                Toast.makeText(CreateTask.this, getResources().getString(R.string.some_wrong), Toast.LENGTH_SHORT).show();

            }

        });
    }


    public void parseNearData(String result){
        List<HashMap<String, String>> nearbyPlacesList = null;
        DataParser dataParser = new DataParser();
        nearbyPlacesList =  dataParser.parse(result);
        // showing nearby places on list
        ShowNearbyPlaces(nearbyPlacesList);
    }

    private void ShowNearbyPlaces(List<HashMap<String, String>> nearbyPlacesList) {
        markers = new Hashtable<String, String>();
        String str="something";
        for (int i = 0; i < nearbyPlacesList.size(); i++) {
            Log.d("onPostExecute","Entered into showing locations");
            MarkerOptions markerOptions = new MarkerOptions();
            HashMap<String, String> googlePlace = nearbyPlacesList.get(i);
            double lat = Double.parseDouble(googlePlace.get("lat"));
            double lng = Double.parseDouble(googlePlace.get("lng"));
            String placeId = googlePlace.get("place_id");
//            LatLng latLngNear=new LatLng(lat,lng);
//            CalculationByDistance(latLng,latLngNear);
            String placeName = googlePlace.get("place_name");
            String vicinity = googlePlace.get("formatted_address");
            String icon= googlePlace.get("icon");
            String rating= googlePlace.get("rating");
            float[] results = new float[1];
            LatLng latLng = new LatLng(lat, lng);
//            markerOptions.position(latLng);
            Location.distanceBetween(latLng.latitude,latLng.longitude,lat,lng,results);
            // distance() calculating distance from lattitude and longitude
            Log.e("pac",placeName+"  "+vicinity+"  "+results+" "+distance(lat,lng,latitude,longitude)+"https://maps.googleapis.com/maps/api/place/photo?"+icon+"&key="+getResources().getString(R.string.google_api_key_default)+"") ;
            NearModel nearByUser=new NearModel();
            nearByUser.setDistance(distance(lat,lng,latitude,longitude));
            nearByUser.setLat(lat);
            nearByUser.setDistanceInLong(distanceInLong(lat,lng,latitude,longitude));
            nearByUser.setLon(lng);
            nearByUser.setVictinity(vicinity);
            nearByUser.setPlacename(placeName);
            nearByUser.setIcon("https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&photoreference="+icon+"&key="+ getResources().getString(R.string.google_api_key_default));
            nearByUser.setRating(rating);
            nearByUser.setPlaceId(placeId);
//           if (!str.matches(vicinity))
            nearByList.add(nearByUser);
            str=vicinity;


        }
        // sorting list according to distance
        Collections.sort(nearByList, new Comparator<NearModel>() {
            @Override
            public int compare(NearModel o1, NearModel o2) {
                return o1.getDistance().compareTo(o2.getDistance());
            }
        });
        nearMeAdapter.setOnCardClickListner(this);
        nearMeAdapter.notifyDataSetChanged();
    }

    private String distance(double lat1, double lon1, double lat2, double lon2) {
        Location locationA = new Location("point A");
        locationA.setLatitude(lat1);
        locationA.setLongitude(lon1);
        Location locationB = new Location("point B");
        locationB.setLatitude(lat2);
        locationB.setLongitude(lon2);
        double dist;
        String distanceStr;
        DecimalFormat newFormat = new DecimalFormat("###.##",new DecimalFormatSymbols(Locale.US));
       double distance = locationA.distanceTo(locationB);
        if (distance>1000) {
            dist = distance / 1000;
            distanceStr = Double.parseDouble(newFormat.format(dist)) + " Km";
        }
        else
            distanceStr=Double.parseDouble(newFormat.format(distance)) + " m";
//        double theta = lon1 - lon2;
//        double dist = Math.sin(deg2rad(lat1))
//                * Math.sin(deg2rad(lat2))
//                + Math.cos(deg2rad(lat1))
//                * Math.cos(deg2rad(lat2))
//                * Math.cos(deg2rad(theta));
//        dist = Math.acos(dist);
//        dist = rad2deg(dist);
//        dist = dist * 60 * 1.1515;
//        DecimalFormat newFormat = new DecimalFormat("###.##");
//        dist= Double.parseDouble(newFormat.format(distance));
        return distanceStr;
    }
    private double distanceInLong(double lat1, double lon1, double lat2, double lon2) {
        Location locationA = new Location("point A");
        locationA.setLatitude(lat1);
        locationA.setLongitude(lon1);
        Location locationB = new Location("point B");
        locationB.setLatitude(lat2);
        locationB.setLongitude(lon2);
        double distance = locationA.distanceTo(locationB);
        return distance;
    }
    private double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    private double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
    }

    @Override
    public void OnCardClicked(View view, int position) {
        NearModel nearModel = nearByList.get(position);
        if (view.getId()==R.id.direction){
            final Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://maps.google.com/maps?" + "saddr="+ nearModel.getLat() + "," + nearModel.getLon() + "&daddr=" + latitude + "," + longitude));
        intent.setClassName("com.google.android.apps.maps","com.google.android.maps.MapsActivity");
        startActivity(intent);
        }
        else {

            Intent intent = new Intent(ListNearMe.this, PlaceDetail.class);
            intent.putExtra("placeid", nearModel.getPlaceId());
            intent.putExtra("imagepath", nearModel.getIcon());
            startActivity(intent);
        }
//        final Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://maps.google.com/maps?" + "saddr="+ nearModel.getLat() + "," + nearModel.getLon() + "&daddr=" + latitude + "," + longitude));
//        intent.setClassName("com.google.android.apps.maps","com.google.android.maps.MapsActivity");
//        startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId()==android.R.id.home)
            super.onBackPressed();
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
// showing search bar on action bar
        menu.clear();
        getMenuInflater().inflate(R.menu.menu_search, menu);
        MenuItem item = menu.findItem(R.id.action_search);
        final ArrayAdapterSearchView searchView = new ArrayAdapterSearchView(ListNearMe.this);
        MenuItemCompat.setShowAsAction(item, MenuItemCompat.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW | MenuItemCompat.SHOW_AS_ACTION_IF_ROOM);
        MenuItemCompat.setActionView(item, searchView);
        final ArrayAdapter<String> arrayAdapter=new ArrayAdapter<String>(ListNearMe.this,R.layout.spinner_item,R.id.textview,getResources().getStringArray(R.array.place_type_name));
        searchView.setAdapter(arrayAdapter);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchView.setText(query);
                place=query;
                nearMeAPi();
                return false;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        searchView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (arrayAdapter.getItem(position).contains(" ")) {
                    String str = arrayAdapter.getItem(position).replace(" ", "_");
                    if (str.matches("Petrol_Pump"))
                        str="gas_station";
                    searchView.setText(arrayAdapter.getItem(position));
                    place=str;
                    nearMeAPi();
                }
                else {
                    String str = arrayAdapter.getItem(position);
                    searchView.setText(arrayAdapter.getItem(position));
                    place=str;
                    nearMeAPi();
                }

            }
        });
        return true;
    }

    public void showInterSitialAdd(){
      final InterstitialAd interstitialAd = new InterstitialAd(this);

        interstitialAd.setAdUnitId(getResources().getString(R.string.interstitial_full_screen));
        AdRequest adRequest = new AdRequest.Builder().build();

        interstitialAd.loadAd(adRequest);

        interstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {

                if (interstitialAd.isLoaded()) {
                    interstitialAd.show();
                }

            }

            @Override
            public void onAdOpened() {


            }

            @Override
            public void onAdFailedToLoad(int errorCode) {

            }
        });
    }
}
