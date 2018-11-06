package com.example.upamanyu.stanfordhealthpp;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class DisplayHospActivity extends AppCompatActivity {

    private List<HashMap<String, String>> hospList;
    GoogleMap mMap;

    ArrayList<String> details;
    ListView lv;
    String temp;

    ArrayAdapter<String> adap;

     private class GetPlaceDuration extends AsyncTask<Object, String, String> {

        String googlePlacesData;
        GoogleMap mMap;
        String url, duration, distance, placeName;

        @Override
        protected String doInBackground(Object... objects) {
            mMap = (GoogleMap) objects[0];
            url = (String) objects[1];
            placeName = (String) objects[2];

            DownloadURL downloadURL = new DownloadURL();
            try {
                googlePlacesData = downloadURL.readURL(url);
            } catch (IOException e) {
                e.printStackTrace();
            }

            return googlePlacesData;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            HashMap<String, String> durationList;
            DataParser parser = new DataParser();
            durationList = parser.parseDirections(s);
            duration = durationList.get("duration");
            distance = durationList.get("distance");

            if(duration!=null && distance!=null) {
                temp += placeName + "\n";
                temp += "Distance: " + distance + " \t Travel Time: " + duration;
                details.add(temp);
            }
            adap.notifyDataSetChanged();
            Log.d("JJJJJJJ", temp);
            temp = "";
        }


    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_hosp);

        temp = "";
        details = new ArrayList<>();

        lv = findViewById(R.id.hospLV);
        adap = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, details);
        lv.setAdapter(adap);

        Intent intent = getIntent();
        double lat  = intent.getDoubleExtra("Lat", 0.0);
        double lng  = intent.getDoubleExtra("Lng", 0.0);

        Iterator<HashMap<String, String>> iterator = ((List<HashMap<String, String>>) intent.getSerializableExtra("Places")).iterator();

        while(iterator.hasNext()){

            HashMap<String, String> tempMap = iterator.next();
            Object dataTransfer[] = new Object[3];
            GetPlaceDuration getNearbyPlacesData = new GetPlaceDuration();

            String url = getUrl(lat, lng, Double.parseDouble(tempMap.get("lat")), Double.parseDouble(tempMap.get("lng")));
            dataTransfer[0] = mMap;
            dataTransfer[1] = url;
            dataTransfer[2] = tempMap.get("placeName");

            getNearbyPlacesData.execute(dataTransfer);
        }
    }

    private String getUrl(double latitude , double longitude, double destLat, double destLong){
        StringBuilder googlePlaceUrl = new StringBuilder("https://maps.googleapis.com/maps/api/directions/json?");
        googlePlaceUrl.append("origin="+latitude + "," + longitude);
        googlePlaceUrl.append("&destination="+destLat + "," + destLong);
        googlePlaceUrl.append("&key="+"AIzaSyCYNBwSkqbMRwmvZYtAUBFQ7eZJmKQJwqE");

        return googlePlaceUrl.toString();
    }
}
