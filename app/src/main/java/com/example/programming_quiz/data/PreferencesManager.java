package com.example.programming_quiz.data;

import android.content.Context;
import android.content.SharedPreferences;

public class PreferencesManager {

    private static final String PREF_NAME = "wellness_prefs";
    private static final String KEY_NICKNAME = "nickname";
    private static final String KEY_DAILY_GOAL = "daily_step_goal";
    private static final String DEFAULT_NICKNAME = "Guest";
    private static final int DEFAULT_GOAL = 8000;

    private final SharedPreferences sharedPreferences;

    public PreferencesManager(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public String getNickname() {
        return sharedPreferences.getString(KEY_NICKNAME, DEFAULT_NICKNAME);
    }

    public void setNickname(String nickname) {
        sharedPreferences.edit().putString(KEY_NICKNAME, nickname).apply();
    }

    public int getDailyGoal() {
        return sharedPreferences.getInt(KEY_DAILY_GOAL, DEFAULT_GOAL);
    }

    public void setDailyGoal(int goal) {
        sharedPreferences.edit().putInt(KEY_DAILY_GOAL, goal).apply();
    }

    public void reset() {
        sharedPreferences.edit()
                .putString(KEY_NICKNAME, DEFAULT_NICKNAME)
                .putInt(KEY_DAILY_GOAL, DEFAULT_GOAL)
                .apply();
    }
}
