package com.example.mytodo;

import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ScrollView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.mytodo.MainActivity.GestureListener;
import com.google.android.material.tabs.TabLayout;
import java.util.Calendar;
import java.util.Date;

public class DayFragment extends Fragment {
  private MainActivity mainActivity;

  private GestureDetector gestureDetector;

  // ボタンのクリック処理が連続して発火しないようにするためのフラグ
  private boolean isButtonClicked = false;
  private static final long DEBOUNCE_DELAY_MS = 300; // デバウンス遅延時間（ミリ秒）

  public DayFragment() {
  }

  public static DayFragment newInstance() {
    DayFragment fragment = new DayFragment();
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
    View view = inflater.inflate(R.layout.fragment_day, container, false);

    gestureDetector = new GestureDetector(getContext(), mainActivity.new GestureListener());
    ScrollView scrollView = view.findViewById(R.id.dayScrollView);

// GlobalLayoutListener を使用してレイアウト完了後にスクロール位置を設定する
    scrollView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
      @Override
      public void onGlobalLayout() {
        // hour を showingDate から取得する
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(mainActivity.showingDate);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);

        // dp からピクセルへ変換
        float density = view.getContext().getResources().getDisplayMetrics().density; // 修正ポイント
        int yPosition = (int) (80 * hour * density);

        // 縦方向に yPosition までスクロール
        scrollView.scrollTo(0, yPosition);

        // リスナーの登録解除
        scrollView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
      }
    });

    // ScrollView に OnTouchListener を設定
    scrollView.setOnTouchListener((v, event) -> {
      boolean isGestureDetected = gestureDetector.onTouchEvent(event);

      // タッチイベントの座標を取得
      float x = event.getX();
      float y = event.getY();

      // ボタンの位置とサイズを取得
      Rect buttonRect = new Rect();
      mainActivity.addButton.getHitRect(buttonRect);
      // Rect の領域を広げる（ここでは 10dp を追加）
      int padding = (int) (32 * mainActivity.getResources().getDisplayMetrics().density); // dp to pixels
      buttonRect.inset(-padding, -padding); // 領域を広げる

      // タッチがボタンの領域内にあるかどうかをチェック
      if (buttonRect.contains((int) x, (int) y)) {
        // ボタンがタッチされた場合
        if (!isButtonClicked) {
          isButtonClicked = true;
          mainActivity.addButton.performClick();

          // デバウンス処理のために、一定時間後にフラグをリセット
          new Handler().postDelayed(() -> isButtonClicked = false, DEBOUNCE_DELAY_MS);

          // イベントを消費して、スクロールを無効にする
          return true;
        }
      }

      // ジェスチャーが検出された場合（横スワイプ）
      if (isGestureDetected) {
        // 横スワイプの場合、スクロールを無効にする
        return true;
      }

      // それ以外の場合、ScrollView のスクロール処理を許可する
      return v.onTouchEvent(event);

    });

    return view;
  }
}
