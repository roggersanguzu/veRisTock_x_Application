package com.netforge.midtermfinalproject;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot; // Import DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot; // Import QuerySnapshot

import java.util.ArrayList;
import java.util.List;

public class BuyerRequestsActivity extends AppCompatActivity {

    // UI Components
    private RecyclerView buyerRequestsRecyclerView;

    // Firebase
    private FirebaseFirestore db;
    private CollectionReference animalRequestsRef;

    // Data and Adapter
    private List<BuyerRequest> buyerRequestsList = new ArrayList<>();
    private BuyerRequestsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_buyer_requests);

        // Initialize Firebase
        db = FirebaseFirestore.getInstance();
        animalRequestsRef = db.collection("AnimalRequests");

        // Initialize Views
        buyerRequestsRecyclerView = findViewById(R.id.buyerRequestsRecyclerView);

        // Set up RecyclerView
        setupRecyclerView();

        // Fetch buyer requests from Firestore
        fetchBuyerRequests();
    }

    /**
     * Sets up the RecyclerView with a layout manager and adapter.
     */
    private void setupRecyclerView() {
        // Use a LinearLayoutManager for vertical scrolling
        buyerRequestsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize the adapter with an empty list
        adapter = new BuyerRequestsAdapter(buyerRequestsList);

        // Set the adapter to the RecyclerView
        buyerRequestsRecyclerView.setAdapter(adapter);
    }

    /**
     * Fetches buyer requests from Firestore and updates the RecyclerView.
     */
    private void fetchBuyerRequests() {
        animalRequestsRef.get() // Fetch all documents in the AnimalRequests collection
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    // Clear the existing list
                    buyerRequestsList.clear();

                    // Add each document as a BuyerRequest object
                    for (DocumentSnapshot document : queryDocumentSnapshots) { // Ensure DocumentSnapshot is imported
                        BuyerRequest request = document.toObject(BuyerRequest.class);
                        if (request != null) {
                            buyerRequestsList.add(request);
                        }
                    }

                    // Notify the adapter that the data has changed
                    runOnUiThread(() -> adapter.notifyDataSetChanged());
                })
                .addOnFailureListener(e -> {
                    // Handle errors
                    Log.e("Firestore", "Error fetching buyer requests", e);
                    Toast.makeText(this, "Failed to load buyer requests: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    // Handle system bars (status bar and navigation bar)
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}