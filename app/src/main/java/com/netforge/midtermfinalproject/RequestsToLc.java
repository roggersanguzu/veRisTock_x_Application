package com.netforge.midtermfinalproject;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;

public class RequestsToLc extends AppCompatActivity {

    private RecyclerView farmerRequestsRecyclerView;
    private Button approveButton, refreshButton; // Added refreshButton
    private FirebaseFirestore db;
    private List<Livestock> livestockRequests;
    private LivestockRequestAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_requests_to_lc);

        // Handle window insets for edge-to-edge display
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize Firebase Firestore
        db = FirebaseFirestore.getInstance();

        // Initialize UI components
        farmerRequestsRecyclerView = findViewById(R.id.farmerRequestsRecyclerView);
        approveButton = findViewById(R.id.approveButton);
        refreshButton = findViewById(R.id.refreshButton); // Initialize refreshButton

        // Set up RecyclerView
        livestockRequests = new ArrayList<>();
        adapter = new LivestockRequestAdapter(livestockRequests);
        farmerRequestsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        farmerRequestsRecyclerView.setAdapter(adapter);

        // Fetch livestock requests when the activity starts
        fetchLivestockRequests();

        // Set up approve button
        approveButton.setOnClickListener(v -> approveSelectedRequests());

        // Set up refresh button
        refreshButton.setOnClickListener(v -> fetchLivestockRequests()); // Trigger data fetching
    }

    /**
     * Fetches all livestock requests from Firestore.
     */
    private void fetchLivestockRequests() {
        db.collection("livestock") // Removed the filter for testing
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    livestockRequests.clear(); // Clear existing data
                    for (DocumentSnapshot document : queryDocumentSnapshots) {
                        Livestock livestock = document.toObject(Livestock.class);
                        if (livestock != null) {
                            livestock.setDocumentId(document.getId()); // Store the document ID
                            livestockRequests.add(livestock);

                            // Log each livestock entry for debugging
                            System.out.println("Firestore: Fetched livestock request: " + livestock.toString());
                        }
                    }

                    // Refresh the RecyclerView adapter
                    adapter.notifyDataSetChanged();

                    // Notify if no data is found
                    if (livestockRequests.isEmpty()) {
                        Toast.makeText(this, "No livestock requests found.", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to fetch livestock requests: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    System.out.println("Firestore Error: " + e.getMessage());
                });
    }

    /**
     * Approves selected livestock requests.
     */
    private void approveSelectedRequests() {
        List<String> selectedDocumentIds = adapter.getSelectedDocumentIds();
        if (selectedDocumentIds.isEmpty()) {
            Toast.makeText(this, "Please select at least one request to approve.", Toast.LENGTH_SHORT).show();
            return;
        }

        for (String documentId : selectedDocumentIds) {
            db.collection("livestock").document(documentId).update("status", "Approved")
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(this, "Selected requests approved successfully!", Toast.LENGTH_SHORT).show();
                        fetchLivestockRequests(); // Refresh the list after approval
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Failed to approve request: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        }
    }
}