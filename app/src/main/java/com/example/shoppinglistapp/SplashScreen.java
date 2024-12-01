package com.example.shoppinglistapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SplashScreen extends AppCompatActivity {

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        ImageView logo = findViewById(R.id.splash_logo);

        // Load animations
        Animation translateAnimation = AnimationUtils.loadAnimation(this, R.anim.translate);
        Animation scaleAnimation = AnimationUtils.loadAnimation(this, R.anim.scale);

        // Start animations
        logo.startAnimation(translateAnimation);
        logo.startAnimation(scaleAnimation);

        // Delay and navigate based on login status
        new Handler().postDelayed(() -> {
            FirebaseUser currentUser = mAuth.getCurrentUser();
            if (currentUser != null) {
                // User is logged in, navigate to ShoppingListActivity
                Intent intent = new Intent(SplashScreen.this, ShoppingListActivity.class);
                startActivity(intent);
            } else {
                // User is not logged in, navigate to LoginActivity
                Intent intent = new Intent(SplashScreen.this, LoginActivity.class);
                startActivity(intent);
            }
            finish();
        }, 2000); // 2-second delay
    }
}
