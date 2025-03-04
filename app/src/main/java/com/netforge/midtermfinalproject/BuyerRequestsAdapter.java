package com.netforge.midtermfinalproject;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class BuyerRequestsAdapter extends RecyclerView.Adapter<BuyerRequestsAdapter.BuyerRequestViewHolder> {

    private final List<BuyerRequest> buyerRequestsList;
    private final List<BuyerRequest> selectedRequests = new ArrayList<>();

    public BuyerRequestsAdapter(List<BuyerRequest> buyerRequestsList) {
        this.buyerRequestsList = buyerRequestsList;
    }

    @NonNull
    @Override
    public BuyerRequestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_item_buyer_request, parent, false);
        return new BuyerRequestViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BuyerRequestViewHolder holder, int position) {
        if (position < buyerRequestsList.size()) { // Prevent out-of-bounds errors
            BuyerRequest request = buyerRequestsList.get(position);
            if (request != null) {
                holder.bind(request);
            }
        }
    }

    @Override
    public int getItemCount() {
        return buyerRequestsList.size();
    }

    /**
     * Retrieves the list of selected requests.
     *
     * @return A list of selected BuyerRequest objects.
     */
    public List<BuyerRequest> getSelectedRequests() {
        return new ArrayList<>(selectedRequests); // Return a copy to prevent modification
    }

    /**
     * Adds a request to the selected requests list.
     *
     * @param request The BuyerRequest to select.
     */
    public void selectRequest(BuyerRequest request) {
        if (!selectedRequests.contains(request)) {
            selectedRequests.add(request);
        }
    }

    /**
     * Removes a request from the selected requests list.
     *
     * @param request The BuyerRequest to deselect.
     */
    public void deselectRequest(BuyerRequest request) {
        selectedRequests.remove(request);
    }

    static class BuyerRequestViewHolder extends RecyclerView.ViewHolder {

        private final TextView tvWeight, tvAge, tvSex, tvLocation, tvPhone, tvEmail;
        private final CheckBox approveCheckBox;

        public BuyerRequestViewHolder(@NonNull View itemView) {
            super(itemView);
            tvWeight = itemView.findViewById(R.id.tvWeight);
            tvAge = itemView.findViewById(R.id.tvAge);
            tvSex = itemView.findViewById(R.id.tvSex);
            tvLocation = itemView.findViewById(R.id.tvLocation);
            tvPhone = itemView.findViewById(R.id.tvPhone);
            tvEmail = itemView.findViewById(R.id.tvEmail);
            approveCheckBox = itemView.findViewById(R.id.approveCheckBox);
        }

        public void bind(BuyerRequest request) {
            tvWeight.setText("Weight: " + request.getWeight());
            tvAge.setText("Age: " + request.getAge());
            tvSex.setText("Sex: " + request.getSex());
            tvLocation.setText("Location: " + request.getLocation());
            tvPhone.setText("Phone: " + request.getBuyerPhone());
            tvEmail.setText("Email: " + request.getBuyerEmail());

            // Optionally, set an OnClickListener for the checkbox
            approveCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked) {
                    // Handle approval logic here
                    Log.d("Approval", "Request approved: " + request.getBuyerUID());
                } else {
                    // Handle unapproval logic here
                    Log.d("Approval", "Request unapproved: " + request.getBuyerUID());
                }
            });
        }
    }
}