package com.netforge.midtermfinalproject;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
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
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Documents_Upload extends AppCompatActivity {

    // UI Components
    private EditText idNumberEditText;
    private ImageView idDocumentPreview, facePreview;
    private Button uploadIdButton, captureFaceButton, uploadFaceButton, submitButton;
    private ProgressBar progressBar;

    // Firebase
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    // Image URIs
    private Uri idDocumentUri = null;
    private Uri facePhotoUri = null;

    // Activity Result Launchers
    private final ActivityResultLauncher<Intent> idPickerLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), this::handleIdDocumentSelection);

    private final ActivityResultLauncher<Intent> cameraLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), this::handleFaceCapture);

    private final ActivityResultLauncher<Intent> facePickerLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), this::handleFacePhotoSelection);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_documents_upload);

        // Request storage permission
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        }

        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Initialize UI components
        idNumberEditText = findViewById(R.id.idNumberEditText);
        idDocumentPreview = findViewById(R.id.idDocumentPreview);
        facePreview = findViewById(R.id.facePreview);
        uploadIdButton = findViewById(R.id.uploadIdButton);
        captureFaceButton = findViewById(R.id.captureFaceButton);
        uploadFaceButton = findViewById(R.id.uploadFaceButton);
        submitButton = findViewById(R.id.submitButton);
        progressBar = findViewById(R.id.progressBar);

        // Verify initialization
        if (idNumberEditText == null || idDocumentPreview == null || facePreview == null ||
                uploadIdButton == null || captureFaceButton == null || uploadFaceButton == null ||
                submitButton == null || progressBar == null) {
            Log.e("Documents_Upload", "One or more UI components failed to initialize");
            Toast.makeText(this, "UI initialization error", Toast.LENGTH_SHORT).show();
            return;
        }

        // Set button listeners
        uploadIdButton.setOnClickListener(v -> openIdPicker());
        captureFaceButton.setOnClickListener(v -> captureFace());
        uploadFaceButton.setOnClickListener(v -> openFacePicker());
        submitButton.setOnClickListener(v -> submitDocuments());
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1 && grantResults.length > 0 && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Storage permission denied", Toast.LENGTH_SHORT).show();
        }
    }

    // Image Picker and Capture Methods
    private void openIdPicker() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        idPickerLauncher.launch(intent);
    }

    private void captureFace() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraLauncher.launch(intent);
    }

    private void openFacePicker() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        facePickerLauncher.launch(intent);
    }

    // Handle Image Selection/Capture Results
    private void handleIdDocumentSelection(ActivityResult result) {
        if (result.getResultCode() == RESULT_OK && result.getData() != null) {
            idDocumentUri = result.getData().getData();
            idDocumentPreview.setImageURI(idDocumentUri);
            idDocumentPreview.setVisibility(View.VISIBLE);
        }
    }

    private void handleFaceCapture(ActivityResult result) {
        if (result.getResultCode() == RESULT_OK && result.getData() != null) {
            Bundle extras = result.getData().getExtras();
            if (extras != null) {
                Bitmap faceBitmap = (Bitmap) extras.get("data");
                facePreview.setImageBitmap(faceBitmap);
                facePreview.setVisibility(View.VISIBLE);
                facePhotoUri = getImageUri(faceBitmap);
            }
        }
    }

    private void handleFacePhotoSelection(ActivityResult result) {
        if (result.getResultCode() == RESULT_OK && result.getData() != null) {
            facePhotoUri = result.getData().getData();
            facePreview.setImageURI(facePhotoUri);
            facePreview.setVisibility(View.VISIBLE);
        }
    }

    // Submit Documents
    private void submitDocuments() {
        if (progressBar != null) {
            progressBar.setVisibility(View.VISIBLE);
        } else {
            Log.e("Documents_Upload", "ProgressBar is null in submitDocuments");
        }

        String idNumber = idNumberEditText.getText().toString().trim();

        // Validate inputs
        if (idNumber.isEmpty()) {
            Toast.makeText(this, "Please enter your ID number", Toast.LENGTH_SHORT).show();
            hideProgressBar();
            return;
        }
        if (idDocumentUri == null) {
            Toast.makeText(this, "Please upload an ID document", Toast.LENGTH_SHORT).show();
            hideProgressBar();
            return;
        }
        if (facePhotoUri == null) {
            Toast.makeText(this, "Please capture or upload a face photo", Toast.LENGTH_SHORT).show();
            hideProgressBar();
            return;
        }

        String userId = mAuth.getCurrentUser() != null ? mAuth.getCurrentUser().getUid() : null;
        if (userId == null) {
            Toast.makeText(this, "User authentication failed. Please log in again.", Toast.LENGTH_SHORT).show();
            hideProgressBar();
            return;
        }

        // Create DocumentData object (for local capturing, only idNumber will be saved)
        DocumentData documentData = new DocumentData(idNumber, userId);
        documentData.setIdDocumentUrl(idDocumentUri.toString());
        documentData.setFacePhotoUrl(facePhotoUri.toString());

        // Save only the idNumber to Firestore and send to IDNumberVerification
        saveToFirestore(idNumber, userId);
    }

    // Save only idNumber to Firestore
    private void saveToFirestore(String idNumber, String userId) {
        DocumentReference docRef = db.collection("documents").document(userId);

        Map<String, Object> data = new HashMap<>();
        data.put("idNumber", idNumber);

        docRef.set(data)
                .addOnSuccessListener(aVoid -> runOnUiThread(() -> {
                    hideProgressBar();
                    Toast.makeText(this, "ID number uploaded successfully!", Toast.LENGTH_SHORT).show();
                    clearFields();
                    submitButton.setEnabled(true);

                }))
                .addOnFailureListener(e -> runOnUiThread(() -> {
                    hideProgressBar();
                    Toast.makeText(this, "Failed to save ID number: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    submitButton.setEnabled(true);
                }));
    }

    // Clear Fields
    private void clearFields() {
        idNumberEditText.setText("");
        idDocumentPreview.setImageDrawable(null);
        facePreview.setImageDrawable(null);
        idDocumentPreview.setVisibility(View.GONE);
        facePreview.setVisibility(View.GONE);
        idDocumentUri = null;
        facePhotoUri = null;
    }

    // Convert Bitmap to Uri
    private Uri getImageUri(Bitmap bitmap) {
        try {
            File cacheDir = getCacheDir();
            File tempFile = File.createTempFile("facePhoto", ".jpg", cacheDir);
            FileOutputStream out = new FileOutputStream(tempFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();

            Uri uri = FileProvider.getUriForFile(this,
                    "com.netforge.midtermfinalproject.fileprovider", tempFile);
            Log.d("Documents_Upload", "Generated URI for face photo: " + uri.toString());
            return uri;
        } catch (IOException e) {
            Log.e("Documents_Upload", "Failed to generate URI from bitmap", e);
            return null;
        }
    }

    // Helper method to hide ProgressBar with null check
    private void hideProgressBar() {
        if (progressBar != null) {
            progressBar.setVisibility(View.GONE);
        } else {
            Log.e("Documents_Upload", "ProgressBar is null when hiding");
        }
    }
}

class DocumentData {
    private String idNumber;
    private String userId;
    private String idDocumentUrl;
    private String facePhotoUrl;

    public DocumentData(String idNumber, String userId) {
        this.idNumber = idNumber;
        this.userId = userId;
    }

    public DocumentData() {}

    public String getIdNumber() { return idNumber; }
    public String getUserId() { return userId; }
    public String getIdDocumentUrl() { return idDocumentUrl; }
    public String getFacePhotoUrl() { return facePhotoUrl; }

    public void setIdDocumentUrl(String idDocumentUrl) { this.idDocumentUrl = idDocumentUrl; }
    public void setFacePhotoUrl(String facePhotoUrl) { this.facePhotoUrl = facePhotoUrl; }
}