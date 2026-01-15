package com.example.programming_quiz;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.programming_quiz.data.WellnessRecord;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.RecordViewHolder> {

    public interface OnRecordClickListener {
        void onRecordClick(WellnessRecord record);
    }

    private final List<WellnessRecord> records = new ArrayList<>();
    private final OnRecordClickListener listener;
    private final SimpleDateFormat displayFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
    private final SimpleDateFormat storageFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    private final NumberFormat numberFormat = NumberFormat.getIntegerInstance();

    public HistoryAdapter(OnRecordClickListener listener) {
        this.listener = listener;
    }

    public void submitList(List<WellnessRecord> newRecords) {
        records.clear();
        if (newRecords != null) {
            records.addAll(newRecords);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecordViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_wellness_record, parent, false);
        return new RecordViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecordViewHolder holder, int position) {
        WellnessRecord record = records.get(position);
        holder.bind(record);
    }

    @Override
    public int getItemCount() {
        return records.size();
    }

    class RecordViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private final TextView dateText;
        private final TextView stepsText;
        private final TextView moodText;

        RecordViewHolder(@NonNull View itemView) {
            super(itemView);
            dateText = itemView.findViewById(R.id.textDate);
            stepsText = itemView.findViewById(R.id.textSteps);
            moodText = itemView.findViewById(R.id.textMood);
            itemView.setOnClickListener(this);
        }

        void bind(WellnessRecord record) {
            dateText.setText(displayFormat.format(new Date(recordDateToMillis(record.getDate()))));
            stepsText.setText("Steps: " + numberFormat.format(record.getSteps()));
            moodText.setText("Mood: " + record.getMood());
        }

        @Override
        public void onClick(View v) {
            int position = getBindingAdapterPosition();
            if (position != RecyclerView.NO_POSITION && listener != null) {
                listener.onRecordClick(records.get(position));
            }
        }

        private long recordDateToMillis(String dateString) {
            try {
                return storageFormat.parse(dateString).getTime();
            } catch (Exception e) {
                return System.currentTimeMillis();
            }
        }
    }
}
