package com.example.mytodo.ui.main;

import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.mytodo.R;
import com.example.mytodo.utils.DateUtils;
import com.example.mytodo.utils.TouchUtils;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class WeekFragment extends Fragment {
  private MainActivity mainActivity;

  private GestureDetector gestureDetector;

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
    calendar.setTime(mainActivity.showingDate);

    // 週の始まり (日曜日) から始める
    calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);

    // 現在の表示中の日付
    Date[] weekDates = new Date[7];
    for (int i = 0; i < 7; i++) {
      weekDates[i] = calendar.getTime();
      calendar.add(Calendar.DAY_OF_YEAR, 1);
    }
    TextView[] textViews = {sunTextView, monTextView, tueTextView, wedTextView, thuTextView, friTextView, satTextView};
    View[] circleViews = {sunCircleView, monCircleView, tueCircleView, wedCircleView, thuCircleView, friCircleView, satCircleView};

    // 曜日ごとに日付を設定し、presentDate と一致する場合は円形を表示
    for (int i = 0; i < textViews.length; i++) {
      setDateAndHighlight(textViews[i], circleViews[i], weekDates[i]);
    }

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
    if (DateUtils.isSameDay(date, mainActivity.presentDate)) {
      circleView.setVisibility(View.VISIBLE); // presentDate と一致する場合、円形の View を表示
    } else {
      circleView.setVisibility(View.GONE); // 他のテキストビューは円形の View を非表示
    }
  }


}
