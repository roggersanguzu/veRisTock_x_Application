package com.netforge.midtermfinalproject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity {

    private EditText username, password;
    private Button btnLogin, btnRegister;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    // Define role constants to avoid hardcoding strings
    private static final String ROLE_LC_OFFICIAL = "Local Council Official";
    private static final String ROLE_FARMER = "Farmer";
    private static final String ROLE_BUYER = "Buyer";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this); // Enable edge-to-edge display
        setContentView(R.layout.activity_main);

        // Handle window insets for edge-to-edge display
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Initialize UI components
        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        btnLogin = findViewById(R.id.loginbtn);
        btnRegister = findViewById(R.id.createbtn);

        // Handle login button click
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginUser();
            }
        });

        // Handle register button click
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, Registration.class));
            }
        });
    }

    /**
     * Authenticates the user with Firebase Authentication.
     */
    private void loginUser() {
        String userUsername = username.getText().toString().trim();
        String pass = password.getText().toString().trim();

        // Input validation
        if (userUsername.isEmpty() || pass.isEmpty()) {
            Toast.makeText(this, "Please enter username and password", Toast.LENGTH_SHORT).show();
            return;
        }

        // Show loading indicator or disable buttons during authentication
        btnLogin.setEnabled(false);
        btnRegister.setEnabled(false);

        // Authenticate user
        mAuth.signInWithEmailAndPassword(userUsername + "@veristockx.com", pass)
                .addOnSuccessListener(authResult -> {
                    FirebaseUser user = authResult.getUser();
                    if (user != null) {
                        fetchUserRole(user.getUid());
                    } else {
                        Toast.makeText(this, "Authentication failed. User ID is null.", Toast.LENGTH_SHORT).show();
                        enableButtons(); // Re-enable buttons on failure
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Login failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    System.out.println("Firebase Auth: Login failed. Error: " + e.getMessage());
                    enableButtons(); // Re-enable buttons on failure
                });
    }

    /**
     * Fetches the user's role from Firestore.
     */
    private void fetchUserRole(String userId) {
        db.collection("users").document(userId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String role = documentSnapshot.getString("role");
                        if (role == null || role.isEmpty()) {
                            Toast.makeText(this, "Invalid role assigned. Please contact support.", Toast.LENGTH_SHORT).show();
                            enableButtons(); // Re-enable buttons if role is invalid
                        } else {
                            navigateToRoleBasedActivity(role);
                        }
                    } else {
                        Toast.makeText(this, "User data not found. Please register first.", Toast.LENGTH_SHORT).show();
                        enableButtons(); // Re-enable buttons if data is missing
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to retrieve user data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    System.out.println("Firestore: Failed to fetch user role. Error: " + e.getMessage());
                    enableButtons(); // Re-enable buttons on failure
                });
    }

    /**
     * Navigates to the appropriate dashboard based on the user's role.
     */
    private void navigateToRoleBasedActivity(String role) {
        Intent intent;
        switch (role) {
            case ROLE_LC_OFFICIAL:
                intent = new Intent(this, LC_landingpage.class);
                break;
            case ROLE_FARMER:
                intent = new Intent(this, Landing_Page.class); // Replace with Farmer-specific activity
                break;
            case ROLE_BUYER:
                intent = new Intent(this, BuyerLanding.class); // Replace with Buyer-specific activity
                break;
            default:
                Toast.makeText(this, "Unknown role: " + role + ". Please contact support.", Toast.LENGTH_SHORT).show();
                enableButtons(); // Re-enable buttons if role is unknown
                return;
        }

        startActivity(intent);
        finish(); // Close the login activity
    }

    /**
     * Re-enables the login and register buttons after an operation.
     */
    private void enableButtons() {
        btnLogin.setEnabled(true);
        btnRegister.setEnabled(true);
    }
}