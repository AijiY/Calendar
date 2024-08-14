package com.example.mytodo;

import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;

import android.os.Handler;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import com.google.android.material.tabs.TabLayout;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
  public static final Date presentDate = new Date();
  public static Date showingDate = presentDate;

  private TabLayout dateTypeTabLayout;
  private GestureDetector gestureDetector;
  public Button addButton;
  private FrameLayout toDoDisplay;

  private static final int DEBOUNCE_DELAY_MS = 300;
  private boolean isButtonClicked = false;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    dateTypeTabLayout = findViewById(R.id.dateTypeTabLayout);
    addButton = findViewById(R.id.addButton);
    toDoDisplay = findViewById(R.id.toDoDisplay);

    // 初期設定: 3番目のタブを選択し、showingDateに基づいて文字を更新
    dateTypeTabLayout.getTabAt(2).select(); // タブの3番目（インデックス2）を選択
    updateTextViewBasedOnDate(showingDate);
    displayFragmentForTab(dateTypeTabLayout.getSelectedTabPosition());

    // GestureDetectorの設定
    gestureDetector = new GestureDetector(this, new GestureListener());


    // タブのクリックリスナー設定
    dateTypeTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
      @Override
      public void onTabSelected(@NonNull TabLayout.Tab tab) {
        // タブが選択されたときに addButton のプロパティを取得
        Rect buttonRect = new Rect();
        addButton.getHitRect(buttonRect);
        Log.d("ButtonRect", "Button Rect: " + buttonRect.toShortString());
        Log.d("ButtonVisibility", "Button Visibility: " + (addButton.getVisibility() == View.VISIBLE ? "VISIBLE" : "GONE"));
        // タブが選択されたときに、タブの位置に応じて画面を表示
        int tabPosition = tab.getPosition();

        // タブのテキストを更新
        updateTextViewBasedOnDate(showingDate);

        // タブの位置に応じて適切なフラグメントを作成
        displayFragmentForTab(tabPosition);

//        タブがDayタブ以外の場合、スワイプジェスチャーを有効にする（DayのスワイプはDayFragmentで設定）
        if (tabPosition != 2) { // Dayタブ以外が選択された場合
          toDoDisplay.setOnTouchListener((v, event) -> {
            gestureDetector.onTouchEvent(event);
            return true;
          });
        } else {
          toDoDisplay.setOnTouchListener(null);
        }
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
    toDoDisplay.setOnTouchListener((v, event) -> {
      boolean isGestureDetected = gestureDetector.onTouchEvent(event);

      // タッチイベントの座標を取得
      float x = event.getX();
      float y = event.getY();

      // ボタンの位置とサイズを取得
      Rect buttonRect = new Rect();
      addButton.getHitRect(buttonRect);
      // Rect の領域を広げる（ここでは 10dp を追加）
      int padding = (int) (32 * this.getResources().getDisplayMetrics().density); // dp to pixels
      buttonRect.inset(-padding, -padding); // 領域を広げる

      // タッチがボタンの領域内にあるかどうかをチェック
      if (buttonRect.contains((int) x, (int) y)) {
        // ボタンがタッチされた場合
        if (!isButtonClicked) {
          isButtonClicked = true;
          addButton.performClick();

          // デバウンス処理のために、一定時間後にフラグをリセット
          new Handler().postDelayed(() -> isButtonClicked = false, DEBOUNCE_DELAY_MS);

          // イベントを消費して、スワイプを無効にする
          return true;
        }
      }

      // 横スワイプ
      return true;


    });

//    ボタンクリックイベント
    addButton.setOnClickListener(v -> {
      Intent intent = new Intent(MainActivity.this, AddToDoActivity.class);
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

    String weekRange = getWeekDayRange(startOfWeekDate, endOfWeekDate);

    if (dateTypeTabLayout.getTabAt(1) != null) {
      dateTypeTabLayout.getTabAt(1).setText(weekRange.toString());
    }

    // 3つ目のタブ: その日
    int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
    String ordinalDate = getOrdinalDate(dayOfMonth);
    if (dateTypeTabLayout.getTabAt(2) != null) {
      dateTypeTabLayout.getTabAt(2).setText(ordinalDate);
    }
  }

  public void displayFragmentForTab(int tabPosition) {
    Fragment fragment;

    switch (tabPosition) {
      case 0:
        fragment = MonthFragment.newInstance(showingDate);
        break;
      case 1:
        fragment = WeekFragment.newInstance(showingDate);
        break;
      case 2:
        fragment = DayFragment.newInstance(showingDate);
        break;
      default:
        fragment = DayFragment.newInstance(showingDate); // デフォルトのフラグメント
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
        showingDate = addMonths(showingDate, direction);
        break;
      case 1: // 週タブ
        showingDate = addWeeks(showingDate, direction);
        break;
      case 2: // 日タブ
        showingDate = addDays(showingDate, direction);
        break;
    }

    // 日付に基づいてタブの内容を更新
    updateTextViewBasedOnDate(showingDate);

    // 現在選択されているタブに基づいてフラグメントを再表示
    displayFragmentForTab(currentTab);
  }

  public Date addMonths(Date date, int amount) {
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(date);
    calendar.add(Calendar.MONTH, amount);
    calendar.set(Calendar.DAY_OF_MONTH, 1); // 月の最初の日に設定
    return calendar.getTime();
  }

  public Date addWeeks(Date date, int amount) {
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(date);
    calendar.add(Calendar.WEEK_OF_YEAR, amount);
    calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY); // 週の最初の日に設定
    return calendar.getTime();
  }

  public Date addDays(Date date, int amount) {
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(date);
    calendar.add(Calendar.DAY_OF_MONTH, amount);
    return calendar.getTime();
  }

  public String getOrdinalDate(int dayOfMonth) {
    String[] suffixes = {"th", "st", "nd", "rd"};
    int j = dayOfMonth % 10;
    int k = dayOfMonth % 100;
    String suffix;

    if (j == 1 && k != 11) {
      suffix = suffixes[1];
    } else if (j == 2 && k != 12) {
      suffix = suffixes[2];
    } else if (j == 3 && k != 13) {
      suffix = suffixes[3];
    } else {
      suffix = suffixes[0];
    }

    return dayOfMonth + suffix;
  }

  public String getWeekDayRange(Date startDate, Date endDate) {
    SimpleDateFormat dateFormat = new SimpleDateFormat("d", Locale.getDefault());
    int startDay = Integer.parseInt(dateFormat.format(startDate));
    int endDay = Integer.parseInt(dateFormat.format(endDate));

    return getOrdinalDate(startDay) + "-" + getOrdinalDate(endDay);
  }
}

