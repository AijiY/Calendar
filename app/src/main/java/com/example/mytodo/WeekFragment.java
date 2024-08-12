package com.example.mytodo;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class WeekFragment extends Fragment {
  private static final String ARG_DATE = "showingDate";
  private Date showingDate;
  private Date presentDate = MainActivity.presentDate;

  public WeekFragment() {
  }

  public static WeekFragment newInstance(Date showingDate) {
    WeekFragment fragment = new WeekFragment();
    Bundle args = new Bundle();
    args.putSerializable(ARG_DATE, showingDate);
    fragment.setArguments(args);
    return fragment;
  }

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    if (getArguments() != null) {
      showingDate = (Date) getArguments().getSerializable(ARG_DATE);
    }
  }

  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_week, container, false);

    // 日付を設定するための TextView と円形の View を取得
    TextView sunTextView = view.findViewById(R.id.SunTextView);
    View sunCircleView = view.findViewById(R.id.sunCircleView);

    TextView monTextView = view.findViewById(R.id.MonTextView);
    View monCircleView = view.findViewById(R.id.monCircleView);

    TextView tueTextView = view.findViewById(R.id.TueTextView);
    View tueCircleView = view.findViewById(R.id.tueCircleView);

    TextView wedTextView = view.findViewById(R.id.WedTextView);
    View wedCircleView = view.findViewById(R.id.wedCircleView);

    TextView thuTextView = view.findViewById(R.id.ThuTextView);
    View thuCircleView = view.findViewById(R.id.thuCircleView);

    TextView friTextView = view.findViewById(R.id.FriTextView);
    View friCircleView = view.findViewById(R.id.friCircleView);

    TextView satTextView = view.findViewById(R.id.SatTextView);
    View satCircleView = view.findViewById(R.id.satCircleView);

    // カレンダーを使って週の日付を計算
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(showingDate);

    // 週の始まり (日曜日) から始める
    calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);

    // 現在の表示中の日付
    Date[] weekDates = new Date[7];
    for (int i = 0; i < 7; i++) {
      weekDates[i] = calendar.getTime();
      calendar.add(Calendar.DAY_OF_YEAR, 1);
    }

    // 曜日ごとに日付を設定し、presentDate と一致する場合は円形を表示
    setDateAndHighlight(sunTextView, sunCircleView, weekDates[0]);
    setDateAndHighlight(monTextView, monCircleView, weekDates[1]);
    setDateAndHighlight(tueTextView, tueCircleView, weekDates[2]);
    setDateAndHighlight(wedTextView, wedCircleView, weekDates[3]);
    setDateAndHighlight(thuTextView, thuCircleView, weekDates[4]);
    setDateAndHighlight(friTextView, friCircleView, weekDates[5]);
    setDateAndHighlight(satTextView, satCircleView, weekDates[6]);



    return view;
  }

  private void setDateAndHighlight(TextView textView, View circleView, Date date) {
    textView.setText(formatDate(date));
    if (isSameDay(date, presentDate)) {
      circleView.setVisibility(View.VISIBLE); // presentDate と一致する場合、円形の View を表示
    } else {
      circleView.setVisibility(View.GONE); // 他のテキストビューは円形の View を非表示
    }
  }

  // presentDate と比較するための日付が同じ日かどうかを確認するヘルパーメソッド
  private boolean isSameDay(Date date1, Date date2) {
    Calendar cal1 = Calendar.getInstance();
    Calendar cal2 = Calendar.getInstance();
    cal1.setTime(date1);
    cal2.setTime(date2);
    return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
        cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR);
  }

  private String formatDate(Date date) {
    SimpleDateFormat sdf = new SimpleDateFormat("d'st'", Locale.ENGLISH);
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(date);
    int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

    // 日付のサフィックス (st, nd, rd, th) を決定
    if (dayOfMonth >= 11 && dayOfMonth <= 13) {
      sdf = new SimpleDateFormat("d'th'", Locale.ENGLISH);
    } else {
      switch (dayOfMonth % 10) {
        case 1: sdf = new SimpleDateFormat("d'st'", Locale.ENGLISH); break;
        case 2: sdf = new SimpleDateFormat("d'nd'", Locale.ENGLISH); break;
        case 3: sdf = new SimpleDateFormat("d'rd'", Locale.ENGLISH); break;
        default: sdf = new SimpleDateFormat("d'th'", Locale.ENGLISH); break;
      }
    }

    return sdf.format(date);
  }
}
