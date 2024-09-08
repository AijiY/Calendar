package com.example.mytodo.ui.main;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import com.example.mytodo.R;
import com.example.mytodo.data.model.Plan;
import com.example.mytodo.data.model.Result;
import com.example.mytodo.data.model.Task;
import com.example.mytodo.database.MyDao;
import com.example.mytodo.database.MyToDoDatabase;
import com.example.mytodo.utils.ClassUtils;
import com.example.mytodo.utils.DateUtils;
import com.example.mytodo.utils.TouchUtils;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MonthFragment extends Fragment {
  private MainActivity mainActivity;

  private GestureDetector gestureDetector;

  private MyToDoDatabase db;
  private MyDao myDao;

  private TextView[] dayTexts = new TextView[35];
  private View[] circles = new View[35];
  private View[] taskViews = new View[35];
  private Date[] monthDates = new Date[35];

  public MonthFragment() {
  }
  public static MonthFragment newInstance() {
    MonthFragment fragment = new MonthFragment();
    Bundle args = new Bundle();
    return fragment;
  }

  @Override
  public void onAttach(Context context) {
    super.onAttach(context);
    if (context instanceof MainActivity) {
      mainActivity = (MainActivity) context; // contextをMainActivityにキャスト
    } else {
      throw new RuntimeException(context.toString()
          + " must be an instance of MainActivity");
    }
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
  }

  @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_month, container, false);

    // TextView と CircleView を取得
    assignToVariables(view);

    // 日付の設定と色分け
    setDayTextsAndColors();

    //    プラン等の表示
    showTextViews();

    //    スワイプを検出するための GestureDetector を作成
    gestureDetector = new GestureDetector(getContext(), mainActivity.new GestureListener());

    //    monthLayoutにスワイプよりもタッチを優先する設定
    LinearLayout monthLayout = view.findViewById(R.id.monthLayout);
    TouchUtils.setPriorityOnClickListener(monthLayout, dayTexts, gestureDetector, 0, getContext());

