package com.example.programming_quiz.data;

import android.content.Context;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class WellnessRepository {

    public interface ResultCallback<T> {
        void onResult(T data);
    }

    private final WellnessDao wellnessDao;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    public WellnessRepository(Context context) {
        wellnessDao = WellnessDatabase.getInstance(context).wellnessDao();
    }

    public ExecutorService getExecutorService() {
        return executorService;
    }

    public void saveOrMergeRecord(String date, int steps, String mood, String note, ResultCallback<WellnessRecord> callback) {
        executorService.execute(() -> {
            WellnessRecord existing = wellnessDao.getRecordByDate(date);
            WellnessRecord updated;
            if (existing == null) {
                updated = new WellnessRecord(date, steps, mood, note);
                wellnessDao.upsert(updated);
            } else {
                existing.setSteps(existing.getSteps() + steps);
                existing.setMood(mood);
                if (note != null && !note.isEmpty()) {
                    existing.setNote(note);
                }
                wellnessDao.updateRecord(existing);
                updated = existing;
            }
            if (callback != null) {
                callback.onResult(updated);
            }
        });
    }

    public void getRecordByDate(String date, ResultCallback<WellnessRecord> callback) {
        executorService.execute(() -> {
            WellnessRecord record = wellnessDao.getRecordByDate(date);
            if (callback != null) {
                callback.onResult(record);
            }
        });
    }

    public void getAllRecords(ResultCallback<List<WellnessRecord>> callback) {
        executorService.execute(() -> {
            List<WellnessRecord> records = wellnessDao.getAllRecords();
            if (callback != null) {
                callback.onResult(records);
            }
        });
    }

    public void getRecordsBetween(String startDate, String endDate, ResultCallback<List<WellnessRecord>> callback) {
        executorService.execute(() -> {
            List<WellnessRecord> records = wellnessDao.getRecordsBetween(startDate, endDate);
            if (callback != null) {
                callback.onResult(records);
            }
        });
    }

    public void clearAll(Runnable onComplete) {
        executorService.execute(() -> {
            wellnessDao.clearAll();
            if (onComplete != null) {
                onComplete.run();
            }
        });
    }
}
