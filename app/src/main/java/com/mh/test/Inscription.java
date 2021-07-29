package com.mh.test;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
import java.net.URLEncoder;

public class Inscription extends AppCompatActivity {
   EditText nom,prenom,email,password,cin,adresse,telephone;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inscription);
        nom=findViewById(R.id.nom);
        prenom=findViewById(R.id.prenom);
        email=findViewById(R.id.emaill);
        adresse=findViewById(R.id.adresse);
        password=findViewById(R.id.motpasse);
        cin=findViewById(R.id.cin);
        telephone=findViewById(R.id.telephone);
        ImageButton inscrir=findViewById(R.id.inscrir);
        inscrir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    String nomt  = URLEncoder.encode(nom.getText().toString(), "UTF-8");
                    String prenomt  = URLEncoder.encode(prenom.getText().toString(), "UTF-8");
                    String emailt  = URLEncoder.encode(email.getText().toString(), "UTF-8");
                    String adresset  = URLEncoder.encode(adresse.getText().toString(), "UTF-8");
                    String passt  = URLEncoder.encode(password.getText().toString(), "UTF-8");
                    String cint  = URLEncoder.encode(cin.getText().toString(), "UTF-8");
                    String telephonet  = URLEncoder.encode(telephone.getText().toString(), "UTF-8");
                    String URL = "http://192.168.43.34/sang/donnateur/inscription.php?nom="+nomt+"&prenom="+prenomt+"&email="+emailt+"&password="+
                            passt+"&cin="+cint+"&telephone="+telephonet+"&adresse="+adresset;
                    Log.e("url",URL);
                    //execution en arriere plan
                    new InsertTask().execute(URL);
                    //Toast.makeText(getApplicationContext(),result,Toast.LENGTH_LONG).show();
                }
                catch (Exception ex){
                    Log.e("error",ex.toString());

                }
            }
        });

    }
    private class InsertTask extends AsyncTask<String, Void, String> {

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
            Log.e("errrrrr",result);
            if(result.equals("false")){
                Toast.makeText(Inscription.this,"" +
                        "Verifier votre connexion",Toast.LENGTH_LONG).show();
            }
            else  if(result.equals("true")){
                Toast.makeText(Inscription.this,"" +
                        "Inscription valider",Toast.LENGTH_LONG).show();
                Intent i = new Intent(Inscription.this, login.class);
                startActivity(i);
            }
        }
    }
}
