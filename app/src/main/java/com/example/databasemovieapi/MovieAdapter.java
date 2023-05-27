package com.example.databasemovieapi;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MyViewHolder> {

    private Context context;
    private List<MovieModel> movieList;
    private MoviesAdapterListener listener;



    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView tvMovieName, tvMovieYear, tvMovieRate;
        public ImageView ivPoster;

        public MyViewHolder(View view) {
            super(view);
            tvMovieName = view.findViewById(R.id.tvMovieName);
            tvMovieYear = view.findViewById(R.id.tvMovieYear);
            tvMovieRate = view.findViewById(R.id.tvMovieRate);
            ivPoster = view.findViewById(R.id.ivPoster);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onMovieSelected(movieList.get(getAdapterPosition()));
                }
            });
            view.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    PopupMenu popupMenu = new PopupMenu(v.getContext(), v);
                    popupMenu.inflate(R.menu.popup_menu);

                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()) {
                                case R.id.menu_delete:
                                    AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                                    builder.setTitle("Delete Item");
                                    builder.setMessage("Are you sure you want to delete this item?");
                                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            deleteItem(getAdapterPosition());
                                        }
                                    });
                                    builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    });

                                    AlertDialog alertDialog = builder.create();
                                    alertDialog.show();
                                    return true;
                                default:
                                    return false;
                            }
                        }
                    });

                    popupMenu.show();

                    return true;
                }
            });
        }
    }

    public MovieAdapter(Context context, List<MovieModel> movieList , MoviesAdapterListener listener) {
        this.context = context;
        this.movieList = movieList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public MovieAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_layout, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MovieAdapter.MyViewHolder holder, int position) {
        final MovieModel movie = this.movieList.get(position);
        holder.tvMovieName.setText(movie.getMovieName());
        holder.tvMovieRate.setText(movie.getMovieRate());

        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        SimpleDateFormat outputFormat = new SimpleDateFormat("dd MMMM yyyy", new Locale("id", "ID"));

        try {
            // Mengubah string tanggal menjadi objek Date
            Date date = inputFormat.parse(movie.getMovieYear());

            // Mengubah format tanggal ke yang diinginkan
            String formattedDate = outputFormat.format(date);

            // Menampilkan tanggal yang telah diformat ke TextView
            holder.tvMovieYear.setText(formattedDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Glide.with(holder.itemView.getContext()).load("https://image.tmdb.org/t/p/original" + movie.getImgPoster()).into(holder.ivPoster);

    }

    @Override
    public int getItemCount() {
        return this.movieList.size();
    }

    private void deleteItem(int position) {
        movieList.remove(position);
        notifyItemRemoved(position);
    }

    public interface MoviesAdapterListener {
        void onMovieSelected(MovieModel movie);
    }
}
