package com.example.smartbillwallet;

import android.app.DatePickerDialog;
import android.content.Intent;
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

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SelectBillActivity extends AppCompatActivity {
    private static final int PICK_IMAGE = 1;

    private ImageView billImageView;
    private EditText billNameInput, categoryInput, amountInput, dateInput;
    private Uri selectedImageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_bill);

        billImageView = findViewById(R.id.billImageView);
        billNameInput = findViewById(R.id.billNameInput);
        categoryInput = findViewById(R.id.categoryInput);
        amountInput = findViewById(R.id.amountInput);
        dateInput = findViewById(R.id.dateInput);
        Button selectButton = findViewById(R.id.selectButton);
        Button saveButton = findViewById(R.id.saveButton);
        dateInput.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(SelectBillActivity.this,
                    (view, selectedYear, selectedMonth, selectedDay) -> {
                        String selectedDate = selectedDay + "/" + (selectedMonth + 1) + "/" + selectedYear;
                        dateInput.setText(selectedDate);
                    }, year, month, day);
            datePickerDialog.show();
        });

        selectButton.setOnClickListener(v -> openGallery());
        saveButton.setOnClickListener(v -> saveBillData());
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK && data != null) {
            selectedImageUri = data.getData();
            billImageView.setImageURI(selectedImageUri);
            Log.d("SelectBillActivity", "Selected Image URI: " + selectedImageUri);
            saveImageFromUri(selectedImageUri);


        }
    }
    private void saveImage(Bitmap bitmap) {
        File file = new File(getExternalFilesDir(null), "bill_image_" + System.currentTimeMillis() + ".png");
        try (FileOutputStream out = new FileOutputStream(file)) {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            selectedImageUri = FileProvider.getUriForFile(this, getApplicationContext().getPackageName() + ".fileprovider", file);
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error saving image", Toast.LENGTH_SHORT).show();
        }
    }
    private void saveImageFromUri(Uri imageUri) {
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
            saveImage(bitmap); // Call your saveImage method
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error processing image", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveBillData() {
        String billName = billNameInput.getText().toString().trim();
        String category = categoryInput.getText().toString().trim();
        String amount = amountInput.getText().toString().trim();
        String date = dateInput.getText().toString().trim();

        if (!billName.isEmpty() && !category.isEmpty() && !amount.isEmpty() && !date.isEmpty()) {
            BillDatabase db = BillDatabase.getInstance(this);
            BillEntity bill = new BillEntity(billName, category, amount, date, selectedImageUri != null ? selectedImageUri.toString() : null);

            // Debug log
            if (selectedImageUri != null) {
                Log.d("SelectBillActivity", "Saving Bill with URI: " + selectedImageUri);
            }

            ExecutorService executor = Executors.newSingleThreadExecutor();
            executor.execute(() -> {
                db.billDao().insert(bill);
                runOnUiThread(() -> {
                    Toast.makeText(this, "Bill saved successfully!", Toast.LENGTH_SHORT).show();
                    clearInputs();
                });
            });
        } else {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
        }
    }

    private void clearInputs() {
        billNameInput.setText("");
        categoryInput.setText("");
        amountInput.setText("");
        dateInput.setText("");
        billImageView.setImageResource(0);
        selectedImageUri = null;
    }
}