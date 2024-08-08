package com.example.mytodo;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class DayAdapter extends RecyclerView.Adapter<DayAdapter.DayViewHolder>{
  private List<String> hours;

  public DayAdapter(List<String> hours) {
    this.hours = hours;
  }

  @NonNull
  @Override
  public DayViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    View view = LayoutInflater.from(parent.getContext())
        .inflate(android.R.layout.simple_list_item_1, parent, false);
    return new DayViewHolder(view);
  }

  @Override
  public void onBindViewHolder(@NonNull DayViewHolder holder, int position) {
    holder.textView.setText(hours.get(position));
  }

  @Override
  public int getItemCount() {
    return hours.size();
  }

  public static class DayViewHolder extends RecyclerView.ViewHolder {
    public TextView textView;

    public DayViewHolder(View itemView) {
      super(itemView);
      textView = itemView.findViewById(android.R.id.text1);
    }
  }
}
