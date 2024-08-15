package com.example.mytodo;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;

import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class AddToDoActivity extends AppCompatActivity {
  private EditText editStartDate;
  private EditText editEndDate;
  private EditText editStartTime;
  private EditText editEndTime;

  private Calendar calendarStart;
  private Calendar calendarEnd;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_add_to_do);

    Button exitButton = findViewById(R.id.exitButton);
    Button storeButton = findViewById(R.id.storeButton);

    exitButton.setOnClickListener(v -> {
      Intent intent = new Intent(AddToDoActivity.this, MainActivity.class);
      startActivity(intent);
    });

    editStartDate = findViewById(R.id.editStartDate);
    editEndDate = findViewById(R.id.editEndDate);
    editStartTime = findViewById(R.id.editStartTime);
    editEndTime = findViewById(R.id.editEndTime);

//    初期値設定
    Date showingDate = MainActivity.showingDate;
    calendarStart = Calendar.getInstance();
    calendarStart.setTime(showingDate);
    calendarEnd = Calendar.getInstance();
    calendarEnd.setTime(showingDate);

    calendarStart.set(Calendar.MINUTE, 0);
    calendarStart.set(Calendar.SECOND, 0);
    calendarStart.set(Calendar.MILLISECOND, 0);

    calendarEnd.set(Calendar.MINUTE, 0);
    calendarEnd.set(Calendar.SECOND, 0);
    calendarEnd.set(Calendar.MILLISECOND, 0);
    calendarEnd.add(Calendar.HOUR_OF_DAY, 1);

    updateDateLabel(editStartDate, calendarStart);
    updateDateLabel(editEndDate, calendarEnd);
    updateTimeLabel(editStartTime, calendarStart);
    updateTimeLabel(editEndTime, calendarEnd);


//    時間編集イベント
    editStartDate.setOnClickListener(v -> showDatePicker(calendarStart, editStartDate));
    editEndDate.setOnClickListener(v -> showDatePicker(calendarEnd, editEndDate));
    editStartTime.setOnClickListener(v -> showTimePicker(calendarStart, editStartTime));
    editEndTime.setOnClickListener(v -> showTimePicker(calendarEnd, editEndTime));
  }

  private void showDatePicker(Calendar calendar, EditText editText) {
    new DatePickerDialog(AddToDoActivity.this, (view, year, month, dayOfMonth) -> {
      calendar.set(year, month, dayOfMonth);
      updateDateLabel(editText, calendar);
    }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
  }

  private void showTimePicker(Calendar calendar, EditText editText) {
    new TimePickerDialog(AddToDoActivity.this, (view, hourOfDay, minute) -> {
      calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
      calendar.set(Calendar.MINUTE, minute);
      updateTimeLabel(editText, calendar);
    }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true).show();
  }

  private void updateDateLabel(EditText editText, Calendar calendar) {
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd EEEE", Locale.getDefault());
    editText.setText(sdf.format(calendar.getTime()));
  }

  private void updateTimeLabel(EditText editText, Calendar calendar) {
    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
    editText.setText(sdf.format(calendar.getTime()));
  }
}