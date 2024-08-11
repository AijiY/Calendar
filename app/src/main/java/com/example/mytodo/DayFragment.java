package com.example.mytodo;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import java.util.Date;

public class DayFragment extends Fragment {
  private static final String ARG_DATE = "showingDate";
  private Date showingDate;

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
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    if (getArguments() != null) {
      showingDate = (Date) getArguments().getSerializable(ARG_DATE);
    }
  }

  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    return inflater.inflate(R.layout.fragment_day, container, false);
  }
}
