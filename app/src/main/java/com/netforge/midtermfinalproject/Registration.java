package com.netforge.midtermfinalproject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class Registration extends AppCompatActivity {

    private EditText fnames, username, phonenumber, id, password;
    private Spinner spinner;
    private Button reGister, cleartxt;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_registration);

        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Initialize views
        fnames = findViewById(R.id.fnames);
        username = findViewById(R.id.username);
        phonenumber = findViewById(R.id.phonenumber);
        id = findViewById(R.id.id);
        password = findViewById(R.id.password);
        spinner = findViewById(R.id.spinner);
        reGister = findViewById(R.id.regbtn);
        cleartxt = findViewById(R.id.cleartxt);

        // Set up spinner adapter
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.options, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        // Handle window insets for edge-to-edge display
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Handle registration button click
        reGister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });

        // Handle clear button click
        cleartxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearFields();
            }
        });
    }

    private void registerUser() {
        String fullName = fnames.getText().toString().trim();
        String userUsername = username.getText().toString().trim();
        String phoneNumber = phonenumber.getText().toString().trim();
        String nationalId = id.getText().toString().trim();
        String pass = password.getText().toString().trim();
        String role = spinner.getSelectedItem().toString();

        // Input validation
        if (fullName.isEmpty() || userUsername.isEmpty() || phoneNumber.isEmpty() || nationalId.isEmpty() || pass.isEmpty() || role.equals("Select Role")) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!userUsername.matches("[a-zA-Z0-9._%+-]+")) {
            Toast.makeText(this, "Invalid username. Use alphanumeric characters only.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (pass.length() < 6) {
            Toast.makeText(this, "Password must be at least 6 characters long.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (role.equals("Select Role")) {
            Toast.makeText(this, "Please select a valid role.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Disable buttons during registration
        reGister.setEnabled(false);
        cleartxt.setEnabled(false);

        // Register user with Firebase Authentication
        mAuth.createUserWithEmailAndPassword(userUsername + "@veristockx.com", pass)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        saveUserData(fullName, userUsername, phoneNumber, nationalId, role);
                        Toast.makeText(Registration.this, "Registration successful!", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        finish(); // Close the registration activity
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(Exception e) {
                        reGister.setEnabled(true);
                        cleartxt.setEnabled(true);
                        Toast.makeText(Registration.this, "Registration failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void saveUserData(String fullName, String userUsername, String phoneNumber, String nationalId, String role) {
        String userId = mAuth.getCurrentUser().getUid();
        DocumentReference userDoc = db.collection("users").document(userId);

        User user = new User(fullName, userUsername, phoneNumber, nationalId, role);

        userDoc.set(user)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(Registration.this, "User data saved successfully", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(Exception e) {
                        Toast.makeText(Registration.this, "Failed to save user data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void clearFields() {
        fnames.setText("");
        username.setText("");
        phonenumber.setText("");
        id.setText("");
        password.setText("");
    }
}

// Model class for User
class User {
    private String fullName, username, phoneNumber, nationalId, role;

    public User() {} // Required empty constructor for Firestore

    public User(String fullName, String username, String phoneNumber, String nationalId, String role) {
        this.fullName = fullName;
        this.username = username;
        this.phoneNumber = phoneNumber;
        this.nationalId = nationalId;
        this.role = role;
    }

    public String getFullName() {
        return fullName;
    }

    public String getUsername() {
        return username;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getNationalId() {
        return nationalId;
    }

    public String getRole() {
        return role;
    }
}