package com.example.programming_quiz;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.programming_quiz.data.PreferencesManager;

public class SettingsActivity extends AppCompatActivity {

    private EditText nicknameInput;
    private EditText goalInput;
    private PreferencesManager preferencesManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_settings);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.settings_root), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        preferencesManager = new PreferencesManager(this);
        initViews();
        populateFields();
    }

    private void initViews() {
        nicknameInput = findViewById(R.id.inputNickname);
        goalInput = findViewById(R.id.inputGoal);
        Button saveButton = findViewById(R.id.buttonSaveSettings);
        Button backButton = findViewById(R.id.buttonBackHome);

        saveButton.setOnClickListener(v -> saveSettings());
        backButton.setOnClickListener(v -> finish());
    }

    private void populateFields() {
        nicknameInput.setText(preferencesManager.getNickname());
        goalInput.setText(String.valueOf(preferencesManager.getDailyGoal()));
    }

    private void saveSettings() {
        String nickname = nicknameInput.getText().toString().trim();
        String goalText = goalInput.getText().toString().trim();

        if (TextUtils.isEmpty(nickname)) {
            nicknameInput.setError(getString(R.string.hint_nickname));
            return;
        }

        int goal;
        try {
            goal = Integer.parseInt(goalText);
        } catch (NumberFormatException e) {
            goalInput.setError(getString(R.string.error_goal_required));
            return;
        }

        if (goal <= 0) {
            goalInput.setError(getString(R.string.error_goal_required));
            return;
        }
 
        preferencesManager.setNickname(nickname);
        preferencesManager.setDailyGoal(goal);
        Toast.makeText(this, R.string.toast_settings_saved, Toast.LENGTH_SHORT).show();
        finish();
    }
}
