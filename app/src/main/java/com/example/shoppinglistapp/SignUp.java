package com.example.shoppinglistapp;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.shoppinglistapp.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignUp extends AppCompatActivity {

    private EditText nameInput, phoneNumberInput, emailInput, passwordInput, confirmPasswordInput;
    private Button signUpButton;
    private TextView loginLink;
    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        // Initialize Firebase Authentication and Realtime Database
        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("users");

        // Initialize UI elements
        nameInput = findViewById(R.id.signupName);
        phoneNumberInput = findViewById(R.id.signupNumber);
        emailInput = findViewById(R.id.signupEmail);
        passwordInput = findViewById(R.id.signupPassword);
        confirmPasswordInput = findViewById(R.id.signupPasswordTwo);
        signUpButton = findViewById(R.id.signupButton);
        loginLink = findViewById(R.id.loginLink);

        // Set up login link to navigate back to the login screen
        setupLoginLink();

        // Handle sign-up button click
        signUpButton.setOnClickListener(v -> registerUser());
    }

    private void setupLoginLink() {
        loginLink.setOnClickListener(v -> {
            // Navigate to LoginActivity
            startActivity(new Intent(SignUp.this, LoginActivity.class));
            finish();
        });
    }

    private void registerUser() {
        // Get input values
        String name = nameInput.getText().toString().trim();
        String phoneNumber = phoneNumberInput.getText().toString().trim();
        String email = emailInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();
        String confirmPassword = confirmPasswordInput.getText().toString().trim();

        // Validate inputs
        if (!validateInputs(name, phoneNumber, email, password, confirmPassword)) {
            return; // Stop if validation fails
        }

        // Create user in Firebase Authentication
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // User created successfully
                        FirebaseUser firebaseUser = mAuth.getCurrentUser();
                        if (firebaseUser != null) {
                            String userId = firebaseUser.getUid();
                            // Save user details to Firebase Realtime Database
                            User newUser = new User(userId, name, phoneNumber, email, "User"); // Default role
                            databaseReference.child(userId).setValue(newUser)
                                    .addOnSuccessListener(aVoid -> {
                                        sendEmailVerification(firebaseUser);
                                        Toast.makeText(this, "Account created! Please verify your email.", Toast.LENGTH_LONG).show();
                                        startActivity(new Intent(SignUp.this, LoginActivity.class));
                                        finish();
                                    })
                                    .addOnFailureListener(e -> Toast.makeText(this, "Error saving user: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                        }
                    } else {
                        // Handle registration error
                        Toast.makeText(this, "Sign Up failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void sendEmailVerification(FirebaseUser firebaseUser) {
        firebaseUser.sendEmailVerification()
                .addOnSuccessListener(aVoid -> Toast.makeText(this, "Verification email sent. Please check your inbox.", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(this, "Failed to send verification email: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private boolean validateInputs(String name, String phoneNumber, String email, String password, String confirmPassword) {
        // Validate name
        if (TextUtils.isEmpty(name)) {
            nameInput.setError("Name is required.");
            return false;
        }
        if (name.length() < 3) {
            nameInput.setError("Name must be at least 3 characters.");
            return false;
        }

        // Validate phone number
        if (TextUtils.isEmpty(phoneNumber)) {
            phoneNumberInput.setError("Phone number is required.");
            return false;
        }
        if (phoneNumber.length() < 10) {
            phoneNumberInput.setError("Enter a valid phone number.");
            return false;
        }

        // Validate email
        if (TextUtils.isEmpty(email)) {
            emailInput.setError("Email is required.");
            return false;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailInput.setError("Enter a valid email address.");
            return false;
        }

        // Validate password
        if (TextUtils.isEmpty(password)) {
            passwordInput.setError("Password is required.");
            return false;
        }
        if (password.length() < 8 || !password.matches(".*[A-Z].*") || !password.matches(".*[!@#$%^&*].*")) {
            passwordInput.setError("Password must be 8+ characters, include a capital letter, and a special character.");
            return false;
        }

        // Validate confirm password
        if (TextUtils.isEmpty(confirmPassword)) {
            confirmPasswordInput.setError("Please confirm your password.");
            return false;
        }
        if (!password.equals(confirmPassword)) {
            confirmPasswordInput.setError("Passwords do not match.");
            return false;
        }

        return true; // All inputs are valid
    }

}
