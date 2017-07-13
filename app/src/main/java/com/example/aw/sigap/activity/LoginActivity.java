package com.example.aw.sigap.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.aw.sigap.R;
import com.example.aw.sigap.app.Config;
import com.example.aw.sigap.app.EndPoint;
import com.example.aw.sigap.app.MyApplication;
import com.example.aw.sigap.utils.Keys;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LoginActivity extends AppCompatActivity {

    @Bind(R.id.email)
    EditText etEmail;

    @Bind(R.id.password)
    EditText etPassword;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private String mEncodedEmail;
    private String TAG = LoginActivity.class.getSimpleName();
    ProgressDialog pDialog;
    Toolbar toolbar;
    private boolean loggedIn = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Loading...");
        pDialog.setCancelable(true);

    }
    @Override
    protected void onResume() {
        super.onResume();
        //In onresume fetching value from sharedpreference
        SharedPreferences sharedPreferences = getSharedPreferences(Config.SHARED_PREF_NAME, Context.MODE_PRIVATE);

        //Fetching the boolean value form sharedpreferences
        loggedIn = sharedPreferences.getBoolean(Config.LOGGEDIN_SHARED_PREF, false);

        //If we will get true
        if (loggedIn) {
            //We will start the Profile Activity
            Intent intent = new Intent(LoginActivity.this, StartActivity.class);
            startActivity(intent);
        }
    }

    @OnClick(R.id.btnLogin)
    public void login() {

        checkLoginWithPass();
        showPDialog();
    }

    @OnClick(R.id.btnLinkToRegisterScreen)
    public void register(){
        Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
        startActivity(intent);
    }

    private void checkLoginWithPass() {
        if (TextUtils.isEmpty(etEmail.getText())) {
            etEmail.setError("Please fill in your email");
            hidePDialog();
            return;
        } else {
            if (!isValidEmail(etEmail.getText().toString())) {
                etEmail.setError("Invalid email");
                hidePDialog();
                return;
            }
        }
        if (TextUtils.isEmpty(etPassword.getText())) {
            etPassword.setError("Please fill in your password");
            hidePDialog();
            return;
        } else {
            if (!isValidPassword(etPassword.getText().toString())) {
                etPassword.setError("Invalid password");
                hidePDialog();
                return;
            }
        }

        SharedPreferences pref = getApplicationContext().getSharedPreferences(Keys.SHARED_PREFS_KEY, 0);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(Keys.EMAIL_SPK, etEmail.getText().toString());

        final String email = etEmail.getText().toString();
        final String password = etPassword.getText().toString();
        //authenticate user
        StringRequest stringRequest = new StringRequest(Request.Method.POST, EndPoint.URL_LOGIN,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(LoginActivity.this,response,Toast.LENGTH_LONG).show();

                        try {
                            JSONObject obj = new JSONObject(response);
                            JSONObject setObj = new JSONObject(obj.getString("username"));
                            //boolean a = (boolean) obj.get("apiKey");
                            boolean b = (boolean) obj.get("error");
                            if (b){
                                Toast.makeText(LoginActivity.this, "Invalid Username or Password", Toast.LENGTH_SHORT).show();
                            }
                            else {
                                String userid = String.valueOf(obj.get("userId"));
                                String username = String.valueOf(setObj.get("first"));
                                String apikey = String.valueOf(obj.get("apiKey"));
                                String phonenumber = String.valueOf(obj.get("phone"));
                                String emailaddress = String.valueOf(obj.get("email"));

                                SharedPreferences id = LoginActivity.this.getSharedPreferences(Config.SHARED_PREF_ID,
                                        Context.MODE_PRIVATE);
                                SharedPreferences user = LoginActivity.this.getSharedPreferences(Config.SHARED_PREF_NAME,
                                        Context.MODE_PRIVATE);
                                SharedPreferences apiKey = LoginActivity.this.getSharedPreferences(Config.SHARED_PREF_API,
                                        Context.MODE_PRIVATE);
                                SharedPreferences phone = LoginActivity.this.getSharedPreferences(Config.SHARED_PREF_PHONE,
                                        Context.MODE_PRIVATE);
                                SharedPreferences email = LoginActivity.this.getSharedPreferences(Config.SHARED_PREF_EMAIL,
                                        Context.MODE_PRIVATE);

                                SharedPreferences.Editor editor = user.edit();
                                SharedPreferences.Editor editor1 = apiKey.edit();
                                SharedPreferences.Editor editor2 = phone.edit();
                                SharedPreferences.Editor editor3 = email.edit();
                                SharedPreferences.Editor editor4 = id.edit();

                                editor.putBoolean(Config.LOGGEDIN_SHARED_PREF, true);
                                editor1.putBoolean(Config.API_SHARED_PREF, true);

                                editor.putString(Config.USERNAME_SHARED_PREF, username);
                                editor1.putString(Config.APIKEY_SHARED_PREF, apikey);
                                editor2.putString(Config.PHONENUMBER_SHARED_PREF, phonenumber);
                                editor3.putString(Config.EMAILADDRESS_SHARED_PREF, emailaddress);
                                editor4.putString(Config.USERID_SHARED_PREF, userid);

                                editor.commit();
                                editor1.commit();
                                editor2.commit();
                                editor3.commit();
                                editor4.commit();

                                Log.d("user", username);
                                Log.d("api", apikey);

                                String fcmregid = FirebaseInstanceId.getInstance().getToken();
                                updateUser(userid, fcmregid, apikey);
                                Intent i = new Intent(LoginActivity.this, StartActivity.class);
                                startActivity(i);
                                Toast.makeText(LoginActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
                                hidePDialog();

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.i("List", "data not successfull access");
                            hidePDialog();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(LoginActivity.this, "Username or Password is empty" ,Toast.LENGTH_LONG).show();
                        hidePDialog();
                    }
                }){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
                params.put("username", email);
                //params.put("email", email);
                params.put("password", password);
                return params;
            }
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map headers = new HashMap();
                headers.put("x-snow-token", "SECRET_API_KEY");

                return headers;
            }
        };
        MyApplication.getInstance().addToRequestQueue(stringRequest);
    }

    public void updateUser(final String id_user, final String fcmregid, final String api_key){
        //Toast.makeText(this, "HAHAHAHA", Toast.LENGTH_SHORT).show();
        StringRequest stringRequest = new StringRequest(Request.Method.PUT,
                EndPoint.URL_USER+"/"+id_user+"/update", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e(TAG, "onResponse: " + response);
                try {
                    JSONObject obj = new JSONObject(response);

                    if (obj.getBoolean("error") == false) {
                        Toast.makeText(LoginActivity.this, "" + obj.getString("message"), Toast.LENGTH_SHORT).show();

                    } else {
                        Toast.makeText(LoginActivity.this, "" + obj.getString("message"), Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    Log.e(TAG, "json parsing error: " + e.getMessage());
                    Toast.makeText(LoginActivity.this, "Json parse error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                NetworkResponse networkResponse = error.networkResponse;
                Log.e(TAG, "Volley error: " + error.getMessage() + ", code: " + networkResponse);
                Toast.makeText(LoginActivity.this, "Volley errror: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map headers = new HashMap();
                headers.put("Authorization", api_key);

                return headers;
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("fcmregid", fcmregid);

                return params;
            }
        };
        //Adding request to request queue
        MyApplication.getInstance().addToRequestQueue(stringRequest);
    }



    private void showPDialog(){
        if(!pDialog.isShowing()){
            pDialog.show();
        }
    }

    private void hidePDialog(){
        if(pDialog.isShowing()){
            pDialog.dismiss();
        }
    }

    public static boolean isValidEmail(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public static boolean isValidPassword(String password) {
        return true;
    }

    public static String encodeEmail(String userEmail) {
        return userEmail.replace(".", ",");
    }
}
