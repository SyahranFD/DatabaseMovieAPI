package com.example.databasemovieapi;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginPage extends AppCompatActivity {

    // creating constant keys for shared preferences.
    public static final String SHARED_PREFS = "shared_prefs";

    // key for storing email.
    public static final String EMAIL_KEY = "email_key";

    // key for storing password.
    public static final String PASSWORD_KEY = "password_key";

    // variable for shared preferences.
    public SharedPreferences sharedpreferences;
    String email, password;

    private static final String TAG = "LoginPage";
    private static final int RC_SIGN_IN_GOOGLE = 123;

    private EditText etUsername, etPassword;
    private MaterialButton btnLogin;
    private ProgressBar pbLogin;
    private ImageButton btnGoogle;
    private TextView tvRegisterHere;

    private GoogleSignInClient mGoogleSignInClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_page);

        checkLogin();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnRegister);
        pbLogin = findViewById(R.id.pbRegister);
        btnGoogle = findViewById(R.id.btnGoogle);
        tvRegisterHere = findViewById(R.id.tvRegisterHere);

        sharedpreferences = getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        email = sharedpreferences.getString(EMAIL_KEY, null);
        password = sharedpreferences.getString(PASSWORD_KEY, null);


        tvRegisterHere.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginPage.this, RegisterPage.class);
                startActivity(intent);
                finish();
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = etUsername.getText().toString();
                String password = etPassword.getText().toString();

                if (username.isEmpty() || password.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Isi Username dan Password", Toast.LENGTH_SHORT).show();
                } else {
                    pbLogin.setVisibility(View.VISIBLE);
                    btnLogin.setEnabled(false);

                    AndroidNetworking.post("https://mediadwi.com/api/latihan/login?username=admin&password=admin")
                            .addBodyParameter("username", username)
                            .addBodyParameter("password", password)
                            .setPriority(Priority.HIGH)
                            .build()
                            .getAsJSONObject(new JSONObjectRequestListener() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    Log.d("sukses login", "onResponse: " + response.toString());

                                    try {
                                        boolean status = response.getBoolean("status");
                                        String message = response.getString("message");

                                        if (status) {
                                            Toast.makeText(getApplicationContext(), "Sukses Login", Toast.LENGTH_SHORT).show();

                                            SharedPreferences.Editor editor = sharedpreferences.edit();

                                            // below two lines will put values for
                                            // email and password in shared preferences.
                                            editor.putString(EMAIL_KEY, etUsername.getText().toString());
                                            editor.putString(PASSWORD_KEY, etPassword.getText().toString());

                                            // to save our data with key and value.
                                            editor.apply();

                                            Intent listMovie = new Intent(LoginPage.this, ListMovieNameActivity.class);
                                            startActivity(listMovie);
                                            finish();

                                        } else {
                                            Toast.makeText(getApplicationContext(), "Gagal Login", Toast.LENGTH_SHORT).show();
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }

                                    pbLogin.setVisibility(View.GONE);
                                    btnLogin.setEnabled(true);
                                }

                                @Override
                                public void onError(ANError error) {
                                    // Handle error
                                }
                            });
                }
            }
        });

        btnGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signInWithGoogle();
            }
        });

    }

    private void signInWithGoogle() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN_GOOGLE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN_GOOGLE) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            String name = account.getDisplayName();
            String email = account.getEmail();

            SharedPreferences.Editor editor = sharedpreferences.edit();

            // below two lines will put values for
            // email and password in shared preferences.
            editor.putString(EMAIL_KEY, name);
            editor.putString(PASSWORD_KEY, email);

            // to save our data with key and value.
            editor.apply();

            Intent intent = new Intent(LoginPage.this, ListMovieNameActivity.class);
            startActivity(intent);

            finish();
        } catch (ApiException e) {
            Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());
        }
    }

    private void checkLogin() {
        sharedpreferences = getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        email = sharedpreferences.getString(EMAIL_KEY, null);
        password = sharedpreferences.getString(PASSWORD_KEY, null);

        // Mengecek apakah email dan kata sandi ada dalam SharedPreferences
        if (email != null && password != null) {
            // Jika email dan kata sandi ada, langsung buka halaman berikutnya
            Intent listMovie = new Intent(LoginPage.this, ListMovieNameActivity.class);
            startActivity(listMovie);
            finish();
        }
    }

}