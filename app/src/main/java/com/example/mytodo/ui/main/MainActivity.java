package com.example.mytodo.ui.main;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.Button;
import android.widget.FrameLayout;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import com.example.mytodo.data.model.Category;
import com.example.mytodo.database.MyDao;
import com.example.mytodo.database.MyToDoDatabase;
import com.example.mytodo.ui.add_or_edit_to_do.AddOrEditToDoActivity;
import com.example.mytodo.R;
import com.example.mytodo.utils.DateUtils;
import com.example.mytodo.utils.TouchUtils;
import com.google.android.material.tabs.TabLayout;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
  public static final Date presentDate = new Date();
  public static Date showingDate = presentDate;

  private MyToDoDatabase db;
  private MyDao myDao;

  public static TabLayout dateTypeTabLayout;
  private GestureDetector gestureDetector;
  public Button addButton;
  private FrameLayout toDoDisplay;

  private static final int DEBOUNCE_DELAY_MS = 300;
  private boolean isButtonClicked = false;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    //    データベース初期設定
    db = MyToDoDatabase.getDatabase(this);
    myDao = db.myDao();
    populateInitialData(db);

    dateTypeTabLayout = findViewById(R.id.dateTypeTabLayout);
    addButton = findViewById(R.id.addButton);
    toDoDisplay = findViewById(R.id.toDoDisplay);

    // 初期設定: 3番目のタブを選択し、showingDateに基づいて文字を更新
    dateTypeTabLayout.getTabAt(2).select(); // タブの3番目（インデックス2）を選択
    updateTextViewBasedOnDate(showingDate);
    displayFragmentForTab(dateTypeTabLayout.getSelectedTabPosition());

//    // GestureDetectorの設定
//    gestureDetector = new GestureDetector(this, new GestureListener());


    // タブのクリックリスナー設定
    dateTypeTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
      @Override
      public void onTabSelected(@NonNull TabLayout.Tab tab) {
        // タブが選択されたときに、タブの位置に応じて画面を表示
        int tabPosition = tab.getPosition();

        // タブの位置に応じて適切なフラグメントを作成
        displayFragmentForTab(tabPosition);

      }
      @Override
      public void onTabUnselected(@NonNull TabLayout.Tab tab) {
        // タブが非選択になったときの処理（必要なら追加）
      }

      @Override
      public void onTabReselected(@NonNull TabLayout.Tab tab) {
        // タブが再選択されたときの処理（必要なら追加）
      }
    });

    // ここでタッチリスナーを設定（クリックの優先設定）
    TouchUtils.setPriorityOnClickListener(toDoDisplay, new Button[]{addButton}, gestureDetector, 32, this);

