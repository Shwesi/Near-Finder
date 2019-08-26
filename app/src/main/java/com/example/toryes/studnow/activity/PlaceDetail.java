package com.example.toryes.studnow.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.toryes.studnow.R;
import com.example.toryes.studnow.adapter.PlaceDetailAdapter;
import com.example.toryes.studnow.bin.NearModel;
import com.example.toryes.studnow.bin.Review;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;


public class PlaceDetail extends AppCompatActivity {
ImageView imageView;
    TextView tvPlaceName,tvAddress,tvNumber,tvTotalReview;
    RatingBar ratingBar;
    RecyclerView recyclerView;
    String placeid;
    PlaceDetailAdapter placeDetailAdapter;
    ArrayList<Review> arrayList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_detail);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        tvAddress= (TextView) findViewById(R.id.tvPlaceName);
        tvPlaceName= (TextView) findViewById(R.id.tvAddress);
        tvNumber= (TextView) findViewById(R.id.tvPhone);
        tvTotalReview= (TextView) findViewById(R.id.tvTotalReviews);
        ratingBar= (RatingBar) findViewById(R.id.ratingBar);
        arrayList=new ArrayList<>();
        recyclerView= (RecyclerView) findViewById(R.id.recyclerview);
        imageView= (ImageView) findViewById(R.id.imageView);
        Glide.with(PlaceDetail.this).load(getIntent().getStringExtra("imagepath")).placeholder(R.drawable.place_holder).into(imageView);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setLayoutManager(new LinearLayoutManager(PlaceDetail.this));
        placeDetailAdapter=new PlaceDetailAdapter(arrayList,PlaceDetail.this);
        recyclerView.setAdapter(placeDetailAdapter);
        callDetailsApi();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId()==android.R.id.home)
            super.onBackPressed();
        return super.onOptionsItemSelected(item);
    }

    public void callDetailsApi(){
        AsyncHttpClient client = new AsyncHttpClient();
        client.setTimeout(30000);
        //post data in json format
        // getUrl() making the url to get nearby resultr
        RequestParams loginCerendentail = new RequestParams();
        client.post(PlaceDetail.this,"https://maps.googleapis.com/maps/api/place/details/json?placeid="+getIntent().getStringExtra("placeid")+"&key="+getResources().getString(R.string.google_api_key_default),loginCerendentail, new AsyncHttpResponseHandler() {
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
                    parsePlaceDetail(str);


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
    public void parsePlaceDetail(String response){

        try {
            JSONObject jsonObject=new JSONObject(response);
            JSONObject jsonObject1=jsonObject.getJSONObject("result");
            setTitle(jsonObject1.getString("name"));
            if (jsonObject1.has("reviews")){
                JSONArray jsonArray=jsonObject1.getJSONArray("reviews");
//                Review review = new Review();
//                review.setTotalrating(jsonObject1.getString("rating"));
//                review.setTotalReview(jsonObject1.getString(jsonArray.length()+""));
//                review.setPhonenumber(jsonObject1.getString("international_phone_number"));
//                review.setPlaceName(jsonObject1.getString("name"));
//                review.setAddress(jsonObject1.getString("formatted_address"));
                tvTotalReview.setText(jsonArray.length()+" Reviews");
                if (jsonObject1.has("international_phone_number"))
                    tvNumber.setText(jsonObject1.getString("international_phone_number"));
                tvAddress.setText(jsonObject1.getString("formatted_address"));
                tvPlaceName.setText(jsonObject1.getString("name"));
                if (jsonObject1.has("rating"))
                    ratingBar.setRating(Float.parseFloat(jsonObject1.getString("rating")));
                for (int i=0;i<jsonArray.length();i++) {
                    JSONObject jsonObject2=jsonArray.getJSONObject(i);
                    Review review = new Review();
                    review.setDetails(jsonObject2.getString("text"));
                    review.setImage(jsonObject2.getString("profile_photo_url"));
                    review.setRating(jsonObject2.getString("rating"));
                    review.setName(jsonObject2.getString("author_name"));

                    arrayList.add(review);
                }
                placeDetailAdapter.notifyDataSetChanged();
            }
            else {
                tvTotalReview.setText(" 0 Reviews");
                if (jsonObject1.has("international_phone_number"))
                    tvNumber.setText(jsonObject1.getString("international_phone_number"));
                tvAddress.setText(jsonObject1.getString("formatted_address"));
                tvPlaceName.setText(jsonObject1.getString("name"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}
