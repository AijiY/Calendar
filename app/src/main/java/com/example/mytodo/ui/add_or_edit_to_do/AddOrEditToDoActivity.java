package com.example.mytodo.ui.add_or_edit_to_do;

import static com.example.mytodo.ui.main.MainActivity.showingDate;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Switch;
import androidx.appcompat.app.AppCompatActivity;
import com.example.mytodo.R;
import com.example.mytodo.data.model.Plan;
import com.example.mytodo.data.model.Task;
import com.example.mytodo.ui.main.MainActivity;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class AddOrEditToDoActivity extends AppCompatActivity {
  private EditText editStartDate;
  private EditText editEndDate;
  private EditText editStartTime;
  private EditText editEndTime;

  private Calendar calendarStart;
  private Calendar calendarEnd;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_add_or_edit_to_do);

    Button exitButton = findViewById(R.id.exitButton);
    Button storeButton = findViewById(R.id.storeButton);
    RadioButton planRadioButton = findViewById(R.id.planRadioButton);
    RadioButton taskRadioButton = findViewById(R.id.taskRadioButton);
    Switch allDaySwitch = findViewById(R.id.allDaySwitch);
    LinearLayout startTimeContainer = findViewById(R.id.startTimeContainer);
    LinearLayout endTimeContainer = findViewById(R.id.endTimeContainer);
    TextInputLayout startTimeLayout = findViewById(R.id.startTimeLayout);
    TextInputLayout endTimeLayout = findViewById(R.id.endTimeLayout);
    TextInputEditText titleInput = findViewById(R.id.titleInput);
    TextInputEditText detailsInput = findViewById(R.id.detailsInput);

    exitButton.setOnClickListener(v -> {
      Intent intent = new Intent(AddOrEditToDoActivity.this, MainActivity.class);
      intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
      startActivity(intent);
      finish(); // AddOrEditToDoActivity を終了
    });



    taskRadioButton.setOnCheckedChangeListener((buttonView, isChecked) -> {
      if (isChecked) {
        endTimeContainer.setVisibility(View.GONE);
        allDaySwitch.setChecked(false);
        allDaySwitch.setVisibility(View.GONE);
      } else {
        endTimeContainer.setVisibility(View.VISIBLE);
        allDaySwitch.setVisibility(View.VISIBLE);
      }
    });

    allDaySwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
      if (isChecked) {
        startTimeLayout.setVisibility(View.GONE);
        endTimeLayout.setVisibility(View.GONE);
      } else {
        startTimeLayout.setVisibility(View.VISIBLE);
        endTimeLayout.setVisibility(View.VISIBLE);
      }
    });

    editStartDate = findViewById(R.id.editStartDate);
    editEndDate = findViewById(R.id.editEndDate);
    editStartTime = findViewById(R.id.editStartTime);
    editEndTime = findViewById(R.id.editEndTime);

