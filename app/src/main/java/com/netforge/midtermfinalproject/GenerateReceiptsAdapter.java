package com.netforge.midtermfinalproject;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class GenerateReceiptsAdapter extends RecyclerView.Adapter<GenerateReceiptsAdapter.GenerateReceiptsViewHolder> {

    private List<Livestock> approvedLivestockRequests;
    private Set<String> selectedDocumentIds;

    public GenerateReceiptsAdapter(List<Livestock> approvedLivestockRequests) {
        this.approvedLivestockRequests = approvedLivestockRequests;
        this.selectedDocumentIds = new HashSet<>();
    }

    @NonNull
    @Override
    public GenerateReceiptsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_item_approved_livestock, parent, false);
        return new GenerateReceiptsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GenerateReceiptsViewHolder holder, int position) {
        Livestock livestock = approvedLivestockRequests.get(position);

        if (livestock == null || livestock.getDocumentId() == null) {
            System.out.println("Adapter: Invalid livestock object at position " + position);
            return;
        }

        // Bind data to views
        holder.animalIDTextView.setText("Animal ID: " + livestock.getAnimalID());
        holder.speciesBreedTextView.setText("Species: " + livestock.getSpecies() + " | Breed: " + livestock.getBreed());
        holder.ageWeightTextView.setText("Age: " + livestock.getAge() + " years | Weight: " + livestock.getWeight() + " kg");
        holder.healthStatusTextView.setText("Health Status: " + livestock.getHealthStatus());

        // Set checkbox state
        holder.selectCheckBox.setChecked(selectedDocumentIds.contains(livestock.getDocumentId()));

        // Handle checkbox selection
        holder.selectCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                selectedDocumentIds.add(livestock.getDocumentId());
            } else {
                selectedDocumentIds.remove(livestock.getDocumentId());
            }
        });
    }

    @Override
    public int getItemCount() {
        return approvedLivestockRequests.size();
    }

    /**
     * Returns the IDs of selected documents.
     */
    public List<String> getSelectedDocumentIds() {
        return new ArrayList<>(selectedDocumentIds);
    }

    // ViewHolder class
    public static class GenerateReceiptsViewHolder extends RecyclerView.ViewHolder {

        private TextView animalIDTextView, speciesBreedTextView, ageWeightTextView, healthStatusTextView;
        private CheckBox selectCheckBox;

        public GenerateReceiptsViewHolder(@NonNull View itemView) {
            super(itemView);

            // Initialize views
            animalIDTextView = itemView.findViewById(R.id.animalIDTextView);
            speciesBreedTextView = itemView.findViewById(R.id.speciesBreedTextView);
            ageWeightTextView = itemView.findViewById(R.id.ageWeightTextView);
            healthStatusTextView = itemView.findViewById(R.id.healthStatusTextView);
            selectCheckBox = itemView.findViewById(R.id.selectCheckBox);

            // Log view references for debugging
            if (selectCheckBox == null) {
                System.out.println("ViewHolder: CheckBox is null");
            }
        }
    }
}