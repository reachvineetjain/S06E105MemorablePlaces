package com.nehvin.s06e105memorableplaces;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    Intent mapIntent=null;
    static List<String> placeList = new ArrayList<String>();
    static List<LatLng> location = new ArrayList<LatLng>();
    static ArrayAdapter<String> arrayAdapter;
    private SharedPreferences sharedPreferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedPreferences = this.getSharedPreferences("com.nehvin.s06e105memorableplaces",Context.MODE_PRIVATE);
        ListView myListView = (ListView) findViewById(R.id.myListView);
        mapIntent = new Intent(getApplicationContext(),MapsActivity.class);

        restorePlaces();
        if(placeList.size() <= 0 && location.size() <= 0) {
            placeList.add("Add a new Place ... ");
            location.add(new LatLng(0, 0));
        }

        arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, placeList);
        myListView.setAdapter(arrayAdapter);

        myListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

                mapIntent.putExtra("placeName", position);
                startActivity(mapIntent);
            }
        });
    }

    private void restorePlaces() {
        ArrayList<String> latitude = new ArrayList<>();
        ArrayList<String> longitude = new ArrayList<>();

        placeList.clear();
        latitude.clear();
        longitude.clear();
        location.clear();

        try {
            placeList = (ArrayList<String>) ObjectSerializer.deserialize(sharedPreferences.getString("PlaceList",ObjectSerializer.serialize(new ArrayList<String>())));
            latitude = (ArrayList<String>)ObjectSerializer.deserialize(sharedPreferences.getString("latitude",ObjectSerializer.serialize(new ArrayList<String>())));
            longitude = (ArrayList<String>)ObjectSerializer.deserialize(sharedPreferences.getString("longitude",ObjectSerializer.serialize(new ArrayList<String>())));
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(placeList.size() > 0 && latitude.size()>0 && longitude.size()>0){
            if(placeList.size() == latitude.size() && latitude.size() == longitude.size()){
                for (int i = 0 ; i < latitude.size(); i++)
                {
                    location.add(new LatLng(Double.parseDouble(latitude.get(i)),Double.parseDouble(longitude.get(i))));
                }
            }
        }
    }


}