package com.example.bookshelf;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class CalendarAdapter extends RecyclerView.Adapter<CalendarViewHolder> {
    private final ArrayList<String> dayOfMonth;
    private final OnItemListener onItemListener;
    private final List<LocalDate> registeredDates;
    private final LocalDate selectedDates;

    public CalendarAdapter(ArrayList<String> dayOfMonth, List<LocalDate> registeredDates, LocalDate selectedDates, OnItemListener onItemListener) {
        this.dayOfMonth = dayOfMonth;
        this.onItemListener = onItemListener;
        this.registeredDates = registeredDates;
        this.selectedDates = selectedDates;

    }

    @NonNull
    @Override
    public CalendarViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.sub_calendar, parent, false);
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        layoutParams.height = (int) (parent.getHeight() * 0.166666666);
        return new CalendarViewHolder(view, onItemListener);
    }

    @Override
    public void onBindViewHolder(@NonNull CalendarViewHolder holder, int position) {
        String dayText = dayOfMonth.get(position);
        holder.dayOfMonth.setText(dayOfMonth.get(position));

        if (!dayText.isEmpty()) {
            LocalDate currentDate = LocalDate.of(selectedDates.getYear(), selectedDates.getMonthValue(), Integer.parseInt(dayText));

            if (registeredDates.contains(currentDate)) {
                holder.markView.setVisibility(View.VISIBLE);
            } else {
                holder.markView.setVisibility(View.GONE);
            }
        } else {
            holder.markView.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return dayOfMonth.size();
    }

    public interface OnItemListener {
        void onItemClick(int position, String dayText);
    }
}