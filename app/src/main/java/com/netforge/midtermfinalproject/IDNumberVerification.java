package com.netforge.midtermfinalproject;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class IDNumberVerification extends AppCompatActivity {

    private static final String TAG = "IDNumberVerification";

    private RecyclerView transactionsRecyclerView;
    private ProgressBar progressBar;
    private IDNumberAdapter adapter;
    private List<String> idNumberList;
    private FirebaseFirestore db;
    private ListenerRegistration listener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_idnumber_verification);

        // Handle window insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize Firebase
        db = FirebaseFirestore.getInstance();

        // Initialize UI components
        transactionsRecyclerView = findViewById(R.id.transactionsRecyclerView);
        progressBar = findViewById(R.id.progressBar);

        // Validate UI components
        if (transactionsRecyclerView == null) {
            Log.e(TAG, "RecyclerView not found in layout");
            Toast.makeText(this, "Error: RecyclerView not found", Toast.LENGTH_LONG).show();
            return;
        }
        if (progressBar == null) {
            Log.e(TAG, "ProgressBar not found in layout");
            Toast.makeText(this, "Error: ProgressBar not found", Toast.LENGTH_LONG).show();
            return;
        }

        // Setup RecyclerView
        transactionsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        idNumberList = new ArrayList<>();
        adapter = new IDNumberAdapter(idNumberList);
        transactionsRecyclerView.setAdapter(adapter);

        // Fetch all submitted IDs from 'documents' collection
        fetchAllSubmittedIds();
    }

    private void fetchAllSubmittedIds() {
        Log.d(TAG, "Fetching all IDs from 'documents' collection");
        showLoading(true);

        db.collection("documents")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    idNumberList.clear();
                    Log.d(TAG, "Total documents fetched: " + queryDocumentSnapshots.size());

                    if (queryDocumentSnapshots.isEmpty()) {
                        Log.w(TAG, "No documents found in 'documents' collection");
                        Toast.makeText(this, "No IDs found in the database", Toast.LENGTH_SHORT).show();
                    } else {
                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            String documentId = document.getId();
                            String idNumber = document.getString("idNumber");
                            Log.d(TAG, "Document ID: " + documentId + ", idNumber: " + idNumber);

                            if (idNumber != null && !idNumber.trim().isEmpty()) {
                                idNumberList.add(idNumber);
                                Log.d(TAG, "Added ID to list: " + idNumber);
                            } else {
                                Log.w(TAG, "No valid 'idNumber' field in document: " + documentId);
                            }
                        }
                    }

                    adapter.notifyDataSetChanged();
                    Log.d(TAG, "Updated list size: " + idNumberList.size());
                    showLoading(false);

                    if (idNumberList.isEmpty()) {
                        Toast.makeText(this, "No valid ID numbers found", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    showLoading(false);
                    Log.e(TAG, "Failed to fetch IDs from 'documents': " + e.getMessage());
                    Toast.makeText(this, "Failed to fetch IDs: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }

    private void showLoading(boolean isLoading) {
        progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        transactionsRecyclerView.setVisibility(isLoading ? View.GONE : View.VISIBLE);
        Log.d(TAG, "Loading state changed: " + (isLoading ? "Showing" : "Hiding"));
    }

    @Override
    protected void onStart() {
        super.onStart();
        listener = db.collection("documents")
                .addSnapshotListener((snapshots, e) -> {
                    if (e != null) {
                        Log.w(TAG, "Real-time listener failed: " + e.getMessage());
                        return;
                    }
                    Log.d(TAG, "Documents collection changed, refreshing data");
                    fetchAllSubmittedIds();
                });
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (listener != null) {
            listener.remove();
            listener = null;
            Log.d(TAG, "Real-time listener removed");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (listener != null) {
            listener.remove();
        }
    }
}