//    ボタンクリックイベント
    addButton.setOnClickListener(v -> {
      Intent intent = new Intent(MainActivity.this, AddOrEditToDoActivity.class);
      startActivity(intent);
    });
  }

  public void updateTextViewBasedOnDate(Date date) {
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(date);

    // 1つ目のタブ: 月名
    String monthName = new SimpleDateFormat("MMMM", Locale.getDefault()).format(date);
    if (dateTypeTabLayout.getTabAt(0) != null) {
      dateTypeTabLayout.getTabAt(0).setText(monthName);
    }

    // 2つ目のタブ: その週の "Sun-Sat"
    Calendar startOfWeek = Calendar.getInstance();
    startOfWeek.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
    startOfWeek.set(Calendar.WEEK_OF_YEAR, calendar.get(Calendar.WEEK_OF_YEAR));
    startOfWeek.set(Calendar.YEAR, calendar.get(Calendar.YEAR));
    Date startOfWeekDate = startOfWeek.getTime();

    Calendar endOfWeek = Calendar.getInstance();
    endOfWeek.setTime(startOfWeekDate);
    endOfWeek.add(Calendar.DAY_OF_WEEK, 6); // 週の終わり
    Date endOfWeekDate = endOfWeek.getTime();

    String weekRange = DateUtils.getWeekDayRange(startOfWeekDate, endOfWeekDate);

    if (dateTypeTabLayout.getTabAt(1) != null) {
      dateTypeTabLayout.getTabAt(1).setText(weekRange.toString());
    }

    // 3つ目のタブ: その日
    int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
    String ordinalDate = DateUtils.getOrdinalDate(dayOfMonth);
    if (dateTypeTabLayout.getTabAt(2) != null) {
      dateTypeTabLayout.getTabAt(2).setText(ordinalDate);
    }
  }

  public void displayFragmentForTab(int tabPosition) {
    Fragment fragment;

    switch (tabPosition) {
      case 0:
        fragment = MonthFragment.newInstance();
        break;
      case 1:
        fragment = WeekFragment.newInstance();
        break;
      case 2:
        fragment = DayFragment.newInstance();
        break;
      default:
        fragment = DayFragment.newInstance(); // デフォルトのフラグメント
        break;
    }

    // フラグメントを表示
    getSupportFragmentManager().beginTransaction()
        .replace(R.id.toDoDisplay, fragment)
        .commit();
  }

  public class GestureListener extends GestureDetector.SimpleOnGestureListener {
    private static final int SWIPE_THRESHOLD = 50;
    private static final int SWIPE_VELOCITY_THRESHOLD = 50;

    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
      // 縦スクロールのしきい値を厳しくする
      if (Math.abs(distanceX) > Math.abs(distanceY) && Math.abs(distanceX) > SWIPE_THRESHOLD) {
        // 横スクロールを検出
        return true;
      }
      // 縦スクロールのしきい値を厳しくする
      if (Math.abs(distanceY) > SWIPE_THRESHOLD) {
        // 縦スクロールを許可
        return false;
      }
      return super.onScroll(e1, e2, distanceX, distanceY);
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
      if (Math.abs(e1.getY() - e2.getY()) > SWIPE_THRESHOLD) {
        return false; // 縦スワイプは無視
      }

      if (Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
        if (e1.getX() - e2.getX() > SWIPE_THRESHOLD) {
          // 左スワイプ: 次の月、週、日
          updateFragmentWithSwipe(1);
        } else if (e2.getX() - e1.getX() > SWIPE_THRESHOLD) {
          // 右スワイプ: 前の月、週、日
          updateFragmentWithSwipe(-1);
        }
        return true;
      }
      return false;
    }
  }

  public void updateFragmentWithSwipe(int direction) {
    // 現在のタブを取得
    int currentTab = dateTypeTabLayout.getSelectedTabPosition();

    // 現在のタブに応じて日付を更新
    switch (currentTab) {
      case 0: // 月タブ
        showingDate = DateUtils.addMonths(showingDate, direction);
        break;
      case 1: // 週タブ
        showingDate = DateUtils.addWeeks(showingDate, direction);
        break;
      case 2: // 日タブ
        showingDate = DateUtils.addDays(showingDate, direction);
        break;
    }
    Log.d("ShowingDate",showingDate.toString());


    // 日付に基づいてタブの内容を更新
    updateTextViewBasedOnDate(showingDate);

    // 現在選択されているタブに基づいてフラグメントを再表示
    displayFragmentForTab(currentTab);
  }

  private void populateInitialData(final MyToDoDatabase db) {
    // データベースにアクセスして初期データを挿入する
    new Thread(() -> {
      MyDao dao = db.myDao();
      if (dao.getCategories().isEmpty()) { // カテゴリが空かどうか確認
        Log.d("DB", "Add 1st Category");
        dao.insertCategory(new Category("none"));
        Log.d("DB", "Add 2nd Category");
        dao.insertCategory(new Category("Add New"));
      }
    }).start();
  }

// 他のActivityがなければ、戻るボタンでアプリを終了
  @SuppressLint("MissingSuperCall")
  @Override
  public void onBackPressed() {
    finish(); // アプリを終了させる
  }
}

