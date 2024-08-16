package com.example.mytodo.data.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import java.util.Calendar;
import java.util.Date;

@Entity(tableName = "Plan")
public class Plan {
  @PrimaryKey(autoGenerate = true)
  private int id;

  @ColumnInfo(name = "title")
  private String title;

  @ColumnInfo(name = "details")
  private String details;

  @ColumnInfo(name = "category")
  private String category;

  @ColumnInfo(name = "is_all_day")
  private boolean isAllDay;

  @ColumnInfo(name = "calendar_start")
  private Calendar calendarStart;

  @ColumnInfo(name = "calendar_end")
  private Calendar calendarEnd;

  public Plan(String title, String details, String category, boolean isAllDay,
      Calendar calendarStart,
      Calendar calendarEnd) {
    this.title = title;
    this.details = details;
    this.category = category;
    this.isAllDay = isAllDay;
    this.calendarStart = calendarStart;
    this.calendarEnd = calendarEnd;
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

  public String getCategory() {
    return category;
  }

  public void setCategory(String category) {
    this.category = category;
  }

  public boolean isAllDay() {
    return isAllDay;
  }

  public void setAllDay(boolean allDay) {
    isAllDay = allDay;
  }

  public Calendar getCalendarStart() {
    return calendarStart;
  }

  public void setCalendarStart(Calendar calendarStart) {
    this.calendarStart = calendarStart;
  }

  public Calendar getCalendarEnd() {
    return calendarEnd;
  }

  public void setCalendarEnd(Calendar calendarEnd) {
    this.calendarEnd = calendarEnd;
  }
}
