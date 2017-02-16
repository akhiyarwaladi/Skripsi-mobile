package com.example.aw.sigap.activity;

import android.app.ProgressDialog;
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

import com.example.aw.sigap.R;
import com.example.aw.sigap.utils.Keys;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //toolbar = (Toolbar)findViewById(R.id.tool_bar);
        //setSupportActionBar(toolbar);
        //toolbar.setVisibility(View.GONE);

        //getSupportActionBar().setTitle("Sigap");
        ButterKnife.bind(this);

        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Loading...");
        pDialog.setCancelable(true);
        mAuth = FirebaseAuth.getInstance();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                    mEncodedEmail = encodeEmail(user.getEmail());

                    Intent intent = new Intent(LoginActivity.this, DashboardActivity.class);
                    startActivity(intent);
                    finish();


                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
            }
        };
    }

    @OnClick(R.id.btnLogin)
    public void login() {
        checkLoginWithPass();
        showPDialog();
    }

    private void checkLoginWithPass() {
        if (TextUtils.isEmpty(etEmail.getText())) {
            etEmail.setError("Please fill in your email");
            return;
        } else {
            if (!isValidEmail(etEmail.getText().toString())) {
                etEmail.setError("Invalid email");
                return;
            }
        }
        if (TextUtils.isEmpty(etPassword.getText())) {
            etPassword.setError("Please fill in your password");
            return;
        } else {
            if (!isValidPassword(etPassword.getText().toString())) {
                etPassword.setError("Invalid password");
                return;
            }
        }

        SharedPreferences pref = getApplicationContext().getSharedPreferences(Keys.SHARED_PREFS_KEY, 0);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(Keys.EMAIL_SPK, etEmail.getText().toString());

        final String email = etEmail.getText().toString();
        final String password = etPassword.getText().toString();
        //authenticate user
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (!task.isSuccessful()) {
                            Toast.makeText(LoginActivity.this, "Login Failed"+task, Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(LoginActivity.this, "Login berhasil"+task, Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(LoginActivity.this, DashboardActivity.class);
                            startActivity(intent);
                            finish();
                            hidePDialog();
                        }
                    }
                });
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
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
