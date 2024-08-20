package com.example.mytodo.data.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import java.util.Calendar;

@Entity(tableName = "Task")
public class Task {
  @PrimaryKey(autoGenerate = true)
  private int id;

  @ColumnInfo(name = "title")
  private String title;

  @ColumnInfo(name = "details")
  private String details;

  @ColumnInfo(name = "calendar_start")
  private Calendar calendarStart;

  @ColumnInfo(name = "finished")
  private boolean finished;

  public Task(String title, String details, Calendar calendarStart, boolean finished) {
    this.title = title;
    this.details = details;
    this.calendarStart = calendarStart;
    this.finished = finished;
  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getDetails() {
    return details;
  }

  public void setDetails(String details) {
    this.details = details;
  }

  public Calendar getCalendarStart() {
    return calendarStart;
  }

  public void setCalendarStart(Calendar calendarStart) {
    this.calendarStart = calendarStart;
  }

  public boolean isFinished() {
    return finished;
  }

  public void setFinished(boolean finished) {
    this.finished = finished;
  }

}