//    日付をクリックしたときの処理
    for (int i = 0; i < dayTexts.length; i++) {
      final Date newShowingDate = monthDates[i];
      dayTexts[i].setOnClickListener(v -> {
//        showingDateに1日加算
        Calendar calendarForShowingDate = Calendar.getInstance();
        calendarForShowingDate.setTime(newShowingDate);
        mainActivity.showingDate = calendarForShowingDate.getTime();
//        DayFragment作成
        mainActivity.dateTypeTabLayout.getTabAt(2).select(); // タブの3番目（インデックス2）を選択
        mainActivity.updateTextViewBasedOnDate(mainActivity.showingDate);
        mainActivity.displayFragmentForTab(mainActivity.dateTypeTabLayout.getSelectedTabPosition());
      });
    }

    return view;
  }

  private void assignToVariables(View view) {
    // 各 TextView を取得
    for (int i = 0; i < 35; i++) {
      String idName = "dayText" + (i + 1);
      int resId = getResources().getIdentifier(idName, "id", getActivity().getPackageName());
      if (resId != 0) {
        dayTexts[i] = view.findViewById(resId);
      }
    }

    // 各 CircleView を取得
    for (int i = 0; i < 35; i++) {
      String idName = "circleView" + (i + 1);
      int resId = getResources().getIdentifier(idName, "id", getActivity().getPackageName());
      if (resId != 0) {
        circles[i] = view.findViewById(resId);
      }
    }

    // 各 dayTaskView を取得
    for (int i = 0; i < 35; i++) {
      String idName = "dayTaskView" + (i + 1);
      int resId = getResources().getIdentifier(idName, "id", getActivity().getPackageName());
      if (resId != 0) {
        taskViews[i] = view.findViewById(resId);
      }
    }
  }

  // 前月の日数を計算するメソッド
  private int getDaysInPreviousMonth(Calendar calendar, int startDayOfWeek) {
    // 前月の日数を計算するためにカレンダーを1か月戻す
    calendar.add(Calendar.MONTH, -1);
    int daysInPreviousMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
    calendar.add(Calendar.MONTH, 1); // 元に戻す
    return daysInPreviousMonth - (startDayOfWeek - 2);
  }

  private void setDayTextsAndColors() {
    // `showingDate` を取得（例: フラグメントの引数などで設定されていると仮定）
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(mainActivity.showingDate);

    // 月の初日を設定
    calendar.set(Calendar.DAY_OF_MONTH, 1);

    // 月の最初の日の曜日と月の日数を取得
    int startDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
    int daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);

    // 現在の日付を取得
    Calendar today = Calendar.getInstance();
    today.setTime(mainActivity.presentDate);

    // 日付の設定
    for (int i = 0; i < 35; i++) {
      TextView dayText = dayTexts[i];
      View circle = circles[i]; // CircleView を取得
      if (dayText != null) {
        Calendar calendarForDate = (Calendar) calendar.clone(); // calendarをクローン

        int day = i - (startDayOfWeek - 1) + 1;
        int dayOfWeek = (i % 7) + 1; // 日曜日が1、土曜日が7

        if (i < startDayOfWeek - 1) {
          // 前月の日付
          dayText.setText(String.valueOf(day + getDaysInPreviousMonth(calendar, startDayOfWeek - 1)));
          dayText.setTextColor(Color.LTGRAY); // 文字色を薄くする

          // 前月の日付を設定
          calendarForDate.add(Calendar.MONTH, -1);
          calendarForDate.set(Calendar.DAY_OF_MONTH, day + getDaysInPreviousMonth(calendar, startDayOfWeek - 1));

          if (dayOfWeek == Calendar.SUNDAY) {
            dayText.setBackgroundColor(Color.parseColor("#FF9F9F")); // 薄い赤（red_white）
          } else if (dayOfWeek == Calendar.SATURDAY) {
            dayText.setBackgroundColor(Color.parseColor("#9F9FFF")); // 薄い青（blue_white）
          } else {
            dayText.setBackgroundColor(Color.TRANSPARENT); // 背景色を透明にする
          }
          circle.setVisibility(View.GONE); // 前月の日付には CircleView を非表示
        } else if (day <= daysInMonth) {
          // 今月の日付
          dayText.setText(String.valueOf(day));
          dayText.setTextColor(Color.BLACK); // 文字色を黒にする
          dayText.setBackgroundColor(Color.TRANSPARENT); // 背景色を透明にする
          // 今月の日付を設定
          calendarForDate.set(Calendar.DAY_OF_MONTH, day);

          // 色分け処理
          if ((i + 1) % 7 == 1) {
            // 1, 8, 15, ... を赤と白の中間色で塗りつぶす
            dayText.setBackgroundColor(Color.parseColor("#FF6F6F")); // 赤と白の中間色
            dayText.setTextColor(Color.WHITE);
          } else if ((i + 1) % 7 == 0) {
            // 7, 14, 21, ... を青と白の中間色で塗りつぶす
            dayText.setBackgroundColor(Color.parseColor("#6F6FFF")); // 青と白の中間色
            dayText.setTextColor(Color.WHITE);
          }
          // 現在の日付に対応する CircleView を表示
          Log.d("MonthFragment", i + ": " + day + ": " + today.get(Calendar.DAY_OF_MONTH));
          if (calendar.get(Calendar.YEAR) == today.get(Calendar.YEAR) &&
              calendar.get(Calendar.MONTH) == today.get(Calendar.MONTH) &&
              day == today.get(Calendar.DAY_OF_MONTH)) {
            circle.setVisibility(View.VISIBLE);  // 現在の日付に対応する CircleView を表示
          } else {
            circle.setVisibility(View.GONE);     // その他の日付に対応する CircleView を非表示
          }
        } else {
          // 翌月の日付
          dayText.setText(String.valueOf(day - daysInMonth));
          dayText.setTextColor(Color.LTGRAY); // 文字色を薄くする

          // 翌月の日付を設定
          calendarForDate.add(Calendar.MONTH, 1);
          calendarForDate.set(Calendar.DAY_OF_MONTH, day - daysInMonth);

          if (dayOfWeek == Calendar.SUNDAY) {
            dayText.setBackgroundColor(Color.parseColor("#FF9F9F")); // 薄い赤（red_white）
          } else if (dayOfWeek == Calendar.SATURDAY) {
            dayText.setBackgroundColor(Color.parseColor("#9F9FFF")); // 薄い青（blue_white）
          } else {
            dayText.setBackgroundColor(Color.TRANSPARENT); // 背景色を透明にする
          }
          circle.setVisibility(View.GONE); // 翌月の日付には CircleView を非表示
        }

        // 設定された日付を monthDates に保存
        monthDates[i] = calendarForDate.getTime();
      }
    }
  }

  private void showTextViews() {
    //    データベースからデータを取得
    db = MyToDoDatabase.getDatabase(getContext());
    myDao = db.myDao();
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(mainActivity.showingDate);
    calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);

    ExecutorService executor = Executors.newSingleThreadExecutor();
    Handler handler = new Handler(Looper.getMainLooper());
    executor.execute(() -> {
      for (int i = 0; i < 35; i++) {
        Log.d("MonthFragment", monthDates[i].toString());
        calendar.setTime(monthDates[i]);
        List<Object> mixedList = new ArrayList<>();
        // 1週間分の日付をループ
        mixedList.addAll(myDao.getPlansByDate(calendar));
        mixedList.addAll(myDao.getTasksByDate(calendar));
        mixedList.addAll(myDao.getResultsByDate(calendar));

        Log.d("monthFragment", "mixedList size: " + mixedList.size());

        mixedList.sort((o1, o2) -> {
          Calendar calendarStart1 = DateUtils.getCalendarStart(o1);
          Calendar calendarStart2 = DateUtils.getCalendarStart(o2);
          int startComparison = calendarStart1.compareTo(calendarStart2);

          if (startComparison != 0) {
            return startComparison; // calendarStartが異なる場合
          }

          // calendarStartが同じ場合の処理
          int classOrder1 = ClassUtils.getClassOrder(o1);
          int classOrder2 = ClassUtils.getClassOrder(o2);

          if (classOrder1 != classOrder2) {
            return Integer.compare(classOrder1, classOrder2); // クラス順でソート
          }

          // 同じクラスのPlanまたはResultでcalendarEndを比較
          if (o1 instanceof Plan || o1 instanceof Result) {
            Calendar calendarEnd1 = DateUtils.getCalendarEnd(o1);
            Calendar calendarEnd2 = DateUtils.getCalendarEnd(o2);
            int endComparison = calendarEnd1.compareTo(calendarEnd2);
            if (endComparison != 0) {
              return endComparison; // calendarEndが異なる場合
            }
          }

          return 0; // それでも同じ場合、順序は変えない
        });

        // Latchを使ってUI更新が完了するまで待つ
        CountDownLatch latch = new CountDownLatch(1);

        int finalI = i;
        //      データ取得後にviewの作成
        handler.post(() -> {
//          プラン、タスク、結果を表示

          for (Object obj : mixedList) {
            if (obj instanceof Plan) {
              Plan plan = (Plan) obj;
              showPlans(List.of(plan), calendar, taskViews[finalI]);
            } else if (obj instanceof Task) {
              Task task = (Task) obj;
              showTasks(List.of(task), calendar, taskViews[finalI]);
            } else if (obj instanceof Result) {
              Result result = (Result) obj;
              showResults(List.of(result), calendar, taskViews[finalI]);
            }
          }
          // UI更新が完了したらlatchをカウントダウン
          latch.countDown();
        });

        try {
          // latchがカウントダウンされるまで待つ
          latch.await();
        } catch (InterruptedException e) {
          e.printStackTrace();
        }

        calendar.add(Calendar.DAY_OF_YEAR, 1);
      }

      // ExecutorService の終了処理
      executor.shutdown();
    });
  }

  private void showPlans(List<Plan> plans, Calendar calendar, View taskView) {
    Log.d("MonthFragment", "showPlans");
//    色リソースの取得
    int viewColor = ContextCompat.getColor(taskView.getContext(), R.color.plan);
    for (Plan plan : plans) {
      Calendar startCal = Calendar.getInstance();
      startCal.setTime(plan.getCalendarStart().getTime());
      Calendar endCal = Calendar.getInstance();
      endCal.setTime(plan.getCalendarEnd().getTime());

      // 新しい TextView を作成
      TextView newTaskView = new TextView(taskView.getContext());

      // スタイルを適用
      newTaskView.setLayoutParams(new ViewGroup.LayoutParams(
          ViewGroup.LayoutParams.MATCH_PARENT,
          ViewGroup.LayoutParams.WRAP_CONTENT
      ));
      newTaskView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 8); // 文字の大きさ
      newTaskView.setTextColor(Color.WHITE); // 文字色
      newTaskView.setBackgroundColor(viewColor); // 背景色 (ライトブルー)
      newTaskView.setPadding(8, 8, 8, 8); // パディング

      // 予定が複数日にまたがる場合、タイトルに（Y/X 日目）を追加
      String displayText = plan.getTitle();
      if (!DateUtils.isSameDayByCalendar(startCal, endCal)) {
        // 日数計算
        long daysBetween = (endCal.getTimeInMillis() - startCal.getTimeInMillis()) / (1000 * 60 * 60 * 24);
        long daysElapsed = (calendar.getTimeInMillis() - startCal.getTimeInMillis()) / (1000 * 60 * 60 * 24) + 1;
        displayText += String.format("（%d/%d 日目）", daysElapsed, daysBetween + 1);
      }
      newTaskView.setText(displayText);

      // クリックリスナーの設定
      newTaskView.setOnClickListener(v -> {
        // ここにクリック時の処理を実装
        // 例: 予定の詳細画面を開く

      });

      // taskView に新しい TextView を追加
      if (taskView instanceof ViewGroup) {
        ((ViewGroup) taskView).addView(newTaskView);
      }

    }
  }

  private void showTasks(List<Task> tasks, Calendar calendar, View taskView) {
    Log.d("MonthFragment", "showTasks");
//    色リソースの取得
    int unfinishedColor = ContextCompat.getColor(taskView.getContext(), R.color.taskUnfinished);
    int finishedColor = ContextCompat.getColor(taskView.getContext(), R.color.taskFinished);

    for (Task task : tasks) {

      // 新しい TextView を作成
      TextView newTaskView = new TextView(taskView.getContext());

      // スタイルを適用
      newTaskView.setLayoutParams(new ViewGroup.LayoutParams(
          ViewGroup.LayoutParams.MATCH_PARENT,
          ViewGroup.LayoutParams.WRAP_CONTENT
      ));
      newTaskView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 8); // 文字の大きさ
      newTaskView.setTextColor(Color.WHITE); // 文字色
      if (task.isFinished()) {
        newTaskView.setBackgroundColor(finishedColor);
      } else {
        newTaskView.setBackgroundColor(unfinishedColor);
      }
      newTaskView.setPadding(8, 8, 8, 8); // パディング

      // 予定が複数日にまたがる場合、タイトルに（Y/X 日目）を追加
      String displayText = task.getTitle();
      newTaskView.setText(displayText);

      // クリックリスナーの設定
      newTaskView.setOnClickListener(v -> {
        // ここにクリック時の処理を実装
        // 例: 予定の詳細画面を開く

      });

      // taskView に新しい TextView を追加
      if (taskView instanceof ViewGroup) {
        ((ViewGroup) taskView).addView(newTaskView);
      }

    }
  }

  private void showResults(List<Result> results, Calendar calendar, View taskView) {
    Log.d("MonthFragment", "showResults");
//    色リソースの取得
    int viewColor = ContextCompat.getColor(taskView.getContext(), R.color.result);
    for (Result result : results) {
      Calendar startCal = Calendar.getInstance();
      startCal.setTime(result.getCalendarStart().getTime());
      Calendar endCal = Calendar.getInstance();
      endCal.setTime(result.getCalendarEnd().getTime());

      // 新しい TextView を作成
      TextView newTaskView = new TextView(taskView.getContext());

      // スタイルを適用
      newTaskView.setLayoutParams(new ViewGroup.LayoutParams(
          ViewGroup.LayoutParams.MATCH_PARENT,
          ViewGroup.LayoutParams.WRAP_CONTENT
      ));
      newTaskView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 8); // 文字の大きさ
      newTaskView.setTextColor(Color.WHITE); // 文字色
      newTaskView.setBackgroundColor(viewColor); // 背景色 (ライトブルー)
      newTaskView.setPadding(8, 8, 8, 8); // パディング

      // 予定が複数日にまたがる場合、タイトルに（Y/X 日目）を追加
      String displayText = result.getTitle();
      if (!DateUtils.isSameDayByCalendar(startCal, endCal)) {
        // 日数計算
        long daysBetween = (endCal.getTimeInMillis() - startCal.getTimeInMillis()) / (1000 * 60 * 60 * 24);
        long daysElapsed = (calendar.getTimeInMillis() - startCal.getTimeInMillis()) / (1000 * 60 * 60 * 24) + 1;
        displayText += String.format("（%d/%d 日目）", daysElapsed, daysBetween + 1);
      }
      newTaskView.setText(displayText);

      // クリックリスナーの設定
      newTaskView.setOnClickListener(v -> {
        // ここにクリック時の処理を実装
        // 例: 予定の詳細画面を開く

      });

      // taskView に新しい TextView を追加
      if (taskView instanceof ViewGroup) {
        ((ViewGroup) taskView).addView(newTaskView);
      }

    }
  }
}
