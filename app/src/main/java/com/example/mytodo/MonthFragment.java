package com.example.mytodo;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import java.util.Calendar;
import java.util.Date;

public class MonthFragment extends Fragment {
  private static final String ARG_DATE = "showingDate";
  private Date showingDate;

  public MonthFragment() {
  }
  public static MonthFragment newInstance(Date showingDate) {
    MonthFragment fragment = new MonthFragment();
    Bundle args = new Bundle();
    args.putSerializable(ARG_DATE, showingDate);
    fragment.setArguments(args);
    return fragment;
  }
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    if (getArguments() != null) {
      showingDate = (Date) getArguments().getSerializable(ARG_DATE);
    }
  }

  @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_month, container, false);

    // `showingDate` を取得（例: フラグメントの引数などで設定されていると仮定）
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(showingDate);

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

    // 日付の設定
    for (int i = 0; i < 35; i++) {
      TextView dayText = dayTexts[i];
      if (dayText != null) {
        int day = i - (startDayOfWeek - 1) + 1;
        int dayOfWeek = (i % 7) + 1; // 日曜日が1、土曜日が7

        if (i < startDayOfWeek - 1) {
          // 前月の日付
          dayText.setText(String.valueOf(day + getDaysInPreviousMonth(calendar, startDayOfWeek - 1)));
          dayText.setTextColor(Color.LTGRAY); // 文字色を薄くする
          if (dayOfWeek == Calendar.SUNDAY) {
            dayText.setBackgroundColor(Color.parseColor("#FF9F9F")); // 薄い赤（red_white）
          } else if (dayOfWeek == Calendar.SATURDAY) {
            dayText.setBackgroundColor(Color.parseColor("#9F9FFF")); // 薄い青（blue_white）
          } else {
            dayText.setBackgroundColor(Color.TRANSPARENT); // 背景色を透明にする
          }
        } else if (day <= daysInMonth) {
          // 今月の日付
          dayText.setText(String.valueOf(day));
          dayText.setTextColor(Color.BLACK); // 文字色を黒にする
          dayText.setBackgroundColor(Color.TRANSPARENT); // 背景色を透明にする
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
        } else {
          // 翌月の日付
          dayText.setText(String.valueOf(day - daysInMonth));
          dayText.setTextColor(Color.LTGRAY); // 文字色を薄くする
          if (dayOfWeek == Calendar.SUNDAY) {
            dayText.setBackgroundColor(Color.parseColor("#FF9F9F")); // 薄い赤（red_white）
          } else if (dayOfWeek == Calendar.SATURDAY) {
            dayText.setBackgroundColor(Color.parseColor("#9F9FFF")); // 薄い青（blue_white）
          } else {
            dayText.setBackgroundColor(Color.TRANSPARENT); // 背景色を透明にする
          }
        }
      }
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
