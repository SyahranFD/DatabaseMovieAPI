package com.example.databasemovieapi;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class MovieModel implements Parcelable {
    private String movieName;
    private String movieYear;
    private String movieRate;
    private String imgPoster;
    private String imgBackdrop;
    private String movieDescription;

    protected MovieModel(Parcel in) {
        movieName = in.readString();
        movieYear = in.readString();
        movieRate = in.readString();
        imgPoster = in.readString();
        imgBackdrop = in.readString();
        movieDescription = in.readString();
    }

    MovieModel() {

    }

    public static final Creator<MovieModel> CREATOR = new Creator<MovieModel>() {
        @Override
        public MovieModel createFromParcel(Parcel in) {
            return new MovieModel(in);
        }

        @Override
        public MovieModel[] newArray(int size) {
            return new MovieModel[size];
        }
    };

    public String getImgPoster() {
        return imgPoster;
    }

    public void setImgPoster(String imgPoster) {
        this.imgPoster = imgPoster;
    }

    public String getMovieName() {
        return movieName;
    }

    public void setMovieName(String movieName) {
        this.movieName = movieName;
    }

    public String getMovieYear() {
        return movieYear;
    }

    public void setMovieYear(String movieYear) {
        this.movieYear = movieYear;
    }

    public String getMovieRate() {
        return movieRate;
    }

    public void setMovieRate(String movieRate) {
        this.movieRate = movieRate;
    }

    public String getImgBackdrop() {
        return imgBackdrop;
    }

    public void setImgBackdrop(String imgBackdrop) {
        this.imgBackdrop = imgBackdrop;
    }

    public String getMovieDescription() {
        return movieDescription;
    }

    public void setMovieDescription(String movieDescription) {
        this.movieDescription = movieDescription;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(movieName);
        dest.writeString(movieYear);
        dest.writeString(movieRate);
        dest.writeString(imgPoster);
        dest.writeString(imgBackdrop);
        dest.writeString(movieDescription);
    }
}
