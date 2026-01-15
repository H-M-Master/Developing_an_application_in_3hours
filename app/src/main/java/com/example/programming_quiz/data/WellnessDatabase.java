package com.example.programming_quiz.data;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {WellnessRecord.class}, version = 1, exportSchema = false)
public abstract class WellnessDatabase extends RoomDatabase {

    private static final String DB_NAME = "wellness_tracker.db";
    private static volatile WellnessDatabase INSTANCE;

    public abstract WellnessDao wellnessDao();

    public static WellnessDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (WellnessDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                                    context.getApplicationContext(),
                                    WellnessDatabase.class,
                                    DB_NAME)
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
