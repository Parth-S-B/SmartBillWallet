package com.example.smartbillwallet;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface BillDao {

    @Insert
    void insert(BillEntity bill);
    @Query("SELECT * FROM bills WHERE name LIKE '%' || :query || '%' OR amount LIKE '%' || :query || '%' OR date LIKE '%' || :query || '%' OR category LIKE '%' || :query || '%'")
    List<BillEntity> searchByFields(String query);

    @Query("SELECT * FROM bills")
    List<BillEntity> getAllBills();

    @Query("SELECT * FROM bills WHERE id = :billId")
    BillEntity getBillById(int billId);

    // Add delete method to delete a specific bill
    @Delete
    void deleteBill(BillEntity bill);
}
