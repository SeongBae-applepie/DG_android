package com.example.bottam_ex.main.dashboard;


import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import com.bumptech.glide.Glide;
import com.example.bottam_ex.R;
import com.example.bottam_ex.main.dashboard.UnifiedSearchInfo;
public class UnifiedSearchAdapter extends RecyclerView.Adapter<UnifiedSearchAdapter.ViewHolder> {

    private List<UnifiedSearchInfo> data;

    public UnifiedSearchAdapter(List<UnifiedSearchInfo> data) {
        this.data = data;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView cropText, korText, oprText, divText;
        ImageView imageView;


        public ViewHolder(View view) {
            super(view);
            cropText = view.findViewById(R.id.cropText);
            korText = view.findViewById(R.id.korText);
            oprText = view.findViewById(R.id.oprText);
            divText = view.findViewById(R.id.divText);
            imageView = view.findViewById(R.id.imageView);


        }


    }



    @NonNull
    @Override
    public UnifiedSearchAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.unified_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UnifiedSearchAdapter.ViewHolder holder, int position) {
        UnifiedSearchInfo info = data.get(position);
        holder.cropText.setText("작물: " + info.cropName);
        holder.korText.setText("병해명: " + info.korName);
        holder.oprText.setText("영문명: " + info.oprName);
        holder.divText.setText("구분: " + info.divName);
        Glide.with(holder.imageView.getContext()).load(info.thumbImg).into(holder.imageView);

        holder.itemView.setOnClickListener(v -> {
            Log.d("holder_itemView","OnClickListener");
            if (listener != null) {
                listener.onItemClick(info);
            }
        });
    }


    public interface OnItemClickListener {
        void onItemClick(UnifiedSearchInfo item);
    }

    private OnItemClickListener listener;

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    @Override
    public int getItemCount() {
        return data.size();
    }
}