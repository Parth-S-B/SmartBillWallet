package com.example.smartbillwallet;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BillImageActivity extends AppCompatActivity {

    private static final int REQUEST_CODE = 101; // Request code for storage permission
    private ImageView fullImageView;
    private Button shareButton, deleteButton;
    private String imageUriString;
    private BillEntity bill; // Pass the entire bill object from MyBillsActivity

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bill_image);

        fullImageView = findViewById(R.id.fullImageView);
        shareButton = findViewById(R.id.shareButton);


        // Check for permissions
        checkPermissions();

        // Get the bill object and image URI from the intent
        bill = (BillEntity) getIntent().getSerializableExtra("billEntity");
        if (bill != null) {
            imageUriString = bill.getImageUri();
            Log.d("BillImageActivity", "Image URI from bill: " + imageUriString);
        } else {
            Log.e("BillImageActivity", "BillEntity is null");
            Toast.makeText(this, "No bill data available", Toast.LENGTH_SHORT).show();
        }

        // Load image if the URI is valid
        if (imageUriString != null) {
            loadImage(imageUriString);
        } else {
            Toast.makeText(this, "No image available to display", Toast.LENGTH_SHORT).show();
        }

        shareButton.setOnClickListener(v -> shareImage());
    }

    private void checkPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_CODE);
        }
    }

    private void loadImage(String imageUriString) {
        if (imageUriString == null || imageUriString.isEmpty()) {
            Toast.makeText(this, "No image URI provided", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            Uri imageUri = Uri.parse(imageUriString);
            Glide.with(this)
                    .load(imageUri)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(fullImageView);
        } catch (Exception e) {
            Log.e("BillImageActivity", "Error loading image: " + e.getMessage(), e);
            Toast.makeText(this, "Error loading image", Toast.LENGTH_SHORT).show();
        }
    }

    private void shareImage() {
        if (imageUriString != null) {
            Uri imageUri = Uri.parse(imageUriString);
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.putExtra(Intent.EXTRA_STREAM, imageUri);
            shareIntent.setType("image/png"); // Assuming the image is PNG
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            shareIntent.putExtra(Intent.EXTRA_TEXT, "Check out my bill!");
            startActivity(Intent.createChooser(shareIntent, "Share Bill Using"));
        } else {
            Toast.makeText(this, "No image available to share", Toast.LENGTH_SHORT).show();
        }
    }

}
