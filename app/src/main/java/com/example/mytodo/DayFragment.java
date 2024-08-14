package com.example.mytodo;

import android.content.Context;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.mytodo.MainActivity.GestureListener;
import com.google.android.material.tabs.TabLayout;
import java.util.Date;

public class DayFragment extends Fragment {
  private MainActivity mainActivity;

  private static final String ARG_DATE = "showingDate";
  private Date showingDate;

  private TabLayout dateTypeTabLayout;
  private GestureDetector gestureDetector;

  public DayFragment() {
  }

  public static DayFragment newInstance(Date showingDate) {
    DayFragment fragment = new DayFragment();
    Bundle args = new Bundle();
    args.putSerializable(ARG_DATE, showingDate);
    fragment.setArguments(args);
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
    if (getArguments() != null) {
      showingDate = (Date) getArguments().getSerializable(ARG_DATE);
    }
  }

  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    View rootView = inflater.inflate(R.layout.fragment_day, container, false);

    gestureDetector = new GestureDetector(getContext(), mainActivity.new GestureListener());
    ScrollView scrollView = rootView.findViewById(R.id.dayScrollView);

    // ScrollView に OnTouchListener を設定
    scrollView.setOnTouchListener((v, event) -> {
      if (gestureDetector.onTouchEvent(event)) {
        // 横スワイプを検出した場合、ScrollView のスクロールを無効にする
        return true;
      } else {
        // 縦スワイプの場合、ScrollView のデフォルトのスクロールを許可する
        return v.onTouchEvent(event);
      }
    });

    return rootView;
  }
}
