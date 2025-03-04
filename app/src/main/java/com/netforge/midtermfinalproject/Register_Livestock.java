package com.netforge.midtermfinalproject;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.FileNotFoundException;
import java.io.InputStream;

public class Register_Livestock extends AppCompatActivity {

    // UI Components
    private EditText etAnimalID, etSpecies, etBreed, etAge, etWeight, etHealthStatus, etVaccinationHistory;
    private Button btnUploadDocument, btnSubmit;
    private ImageView ivUploadedDocument;
    private ProgressBar progressBar;

    // Firebase
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    // Image URI
    private Uri uploadedImageUri = null;

    // Activity Result Launcher for selecting images
    private final ActivityResultLauncher<Intent> imagePickerLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), this::handleImageSelection);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_livestock);

        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Initialize UI components
        etAnimalID = findViewById(R.id.etAnimalID);
        etSpecies = findViewById(R.id.etSpecies);
        etBreed = findViewById(R.id.etBreed);
        etAge = findViewById(R.id.etAge);
        etWeight = findViewById(R.id.etWeight);
        etHealthStatus = findViewById(R.id.etHealthStatus);
        etVaccinationHistory = findViewById(R.id.etVaccinationHistory);
        btnUploadDocument = findViewById(R.id.btnUploadDocument);
        btnSubmit = findViewById(R.id.btnSubmit);
        ivUploadedDocument = findViewById(R.id.ivUploadedDocument);
        progressBar = findViewById(R.id.progressBar);

        // Set up upload button
        btnUploadDocument.setOnClickListener(v -> openImagePicker());

        // Set up submit button
        btnSubmit.setOnClickListener(v -> submitLivestockData());
    }

    /**
     * Opens the image picker to allow the user to select an image.
     */
    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*"); // Allow only images
        imagePickerLauncher.launch(intent);
    }

    /**
     * Handles the result of the image picker.
     */
    private void handleImageSelection(ActivityResult result) {
        if (result.getResultCode() == RESULT_OK && result.getData() != null) {
            uploadedImageUri = result.getData().getData(); // Get the selected image URI
            try {
                InputStream inputStream = getContentResolver().openInputStream(uploadedImageUri);
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream); // Decode the image
                ivUploadedDocument.setImageBitmap(bitmap); // Set the image to ImageView
                ivUploadedDocument.setVisibility(View.VISIBLE); // Make the ImageView visible
            } catch (FileNotFoundException e) {
                Toast.makeText(this, "Error loading image", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * Handles the submission of livestock data.
     */
    private void submitLivestockData() {
        // Show progress bar
        progressBar.setVisibility(View.VISIBLE);

        // Get user inputs
        String animalID = etAnimalID.getText().toString().trim();
        String species = etSpecies.getText().toString().trim();
        String breed = etBreed.getText().toString().trim();
        String ageStr = etAge.getText().toString().trim();
        String weightStr = etWeight.getText().toString().trim();
        String healthStatus = etHealthStatus.getText().toString().trim();
        String vaccinationHistory = etVaccinationHistory.getText().toString().trim();

        // Validate inputs
        if (animalID.isEmpty() || species.isEmpty() || breed.isEmpty() || ageStr.isEmpty() || weightStr.isEmpty()) {
            Toast.makeText(this, "Please fill all required fields", Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(View.GONE);
            return;
        }

        if (uploadedImageUri == null) {
            Toast.makeText(this, "Please upload an image", Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(View.GONE);
            return;
        }

        try {
            int age = Integer.parseInt(ageStr);
            double weight = Double.parseDouble(weightStr);

            // Get the current user's ID
            String farmerId = mAuth.getCurrentUser().getUid();
            if (farmerId == null) {
                Toast.makeText(this, "User authentication failed. Please log in again.", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
                return;
            }

            // Create a Livestock object
            Livestock livestock = new Livestock(
                    animalID,
                    species,
                    breed,
                    age,
                    weight,
                    healthStatus,
                    vaccinationHistory,
                    uploadedImageUri.toString(), // Store the image URI as a string
                    farmerId
            );

            // Save the livestock data to Firestore
            saveLivestockData(livestock);

        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid age or weight value", Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(View.GONE);
        }
    }

    /**
     * Saves the livestock data to Firestore.
     */
    private void saveLivestockData(Livestock livestock) {
        DocumentReference livestockDoc = db.collection("livestock").document(livestock.getAnimalID());

        livestockDoc.set(livestock)
                .addOnSuccessListener(aVoid -> {
                    runOnUiThread(() -> { // Ensure updates happen on the UI thread
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(this, "Livestock registered successfully!", Toast.LENGTH_SHORT).show();
                        clearFields(); // Clear input fields after successful submission
                    });
                })
                .addOnFailureListener(e -> {
                    runOnUiThread(() -> { // Ensure updates happen on the UI thread
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(this, "Failed to register livestock: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
                });
    }

    /**
     * Clears all input fields.
     */
    private void clearFields() {
        etAnimalID.setText("");
        etSpecies.setText("");
        etBreed.setText("");
        etAge.setText("");
        etWeight.setText("");
        etHealthStatus.setText("");
        etVaccinationHistory.setText("");
        ivUploadedDocument.setImageDrawable(null); // Clear the uploaded image
        ivUploadedDocument.setVisibility(View.GONE); // Hide the ImageView
    }
}