package com.example.bookshelf;

import android.graphics.Color;
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
    private LocalDate selectedDates;

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

    public void updateSelectedDate(LocalDate newSelectedDate) {
        LocalDate oldSelectedDate = this.selectedDates;
        this.selectedDates = newSelectedDate;
        int oldPosition = -1;
        int newPosition = -1;
        for (int i = 0; i < dayOfMonth.size(); i++) {
            String dayText = dayOfMonth.get(i);
            if (!dayText.isEmpty()) {
                LocalDate date = LocalDate.of(newSelectedDate.getYear(), newSelectedDate.getMonthValue(), Integer.parseInt(dayText));
                if (date.equals(oldSelectedDate)) {
                    oldPosition = i;
                }
                if (date.equals(newSelectedDate)) {
                    newPosition = i;
                }
            }
        }
        if (oldPosition != -1) {
            notifyItemChanged(oldPosition);
        }
        if (newPosition != -1) {
            notifyItemChanged(newPosition);
        }
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

            if (currentDate.equals(this.selectedDates)) {
                holder.calendarCell.setBackgroundColor(Color.LTGRAY);
            } else {
                holder.calendarCell.setBackgroundColor(Color.TRANSPARENT);
            }
        } else {
            holder.markView.setVisibility(View.GONE);
            holder.calendarCell.setBackgroundColor(Color.TRANSPARENT);
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