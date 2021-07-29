package com.mh.test;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

public class ConditionActivity extends AppCompatActivity {
    CheckBox cnd1,cnd2,cnd3,cnd4,cnd5,cnd6,cnd7,cnd8;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_condition);
        Button valider=findViewById(R.id.valider);
         cnd1=findViewById(R.id.cnd1);
         cnd2=findViewById(R.id.cnd2);
         cnd3=findViewById(R.id.cnd3);
         cnd4=findViewById(R.id.cnd4);
         cnd5=findViewById(R.id.cnd5);
         cnd6=findViewById(R.id.cnd6);
         cnd7=findViewById(R.id.cnd7);
         cnd8=findViewById(R.id.cnd8);
        valider.setOnClickListener(new
                                           View.OnClickListener() {
                                               @Override
                                               public void onClick(View v) {
                                                   if(cnd1.isChecked()&&cnd2.isChecked()&&cnd3.isChecked()&&cnd4.isChecked()&&cnd5.isChecked()&&cnd6.isChecked()
                                                           &&cnd7.isChecked()&&cnd8.isChecked())
                                                   {
                                                   Intent x = new Intent(ConditionActivity.this,login.class);
                                                   startActivity(x);
                                                   }
                                                   else{
                                                       Toast.makeText(getApplicationContext(),"Conditions du don incompatible",Toast.LENGTH_LONG).show();
                                                   }
                                               }
                                           });

    }
}
