package com.mh.test;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
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
import java.util.StringTokenizer;

import static android.content.Context.MODE_PRIVATE;

public class Profil_donnateur extends AppCompatActivity {
    EditText edtnom,edtprenom,edtemail,edtadress,edttelephone,edtpassword;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profil_donnateur);
        edtnom=(EditText)findViewById(R.id.editText3);
        edtprenom=(EditText)findViewById(R.id.editText10);
        edtemail=(EditText)findViewById(R.id.editText11);
        edtadress=(EditText)findViewById(R.id.editText12);
        edttelephone=(EditText)findViewById(R.id.editText13);
        edtpassword=(EditText)findViewById(R.id.editText14);
        SharedPreferences prefs = getSharedPreferences("pref", MODE_PRIVATE);
        String cin = prefs.getString("cin", "No");
        try{
            String URL = "http://192.168.43.34/sang/donnateur/getprofil.php?cin="+cin;
            //Log.e("url",URL);
            new SimpleTask().execute(URL);
            //Toast.makeText(getApplicationContext(),result,Toast.LENGTH_LONG).show();
        }
        catch (Exception ex){
            Log.e("error",ex.toString());

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
            // Dismiss ProgressBar
            //updateWebView(result);
            Log.e("errrrrr",result);
            if(result.equals("false")){
                Toast.makeText(Profil_donnateur.this,"" +
                        "erreur",Toast.LENGTH_LONG).show();
            }
            else{

                StringTokenizer st = new StringTokenizer(result,"*");
                String nom=st.nextToken();
                String prenom=st.nextToken();
                String telephone=st.nextToken();
                String adress=st.nextToken();
                String email=st.nextToken();
                String password=st.nextToken();
                edtnom.setText(nom);
                edtnom.setEnabled(false);
                edtprenom.setText(prenom);
                edtprenom.setEnabled(false);
                edttelephone.setText(telephone);
                edtadress.setText(adress);
                edtemail.setText(email);
                edtpassword.setText(password);

            }
        }
    }
}
