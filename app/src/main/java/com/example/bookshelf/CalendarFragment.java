package com.example.bookshelf;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class CalendarFragment extends Fragment implements CalendarAdapter.OnItemListener {
    private TextView monthYearText;
    private RecyclerView calendarRecyclerView;
    private LocalDate selectedDates;
    private CalendarAdapter calendarAdapter;
    private ListView calendarBookList;
    private List<Book> bookList;
    private CalendarBookAdapter calendarBookAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_calendar, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        monthYearText = view.findViewById(R.id.monthYearTextView);
        calendarRecyclerView = view.findViewById(R.id.calendarRecyclerView);
        selectedDates = LocalDate.now();
        calendarBookList = view.findViewById(R.id.calendarBookList);
        setMonthView();
        bookList = new ArrayList<>();
        calendarBookAdapter = new CalendarBookAdapter(getContext(), bookList);
        calendarBookList.setAdapter(calendarBookAdapter);

        Button previousButton = view.findViewById(R.id.previousMonthButton);
        Button nextButton = view.findViewById(R.id.nextMonthButton);

        previousButton.setOnClickListener(v -> {
            selectedDates = selectedDates.minusMonths(1);
            setMonthView();
        });

        nextButton.setOnClickListener(v -> {
            selectedDates = selectedDates.plusMonths(1);
            setMonthView();
        });

        loadBookList();
    }

    private void setMonthView() {
        monthYearText.setText(monthYearFromDate(selectedDates));
        List<LocalDate> registeredDates = getRegisteredDatesFromDatabase();

        calendarAdapter = new CalendarAdapter(daysInMonthArray(selectedDates), registeredDates, selectedDates, this);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(requireContext(), 7);
        calendarRecyclerView.setLayoutManager(layoutManager);
        calendarRecyclerView.setAdapter(calendarAdapter);
    }

    private List<LocalDate> getRegisteredDatesFromDatabase() {
        List<LocalDate> registeredDates = new ArrayList<>();
        DatabaseHelper dbHelper = new DatabaseHelper(getContext());
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String[] columns = {"date"};
        Cursor cursor = db.query("books", columns, null, null, null, null, null);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");

        int dateColumnIndex = cursor.getColumnIndexOrThrow("date");
        while (cursor.moveToNext()) {
            String dateString = cursor.getString(dateColumnIndex);
            if (!dateString.isEmpty()) {
                LocalDate date = LocalDate.parse(dateString, formatter);
                registeredDates.add(date);
            }
        }
        cursor.close();
        db.close();

        return registeredDates;
    }

    private ArrayList<String> daysInMonthArray(LocalDate date) {
        ArrayList<String> daysInMonthArray = new ArrayList<>();
        YearMonth yearMonth = YearMonth.from(date);

        int daysInMonth = yearMonth.lengthOfMonth();

        LocalDate firstOfMonth = selectedDates.withDayOfMonth(1);
        int dayOfWeek = firstOfMonth.getDayOfWeek().getValue();

        for (int i = 1; i <= 42; i++) {
            if (i <= dayOfWeek || i > daysInMonth + dayOfWeek) {
                daysInMonthArray.add("");
            } else {
                daysInMonthArray.add(String.valueOf(i - dayOfWeek));
            }
        }
        return daysInMonthArray;
    }

    private String monthYearFromDate(LocalDate date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy'年' LLLL");
        return date.format(formatter);
    }

    @Override
    public void onItemClick(int position, String dayText) {
        if (!dayText.isEmpty()){
            LocalDate clickedDate = LocalDate.of(selectedDates.getYear(), selectedDates.getMonth(), Integer.parseInt(dayText));
            selectedDates = clickedDate;
            calendarAdapter.updateSelectedDate(selectedDates);

            List<Book> books = getBooksForDate(clickedDate);
            CalendarBookAdapter bookAdapter = new CalendarBookAdapter(getContext(), books);
            calendarBookList.setAdapter(bookAdapter);

            calendarBookList.setOnItemClickListener((parent, view, position1, id) -> {
                Book selectedBook = books.get(position1);
                CalendarBookDetailFragment detailFragment = new CalendarBookDetailFragment();
                Bundle args = new Bundle();
                args.putInt("BOOK_ID", selectedBook.getId());
                detailFragment.setArguments(args);
                detailFragment.show(getChildFragmentManager(), "CalendarBookDetailFragment");
            });
        }
    }

    private List<Book> getBooksForDate(LocalDate date) {
        List<Book> bookList = new ArrayList<>();
        DatabaseHelper dbHelper = new DatabaseHelper(requireContext());
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");
        String dateString = date.format(formatter);

        String[] columns = {"id", "image", "title", "author", "date", "yet", "rating", "thought"};
        String selection = "date = ?";
        String[] selectionArgs = {dateString};

        try (Cursor cursor = db.query("books", columns, selection, selectionArgs, null, null, null)){
            while (cursor.moveToNext()) {
                Book book = new Book(
                        cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                        cursor.getString(cursor.getColumnIndexOrThrow("image")),
                        cursor.getString(cursor.getColumnIndexOrThrow("title")),
                        cursor.getString(cursor.getColumnIndexOrThrow("author")),
                        cursor.getString(cursor.getColumnIndexOrThrow("date")),
                        cursor.getInt(cursor.getColumnIndexOrThrow("yet")),
                        cursor.getFloat(cursor.getColumnIndexOrThrow("rating")),
                        cursor.getString(cursor.getColumnIndexOrThrow("thought"))
                );
                bookList.add(book);
            }
        } finally {
            db.close();
        }
        return bookList;
    }

    public void loadBookList() {
        bookList = getBooksForDate(selectedDates);
        CalendarBookAdapter bookAdapter = new CalendarBookAdapter(getContext(), bookList);
        calendarBookList.setAdapter(bookAdapter);
    }
}