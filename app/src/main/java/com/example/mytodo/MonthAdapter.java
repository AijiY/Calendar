package com.example.mytodo;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class MonthAdapter extends RecyclerView.Adapter<MonthAdapter.MonthViewHolder>{
  private List<String> days;

  public MonthAdapter(List<String> days) {
    this.days = days;
  }

  @NonNull
  @Override
  public MonthViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    View view = LayoutInflater.from(parent.getContext())
        .inflate(android.R.layout.simple_list_item_1, parent, false);
    return new MonthViewHolder(view);
  }

  @Override
  public void onBindViewHolder(@NonNull MonthViewHolder holder, int position) {
    holder.textView.setText(days.get(position));
  }

  @Override
  public int getItemCount() {
    return days.size();
  }

  public static class MonthViewHolder extends RecyclerView.ViewHolder {
    public TextView textView;

    public MonthViewHolder(View itemView) {
      super(itemView);
      textView = itemView.findViewById(android.R.id.text1);
    }
  }
}
