package com.example.mytodo.ui.main;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.mytodo.R;
import com.example.mytodo.utils.TouchUtils;
import java.util.Calendar;
import java.util.Date;

public class MonthFragment extends Fragment {
  private MainActivity mainActivity;

  private GestureDetector gestureDetector;

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

    // `showingDate` を取得（例: フラグメントの引数などで設定されていると仮定）
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(mainActivity.showingDate);

    // 月の初日を設定
    calendar.set(Calendar.DAY_OF_MONTH, 1);

    // 月の最初の日の曜日と月の日数を取得
    int startDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
    int daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);

    // 各 TextView を取得
    TextView[] dayTexts = new TextView[35];
    for (int i = 0; i < 35; i++) {
      String idName = "dayText" + (i + 1);
      int resId = getResources().getIdentifier(idName, "id", getActivity().getPackageName());
      if (resId != 0) {
        dayTexts[i] = view.findViewById(resId);
      }
    }

    // 各 CircleView を取得
    View[] circles = new View[35];
    for (int i = 0; i < 35; i++) {
      String idName = "circleView" + (i + 1);
      int resId = getResources().getIdentifier(idName, "id", getActivity().getPackageName());
      if (resId != 0) {
        circles[i] = view.findViewById(resId);
      }
    }

    // 現在の日付を取得
    Calendar today = Calendar.getInstance();
    today.setTime(mainActivity.presentDate);

    Date[] monthDates = new Date[35];
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



  // 前月の日数を計算するメソッド
  private int getDaysInPreviousMonth(Calendar calendar, int startDayOfWeek) {
    // 前月の日数を計算するためにカレンダーを1か月戻す
    calendar.add(Calendar.MONTH, -1);
    int daysInPreviousMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
    calendar.add(Calendar.MONTH, 1); // 元に戻す
    return daysInPreviousMonth - (startDayOfWeek - 2);
  }
}
