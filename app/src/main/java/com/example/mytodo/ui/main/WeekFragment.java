package com.example.mytodo.ui.main;

import android.content.Context;
import android.graphics.Color;
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
import androidx.annotation.NonNull;
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

public class WeekFragment extends Fragment {
  private MainActivity mainActivity;

  private GestureDetector gestureDetector;

  private MyToDoDatabase db;
  private MyDao myDao;

  public WeekFragment() {
  }

  public static WeekFragment newInstance() {
    WeekFragment fragment = new WeekFragment();
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
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
  }

  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_week, container, false);


    // 日付を設定するための TextView と円形の View を取得
    TextView sunTextView = view.findViewById(R.id.SunTextView);
    View sunCircleView = view.findViewById(R.id.sunCircleView);
    View sunTaskView = view.findViewById(R.id.sunTaskView);

    TextView monTextView = view.findViewById(R.id.MonTextView);
    View monCircleView = view.findViewById(R.id.monCircleView);
    View monTaskView = view.findViewById(R.id.monTaskView);

    TextView tueTextView = view.findViewById(R.id.TueTextView);
    View tueCircleView = view.findViewById(R.id.tueCircleView);
    View tueTaskView = view.findViewById(R.id.tueTaskView);

    TextView wedTextView = view.findViewById(R.id.WedTextView);
    View wedCircleView = view.findViewById(R.id.wedCircleView);
    View wedTaskView = view.findViewById(R.id.wedTaskView);

    TextView thuTextView = view.findViewById(R.id.ThuTextView);
    View thuCircleView = view.findViewById(R.id.thuCircleView);
    View thuTaskView = view.findViewById(R.id.thuTaskView);

    TextView friTextView = view.findViewById(R.id.FriTextView);
    View friCircleView = view.findViewById(R.id.friCircleView);
    View friTaskView = view.findViewById(R.id.friTaskView);

    TextView satTextView = view.findViewById(R.id.SatTextView);
    View satCircleView = view.findViewById(R.id.satCircleView);
    View satTaskView = view.findViewById(R.id.satTaskView);

    // カレンダーを使って週の日付を計算
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(mainActivity.showingDate);
    calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);

    // 現在の表示中の日付
    Date[] weekDates = new Date[7];
    for (int i = 0; i < 7; i++) {
      weekDates[i] = calendar.getTime();
      calendar.add(Calendar.DAY_OF_YEAR, 1);
    }
    TextView[] textViews = {sunTextView, monTextView, tueTextView, wedTextView, thuTextView, friTextView, satTextView};
    View[] circleViews = {sunCircleView, monCircleView, tueCircleView, wedCircleView, thuCircleView, friCircleView, satCircleView};
    View[] taskViews = {sunTaskView, monTaskView, tueTaskView, wedTaskView, thuTaskView, friTaskView, satTaskView};


    // 曜日ごとに日付を設定し、presentDate と一致する場合は円形を表示
    for (int i = 0; i < textViews.length; i++) {
      setDateAndHighlight(textViews[i], circleViews[i], weekDates[i]);
    }

//    プラン等の表示
//    データベースからデータを取得
    db = MyToDoDatabase.getDatabase(getContext());
    myDao = db.myDao();
    calendar.setTime(mainActivity.showingDate);
    calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);

    ExecutorService executor = Executors.newSingleThreadExecutor();
    Handler handler = new Handler(Looper.getMainLooper());
    executor.execute(() -> {
      for (View taskView : taskViews) {
        Log.d("WeekFragment", calendar.getTime().toString());
        List<Object> mixedList = new ArrayList<>();
        // 1週間分の日付をループ
        mixedList.addAll(myDao.getPlansByDate(calendar));
        mixedList.addAll(myDao.getTasksByDate(calendar));
        mixedList.addAll(myDao.getResultsByDate(calendar));

        Log.d("WeekFragment", "mixedList size: " + mixedList.size());

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

  //      データ取得後にviewの作成
        handler.post(() -> {
//          プラン、タスク、結果を表示

          for (Object obj : mixedList) {
            if (obj instanceof Plan) {
              Plan plan = (Plan) obj;
              showPlans(List.of(plan), calendar, taskView);
            } else if (obj instanceof Task) {
              Task task = (Task) obj;
              showTasks(List.of(task), calendar, taskView);
            } else if (obj instanceof Result) {
              Result result = (Result) obj;
              showResults(List.of(result), calendar, taskView);
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



//    スワイプを検出するための GestureDetector を作成
    gestureDetector = new GestureDetector(getContext(), mainActivity.new GestureListener());

//    weekLayoutにスワイプよりもタッチを優先する設定
    LinearLayout weekLayout = view.findViewById(R.id.weekLayout);
    TouchUtils.setPriorityOnClickListener(weekLayout, textViews, gestureDetector, 0, getContext());

//    日付をクリックしたときの処理
    for (int i = 0; i < textViews.length; i++) {
      final Date newShowingDate = weekDates[i];
      textViews[i].setOnClickListener(v -> {
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

  private void setDateAndHighlight(TextView textView, View circleView, Date date) {
    textView.setText(DateUtils.formatDate(date));
    if (DateUtils.isSameDayByDate(date, mainActivity.presentDate)) {
      circleView.setVisibility(View.VISIBLE); // presentDate と一致する場合、円形の View を表示
    } else {
      circleView.setVisibility(View.GONE); // 他のテキストビューは円形の View を非表示
    }
  }

  private void showPlans(List<Plan> plans, Calendar calendar, View taskView) {
    Log.d("WeekFragment", "showPlans");
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
    Log.d("WeekFragment", "showTasks");
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
    Log.d("WeekFragment", "showResults");
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
