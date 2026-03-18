package com.example.smartbillwallet;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BillAdapter extends RecyclerView.Adapter<BillAdapter.BillViewHolder> {
    private List<BillEntity> billList;
    private Context context;

    public BillAdapter(Context context, List<BillEntity> billList) {
        this.context = context;
        this.billList = billList;
    }

    @NonNull
    @Override
    public BillViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.bill_item, parent, false);
        return new BillViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BillViewHolder holder, int position) {
        BillEntity bill = billList.get(position);
        holder.billNameTextView.setText(bill.getName());
        holder.categoryTextView.setText(bill.getCategory());
        holder.amountTextView.setText(bill.getAmount());
        holder.dateTextView.setText(bill.getDate());


        // Log URI to check if it's correct
        Log.d("BillAdapter", "Loading image for Bill: " + bill.getName() + ", URI: " + bill.getImageUri());

        if (bill.getImageUri() != null) {
            Glide.with(context)
                    .load(Uri.parse(bill.getImageUri()))
                    .apply(new RequestOptions()
                            .diskCacheStrategy(DiskCacheStrategy.ALL)) // Cache strategy
                    .into(holder.billImageView); // Set the ImageView

        } else {
            holder.billImageView.setImageResource(R.drawable.default_image); // Default image
        }


        holder.billImageView.setOnClickListener(v -> openBillImageActivity(bill));


        holder.deleteButton.setOnClickListener(v -> deleteBill(bill, position));

        holder.shareButton.setOnClickListener(v -> shareBill(bill));
    }


    @Override
    public int getItemCount() {
        return billList.size();
    }
    private void openBillImageActivity(BillEntity bill) {
        Intent intent = new Intent(context, BillImageActivity.class);
        intent.putExtra("billEntity", bill); // Make sure this line is included
        context.startActivity(intent);
    }

    private void shareBill(BillEntity bill) {
        String shareBody = "Bill Name: " + bill.getName() + "\n" +
                "Category: " + bill.getCategory() + "\n" +
                "Amount: " + bill.getAmount() + "\n" +
                "Date: " + bill.getDate();

        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Bill Details");
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareBody);

        // Check if the image URI is available and set the appropriate type
        if (bill.getImageUri() != null) {
            Uri imageUri = Uri.parse(bill.getImageUri());
            shareIntent.putExtra(Intent.EXTRA_STREAM, imageUri);
            shareIntent.setType("image/png"); // Set type to image/png for sharing images
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION); // Grant permission to read the URI
        } else {
            shareIntent.setType("text/plain"); // Set type to text/plain if no image
        }

        // Start the share intent
        context.startActivity(Intent.createChooser(shareIntent, "Share Bill Using"));
    }
    private void deleteBill(BillEntity bill, int position) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            try {
                // Get the instance of the database and delete the bill
                BillDatabase db = BillDatabase.getInstance(context);
                db.billDao().deleteBill(bill);

                // Safely update UI on the main thread
                if (context instanceof MyBillsActivity) {
                    ((MyBillsActivity) context).runOnUiThread(() -> {
                        // Check if the position is still valid before removing
                        if (position >= 0 && position < billList.size()) {
                            billList.remove(position); // Remove the bill from the list
                            notifyItemRemoved(position); // Notify the RecyclerView
                            Toast.makeText(context, "Bill deleted successfully", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            } catch (Exception e) {
                // Log the exception for debugging purposes
                e.printStackTrace();

                // Ensure any errors are displayed on the UI thread
                if (context instanceof MyBillsActivity) {
                    ((MyBillsActivity) context).runOnUiThread(() ->
                            Toast.makeText(context, "Error deleting bill: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                    );
                }
            } finally {
                // Always shut down the executor to prevent thread leakage
                executor.shutdown();
            }
        });
    }


    public static class BillViewHolder extends RecyclerView.ViewHolder {
        TextView billNameTextView, categoryTextView, amountTextView, dateTextView;
        ImageView billImageView;
        Button shareButton, deleteButton;

        public BillViewHolder(@NonNull View itemView) {
            super(itemView);
            billNameTextView = itemView.findViewById(R.id.billNameTextView);
            categoryTextView = itemView.findViewById(R.id.categoryTextView);
            amountTextView = itemView.findViewById(R.id.amountTextView);
            dateTextView = itemView.findViewById(R.id.dateTextView);
            billImageView = itemView.findViewById(R.id.billImageView);
            shareButton = itemView.findViewById(R.id.shareButton);
            deleteButton = itemView.findViewById(R.id.deleteButton);

        }
    }
}
