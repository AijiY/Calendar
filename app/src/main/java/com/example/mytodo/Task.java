package com.example.mytodo;

import java.util.Calendar;

public class Task {
  private String title;
  private String description;
  private Calendar CalendarStart;

  public Task(String title, String description, Calendar calendarStart) {
    this.title = title;
    this.description = description;
    CalendarStart = calendarStart;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public Calendar getCalendarStart() {
    return CalendarStart;
  }

  public void setCalendarStart(Calendar calendarStart) {
    CalendarStart = calendarStart;
  }
}
