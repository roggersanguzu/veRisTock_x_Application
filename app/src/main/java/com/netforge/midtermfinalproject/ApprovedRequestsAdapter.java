package com.netforge.midtermfinalproject;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ApprovedRequestsAdapter extends RecyclerView.Adapter<ApprovedRequestsAdapter.ApprovedRequestViewHolder> {

    private final List<AnimalRequest> approvedRequestsList;

    public ApprovedRequestsAdapter(List<AnimalRequest> approvedRequestsList) {
        this.approvedRequestsList = approvedRequestsList;
    }

    @NonNull
    @Override
    public ApprovedRequestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_item_buyer_request, parent, false);
        return new ApprovedRequestViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ApprovedRequestViewHolder holder, int position) {
        if (position < approvedRequestsList.size()) { // Prevent out-of-bounds errors
            AnimalRequest request = approvedRequestsList.get(position);
            if (request != null) {
                holder.bind(request);
            }
        }
    }

    @Override
    public int getItemCount() {
        return approvedRequestsList.size();
    }

    static class ApprovedRequestViewHolder extends RecyclerView.ViewHolder {

        private final TextView tvWeight, tvAge, tvSex, tvLocation;

        public ApprovedRequestViewHolder(@NonNull View itemView) {
            super(itemView);
            tvWeight = itemView.findViewById(R.id.tvWeight);
            tvAge = itemView.findViewById(R.id.tvAge);
            tvSex = itemView.findViewById(R.id.tvSex);
            tvLocation = itemView.findViewById(R.id.tvLocation);
        }

        public void bind(AnimalRequest request) {
            tvWeight.setText("Weight: " + request.getWeight());
            tvAge.setText("Age: " + request.getAge());
            tvSex.setText("Sex: " + request.getSex());
            tvLocation.setText("Location: " + request.getLocation());
        }
    }
}