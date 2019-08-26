package com.example.toryes.studnow.fragment;

import android.app.SearchManager;
import android.content.Context;

import android.content.Intent;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.example.toryes.studnow.R;
import com.example.toryes.studnow.Utils.Constants;
import com.example.toryes.studnow.Utils.Prefrence;
import com.example.toryes.studnow.activity.ListNearMe;
import com.example.toryes.studnow.activity.LoginActvity;
import com.example.toryes.studnow.adapter.ArrayAdapterSearchView;
import com.example.toryes.studnow.adapter.MapItemAdapter;
import com.example.toryes.studnow.adapter.NearMeAdapter;
import com.example.toryes.studnow.bin.LoginCredentials;
import com.example.toryes.studnow.bin.Members;
import com.example.toryes.studnow.bin.NearByUser;
import com.example.toryes.studnow.placeapi.GetNearbyPlacesData;
import com.example.toryes.studnow.placeapi.GooglePlacesAutocompleteAdapter;
import com.example.toryes.studnow.service.LocationTracker;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

/**
 * Created by TORYES on 7/1/2017.
 */

public class MapFragment  extends AppCompatActivity implements OnMapReadyCallback,Constants{
    private GoogleMap mMap;
    ArrayList<Members> arrayList=new ArrayList<>();
    private int PROXIMITY_RADIUS = 10000;
    double latitude,longitude;
    ArrayList<NearByUser> customer_list;
    String place;
    private Hashtable<String, String> markers;
    Marker marker;
    LoginCredentials loginCredentials;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map_fragment);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        setHasOptionsMenu(true);
        Intent arguments = getIntent();
        loginCredentials= Prefrence.getLoginCeredential(MapFragment.this);
//        handleIntent(getActivity().getIntent());
        markers = new Hashtable<String, String>();
        LocationTracker locationTracker=new LocationTracker(MapFragment.this);
        if(locationTracker.canGetLocation()){
            latitude=locationTracker.getLatitude();
            longitude=locationTracker.getLongitude();

        }
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(MapFragment.this);
        Gson gson = new Gson();
        String json = sharedPrefs.getString("near_by_user", null);
        Type type = new TypeToken<ArrayList<NearByUser>>() {}.getType();
        customer_list = gson.fromJson(json, type);
//        customer_list = arguments.getParcelableArrayListExtra("near_by_user");
        if (customer_list!=null) {
            for (int i = 0; i < customer_list.size(); i++) {
                NearByUser nearByUser = customer_list.get(i);
                Log.e("hello", nearByUser.getLatitude() + "   " + nearByUser.getLongitude());
                addMemberDetail(nearByUser.getLatitude(), nearByUser.getLongitude(), nearByUser.getFirstname(),nearByUser.getImage(),nearByUser.getPhone(),nearByUser.getDistance(),nearByUser.getDistanceunit());
            }
        }
        place=arguments.getStringExtra(KEY_PLACE);
//        if (place!=null){
//            searchNearByPlace(place);
//        }
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        String arr[]=getResources().getStringArray(R.array.place_type_name);
        // Add a marker in Sydney and move the camera
