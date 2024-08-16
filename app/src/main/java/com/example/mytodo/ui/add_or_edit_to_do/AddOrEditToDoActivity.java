package com.example.mytodo.ui.add_or_edit_to_do;

import static com.example.mytodo.ui.main.MainActivity.showingDate;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.mytodo.R;
import com.example.mytodo.data.model.Category;
import com.example.mytodo.data.model.Plan;
import com.example.mytodo.data.model.Result;
import com.example.mytodo.data.model.Task;
import com.example.mytodo.database.MyDao;
import com.example.mytodo.database.MyToDoDatabase;
import com.example.mytodo.ui.main.MainActivity;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class AddOrEditToDoActivity extends AppCompatActivity {
  private EditText editStartDate;
  private EditText editEndDate;
  private EditText editStartTime;
  private EditText editEndTime;

  private Calendar calendarStart;
  private Calendar calendarEnd;

  private int currentCategorySelection = 0;

  private MyToDoDatabase db;
  private MyDao myDao;

  private List<String> categories;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_add_or_edit_to_do);

//　レイアウトの各ビューを取得
    Button exitButton = findViewById(R.id.exitButton);
    Button storeButton = findViewById(R.id.storeButton);
    RadioButton planRadioButton = findViewById(R.id.planRadioButton);
    RadioButton taskRadioButton = findViewById(R.id.taskRadioButton);
    RadioButton recordRadioButton = findViewById(R.id.recordRadioButton);
    Switch allDaySwitch = findViewById(R.id.allDaySwitch);
    LinearLayout startTimeContainer = findViewById(R.id.startTimeContainer);
    LinearLayout endTimeContainer = findViewById(R.id.endTimeContainer);
    TextInputLayout startTimeLayout = findViewById(R.id.startTimeLayout);
    TextInputLayout endTimeLayout = findViewById(R.id.endTimeLayout);
    TextInputEditText titleInput = findViewById(R.id.titleInput);
    TextInputEditText detailsInput = findViewById(R.id.detailsInput);
    Spinner categorySpinner = findViewById(R.id.categorySpinner);
    LinearLayout categoryContainer = findViewById(R.id.categoryContainer);

//    データベース初期設定
    db = MyToDoDatabase.getDatabase(this);
    myDao = db.myDao();

    // データベースからカテゴリを取得してSpinnerに設定
    Executor executor = Executors.newSingleThreadExecutor();
    executor.execute(() -> {
      // データベースからカテゴリを全件取得
      categories = myDao.getAllCategoryNames();

      // UIスレッドでSpinnerに反映
      runOnUiThread(() -> {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
            R.layout.spinner_item, // アイテム表示用のカスタムレイアウト
            categories);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        categorySpinner.setAdapter(adapter);
      });
    });

    categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
      @Override
      public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (position == 1) { // 「Add New」なら
          showAddNewItemDialog(currentCategorySelection); // ダイアログを表示
        }
      }

      @Override
      public void onNothingSelected(AdapterView<?> parent) {
        // 特に何もしない
      }
    });

//    exitButton が押されたら MainActivity に戻る
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
        categorySpinner.setSelection(0);
        categoryContainer.setVisibility(View.GONE);
      } else {
        endTimeContainer.setVisibility(View.VISIBLE);
        allDaySwitch.setVisibility(View.VISIBLE);
        categoryContainer.setVisibility(View.VISIBLE);
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

//    時刻初期値設定
    setInitialDateTime();

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
        // トーストメッセージで警告
        Toast.makeText(this, "Input title.", Toast.LENGTH_SHORT).show();
        return; // 処理を終了してリスナーを抜ける
      }
      if (planRadioButton.isChecked()) {
        // allDaySwitch がチェックされている場合の処理
        addNewPlan(title, details, calendarStart, calendarEnd, allDaySwitch, categorySpinner);
      } else if (taskRadioButton.isChecked()) {
        Task task = new Task(title, details, false);
        new Thread(() -> myDao.insertTask(task)).start();
      } else {
        Result result = new Result(title, details, categorySpinner.getSelectedItem().toString(), allDaySwitch.isChecked(), calendarStart, calendarEnd);
        new Thread(() -> myDao.insertResult(result)).start();
      }

