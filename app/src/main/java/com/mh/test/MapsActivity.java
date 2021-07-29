
package com.mh.test;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Array;
import java.util.ArrayList;
import java.util.HashMap;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    // variable latlng speciale pour googleMap
    LatLng position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        String url="http://192.168.43.34/sang/donnateur/gethopitalslocation.php";
        new SimpleTask().execute(url);
    }
    private class SimpleTask extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            // Create Show ProgressBar
        }

        protected String doInBackground(String... urls)   {
            String result = "";
            try {

                HttpGet httpGet = new HttpGet(urls[0]);
                HttpClient client = new DefaultHttpClient();

                HttpResponse response = client.execute(httpGet);

                int statusCode = response.getStatusLine().getStatusCode();

                if (statusCode == 200) {
                    InputStream inputStream = response.getEntity().getContent();
                    BufferedReader reader = new BufferedReader
                            (new InputStreamReader(inputStream));
                    String line;
                    while ((line = reader.readLine()) != null) {
                        result += line;
                    }
                }

            } catch (ClientProtocolException e) {

            } catch (IOException e) {

            }
            return result;
        }
        protected void onPostExecute(String result)  {

            try {
                JSONArray json = new JSONArray(result);
                int[] dist = new int[json.length()];
                   for (int i = 0; i < json.length(); i++) {
                       JSONObject info = json.getJSONObject(i);
                       Double dt=distanceBetween(Double.parseDouble(info.getString("latitude")),Double.parseDouble(info.getString("longitude")),position.latitude,position.longitude);
                       dist[i]=(int)dt.intValue();
                       LatLng position = new LatLng(Double.parseDouble(info.getString("latitude")), Double.parseDouble(info.getString("longitude")));
                       mMap.addMarker(new MarkerOptions()
                               .position(position)
                               .title("Bank du sang")
                               .snippet(info.getString("ville"))
                               .icon(bitmapDescriptorFromVector(MapsActivity.this, R.drawable.ic_local_hospital_black_24dp)));
                        Log.e("info",info.getString("ville"));
                    }
                    int min =dist[0];
                    int pos=0;
                for (int i = 1; i < json.length(); i++) {
                    if (dist[i]<min) {
                        min=dist[i];
                        pos=i;
                    }
                }
                JSONObject info = json.getJSONObject(pos);
                LatLng pppbank= new LatLng(Double.parseDouble(info.getString("latitude")),Double.parseDouble(info.getString("longitude")));
                Toast.makeText(getApplicationContext()," La plus proche bank est :"+info.getString("ville"),Toast.LENGTH_LONG).show();
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(pppbank,13));
                Polyline line = mMap.addPolyline(new PolylineOptions()
                        .add(new LatLng(pppbank.latitude, pppbank.longitude), new LatLng(position.latitude, position.longitude))
                        .width(5)
                        .color(Color.RED));



            } catch (JSONException ex) {
                Log.e("errr",ex.getMessage());
            }

        }
    }
    private BitmapDescriptor bitmapDescriptorFromVector(Context context, int vectorResId) {
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }
    private double distanceBetween(double lat1, double lon1, double lat2, double lon2) {
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1))
                * Math.sin(deg2rad(lat2))
                + Math.cos(deg2rad(lat1))
                * Math.cos(deg2rad(lat2))
                * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = dist * 180.0 / Math.PI;
        dist = dist * 60 * 1.1515*1000;
        return (dist);
    }

    private double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        //activer gps=
        GPSTracker gps=new GPSTracker(getApplicationContext());
       //creer un marker de votre position;
        position = new LatLng(gps.getLatitude(), gps.getLongitude());
        mMap.addMarker(new MarkerOptions()
                .position(position).title("C'est moi")
                .snippet("Donnateur :)")
                .icon(bitmapDescriptorFromVector(MapsActivity.this, R.drawable.ic_my_location_black_24dp)));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(position,10));
        //mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);

    }
}
