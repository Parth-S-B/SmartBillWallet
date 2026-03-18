package com.example.smartbillwallet;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LoginActivity extends AppCompatActivity {
    private EditText emailInput, passwordInput;
    private Button loginButton;
    private TextView registerTextView;
    private ExecutorService executorService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        emailInput = findViewById(R.id.emailInput);
        passwordInput = findViewById(R.id.passwordInput);
        loginButton = findViewById(R.id.loginButton);
        registerTextView = findViewById(R.id.registerTextView);
        executorService = Executors.newSingleThreadExecutor();

        loginButton.setOnClickListener(v -> loginUser());
        registerTextView.setOnClickListener(v -> {
            // Navigate to Registration Activity
            startActivity(new Intent(LoginActivity.this, RegistrationActivity.class));
        });
    }

    private void loginUser() {
        String email = emailInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Perform the login validation on a background thread
        executorService.execute(() -> {
            BillDatabase db = BillDatabase.getInstance(this);
            UserDao userDao = db.userDao();
            User user = userDao.getUserByEmail(email);

            runOnUiThread(() -> {
                if (user != null && user.getPassword().equals(password)) {
                    // Login successful
                    Toast.makeText(this, "Login successful!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    // Login failed
                    Toast.makeText(this, "Invalid email or password", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Shutdown the ExecutorService to avoid memory leaks
        executorService.shutdown();
    }
}
