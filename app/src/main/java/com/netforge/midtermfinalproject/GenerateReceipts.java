package com.netforge.midtermfinalproject;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
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

public class GenerateReceipts extends AppCompatActivity {

    private RecyclerView approvalsRecyclerView;
    private Button generateReceiptButton;
    private TextView emptyTextView;
    private FirebaseFirestore db;
    private List<Livestock> approvedLivestockRequests;
    private ApprovedLivestockAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_generate_receipts);

        // Handle window insets for edge-to-edge display
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize Firebase Firestore
        db = FirebaseFirestore.getInstance();

        // Initialize UI components
        approvalsRecyclerView = findViewById(R.id.approvedItemsRecyclerView);
        generateReceiptButton = findViewById(R.id.generateReceiptButton);
        emptyTextView = findViewById(R.id.emptyTextView);

        // Set up RecyclerView
        approvedLivestockRequests = new ArrayList<>();
        adapter = new ApprovedLivestockAdapter(approvedLivestockRequests);
        approvalsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        approvalsRecyclerView.setAdapter(adapter);

        // Fetch approved livestock requests
        fetchApprovedLivestockRequests();

        // Set up generate receipt button
        generateReceiptButton.setOnClickListener(v -> generateSelectedReceipts());
    }

    /**
     * Fetches all approved livestock requests from Firestore.
     */
    private void fetchApprovedLivestockRequests() {
        db.collection("livestock")
                .whereEqualTo("status", "Approved") // Filter for approved requests
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    runOnUiThread(() -> { // Ensure updates happen on the UI thread
                        approvedLivestockRequests.clear(); // Clear existing data
                        for (DocumentSnapshot document : queryDocumentSnapshots) {
                            Livestock livestock = document.toObject(Livestock.class);
                            if (livestock != null) {
                                livestock.setDocumentId(document.getId()); // Store the document ID
                                approvedLivestockRequests.add(livestock);

                                // Log each livestock entry for debugging
                                System.out.println("Firestore: Fetched approved livestock request: " + livestock.toString());
                            } else {
                                System.out.println("Firestore: Invalid livestock object found");
                            }
                        }

                        // Refresh the RecyclerView adapter
                        adapter.notifyDataSetChanged();

                        // Toggle visibility of RecyclerView and emptyTextView
                        if (approvedLivestockRequests.isEmpty()) {
                            emptyTextView.setVisibility(View.VISIBLE);
                            approvalsRecyclerView.setVisibility(View.GONE);
                        } else {
                            emptyTextView.setVisibility(View.GONE);
                            approvalsRecyclerView.setVisibility(View.VISIBLE);
                        }
                    });
                })
                .addOnFailureListener(e -> {
                    runOnUiThread(() -> { // Ensure updates happen on the UI thread
                        Toast.makeText(this, "Failed to fetch approved livestock requests: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        System.out.println("Firestore Error: " + e.getMessage());

                        // Show emptyTextView if fetching fails
                        emptyTextView.setVisibility(View.VISIBLE);
                        approvalsRecyclerView.setVisibility(View.GONE);
                    });
                });
    }

    /**
     * Generates receipts for selected livestock items.
     */
    private void generateSelectedReceipts() {
        List<String> selectedDocumentIds = adapter.getSelectedDocumentIds();
        if (selectedDocumentIds.isEmpty()) {
            Toast.makeText(this, "Please select at least one item to generate receipts.", Toast.LENGTH_SHORT).show();
            return;
        }

        for (String documentId : selectedDocumentIds) {
            db.collection("livestock").document(documentId).update("receiptGenerated", true)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(this, "Receipts generated successfully!", Toast.LENGTH_SHORT).show();
                        fetchApprovedLivestockRequests(); // Refresh the list after generating receipts
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Failed to generate receipt: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        }
    }
}