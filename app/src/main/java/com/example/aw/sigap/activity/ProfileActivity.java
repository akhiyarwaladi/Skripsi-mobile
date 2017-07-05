package com.example.aw.sigap.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.example.aw.sigap.R;
import com.example.aw.sigap.app.Config;

public class ProfileActivity extends AppCompatActivity {

    String username, phonenumber, emailaddress;
    TextView name, phone, email;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        name = (TextView) findViewById(R.id.usernameTxtVw);
        phone = (TextView) findViewById(R.id.phoneTxtVw);
        email = (TextView) findViewById(R.id.emailTxtVw);

        final SharedPreferences sharedPreferencesName= getSharedPreferences(Config.SHARED_PREF_NAME,
                Context.MODE_PRIVATE);
        final SharedPreferences sharedPreferencesPhone = getSharedPreferences(Config.SHARED_PREF_PHONE,
                Context.MODE_PRIVATE);
        final SharedPreferences sharedPreferencesEmail = getSharedPreferences(Config.SHARED_PREF_EMAIL,
                Context.MODE_PRIVATE);

        username = sharedPreferencesName.getString(Config.USERNAME_SHARED_PREF, "");
        phonenumber = sharedPreferencesPhone.getString(Config.PHONENUMBER_SHARED_PREF, "");
        emailaddress = sharedPreferencesEmail.getString(Config.EMAILADDRESS_SHARED_PREF, "");

        name.setText(username);
        phone.setText(phonenumber);
        email.setText(emailaddress);
    }
}
