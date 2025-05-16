package com.example.bottam_ex.main.dashboard.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.bottam_ex.R;

import java.util.ArrayList;
import java.util.List;

public class DiseaseImageAdapter extends RecyclerView.Adapter<DiseaseImageAdapter.ViewHolder> {

    private final List<String> imageUrls;
    private final List<String> imageTitles;

    public DiseaseImageAdapter(List<String> imageUrls, List<String> imageTitles) {
        this.imageUrls = (imageUrls != null) ? imageUrls : new ArrayList<>();
        this.imageTitles = (imageTitles != null) ? imageTitles : new ArrayList<>();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView title;

        public ViewHolder(View view) {
            super(view);
            imageView = view.findViewById(R.id.imageView);
            title = view.findViewById(R.id.imageTitle);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.disease_image_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Glide.with(holder.imageView.getContext())
                .load(imageUrls.get(position))
                .placeholder(R.drawable.ic_launcher_background)
                .into(holder.imageView);

        holder.title.setText(
                (imageTitles != null && imageTitles.size() > position) ? imageTitles.get(position) : ""
        );
    }

    @Override
    public int getItemCount() {
        return imageUrls.size();
    }
}

