package com.example.programming_quiz.data;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface WellnessDao {

    @Query("SELECT * FROM wellness_records WHERE date = :date LIMIT 1")
    WellnessRecord getRecordByDate(String date);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void upsert(WellnessRecord record);

    @Update
    void updateRecord(WellnessRecord record);

    @Query("SELECT * FROM wellness_records ORDER BY date DESC")
    List<WellnessRecord> getAllRecords();

    @Query("DELETE FROM wellness_records")
    void clearAll();

    @Query("SELECT * FROM wellness_records WHERE date BETWEEN :startDate AND :endDate ORDER BY date DESC")
    List<WellnessRecord> getRecordsBetween(String startDate, String endDate);
}