//  デバッグ
      new Thread(() -> {
        // データベースからデータを取得
        List<Plan> plans = myDao.getAllPlans();
        List<Task> tasks = myDao.getAllTasks();
        List<Result> results = myDao.getAllResults();
        List<Category> categories2 = myDao.getAllCategories();

        // Gsonのインスタンスを作成（インデント付き）
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        Type plansType = new TypeToken<List<Plan>>() {}.getType();
        Type tasksType = new TypeToken<List<Task>>() {}.getType();
        Type resultsType = new TypeToken<List<Result>>() {}.getType();
        Type categoriesType = new TypeToken<List<Category>>() {}.getType();

        String plansJson = gson.toJson(plans, plansType);
        String tasksJson = gson.toJson(tasks, tasksType);
        String resultsJson = gson.toJson(results, resultsType);
        String categoriesJson = gson.toJson(categories2, categoriesType);

        // 整形してログに出力
        Log.d("MyTag", "Plans JSON:\n" + plansJson);
        Log.d("MyTag", "Tasks JSON:\n" + tasksJson);
        Log.d("MyTag", "Results JSON:\n" + resultsJson);
        Log.d("MyTag", "Categories JSON:\n" + categoriesJson);
      }).start();
//      デバッグ終了

      Intent intent = new Intent(AddOrEditToDoActivity.this, MainActivity.class);
      intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
      startActivity(intent);
      finish(); // AddOrEditToDoActivity を終了
    });
  }

  private void setInitialDateTime() {
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
        // トーストメッセージで警告
        Toast.makeText(this, "End time must be after start time.", Toast.LENGTH_SHORT).show();
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
        // トーストメッセージで警告
        Toast.makeText(this, "End time must be after start time.", Toast.LENGTH_SHORT).show();
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

  private void showAddNewItemDialog(int currentSelection) {
    // Spinnerとその選択状態を取得
    Spinner categorySpinner = findViewById(R.id.categorySpinner);

    // ダイアログのレイアウトをインフレート
    LayoutInflater inflater = LayoutInflater.from(this);
    View dialogView = inflater.inflate(R.layout.dialog_add_new_category, null);

    // ダイアログビルダーを作成
    AlertDialog.Builder builder = new AlertDialog.Builder(this);
    builder.setTitle("Add New Category")
        .setView(dialogView)
        .setPositiveButton("Add", null) // 初期設定でボタンの動作を無効にする
        .setNegativeButton("Cancel", (dialog, which) -> {
          // ダイアログをキャンセルしたときにSpinnerの選択を元に戻す
          categorySpinner.setSelection(currentSelection);
          dialog.dismiss();
        });

    // ダイアログを作成
    AlertDialog dialog = builder.create();

    // ダイアログ表示後の処理
    dialog.setOnShowListener(d -> {
      // EditTextを取得
      EditText newItemInput = dialogView.findViewById(R.id.new_item_input);

      // EditTextにフォーカスを設定
      newItemInput.requestFocus();

      // ソフトウェアキーボードを表示するための処理
      InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
      imm.showSoftInput(newItemInput, InputMethodManager.SHOW_IMPLICIT);

      // PositiveButtonの動作をカスタマイズ
      Button addButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
      addButton.setOnClickListener(v -> {
        // 新しい項目を取得してSpinnerに追加する
        String newItem = newItemInput.getText().toString().trim(); // 空白をトリム

        // Spinnerに新しいアイテムを追加
        ArrayAdapter<String> adapter = (ArrayAdapter<String>) categorySpinner.getAdapter();

        // 重複チェック
        if (newItem.isEmpty() || categories.contains(newItem)) {
          // トーストメッセージで警告
          Toast.makeText(this, "Category already exists or is empty.", Toast.LENGTH_SHORT).show();
        } else {
          // Executorを使用して非同期でデータベース操作を実行
          Executor executor = Executors.newSingleThreadExecutor();
          executor.execute(() -> {
            // 1. newItem を Category テーブルに追加
            Category newCategory = new Category(newItem);
            myDao.insertCategory(newCategory);

            // 2. categories を更新されたデータベースの内容で上書き
            categories = myDao.getAllCategoryNames();

            // UIスレッドでSpinnerを更新
            runOnUiThread(() -> {
              // 3. Spinnerのアダプターに変更を通知
              adapter.clear();
              adapter.addAll(categories);
              adapter.notifyDataSetChanged();

              // 4. 新しいアイテムを選択する
              currentCategorySelection = adapter.getPosition(newItem);
              categorySpinner.setSelection(currentCategorySelection);

              // 5. ダイアログを閉じる
              dialog.dismiss(); // 正常な場合のみダイアログを閉じる
            });
          });
        }
      });
    });

    // ダイアログを表示
    dialog.show();
  }

  private void addNewPlan(String title, String details, Calendar calendarStart, Calendar calendarEnd, Switch allDaySwitch, Spinner categorySpinner) {
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
    Plan plan = new Plan(title, details, categorySpinner.getSelectedItem().toString(), allDaySwitch.isChecked(), calendarStart, calendarEnd);
    new Thread(() -> myDao.insertPlan(plan)).start();
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