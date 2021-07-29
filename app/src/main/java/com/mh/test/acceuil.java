package com.mh.test;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class acceuil extends AppCompatActivity {
    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String CORSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private Boolean mLocationPermissionGranted = false;
    Button profil,historique,btngps,btnmap;
    ImageButton deconnect;
    String cin="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profil);
        profil = (Button)findViewById(R.id.button10);
        deconnect = (ImageButton)findViewById(R.id.button13);
        historique = (Button)findViewById(R.id.button11);
        btngps = (Button)findViewById(R.id.button12);
        btnmap = (Button)findViewById(R.id.button19);
        //retrouver le cin de donnateur dans le dossier local (pref)
        SharedPreferences prefs = getSharedPreferences("pref", MODE_PRIVATE);
        cin = prefs.getString("cin", "No");
        profil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(acceuil.this, Profil_donnateur.class);
                startActivity(i);
            }
        });
        btngps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // verifier l'autorisation d'acces a la gps
                getLocationPermission();
                //faire un appel a la classe java GPSTracker pour recuprer les coordonnee geographiques
                GPSTracker tracker = new GPSTracker(acceuil.this);
                if (!tracker.canGetLocation()) {
                    tracker.showSettingsAlert();
                } else {
                    Double latitude = tracker.getLatitude();
                    Double longitude = tracker.getLongitude();
                    Log.e("gps",String.valueOf(latitude)+" "+String.valueOf(longitude));
                    String URL = "http://192.168.43.34/sang/donnateur/insertgps.php?latitude="+String.valueOf(latitude)+
                            "&longitude="+String.valueOf(longitude)+"&cin="+cin;
                    new SimpleTask().execute(URL);
                }
            }
        });
        btnmap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(acceuil.this, MapsActivity.class);
                startActivity(i);
            }
        });
        deconnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // suuprimer le variable local
                SharedPreferences.Editor editor = getSharedPreferences("pref", MODE_PRIVATE).edit();
                editor.remove("cin");
                editor.clear();
                editor.commit();
                Intent i = new Intent(acceuil.this, login.class);
                startActivity(i);
            }
        });
        historique.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(acceuil.this, historique.class);
                startActivity(i);
            }
        });
    }
    private void getLocationPermission(){
        String[] permission={ Manifest.permission.ACCESS_FINE_LOCATION,
                android.Manifest.permission.ACCESS_COARSE_LOCATION};
        if(ContextCompat.checkSelfPermission(this.getApplicationContext(),
                FINE_LOCATION)== PackageManager.PERMISSION_GRANTED){
            if(ContextCompat.checkSelfPermission(this.getApplicationContext(),
                    CORSE_LOCATION)== PackageManager.PERMISSION_GRANTED){
                mLocationPermissionGranted=true;
            }else{
                ActivityCompat.requestPermissions(this,permission,LOCATION_PERMISSION_REQUEST_CODE);
            }
        }else{
            ActivityCompat.requestPermissions(this,permission,LOCATION_PERMISSION_REQUEST_CODE);
        }
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
            Toast.makeText(acceuil.this,"" +
                    result,Toast.LENGTH_LONG).show();
        }
    }
}
