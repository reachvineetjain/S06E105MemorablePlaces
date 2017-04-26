package com.nehvin.s06e105memorableplaces;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;

public class MainActivity extends AppCompatActivity {

    Intent mapIntent=null;
    static List<String> placeList = new ArrayList<String>();
    static List<LatLng> location = new ArrayList<LatLng>();
    static ArrayAdapter<String> arrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ListView myListView = (ListView) findViewById(R.id.myListView);
        mapIntent = new Intent(getApplicationContext(),MapsActivity.class);
        placeList.add("Add a new Place ... ");
        location.add(new LatLng(0,0));

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
}