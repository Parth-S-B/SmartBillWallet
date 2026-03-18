package com.example.smartbillwallet;

import android.Manifest;
import android.app.DatePickerDialog;
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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CaptureBillActivity extends AppCompatActivity {
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_PERMISSION_CODE = 100;

    private ImageView billImageView;
    private EditText billNameInput, categoryInput, amountInput, dateInput;
    private Uri currentImageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_capture_bill);

        // Initialize views
        billImageView = findViewById(R.id.billImageView);
        billNameInput = findViewById(R.id.billNameInput);
        categoryInput = findViewById(R.id.categoryInput);
        amountInput = findViewById(R.id.amountInput);
        dateInput = findViewById(R.id.dateInput);
        Button captureButton = findViewById(R.id.captureButton);
        Button saveButton = findViewById(R.id.saveButton);
        dateInput.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(CaptureBillActivity.this,
                    (view, selectedYear, selectedMonth, selectedDay) -> {
                        String selectedDate = selectedDay + "/" + (selectedMonth + 1) + "/" + selectedYear;
                        dateInput.setText(selectedDate);
                    }, year, month, day);
            datePickerDialog.show();
        });

        // Set onClickListener for the capture button
        captureButton.setOnClickListener(v -> {
            // Check for camera permission
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA},
                        REQUEST_PERMISSION_CODE);
            } else {
                // Permission already granted, open camera
                dispatchTakePictureIntent();
            }
        });

        // Set onClickListener for the save button
        saveButton.setOnClickListener(v -> {
            // Handle saving the bill data
            saveBillData();
        });
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            // Handle the returned image
            Bundle extras = data.getExtras();
            if (extras != null) {
                Bitmap imageBitmap = (Bitmap) extras.get("data");
                billImageView.setImageBitmap(imageBitmap);
                saveImage(imageBitmap);
            }
        }
    }

    private void saveImage(Bitmap bitmap) {
        File file = new File(getExternalFilesDir(null), "bill_image_" + System.currentTimeMillis() + ".png");
        try (FileOutputStream out = new FileOutputStream(file)) {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            currentImageUri = FileProvider.getUriForFile(this, getApplicationContext().getPackageName() + ".fileprovider", file);
            // Log the URI being saved
            Log.d("CaptureBillActivity", "Saving Bill with URI: " + currentImageUri);
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error saving image", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveBillData() {
        String billName = billNameInput.getText().toString().trim();
        String category = categoryInput.getText().toString().trim();
        String amount = amountInput.getText().toString().trim();
        String date = dateInput.getText().toString().trim();

        if (!billName.isEmpty() && !category.isEmpty() && !amount.isEmpty() && !date.isEmpty()) {
            // Assuming BillDatabase and BillEntity are properly set up
            BillDatabase db = BillDatabase.getInstance(this);
            // Pass the imageUri to the BillEntity constructor
            BillEntity bill = new BillEntity(billName, category, amount, date, currentImageUri != null ? currentImageUri.toString() : null);

            ExecutorService executor = Executors.newSingleThreadExecutor();
            executor.execute(() -> {
                db.billDao().insert(bill); // Ensure this method matches your DAO method name
                runOnUiThread(() -> {
                    Toast.makeText(this, "Bill saved successfully!", Toast.LENGTH_SHORT).show();
                    clearInputs();
                });
            });
        } else {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
        }
    }

    // Handle permission request response
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, open the camera
                dispatchTakePictureIntent();
            } else {
                Toast.makeText(this, "Camera permission is required to take pictures", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void clearInputs() {
        billNameInput.setText("");
        categoryInput.setText("");
        amountInput.setText("");
        dateInput.setText("");
        billImageView.setImageResource(0); // Reset the image view
        currentImageUri = null; // Clear the image URI
    }
}
