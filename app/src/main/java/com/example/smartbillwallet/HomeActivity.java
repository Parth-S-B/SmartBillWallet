package com.example.smartbillwallet;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class HomeActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Initialize buttons
        Button captureButton = findViewById(R.id.captureButton);
        Button selectButton = findViewById(R.id.selectButton);
        Button myBillsButton = findViewById(R.id.myBillsButton);
        Button logoutButton = findViewById(R.id.logoutButton);

        // Capture Bill button click
        captureButton.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, CaptureBillActivity.class);
            startActivity(intent);
        });

        // Select Bill button click
        selectButton.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, SelectBillActivity.class);
            startActivity(intent);
        });

        // My Bills button click
        myBillsButton.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, MyBillsActivity.class);
            startActivity(intent);
        });

        // Logout button click
        logoutButton.setOnClickListener(v -> {
            // Clear the user's login state
            SharedPreferences sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.remove("is_logged_in"); // Clear the login status
            editor.remove("user_email");   // Optionally clear the email
            editor.apply();

            Toast.makeText(HomeActivity.this, "Logged out successfully", Toast.LENGTH_SHORT).show();

            // Navigate back to Login Activity
            Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
            startActivity(intent);
            finish(); // Close the HomeActivity
        });
    }
}
