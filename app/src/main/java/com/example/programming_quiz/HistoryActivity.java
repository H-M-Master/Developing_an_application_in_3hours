package com.example.programming_quiz;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.programming_quiz.data.PreferencesManager;
import com.example.programming_quiz.data.WellnessRecord;
import com.example.programming_quiz.data.WellnessRepository;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class HistoryActivity extends AppCompatActivity implements HistoryAdapter.OnRecordClickListener {

    private TextView weeklySummaryText;
    private TextView weekRangeText;
    private TextView emptyStateText;
    private HistoryAdapter adapter;
    private PreferencesManager preferencesManager;
    private WellnessRepository wellnessRepository;

    private final SimpleDateFormat storageDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_history);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.history_root), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        preferencesManager = new PreferencesManager(this);
        wellnessRepository = new WellnessRepository(this);

        initViews();
        loadRecords();
    }

    private void initViews() {
        weeklySummaryText = findViewById(R.id.textWeeklySummary);
        weekRangeText = findViewById(R.id.textWeekRange);
        emptyStateText = findViewById(R.id.textEmptyState);
        RecyclerView recyclerView = findViewById(R.id.recyclerHistory);
        Button backButton = findViewById(R.id.buttonBack);

        adapter = new HistoryAdapter(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        backButton.setOnClickListener(v -> finish());
    }

    private void loadRecords() {
        wellnessRepository.getAllRecords(records -> runOnUiThread(() -> {
            adapter.submitList(records);
            boolean isEmpty = records == null || records.isEmpty();
            emptyStateText.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
        }));

        Calendar weekStart = getWeekStart();
        Calendar weekEnd = (Calendar) weekStart.clone();
        weekEnd.add(Calendar.DAY_OF_YEAR, 6);

        String startKey = storageDateFormat.format(weekStart.getTime());
        String endKey = storageDateFormat.format(weekEnd.getTime());
        SimpleDateFormat displayFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
        weekRangeText.setText(getString(R.string.week_range_template,
            displayFormat.format(weekStart.getTime()),
            displayFormat.format(weekEnd.getTime())));

        wellnessRepository.getRecordsBetween(startKey, endKey, weeklyRecords -> runOnUiThread(() -> {
            int goal = preferencesManager.getDailyGoal();
            int completedDays = 0;
            if (weeklyRecords != null) {
                for (WellnessRecord record : weeklyRecords) {
                    if (record.getSteps() >= goal) {
                        completedDays++;
                    }
                }
            }
            weeklySummaryText.setText(getString(R.string.weekly_summary_template, completedDays));
        }));
    }

    private Calendar getWeekStart() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        int daysFromSunday = (dayOfWeek + 7 - Calendar.SUNDAY) % 7;
        calendar.add(Calendar.DAY_OF_MONTH, -daysFromSunday);
        return calendar;
    }

    @Override
    public void onRecordClick(WellnessRecord record) {
        String note = record.getNote();
        if (note == null || note.trim().isEmpty()) {
            Toast.makeText(this, getString(R.string.label_remark) + ": (none)", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, note, Toast.LENGTH_SHORT).show();
        }
    }
}