//    初期値設定
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
    editStartDate.setOnClickListener(v -> showStartDatePicker(calendarStart, editStartDate, calendarEnd, editEndDate));
    editEndDate.setOnClickListener(v -> showEndDatePicker(calendarEnd, editEndDate, calendarStart));
    editStartTime.setOnClickListener(v -> showStartTimePicker(calendarStart, editStartTime, calendarEnd, editEndTime, editEndDate));
    editEndTime.setOnClickListener(v -> showEndTimePicker(calendarEnd, editEndTime, calendarStart));

    // タスクの保存処理
    storeButton.setOnClickListener(v -> {
      String title = titleInput.getText().toString();
      String details = detailsInput.getText().toString();
      if (title.isEmpty()) {
        // タイトルが空の場合、警告ダイアログを表示
        new AlertDialog.Builder(AddOrEditToDoActivity.this)
            .setTitle("Warning")
            .setMessage("Input title.")
            .setPositiveButton("OK", (dialog, which) -> dialog.dismiss()) // OKボタンでダイアログを閉じる
            .show();
        return; // 処理を終了してリスナーを抜ける
      }
      if (planRadioButton.isChecked()) {
        // allDaySwitch がチェックされている場合の処理
        if (allDaySwitch.isChecked()) {
          // calendarStart を 0時に設定
          calendarStart.set(Calendar.HOUR_OF_DAY, 0);
          calendarStart.set(Calendar.MINUTE, 0);
          calendarStart.set(Calendar.SECOND, 0);
          calendarStart.set(Calendar.MILLISECOND, 0);

          // calendarEnd を翌日の0時に設定
          Calendar endDate = (Calendar) calendarStart.clone(); // calendarStart をクローン
          endDate.add(Calendar.DAY_OF_MONTH, 1); // 1日追加
          endDate.set(Calendar.HOUR_OF_DAY, 0);
          endDate.set(Calendar.MINUTE, 0);
          endDate.set(Calendar.SECOND, 0);
          endDate.set(Calendar.MILLISECOND, 0);

          calendarEnd.setTime(endDate.getTime()); // calendarEnd に設定
        }

        // デバッグ用に出力
        Log.d("DEBUG", "Adjusted Start Date: " + calendarStart.getTime());
        Log.d("DEBUG", "Adjusted End Date: " + calendarEnd.getTime());
        MainActivity.plans.add(new Plan(title, details, calendarStart, calendarEnd, allDaySwitch.isChecked()));
      } else {
        MainActivity.tasks.add(new Task(title, details, calendarStart));
      }

      Intent intent = new Intent(AddOrEditToDoActivity.this, MainActivity.class);
      intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
      startActivity(intent);
      finish(); // AddOrEditToDoActivity を終了
    });
  }

  private void showEndDatePicker(Calendar calendarEnd, EditText editEndDate, Calendar calendarStart) {
    new DatePickerDialog(AddOrEditToDoActivity.this, (view, year, month, dayOfMonth) -> {
      // 変更前の日付を記録
      Calendar previousEndDate = Calendar.getInstance();
      previousEndDate.setTime(calendarEnd.getTime());
      // 新しい日付を設定
      calendarEnd.set(year, month, dayOfMonth);

      // 逆転チェック
      if (calendarEnd.before(calendarStart) || calendarEnd.equals(calendarStart)) {
        // 逆転している場合、ユーザーに警告を表示
        new AlertDialog.Builder(AddOrEditToDoActivity.this)
            .setTitle("Warning")
            .setMessage("End time must be after start time.")
            .setPositiveButton("OK", (dialog, which) -> {
              // OKボタンが押されたら、もう一度日付を選択できるようにする
              dialog.dismiss();
            })
            .show();
        // calendarEndを変更前の日付に戻す
        calendarEnd.setTime(previousEndDate.getTime());
      } else {
        // 逆転していない場合は日付を更新
        updateDateLabel(editEndDate, calendarEnd);
      }
    }, calendarEnd.get(Calendar.YEAR), calendarEnd.get(Calendar.MONTH), calendarEnd.get(Calendar.DAY_OF_MONTH)).show();
  }

  private void showEndTimePicker(Calendar calendarEnd, EditText editEndTime, Calendar calendarStart) {
    new TimePickerDialog(AddOrEditToDoActivity.this, (view, hourOfDay, minute) -> {
      // 変更前の時間を記録
      Calendar previousEndTime = Calendar.getInstance();
      previousEndTime.setTime(calendarEnd.getTime());

      // 新しい時間を設定
      calendarEnd.set(Calendar.HOUR_OF_DAY, hourOfDay);
      calendarEnd.set(Calendar.MINUTE, minute);

      // 逆転チェック
      if (calendarEnd.before(calendarStart) || calendarEnd.equals(calendarStart)) {
        // 逆転している場合、ユーザーに警告を表示
        new AlertDialog.Builder(AddOrEditToDoActivity.this)
            .setTitle("Warning")
            .setMessage("End time must be after start time.")
            .setPositiveButton("OK", (dialog, which) -> {
              // OKボタンが押されたら、もう一度時間を選択できるようにする
              dialog.dismiss();
            })
            .show();
        // calendarEndを変更前の時間に戻す
        calendarEnd.setTime(previousEndTime.getTime());
      } else {
        // 逆転していない場合は時間を更新
        updateTimeLabel(editEndTime, calendarEnd);
      }
    }, calendarEnd.get(Calendar.HOUR_OF_DAY), calendarEnd.get(Calendar.MINUTE), true).show();
  }

  private void showStartDatePicker(Calendar calendarStart, EditText editStartDate, Calendar calendarEnd, EditText editEndDate) {
    // 変更前の日付を記録
    Calendar previousStartDate = Calendar.getInstance();
    previousStartDate.setTime(calendarStart.getTime());

    new DatePickerDialog(AddOrEditToDoActivity.this, (view, year, month, dayOfMonth) -> {
      // 変更後の日付を設定
      calendarStart.set(year, month, dayOfMonth);
      updateDateLabel(editStartDate, calendarStart);

      // 変更後の日付と変更前の日付の差分を計算
      long diffInMillis = calendarStart.getTimeInMillis() - previousStartDate.getTimeInMillis();
      int daysDifference = (int) (diffInMillis / (1000 * 60 * 60 * 24)); // ミリ秒を日数に変換

      // endDateを同じ分だけ調整
      calendarEnd.add(Calendar.DAY_OF_MONTH, daysDifference);
      updateDateLabel(editEndDate, calendarEnd);

    }, calendarStart.get(Calendar.YEAR), calendarStart.get(Calendar.MONTH), calendarStart.get(Calendar.DAY_OF_MONTH)).show();
  }

  private void showStartTimePicker(Calendar calendarStart, EditText editStartTime, Calendar calendarEnd, EditText editEndTime, EditText editEndDate) {
    // 変更前の時間を記録
    Calendar previousStartTime = Calendar.getInstance();
    previousStartTime.setTime(calendarStart.getTime());

    new TimePickerDialog(AddOrEditToDoActivity.this, (view, hourOfDay, minute) -> {
      // 変更後の時間を設定
      calendarStart.set(Calendar.HOUR_OF_DAY, hourOfDay);
      calendarStart.set(Calendar.MINUTE, minute);
      updateTimeLabel(editStartTime, calendarStart);

      // 変更後の時間と変更前の時間の差分を計算
      long diffInMillis = calendarStart.getTimeInMillis() - previousStartTime.getTimeInMillis();
      int minutesDifference = (int) (diffInMillis / (1000 * 60)); // ミリ秒を分数に変換

      // endTimeを同じ分だけ調整
      calendarEnd.add(Calendar.MINUTE, minutesDifference);
      updateTimeLabel(editEndTime, calendarEnd);
      updateDateLabel(editEndDate, calendarEnd);

    }, calendarStart.get(Calendar.HOUR_OF_DAY), calendarStart.get(Calendar.MINUTE), true).show();
  }

  private void updateDateLabel(EditText editText, Calendar calendar) {
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd EEEE", Locale.getDefault());
    editText.setText(sdf.format(calendar.getTime()));
  }

  private void updateTimeLabel(EditText editText, Calendar calendar) {
    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
    editText.setText(sdf.format(calendar.getTime()));
  }

  // 戻るボタンでActivity終了
  @SuppressLint("MissingSuperCall")
  @Override
  public void onBackPressed() {
    Intent intent = new Intent(AddOrEditToDoActivity.this, MainActivity.class);
    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
    startActivity(intent);
    finish(); // AddOrEditToDoActivity を終了
  }
}