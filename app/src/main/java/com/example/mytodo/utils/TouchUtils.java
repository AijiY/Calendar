package com.example.mytodo.utils;

import android.content.Context;
import android.graphics.Rect;
import android.os.Handler;
import android.view.GestureDetector;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.example.mytodo.ui.main.MainActivity;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.atomic.AtomicBoolean;

public class TouchUtils {

  public static void setPriorityOnClickListener(ViewGroup viewGroup, View[] views, GestureDetector gestureDetector, int hitRectRoom, Context context) {
    final AtomicBoolean isButtonClicked = new AtomicBoolean(false);
    final long DEBOUNCE_DELAY_MS = 300;

    viewGroup.setOnTouchListener((v, event) -> {
      boolean isGestureDetected = gestureDetector.onTouchEvent(event);

      // タッチイベントの座標を取得
      float x = event.getX();
      float y = event.getY();

      // ボタンの位置とサイズを取得
      Rect buttonRect = new Rect();

      for (View view : views) {
        view.getHitRect(buttonRect);
        // Rect の領域を広げる（ここでは 32dp を追加）
        buttonRect = strechedRect(buttonRect, hitRectRoom, context);
        // タッチがボタンの領域内にあるかどうかをチェック
        if (buttonRect.contains((int) x, (int) y)) {
          // ボタンがタッチされた場合
          if (!isButtonClicked.get()) {
            isButtonClicked.set(true);
            view.performClick();

            // デバウンス処理のために、一定時間後にフラグをリセット
            new Handler().postDelayed(() -> isButtonClicked.set(false), DEBOUNCE_DELAY_MS);

            // イベントを消費して、スクロールを無効にする
            return true;
          }
        }
      }

      // 横スワイプ
      return true;
    });

  }

  private static Rect strechedRect(Rect rect, int hitRectRoom, Context context) {
    int padding = (int) (hitRectRoom * context.getResources().getDisplayMetrics().density); // dp to pixels
    rect.inset(-padding, -padding); // 領域を広げる
    return rect;
  }



}
