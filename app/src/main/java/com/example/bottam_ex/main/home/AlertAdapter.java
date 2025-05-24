package com.example.bottam_ex.main.home;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bottam_ex.R;
import com.example.bottam_ex.data.model.AlertItem;

import java.util.List;

public class AlertAdapter extends RecyclerView.Adapter<AlertAdapter.AlertViewHolder> {
    private final List<AlertItem> alertList;

    public AlertAdapter(List<AlertItem> alertList) {
        this.alertList = alertList;
    }

    @NonNull
    @Override
    public AlertViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_home_alert, parent, false);
        return new AlertViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AlertViewHolder holder, int position) {
        AlertItem item = alertList.get(position);
        holder.alertTitle.setText(item.getTitle());
        holder.alertTag.setText(item.getType());

        switch (item.getType()) {
            case "경보":
                holder.alertTag.setBackgroundResource(R.drawable.tag_background_warning);
                break;
            case "주의보":
            case "주의":
                holder.alertTag.setBackgroundResource(R.drawable.tag_background_warning);
                break;
            default:
                holder.alertTag.setBackgroundResource(R.drawable.tag_background_safe);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return alertList.size();
    }

    static class AlertViewHolder extends RecyclerView.ViewHolder {
        TextView alertTag, alertTitle;

        AlertViewHolder(@NonNull View itemView) {
            super(itemView);
            alertTag = itemView.findViewById(R.id.alert_tag);
            alertTitle = itemView.findViewById(R.id.alert_title);
        }
    }
}