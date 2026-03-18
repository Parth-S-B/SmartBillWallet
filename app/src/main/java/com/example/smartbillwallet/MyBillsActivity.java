package com.example.smartbillwallet;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MyBillsActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private BillAdapter billAdapter; // Declare without initialization here
    private List<BillEntity> billList; // Use BillEntity here
        Button searchBills;
        EditText search;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_bills);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        searchBills = findViewById(R.id.searchButton);
        billList = new ArrayList<>(); // Initialize billList
        billAdapter = new BillAdapter(this, billList); // Pass context and BillEntity list
        recyclerView.setAdapter(billAdapter);
        search = findViewById(R.id.searchInput);
        loadBills(); // Load bills from the database
        searchBills.setOnClickListener(v -> {
            String query = search.getText().toString().trim();
            if (!query.isEmpty()) {
                searchBills(query); // Perform search based on user input
            } else {
                // Optionally, reload all bills if query is empty
                loadBills();
            }
        });
    }

    private void searchBills(String query) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            BillDatabase db = BillDatabase.getInstance(this);
            List<BillEntity> searchedBills = db.billDao().searchByFields(query);

            runOnUiThread(() -> {
                billList.clear();
                if (searchedBills != null && !searchedBills.isEmpty()) {
                    billList.addAll(searchedBills);
                } else {
                    Toast.makeText(this, "No results found", Toast.LENGTH_SHORT).show();
                }
                billAdapter.notifyDataSetChanged();
            });
        });
    }



    private void loadBills() {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            try {
                BillDatabase db = BillDatabase.getInstance(this);
                List<BillEntity> bills = db.billDao().getAllBills();

                runOnUiThread(() -> {
                    billList.clear();
                    if (bills != null && !bills.isEmpty()) {
                        for (BillEntity bill : bills) {
                            Log.d("MyBillsActivity", "Retrieved Bill with URI: " + bill.getImageUri());
                        }
                        billList.addAll(bills);
                    } else {
                        Toast.makeText(this, "No bills found in database", Toast.LENGTH_SHORT).show();
                    }
                    billAdapter.notifyDataSetChanged();
                });
            } catch (Exception e) {
                runOnUiThread(() -> {
                    Toast.makeText(this, "Error loading bills: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
            }
        });
    }
}
