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

    // 日付を設定するための TextView を取得
    TextView sunTextView = view.findViewById(R.id.SunTextView);
    TextView monTextView = view.findViewById(R.id.MonTextView);
    TextView tueTextView = view.findViewById(R.id.TueTextView);
    TextView wedTextView = view.findViewById(R.id.WedTextView);
    TextView thuTextView = view.findViewById(R.id.ThuTextView);
    TextView friTextView = view.findViewById(R.id.FriTextView);
    TextView satTextView = view.findViewById(R.id.SatTextView);

    // カレンダーを使って週の日付を計算
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(showingDate);

    // 週の始まり (日曜日) から始める
    calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);

    // 曜日ごとに日付を設定
    sunTextView.setText(formatDate(calendar.getTime()));
    calendar.add(Calendar.DAY_OF_YEAR, 1);
    monTextView.setText(formatDate(calendar.getTime()));
    calendar.add(Calendar.DAY_OF_YEAR, 1);
    tueTextView.setText(formatDate(calendar.getTime()));
    calendar.add(Calendar.DAY_OF_YEAR, 1);
    wedTextView.setText(formatDate(calendar.getTime()));
    calendar.add(Calendar.DAY_OF_YEAR, 1);
    thuTextView.setText(formatDate(calendar.getTime()));
    calendar.add(Calendar.DAY_OF_YEAR, 1);
    friTextView.setText(formatDate(calendar.getTime()));
    calendar.add(Calendar.DAY_OF_YEAR, 1);
    satTextView.setText(formatDate(calendar.getTime()));

    return view;
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
