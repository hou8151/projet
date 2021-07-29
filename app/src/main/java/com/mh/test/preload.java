package com.mh.test;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.webkit.WebView;

public class preload extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //liaison entre  d'activity et leur layout (xml)
        setContentView(R.layout.activity_preload);
        WebView nav = (WebView)findViewById(R.id.webview1);
        nav.loadUrl("file:///android_asset/load.html");
        //thread  les temps d'attende pour passer a la page suivant
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                Intent x;
                SharedPreferences prefs = getSharedPreferences("pref", MODE_PRIVATE);
                String cin = prefs.getString("cin", "No");
                if(cin.equals("No")){
                    x = new Intent(preload.this,ConditionActivity.class);
                    startActivity(x);
                }
                else{
                    x = new Intent(preload.this,acceuil.class);
                    startActivity(x);

                }

            }
        }, 5000);



    }
}
