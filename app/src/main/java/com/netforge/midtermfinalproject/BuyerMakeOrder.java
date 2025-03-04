package com.netforge.midtermfinalproject;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class BuyerMakeOrder extends AppCompatActivity {

    // UI Components
    private TextInputEditText weightEditText, ageEditText, locationEditText, buyerPhoneEditText, buyerEmailEditText;
    private RadioGroup sexRadioGroup;
    private MaterialButton sendButton, cancelButton;
    private ProgressBar progressBar;

    // Firebase
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_buyer_make_order);

        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Initialize Views
        weightEditText = findViewById(R.id.weightEditText);
        ageEditText = findViewById(R.id.ageEditText);
        locationEditText = findViewById(R.id.locationEditText);
        buyerPhoneEditText = findViewById(R.id.buyerPhoneEditText);
        buyerEmailEditText = findViewById(R.id.buyerEmailEditText);
        sexRadioGroup = findViewById(R.id.sexRadioGroup);
        sendButton = findViewById(R.id.sendButton);
        cancelButton = findViewById(R.id.cancelButton);
        progressBar = findViewById(R.id.progressBar);

        // Set OnClickListeners
        sendButton.setOnClickListener(v -> sendRequestToFirestore());
        cancelButton.setOnClickListener(v -> finish()); // Close the activity on cancel

        // Handle system bars (status bar and navigation bar)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    /**
     * Sends the buyer request to Firestore.
     */
    private void sendRequestToFirestore() {
        // Show progress bar
        progressBar.setVisibility(View.VISIBLE);

        // Get user inputs
        String weight = weightEditText.getText().toString().trim();
        String age = ageEditText.getText().toString().trim();
        String location = locationEditText.getText().toString().trim();
        String phone = buyerPhoneEditText.getText().toString().trim();
        String email = buyerEmailEditText.getText().toString().trim();
        String sex = getSelectedSex();

        // Validate inputs
        if (weight.isEmpty() || age.isEmpty() || location.isEmpty() || phone.isEmpty() || email.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(View.GONE);
            return;
        }

        try {
            double weightValue = Double.parseDouble(weight); // Parse weight as a double
            int ageValue = Integer.parseInt(age); // Parse age as an integer

            // Get the current user's ID
            String buyerUID = mAuth.getCurrentUser().getUid();
            if (buyerUID == null) {
                Toast.makeText(this, "User authentication failed. Please log in again.", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
                return;
            }

            // Create a BuyerRequest object
            BuyerRequest request = new BuyerRequest(
                    buyerUID,
                    weightValue,
                    ageValue,
                    sex,
                    location,
                    phone,
                    email
            );

            // Save the request to Firestore
            saveRequestToFirestore(request);

        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid weight or age value", Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(View.GONE);
        }
    }

    /**
     * Saves the buyer request to Firestore.
     */
    private void saveRequestToFirestore(BuyerRequest request) {
        // Generate a unique document ID for the request
        DocumentReference requestDoc = db.collection("AnimalRequests").document();

        // Save the request data
        requestDoc.set(request)
                .addOnSuccessListener(aVoid -> {
                    runOnUiThread(() -> { // Ensure updates happen on the UI thread
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(this, "Request sent successfully!", Toast.LENGTH_SHORT).show();
                        clearFields(); // Clear input fields after successful submission
                    });
                })
                .addOnFailureListener(e -> {
                    runOnUiThread(() -> { // Ensure updates happen on the UI thread
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(this, "Failed to send request: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        Log.e("Firestore", "Error saving request", e);
                    });
                });
    }

    /**
     * Clears all input fields.
     */
    private void clearFields() {
        weightEditText.setText("");
        ageEditText.setText("");
        locationEditText.setText("");
        buyerPhoneEditText.setText("");
        buyerEmailEditText.setText("");
        sexRadioGroup.clearCheck(); // Clear the selected radio button
    }

    /**
     * Gets the selected sex from the RadioGroup.
     *
     * @return The selected sex ("Male" or "Female").
     */
    private String getSelectedSex() {
        int selectedId = sexRadioGroup.getCheckedRadioButtonId();
        if (selectedId == R.id.maleRadioButton) {
            return "Male";
        } else if (selectedId == R.id.femaleRadioButton) {
            return "Female";
        }
        return ""; // Default value if no radio button is selected
    }
}