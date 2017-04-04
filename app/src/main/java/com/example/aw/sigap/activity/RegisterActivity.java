package com.example.aw.sigap.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.aw.sigap.R;
import com.example.aw.sigap.app.Config;
import com.example.aw.sigap.app.EndPoint;
import com.example.aw.sigap.app.MyApplication;
import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by AW on 3/6/2017.
 */

public class RegisterActivity extends AppCompatActivity {
    ProgressDialog pDialog;

    @Bind(R.id.name)
    EditText etName;

    @Bind(R.id.email)
    EditText etEmail;

    @Bind(R.id.password)
    EditText etPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        ButterKnife.bind(this);

        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Loading...");
        pDialog.setCancelable(true);
    }


    @OnClick(R.id.btnRegister)
    public void register() {

        Register();
        showPDialog();
    }

    @OnClick(R.id.btnLinkToLoginScreen)
    public void login(){
        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
        startActivity(intent);
    }

    private void Register(){
        final String name = etName.getText().toString();
        final String email = etEmail.getText().toString();
        final String password = etPassword.getText().toString();
        final String token = FirebaseInstanceId.getInstance().getToken();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, EndPoint.URL_REGISTER,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject obj = new JSONObject(response);
                            boolean b = (boolean) obj.get("error");
                            if (b){
                                Toast.makeText(RegisterActivity.this, "Username already exist", Toast.LENGTH_SHORT).show();

                            }
                            else {
                                Intent i = new Intent(RegisterActivity.this, LoginActivity.class);
                                startActivity(i);
                                Toast.makeText(RegisterActivity.this, "Succesfully registered" ,Toast.LENGTH_LONG).show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.i("List", "data not successfull access"); }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(RegisterActivity.this,"Username or Password is empty",Toast.LENGTH_LONG).show();
                    }

                }){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();

                params.put("name", name);
                params.put("email", email);
                params.put("password", password);
                params.put("fcmregid", token);
                return params;
            }
        };
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
}
