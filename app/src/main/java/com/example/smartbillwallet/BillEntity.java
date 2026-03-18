package com.example.smartbillwallet;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity(tableName = "bills")
public class BillEntity implements Serializable {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String name;
    private String category;
    private String amount;
    private String date;
    private String imageUri; // URI of the image

    public BillEntity(String name, String category, String amount, String date, String imageUri) {
        this.name = name;
        this.category = category;
        this.amount = amount;
        this.date = date;
        this.imageUri = imageUri;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getImageUri() {
        return imageUri;
    }

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }
}
