package com.example.womensafety;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends UpdateContacts {
Button updateContacts,sendLoc,about;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

     //   ActionBar actionBar = getSupportActionBar();
       // actionBar.hide();

        updateContacts = findViewById(R.id.b1);
        sendLoc = findViewById(R.id.b2);
         about = findViewById(R.id.a);

        updateContacts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),UpdateContacts.class);
                startActivity(intent);
            }
        });

        sendLoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    SharedPreferences sp = getSharedPreferences("Contacts", MODE_PRIVATE);
                    String s1 = sp.getString("1", "chooseContact1");
                    String s2 = sp.getString("2", "chooseContact2");
                    String s3 = sp.getString("3", "chooseContact3");
                    String s4 = sp.getString("4", "chooseContact4");

                    SmsManager smgr = SmsManager.getDefault();
                    if (s1 == "chooseContact1" && s2 == "chooseContact2" && s3 == "chooseContact3" && s4 == "chooseContact4")
                        Toast.makeText(MainActivity.this, "  No contacts selected.\n" +"PLEASE SET CONTACTS", Toast.LENGTH_SHORT).show();
                    else {
                        String latitude = lati;
                        String longitude = longi;
                    if (s1 != "chooseContact1")
                            smgr.sendTextMessage(s1, null, "Help me!!!\n I am in danger.\n My location is:\n"+"https://www.google.com/maps/search/?api=1&query="+latitude+","+longitude, null, null);
                        if (s2 != "chooseContact2")
                            smgr.sendTextMessage(s2, null, "Help me!!!\n I am in danger.\n My location is:\n"+"https://www.google.com/maps/search/?api=1&query="+latitude+","+longitude, null, null);
                        if (s3 != "chooseContact3")
                            smgr.sendTextMessage(s3, null, "Help me!!!\n I am in danger.\n My location is:\n"+"https://www.google.com/maps/search/?api=1&query="+latitude+","+longitude, null, null);
                        if (s4 != "chooseContact4")
                            smgr.sendTextMessage(s4, null, "Help me!!!\n I am in danger.\n My location is:\n"+"https://www.google.com/maps/search/?api=1&query="+latitude+","+longitude, null, null);
                        Toast.makeText(MainActivity.this, "SMS Sent Successfully", Toast.LENGTH_SHORT).show();
                    }
                }
                catch (Exception e){
                    Toast.makeText(MainActivity.this, "SMS Failed to Send, Make sure all permission are given to app from settings", Toast.LENGTH_SHORT).show();
                }
            }
        });

         about.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 Intent intent = new Intent(getApplicationContext(),About.class);
                 startActivity(intent);
             }
         });
    }
}