//        searchNearByPlace(place);
        if (place!=null){
            searchNearByPlace(place);
            if (place.contains("_")) {
                String title = place.replace("_", " ");
                for (int i=0;i<arr.length;i++)
                    if (title.equalsIgnoreCase("gas station"))
                        setTitle("Petrol Pump");
                  else  if (arr[i].equalsIgnoreCase(title))
//                title = title.substring(0,1).toUpperCase()+ title.substring(1).toLowerCase();
                setTitle(arr[i]);

            }
            else {
                place = place.substring(0,1).toUpperCase()+ place.substring(1).toLowerCase();
                setTitle(place);
            }
        }
        else {
            for (int i = 0; i < arrayList.size(); i++) {
                Members members = arrayList.get(i);
                Log.e("memIm",members.getDistance()+"");
                LatLng TutorialsPoint = new LatLng(Double.parseDouble(members.getLatitude()), Double.parseDouble(members.getLongitude()));
                if (members.getLatitude().trim().length() > 0 && members.getLongitude().trim().length() > 0) {
                    final Marker hamburg = googleMap.addMarker(new MarkerOptions().position(TutorialsPoint)
                            .title(members.getName()).snippet(members.getDistance()+" "+members.getdUnit()));

                    markers.put(hamburg.getId(), members.getImg()+"");
                    mMap.setInfoWindowAdapter(new MapItemAdapter(MapFragment.this,markers,marker));
                }
                LatLng latLngNear=new LatLng(latitude,longitude);
                final Marker hamburg = googleMap.addMarker(new MarkerOptions().position(latLngNear).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                        .title("Your Location"));

                markers.put(hamburg.getId(), loginCredentials.getImage()+"");
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLngNear,
                        20));
                mMap.setInfoWindowAdapter(new MapItemAdapter(MapFragment.this,markers,marker));
            }
    }
}
    public void addMemberDetail(String lat,String lon,String name,String img,String phone,String distance,String dUnit){
        Members members=new Members();
        members.setLatitude(lat);
        members.setLongitude(lon);
        members.setName(name);
        members.setImg(img);
        members.setPhone(phone);
        members.setDistance(distance);
        members.setdUnit(dUnit);
        arrayList.add(members);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        menu.clear();
        getMenuInflater().inflate(R.menu.menu_search, menu);
        MenuItem item = menu.findItem(R.id.action_search);
        final ArrayAdapterSearchView searchView = new ArrayAdapterSearchView(getSupportActionBar().getThemedContext());
        MenuItemCompat.setShowAsAction(item, MenuItemCompat.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW | MenuItemCompat.SHOW_AS_ACTION_IF_ROOM);
        MenuItemCompat.setActionView(item, searchView);
        final ArrayAdapter<String> arrayAdapter=new ArrayAdapter<String>(MapFragment.this,R.layout.list_item,getResources().getStringArray(R.array.place_type_name));
        searchView.setAdapter(arrayAdapter);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchView.setText(query+" Near Me");
                searchNearByPlace(query);
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
                  searchView.setText(arrayAdapter.getItem(position)+" Near Me");
                  searchNearByPlace(str);
              }
              else {
                  String str = arrayAdapter.getItem(position);
                  searchView.setText(arrayAdapter.getItem(position)+" Near Me");
                  searchNearByPlace(str);
              }

            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == android.R.id.home) {
            super.onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    private String getUrl(double latitude, double longitude, String nearbyPlace) {
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

    private void showLocations(LatLng latLng,String address){
        MarkerOptions markerOptions = new
                MarkerOptions().position(latLng).title(address);
            mMap.clear();
            mMap.addMarker(markerOptions);
            CameraUpdate cameraPosition = CameraUpdateFactory.newLatLng(latLng);
            mMap.animateCamera(cameraPosition);

    }

    public LatLng getLocationFromAddress(Context context,String strAddress) {

        Geocoder coder = new Geocoder(context);
        List<Address> address;
        LatLng p1 = null;

        try {
            // May throw an IOException
            address = coder.getFromLocationName(strAddress, 5);
            if (address == null) {
                return null;
            }
            Address location = address.get(0);
            location.getLatitude();
            location.getLongitude();

            p1 = new LatLng(location.getLatitude(), location.getLongitude() );

        } catch (IOException ex) {

            ex.printStackTrace();
        }

        return p1;
    }

    public void searchNearByPlace(String place){
        mMap.clear();

//        mMap.setInfoWindowAdapter(new In);
        String url = getUrl(latitude, longitude, place.toLowerCase());
        Object[] DataTransfer = new Object[2];
        DataTransfer[0] = mMap;
        DataTransfer[1] = url;
        Log.d("onClick", url);
        LatLng latLng=new LatLng(latitude,longitude);
        GetNearbyPlacesData getNearbyPlacesData = new GetNearbyPlacesData(latLng,MapFragment.this);
        getNearbyPlacesData.execute(DataTransfer);
//        Toast.makeText(MapFragment.this,"Nearby  "+place, Toast.LENGTH_LONG).show();
    }
}
