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
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class WeekFragment extends Fragment {
  private MainActivity mainActivity;

  private GestureDetector gestureDetector;

  // ボタンのクリック処理が連続して発火しないようにするためのフラグ
  private boolean isButtonClicked = false;
  private static final long DEBOUNCE_DELAY_MS = 300; // デバウンス遅延時間（ミリ秒）

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

//    ここからTextViewにDayFragmentに移動するためのクリックイベントを設定
    gestureDetector = new GestureDetector(getContext(), mainActivity.new GestureListener());
    LinearLayout weekLayout = view.findViewById(R.id.weekLayout);

    weekLayout.setOnTouchListener((v, event) -> {
      boolean isGestureDetected = gestureDetector.onTouchEvent(event);

      // タッチイベントの座標を取得
      float x = event.getX();
      float y = event.getY();

      // ボタンの位置とサイズを取得
      Rect buttonRect = new Rect();

      for (TextView textView : textViews) {
        textView.getHitRect(buttonRect);
        // タッチがボタンの領域内にあるかどうかをチェック
        if (buttonRect.contains((int) x, (int) y)) {
          // ボタンがタッチされた場合
          if (!isButtonClicked) {
            isButtonClicked = true;
            textView.performClick();

            // デバウンス処理のために、一定時間後にフラグをリセット
            new Handler().postDelayed(() -> isButtonClicked = false, DEBOUNCE_DELAY_MS);

            // イベントを消費して、スクロールを無効にする
            return true;
          }
        }
      }

      // 横スワイプ
      return true;
    });

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
