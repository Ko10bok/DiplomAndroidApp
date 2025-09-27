package com.example.carengineoil;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import android.content.Context;

@Database(entities = {Oil.class}, version = 3, exportSchema = false)
public abstract class OilDB extends RoomDatabase {

    public abstract OilDao oilDao();

    private static volatile OilDB INSTANCE;

    public static OilDB getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (OilDB.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    OilDB.class, "oil_database")
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}

