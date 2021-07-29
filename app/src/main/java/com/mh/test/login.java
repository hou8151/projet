package com.mh.test;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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
import java.net.URLEncoder;

public class login extends AppCompatActivity {
    EditText email,password;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e("error","debut");
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                .detectDiskReads()
                .detectDiskWrites()
                .detectNetwork()   // or .detectAll() for all detectable problems
                .penaltyLog()
                .build());
        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                .detectLeakedSqlLiteObjects()
                .detectLeakedClosableObjects()
                .penaltyLog()
                .penaltyDeath()
                .build());
        setContentView(R.layout.layout_login);
        email=(EditText)findViewById(R.id.email);
        password=(EditText)findViewById(R.id.password);
        Button connecter=(Button)findViewById(R.id.connexion);
        connecter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("error","btn connecter");
                String mail=email.getText().toString();
                String pass=password.getText().toString();
                //Toast.makeText(getApplicationContext(),mail+"  "+pass,Toast.LENGTH_LONG).show();
                if(mail.equals("")|| pass.equals(""))
                {
                    Toast.makeText(getApplicationContext(),"Remplir les champs",Toast.LENGTH_LONG).show();
                }
                else if(!mail.contains("@") || !mail.contains(".")){
                    Toast.makeText(getApplicationContext(),"mail incorrect",Toast.LENGTH_LONG).show();
                }
                else{
                    try{
                        String eml  = URLEncoder.encode(mail, "UTF-8");
                        String pswd   = URLEncoder.encode(pass, "UTF-8");
                        String URL = "http://192.168.43.34/sang/donnateur/connecter.php?email="+eml+"&password="+pswd;
                        //Log.e("url",URL);
                        //execution en arriere plan d'un thread
                        new SimpleTask().execute(URL);
                        //Toast.makeText(getApplicationContext(),result,Toast.LENGTH_LONG).show();
                    }
                    catch (Exception ex){
                        Log.e("error",ex.toString());

                    }

                }
            }
        });
        Button inscrir=(Button)findViewById(R.id.enregistrer);
        inscrir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent x;
                x = new Intent(login.this,Inscription.class);
                startActivity(x);
            }
        });
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
            Log.e("errrrrr",result);
            if(result.equals("false")){
                Toast.makeText(login.this,"" +
                        "login incorrect",Toast.LENGTH_LONG).show();
            }
            else{
                //enregistrer un variable (cin ) dans un dossier local ( pref )
                SharedPreferences.Editor editor = getSharedPreferences("pref", MODE_PRIVATE).edit();
                editor.putString("cin", result);
                editor.apply();
                //passage entre activite
                Intent i = new Intent(login.this, acceuil.class);
                startActivity(i);
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}

