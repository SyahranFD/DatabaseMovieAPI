package com.example.databasemovieapi;

import static android.content.ContentValues.TAG;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
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

import org.json.JSONException;
import org.json.JSONObject;

public class LoginPage extends AppCompatActivity {

    EditText etUsername, etPassword;
    MaterialButton btnLogin;
    ProgressBar pbLogin;
    ImageButton btnGoogle, btnFacebook;

    private static final int RC_SIGN_IN_GOOGLE = 123;
    private GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_page);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);


        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        pbLogin = findViewById(R.id.pbLogin);
        btnGoogle = findViewById(R.id.btnGoogle);
        btnFacebook = findViewById(R.id.btnFacebook);

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

                                            Intent calculator = new Intent(LoginPage.this, MainActivity.class);
                                            startActivity(calculator);
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
            // Sign-in was successful, you can retrieve user details from the account object
            String name = account.getDisplayName();
            String email = account.getEmail();
            // ...
            Intent intent = new Intent(LoginPage.this, ListMovieNameActivity.class);
            startActivity(intent);

            // Finish MainActivity if needed
            finish();
        } catch (ApiException e) {
            // Sign-in failed, handle the exception
            Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());
        }
    }

}