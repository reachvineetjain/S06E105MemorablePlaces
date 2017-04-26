package com.nehvin.s06e105memorableplaces;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private LocationManager locMgr;
    private LocationListener locListner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 1 && permissions.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            startListening();
        }
    }

    private void startListening() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locMgr.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locListner);
        }
        Location lastUnkownLocation = fetchBestLocation();
        if (lastUnkownLocation != null) {
            updateCurrentLoc(lastUnkownLocation);
        }
    }

    private Location fetchBestLocation() {
        Location locationGPS = null;
        Location locationNetwork = null;

        // get both but return more accurate of GPS & network location
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationGPS = locMgr.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            locationNetwork = locMgr.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        }

        if (locationGPS == null && locationNetwork == null) return null;
        else
            if (locationGPS == null) return locationNetwork;
        else
            if (locationNetwork == null) return locationGPS;
        else
            return (locationGPS.getAccuracy() < locationNetwork.getAccuracy() ? locationGPS : locationNetwork);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        Intent intent = getIntent();

        if (intent.getIntExtra("placeName", 0) == 0) {
            // Add a marker in Sydney and move the camera
            locMgr = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

            locListner = new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {

                    if (location != null) {
                        updateCurrentLoc(location);
                    }
                }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {
                }

                @Override
                public void onProviderEnabled(String provider) {
                }

                @Override
                public void onProviderDisabled(String provider) {
                }
            };

            mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
                @Override
                public void onMapLongClick(LatLng latLng) {
//                    String nameOfPlace = ;
                    mMap.addMarker(new MarkerOptions()
                            .position(latLng)
                            .title(getAddressOnMarker(latLng))
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
                }
            });

            if (Build.VERSION.SDK_INT < 23) {
                locMgr.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locListner);
            } else {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                } else {
                    locMgr.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locListner);
                    Location location = fetchBestLocation();
                    updateCurrentLoc(location);
                }
            }
        }
        else
        {
            Location currentLoc = new Location(LocationManager.GPS_PROVIDER);
            currentLoc.setLatitude(MainActivity.location.get(intent.getIntExtra("placeName", 0)).latitude);
            currentLoc.setLongitude(MainActivity.location.get(intent.getIntExtra("placeName", 0)).longitude);
            updateCurrentLoc(currentLoc);
        }
    }

    private void updateCurrentLoc(Location lastUnkownLocation) {
        LatLng currentLocation = new LatLng(lastUnkownLocation.getLatitude(), lastUnkownLocation.getLongitude());

        String nameOfPlace = getAddressOnMarker(lastUnkownLocation);

        mMap.clear();
        mMap.addMarker(new MarkerOptions().position(currentLocation).title(nameOfPlace));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 15));
    }

    @NonNull
    private String getAddressOnMarker(LatLng lastUnkownLatLng) {
        Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
        String nameOfPlace = "";
        try {
            List<Address> address = geocoder.getFromLocation(lastUnkownLatLng.latitude, lastUnkownLatLng.longitude, 1);

            if (address != null && address.size() > 0) {
                if (address.get(0).getThoroughfare() != null )
                {
                    if (address.get(0).getSubThoroughfare() != null )
                    {
                        nameOfPlace += address.get(0).getSubThoroughfare()+" ";
                    }
                    nameOfPlace += address.get(0).getThoroughfare();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(nameOfPlace == "")
        {
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm yyyy-MM-dd");
            nameOfPlace = sdf.format(new Date());
        }

        MainActivity.placeList.add(nameOfPlace);
        MainActivity.location.add(lastUnkownLatLng);
        MainActivity.arrayAdapter.notifyDataSetChanged();

        return nameOfPlace;
    }

    @NonNull
    private String getAddressOnMarker(Location lastUnkownLocation) {
        Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
        String nameOfPlace = "";
        try {
            List<Address> address = geocoder.getFromLocation(lastUnkownLocation.getLatitude(), lastUnkownLocation.getLongitude(), 1);

            if (address != null && address.size() > 0) {
                if (address.get(0).getThoroughfare() != null )
                {
                    if (address.get(0).getSubThoroughfare() != null )
                    {
                        nameOfPlace += address.get(0).getSubThoroughfare()+" ";
                    }
                    nameOfPlace += address.get(0).getThoroughfare();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(nameOfPlace == "")
        {
            SimpleDateFormat sdf = new SimpleDateFormat("mm:HH yyyyMMdd");
            nameOfPlace = sdf.format(new Date());
        }

        return nameOfPlace;
    }
}