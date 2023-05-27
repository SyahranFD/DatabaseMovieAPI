package com.example.databasemovieapi;

import static com.example.databasemovieapi.LoginPage.EMAIL_KEY;
import static com.example.databasemovieapi.LoginPage.PASSWORD_KEY;
import static com.example.databasemovieapi.LoginPage.SHARED_PREFS;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.google.firebase.auth.FirebaseAuth;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class ListMovieNameActivity extends AppCompatActivity implements MovieAdapter.MoviesAdapterListener {
    RecyclerView rvMovie;
    ArrayList<MovieModel> listDataMovie;
    private MovieAdapter adapterListMovie;
    ProgressBar pbMovie;
    private Toolbar toolbar;
    private FirebaseAuth firebaseAuth;

    SharedPreferences sharedpreferences;

    public void getMovieOnline(){
        rvMovie = findViewById(R.id.rvMovie);

        String url = "https://api.themoviedb.org/3/movie/popular?api_key=5e30e788ba0016174d8c885253084699";
        AndroidNetworking.get(url)
                .setTag("test")
                .setPriority(Priority.LOW)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        try {
                            JSONArray jsonArrayMovie = jsonObject.getJSONArray("results");
                            for (int i = 0; i < jsonArrayMovie.length(); i++) {
                                MovieModel myMovie = new MovieModel();
                                JSONObject jsonTeam = jsonArrayMovie.getJSONObject(i);
                                myMovie.setMovieName(jsonTeam.getString("original_title"));
                                myMovie.setMovieYear(jsonTeam.getString("release_date"));
                                myMovie.setMovieRate(jsonTeam.getString("vote_average"));
                                myMovie.setImgPoster(jsonTeam.getString("poster_path"));
                                myMovie.setImgBackdrop(jsonTeam.getString("backdrop_path"));
                                myMovie.setMovieDescription(jsonTeam.getString("overview"));

                                listDataMovie.add(myMovie);
                            }

                            adapterListMovie = new MovieAdapter(getApplicationContext(), listDataMovie, ListMovieNameActivity.this);
                            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
                            rvMovie.setHasFixedSize(true);
                            rvMovie.setLayoutManager(mLayoutManager);
                            rvMovie.setAdapter(adapterListMovie);

                            pbMovie.setVisibility(View.GONE);
                            rvMovie.setVisibility(View.VISIBLE);


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.d("GAGAL", "onError: " + anError.toString());
                    }
                });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_movie_name);

        listDataMovie = new ArrayList<>();
        pbMovie = findViewById(R.id.pbMovie);
        getMovieOnline();

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        firebaseAuth = FirebaseAuth.getInstance();

        sharedpreferences = getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
    }


    @Override
    public void onMovieSelected(MovieModel movie) {
        String releaseDate = movie.getMovieYear();

        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        SimpleDateFormat outputFormat = new SimpleDateFormat("dd MMMM yyyy", Locale.getDefault());

        try {
            Date date = inputFormat.parse(releaseDate);
            releaseDate = outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Intent intent = new Intent(ListMovieNameActivity.this, DetailMovie.class);
        intent.putExtra("myMovie", movie);
        intent.putExtra("release_date", releaseDate);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.options_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_logout) {
            // Menghapus data dari SharedPreferences
            SharedPreferences.Editor editor = sharedpreferences.edit();
            editor.remove(EMAIL_KEY);
            editor.remove(PASSWORD_KEY);
            editor.apply();

            firebaseAuth.signOut();

            Intent intent = new Intent(ListMovieNameActivity.this, LoginPage.class);
            startActivity(intent);
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}