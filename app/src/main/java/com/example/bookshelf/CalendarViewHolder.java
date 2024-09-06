package com.example.bookshelf;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

public class CalendarViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    public final TextView dayOfMonth;
    private final CalendarAdapter.OnItemListener onItemListener;
    public final View markView;
    public final View calendarCell;

    public CalendarViewHolder(View itemView, CalendarAdapter.OnItemListener onItemListener) {
        super(itemView);
        calendarCell = itemView.findViewById(R.id.calendarCell);
        dayOfMonth = itemView.findViewById(R.id.cellDayText);
        markView = itemView.findViewById(R.id.markView);
        this.onItemListener = onItemListener;
        itemView.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        onItemListener.onItemClick(getAdapterPosition(), dayOfMonth.getText().toString());
    }
}