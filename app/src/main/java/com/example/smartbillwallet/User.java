package com.example.smartbillwallet;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "users")
public class User {
    @PrimaryKey(autoGenerate = true)
    public int id;
    public String email;
    public String password;

    // Constructor
    public User(String email, String password) {
        this.email = email;
        this.password = password;
    }

    // Getter for password
    public String getPassword() {
        return password;
    }

    // Setter for password (if needed)
    public void setPassword(String password) {
        this.password = password;
    }

    // Getter for email
    public String getEmail() {
        return email;
    }

    // Setter for email (if needed)
    public void setEmail(String email) {
        this.email = email;
    }
}
