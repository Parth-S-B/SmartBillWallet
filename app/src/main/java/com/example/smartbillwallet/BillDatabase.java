package com.example.smartbillwallet;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import android.content.Context;

@Database(entities = {BillEntity.class, User.class}, version = 1)
public abstract class BillDatabase extends RoomDatabase {
    private static BillDatabase instance;

    public abstract UserDao userDao();
    public abstract BillDao billDao();

    public static synchronized BillDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                            BillDatabase.class, "bill_database")
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return instance;
    }
}
