package com.mh.test;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
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

public class historique extends AppCompatActivity{
    TextView tvnom,tvnbr,tvgrsang,tvdernier,tvprochaine;
    ImageView img;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historique);
        tvnom=(TextView)findViewById(R.id.textView7) ;
        img=(ImageView)findViewById(R.id.imageView2);
        tvnbr=(TextView)findViewById(R.id.textView8) ;
        tvgrsang=(TextView)findViewById(R.id.textView6) ;
        tvdernier=(TextView)findViewById(R.id.textView5) ;
        tvprochaine=(TextView)findViewById(R.id.textView4) ;
        SharedPreferences prefs = getSharedPreferences("pref", MODE_PRIVATE);
        String cin = prefs.getString("cin", "No");
        try{
            String URL = "http://192.168.43.34/sang/donnateur/gethistorique.php?cin="+cin;
         new SimpleTask().execute(URL);
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
           // Log.e("errrrrr",result);
            if(result.equals("false")){
                Toast.makeText(historique.this,"" +
                        "erreur reseau.",Toast.LENGTH_LONG).show();
            }
            else{
                //pour lire l* dans la resultat (set on php gethistorique.php )
                //StringTokenizer une methode de java utiliser pour les decopage d'un text (chaine de caractere) selon un delimiteur)
                StringTokenizer st = new StringTokenizer(result,"*");
                tvnom.setText("Donnateur : " + st.nextToken());
                tvnbr.setText("Nombre total du don : " + st.nextToken());
                String types=st.nextToken();
                tvgrsang.setText("Groupe sanguin :"+types);
                if(st.hasMoreTokens()){
                    String dt=st.nextToken();
                    StringTokenizer st2 = new StringTokenizer(dt,"-");
                    String annee=st2.nextToken();
                    String mois=st2.nextToken();
                    String jours=st2.nextToken();
                    int a=Integer.parseInt(annee);
                    int m=Integer.parseInt(mois)+3;
                    if(m>12){
                        a=Integer.parseInt(annee)+1;
                        m=m%12;
                    }
                    //changer la date
                    tvprochaine.setText("Date du prochaine don : "+String.valueOf(a)+"-"+String.valueOf(m)+"-"+jours);
                    tvdernier.setText("Date du dernier don :"+dt);
                }
                else{
                    tvprochaine.setText("Vous pouvez donner votre sang aujourd'hui.");
                    tvdernier.setText("Vous n'avez pas d'historique");
                }
                img.setImageResource(R.drawable.bb);
                if(types.equals("B PLUS")){
                    img.setImageResource(R.drawable.bb);
                }
                if(types.equals("B MOINS")){
                    img.setImageResource(R.drawable.bmoin);
                }
                if(types.equals("A PLUS")){
                    img.setImageResource(R.drawable.aplus);
                } if(types.equals("A MOINS")){
                    img.setImageResource(R.drawable.amoin);
                }

            }
        }
    }
}
