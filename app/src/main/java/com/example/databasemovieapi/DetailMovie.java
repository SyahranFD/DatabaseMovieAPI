package com.example.databasemovieapi;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

public class DetailMovie extends AppCompatActivity {

    Intent i;
    MovieModel movieModel;
    TextView tvMovieName, tvMovieYear, tvMovieRate, tvMovieDescription;
    ImageView btnArrow, ivBackdrop, ivPoster;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_movie);

        btnArrow = findViewById(R.id.btnArrow);
        btnArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DetailMovie.this, ListMovieNameActivity.class);
                startActivity(intent);
            }
        });

        i = getIntent();
        movieModel = (MovieModel) i.getParcelableExtra("myMovie");

        ivBackdrop = findViewById(R.id.ivBackdropDetail);
        Glide.with(this).load("https://image.tmdb.org/t/p/original" + movieModel.getImgBackdrop()).into(ivBackdrop);

        ivPoster = findViewById(R.id.ivPosterDetail);
        Glide.with(this).load("https://image.tmdb.org/t/p/original" + movieModel.getImgPoster()).into(ivPoster);

        tvMovieName = findViewById(R.id.tvMovieNameDetail);
        tvMovieName.setText(movieModel.getMovieName());

        tvMovieYear = findViewById(R.id.tvMovieYearDetail);
        tvMovieYear.setText(movieModel.getMovieYear());

        tvMovieRate = findViewById(R.id.tvMovieRateDetail);
        tvMovieRate.setText(movieModel.getMovieRate());

        tvMovieDescription = findViewById(R.id.tvMovieDescriptionDetail);
        tvMovieDescription.setText(movieModel.getMovieDescription());

    }
}