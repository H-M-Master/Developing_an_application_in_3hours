package com.example.programming_quiz;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.programming_quiz.data.PreferencesManager;
import com.example.programming_quiz.data.WellnessRepository;
import com.google.android.material.appbar.MaterialToolbar;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private TextView welcomeTextView;
    private TextView todaySummaryTextView;
    private TextView selectedDateTextView;
    private EditText stepsInput;
    private EditText noteInput;
    private Spinner moodSpinner;

    private PreferencesManager preferencesManager;
    private WellnessRepository wellnessRepository;

    private final Calendar selectedDate = Calendar.getInstance();
    private final Calendar todayCalendar = Calendar.getInstance();
    private final SimpleDateFormat storageDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    private final SimpleDateFormat displayDateFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());

    private int dailyGoal;
    private int todaySteps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        MaterialToolbar toolbar = findViewById(R.id.topAppBar);
        setSupportActionBar(toolbar);
        
        toolbar.post(() -> {
            if (toolbar.getOverflowIcon() != null) {
                toolbar.getOverflowIcon().setTint(getResources().getColor(R.color.brand_yellow));
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        preferencesManager = new PreferencesManager(this);
        wellnessRepository = new WellnessRepository(this);

        initViews();
        setupMoodSpinner();
        setupListeners();
        updateSelectedDateLabel();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadUserPreferences();
        loadTodaySummary();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            openSettings();
            return true;
        } else if (id == R.id.action_clear_data) {
            confirmClearData();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initViews() {
        welcomeTextView = findViewById(R.id.textWelcome);
        todaySummaryTextView = findViewById(R.id.textTodaySummary);
        selectedDateTextView = findViewById(R.id.textSelectedDate);
        stepsInput = findViewById(R.id.inputSteps);
        noteInput = findViewById(R.id.inputNote);
        moodSpinner = findViewById(R.id.spinnerMood);
    }

    private void setupMoodSpinner() {
        ArrayAdapter<CharSequence> moodAdapter = ArrayAdapter.createFromResource(
                this,
                R.array.mood_options,
                R.layout.item_spinner_text);
        moodAdapter.setDropDownViewResource(R.layout.item_spinner_dropdown);
        moodSpinner.setAdapter(moodAdapter);
    }

    private void setupListeners() {
        Button pickDateButton = findViewById(R.id.buttonPickDate);
        Button saveButton = findViewById(R.id.buttonSave);
        Button historyButton = findViewById(R.id.buttonHistory);

        pickDateButton.setOnClickListener(v -> openDatePicker());
        saveButton.setOnClickListener(v -> saveRecord());
        historyButton.setOnClickListener(v -> startActivity(new Intent(this, HistoryActivity.class)));
    }

    private void openSettings() {
        startActivity(new Intent(this, SettingsActivity.class));
    }

    private void confirmClearData() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.dialog_clear_title)
                .setMessage(R.string.dialog_clear_message)
                .setPositiveButton(android.R.string.ok, (dialog, which) -> performClearData())
                .setNegativeButton(android.R.string.cancel, null)
                .show();
    }

    private void performClearData() {
        disableInputs(true);
        wellnessRepository.clearAll(() -> runOnUiThread(() -> {
            preferencesManager.reset();
            todaySteps = 0;
            clearInputs();
            loadUserPreferences();
            loadTodaySummary();
            disableInputs(false);
            Toast.makeText(this, R.string.toast_all_cleared, Toast.LENGTH_SHORT).show();
        }));
    }

    private void loadUserPreferences() {
        String nickname = preferencesManager.getNickname();
        dailyGoal = preferencesManager.getDailyGoal();
        welcomeTextView.setText(getString(R.string.welcome_template, nickname));
        updateSummaryLabel();
    }

    private void loadTodaySummary() {
        todayCalendar.setTimeInMillis(System.currentTimeMillis());
        String todayKey = storageDateFormat.format(todayCalendar.getTime());
        wellnessRepository.getRecordByDate(todayKey, record -> runOnUiThread(() -> {
            todaySteps = record != null ? record.getSteps() : 0;
            updateSummaryLabel();
        }));
    }

    private void updateSummaryLabel() {
        todaySummaryTextView.setText(getString(R.string.today_summary_template, todaySteps, dailyGoal));
    }

    private void updateSelectedDateLabel() {
        selectedDateTextView.setText(displayDateFormat.format(selectedDate.getTime()));
    }

    private void openDatePicker() {
        DatePickerDialog dialog = new DatePickerDialog(
                this,
                (view, year, month, day) -> {
                    selectedDate.set(Calendar.YEAR, year);
                    selectedDate.set(Calendar.MONTH, month);
                    selectedDate.set(Calendar.DAY_OF_MONTH, day);
                    updateSelectedDateLabel();
                },
                selectedDate.get(Calendar.YEAR),
                selectedDate.get(Calendar.MONTH),
                selectedDate.get(Calendar.DAY_OF_MONTH));
        dialog.getDatePicker().setMaxDate(System.currentTimeMillis());
        dialog.setOnShowListener(d -> {
            int blue = getResources().getColor(R.color.brand_blue);
            int yellow = getResources().getColor(R.color.brand_yellow);
            DatePicker picker = dialog.getDatePicker();
            setAllBackgrounds(picker, yellow);
            for (int i = 0; i < picker.getChildCount(); i++) {
                View child = picker.getChildAt(i);
                if (child instanceof ViewGroup) {
                    ViewGroup vg = (ViewGroup) child;
                    for (int j = 0; j < vg.getChildCount(); j++) {
                        View sub = vg.getChildAt(j);
                        if (sub instanceof NumberPicker) {
                            NumberPicker np = (NumberPicker) sub;
                            int count = np.getChildCount();
                            for (int k = 0; k < count; k++) {
                                View numberView = np.getChildAt(k);
                                if (numberView instanceof EditText) {
                                    ((EditText) numberView).setTextColor(blue);
                                }
                            }
                        }
                        if (sub instanceof TextView) {
                            ((TextView) sub).setTextColor(blue);
                        }
                    }
                }
            }
            setAllTextViewColor(picker, blue);
        });
        dialog.show();
    }

    private void saveRecord() {
        String stepsText = stepsInput.getText().toString().trim();
        if (TextUtils.isEmpty(stepsText)) {
            stepsInput.setError(getString(R.string.error_steps_required));
            return;
        }

        int steps;
        try {
            steps = Integer.parseInt(stepsText);
        } catch (NumberFormatException e) {
            stepsInput.setError(getString(R.string.error_steps_required));
            return;
        }

        if (steps <= 0) {
            stepsInput.setError(getString(R.string.error_steps_required));
            return;
        }

        String mood = moodSpinner.getSelectedItem().toString();
        String note = noteInput.getText().toString().trim();
        if (note.isEmpty()) {
            note = null;
        }

        final String recordDate = storageDateFormat.format(selectedDate.getTime());

        disableInputs(true);
        wellnessRepository.saveOrMergeRecord(recordDate, steps, mood, note, savedRecord -> runOnUiThread(() -> {
            disableInputs(false);
            Toast.makeText(this, getString(R.string.toast_saved, steps, mood), Toast.LENGTH_SHORT).show();
            clearInputs();
            loadTodaySummary();
        }));
    }

    private void disableInputs(boolean disable) {
        stepsInput.setEnabled(!disable);
        noteInput.setEnabled(!disable);
        moodSpinner.setEnabled(!disable);
        View pickDateButton = findViewById(R.id.buttonPickDate);
        if (pickDateButton != null) {
            pickDateButton.setEnabled(!disable);
        }
        View saveButton = findViewById(R.id.buttonSave);
        if (saveButton != null) {
            saveButton.setEnabled(!disable);
        }
        View historyButton = findViewById(R.id.buttonHistory);
        if (historyButton != null) {
            historyButton.setEnabled(!disable);
        }
    }

    private void clearInputs() {
        stepsInput.setText("");
        noteInput.setText("");
    }

    
    private void setAllBackgrounds(View view, int color) {
        if (view == null) return;
        view.setBackgroundColor(color);
        if (view instanceof ViewGroup) {
            ViewGroup vg = (ViewGroup) view;
            for (int i = 0; i < vg.getChildCount(); i++) {
                setAllBackgrounds(vg.getChildAt(i), color);
            }
        }
    }

    
    private void setAllTextViewColor(View view, int color) {
        if (view == null) return;
        if (view instanceof TextView) {
            ((TextView) view).setTextColor(color);
        }
        if (view instanceof ViewGroup) {
            ViewGroup vg = (ViewGroup) view;
            for (int i = 0; i < vg.getChildCount(); i++) {
                setAllTextViewColor(vg.getChildAt(i), color);
            }
        }
    }
}