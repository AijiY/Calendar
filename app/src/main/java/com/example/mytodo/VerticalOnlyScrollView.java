package com.example.mytodo;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ScrollView;

public class VerticalOnlyScrollView extends ScrollView {
  private float lastX, lastY;
  private boolean isVerticalScroll;

  public VerticalOnlyScrollView(Context context) {
    super(context);
  }

  public VerticalOnlyScrollView(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  public VerticalOnlyScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
  }

  @Override
  public boolean onInterceptTouchEvent(MotionEvent ev) {
    switch (ev.getAction()) {
      case MotionEvent.ACTION_DOWN:
        lastX = ev.getX();
        lastY = ev.getY();
        isVerticalScroll = false; // 初期状態では垂直スクロールが開始されていない
        break;
      case MotionEvent.ACTION_MOVE:
        float xDiff = Math.abs(ev.getX() - lastX);
        float yDiff = Math.abs(ev.getY() - lastY);

        // 垂直方向の動きが大きい場合、スクロールをインターセプト
        if (yDiff > xDiff) {
          isVerticalScroll = true;
          return true; // イベントをインターセプトして、縦スクロールを開始
        }
        break;
    }
    return super.onInterceptTouchEvent(ev);
  }

  @Override
  public boolean onTouchEvent(MotionEvent ev) {
    // 縦方向のスクロールのみを処理
    if (isVerticalScroll) {
      return super.onTouchEvent(ev);
    }
    return false; // 横方向の動きの場合、スクロールを無効化
  }
}
