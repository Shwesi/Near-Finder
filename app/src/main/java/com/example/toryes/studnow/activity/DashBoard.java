package com.example.toryes.studnow.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.toryes.studnow.R;
import com.example.toryes.studnow.Utils.Constants;
import com.example.toryes.studnow.adapter.DashBoardAdapter;
import com.example.toryes.studnow.fragment.AllUserFragment;
import com.example.toryes.studnow.fragment.MapFragment;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;

import java.util.ArrayList;

/**
 * Created by TORYES on 8/10/2017.
 */

public class DashBoard extends Fragment implements DashBoardAdapter.ItemClickListener,Constants {
    ArrayList<String> dashboarArray;
    DashBoardAdapter dashBoardAdapter;
    int PLACE_PICKER_REQUEST = 1;
    String str[];
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_dashboard,null,false);
        getActivity().setTitle(getActivity().getResources().getString(R.string.find_near));
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recyclerview);
        dashboarArray=new ArrayList<>();
        String data[]={"1","2","1","2"};
        // Add or remove nearby places
        // change icon of dashboard and change string or add/remove image and string
        Integer img[]={R.drawable.airport,R.drawable.railway_station,R.drawable.bus_station,R.drawable.parking,R.drawable.atm,R.drawable.cinema,R.drawable.beershop,R.drawable.hospital,R.drawable.club,R.drawable.petrol_pump
        ,R.drawable.friends,R.drawable.shopping,R.drawable.restaurant,R.drawable.temple,R.drawable.salon};
       str=new String[]{"AIRPORT", "RAILWAY STATION", "BUS STATION", "PARKING", "ATM", "CINEMA", "BEER SHOP", "HOSPITAL", "CLUBS", "PETROL PUMPS", "FRIENDS", "SHOPPING MALL", "RESTAURANT", "TEMPLE", "BEAUTY SALON"};
        int numberOfColumns = 3;
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), numberOfColumns));
        dashBoardAdapter = new DashBoardAdapter(getActivity(),str,img);
        //setting cliclk listener on recyclerview item
        dashBoardAdapter.setClickListener(this);
        recyclerView.setAdapter(dashBoardAdapter);
        return view;
    }

    @Override
    public void onItemClick(View view, int position) {
        Intent intent=new Intent(getActivity(), ListNearMe.class);

        switch (position){
            case 0:
                intent.putExtra(KEY_PLACE,"airport");
                startActivity(intent);
                break;
            case 1:
                intent.putExtra(KEY_PLACE,"train_station");

                startActivity(intent);
                break;
            case 2:
                intent.putExtra(KEY_PLACE,"bus_station");
                startActivity(intent);
                break;
            case 3:
                intent.putExtra(KEY_PLACE,"parking");
                startActivity(intent);
                break;
            case 4:
                intent.putExtra(KEY_PLACE,"atm");
                startActivity(intent);
                break;
            case 5:
                intent.putExtra(KEY_PLACE,"movie_theater");
                startActivity(intent);
                break;
            case 6:
                intent.putExtra(KEY_PLACE,"liquor_store");
                startActivity(intent);
                break;
            case 7:
                intent.putExtra(KEY_PLACE,"hospital");
                startActivity(intent);
                break;

            case 8:
                intent.putExtra(KEY_PLACE,"night_club");
                startActivity(intent);
                break;
            case 9:
                intent.putExtra(KEY_PLACE,"gas_station");
                startActivity(intent);
                break;
            case 10:
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.container,new AllUserFragment()).commit();

//                intent.putExtra(KEY_PLACE,"bar");
//                startActivity(intent);
                break;
            case 11:
                intent.putExtra(KEY_PLACE,"shopping_mall");
                startActivity(intent);
                break;

            case 12:
                intent.putExtra(KEY_PLACE,"restaurant");
                startActivity(intent);
                break;
            case 13:
                intent.putExtra(KEY_PLACE,"hindu_temple");
                startActivity(intent);
                break;

            case 14:
                intent.putExtra(KEY_PLACE,"beauty_salon");
                startActivity(intent);
                break;

        }

    }


}
