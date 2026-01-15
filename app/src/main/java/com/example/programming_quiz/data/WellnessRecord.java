package com.example.programming_quiz.data;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "wellness_records")
public class WellnessRecord {

    @PrimaryKey
    @NonNull
    private String date;

    private int steps;

    @NonNull
    private String mood;

    private String note;

    public WellnessRecord(@NonNull String date, int steps, @NonNull String mood, String note) {
        this.date = date;
        this.steps = steps;
        this.mood = mood;
        this.note = note;
    }

    @NonNull
    public String getDate() {
        return date;
    }

    public void setDate(@NonNull String date) {
        this.date = date;
    }

    public int getSteps() {
        return steps;
    }

    public void setSteps(int steps) {
        this.steps = steps;
    }

    @NonNull
    public String getMood() {
        return mood;
    }

    public void setMood(@NonNull String mood) {
        this.mood = mood;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
