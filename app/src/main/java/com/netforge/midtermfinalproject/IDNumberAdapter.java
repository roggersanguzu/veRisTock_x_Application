package com.netforge.midtermfinalproject;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class IDNumberAdapter extends RecyclerView.Adapter<IDNumberAdapter.ViewHolder> {

    private List<String> idNumberList;

    public IDNumberAdapter(List<String> idNumberList) {
        this.idNumberList = idNumberList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.activity_item_id_number, parent, false); // Use your custom layout
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String idNumber = idNumberList.get(position);
        holder.idNumberTextView.setText(idNumber); // Bind data to your TextView
    }

    @Override
    public int getItemCount() {
        return idNumberList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView idNumberTextView;

        ViewHolder(View itemView) {
            super(itemView);
            idNumberTextView = itemView.findViewById(R.id.idNumberTextView); // Match your ID
        }
    }
}