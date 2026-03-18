package com.example.smartbillwallet;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

@Dao
public interface UserDao {
    @Insert
    void insert(User user); // Insert a new user

    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    User getUserByEmail(String email); // Fetch a user by email

    @Query("SELECT * FROM users WHERE id = :id LIMIT 1")
    User getUserById(int id); // Fetch a user by ID
}
