package com.example.databasemovieapi;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
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

public class RegisterPage extends AppCompatActivity {

    private static final String TAG = "RegisterPage";
    private static final int RC_SIGN_IN_GOOGLE = 123;

    private EditText etUsername, etPassword, etFullName, etEmail;
    private MaterialButton btnRegister;
    private ProgressBar pbRegister;
    private ImageButton btnGoogle;
    private TextView tvLoginHere;

    private GoogleSignInClient mGoogleSignInClient;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_page);

        firebaseAuth = FirebaseAuth.getInstance();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        etFullName = findViewById(R.id.etFullName);
        etEmail = findViewById(R.id.etEmail);
        btnRegister = findViewById(R.id.btnRegister);
        pbRegister = findViewById(R.id.pbRegister);
        btnGoogle = findViewById(R.id.btnGoogle);
        tvLoginHere = findViewById(R.id.tvLoginHere);

        tvLoginHere.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterPage.this, LoginPage.class);
                startActivity(intent);
                finish();
            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = etUsername.getText().toString();
                String password = etPassword.getText().toString();
                String fullName = etFullName.getText().toString();
                String email = etEmail.getText().toString();

                if (username.isEmpty() || password.isEmpty() || fullName.isEmpty() || email.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Isi Data Dengan Lengkap", Toast.LENGTH_SHORT).show();
                } else {
                    pbRegister.setVisibility(View.VISIBLE);
                    btnRegister.setEnabled(false);

                    AndroidNetworking.post("https://mediadwi.com/api/latihan/register-user")
                            .addBodyParameter("username", username)
                            .addBodyParameter("password", password)
                            .addBodyParameter("full_name", fullName)
                            .addBodyParameter("email", email)
                            .setPriority(Priority.HIGH)
                            .build()
                            .getAsJSONObject(new JSONObjectRequestListener() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    Log.d("sukses Register", "onResponse: " + response.toString());

                                    try {
                                        boolean status = response.getBoolean("status");
                                        String message = response.getString("message");

                                        if (status) {
                                            Toast.makeText(getApplicationContext(), "Sukses Register", Toast.LENGTH_SHORT).show();

                                            Intent loginPage = new Intent(RegisterPage.this, LoginPage.class);
                                            startActivity(loginPage);
                                            finish();

                                        } else {
                                            Toast.makeText(getApplicationContext(), "Gagal Register", Toast.LENGTH_SHORT).show();
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }

                                    pbRegister.setVisibility(View.GONE);
                                    btnRegister.setEnabled(true);
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

        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser != null) {
            Intent intent = new Intent(RegisterPage.this, ListMovieNameActivity.class);
            startActivity(intent);
            finish();
        }
        Log.d(TAG, "Current user: " + currentUser);
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

            Intent intent = new Intent(RegisterPage.this, ListMovieNameActivity.class);
            startActivity(intent);

            finish();
        } catch (ApiException e) {
            Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());
        }
    }

}
