package com.example.mytodo.data.model;

import java.util.Calendar;

public class Result {
  private String title;
  private String details;
  private Calendar calendarStart;
  private Calendar calendarEnd;
  private boolean isAllDay;
  private String category;

  public Result(String title, String details, Calendar calendarStart, Calendar calendarEnd,
      boolean isAllDay, String category) {
    this.title = title;
    this.details = details;
    this.calendarStart = calendarStart;
    this.calendarEnd = calendarEnd;
    this.isAllDay = isAllDay;
    this.category = category;
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

  public Calendar getCalendarEnd() {
    return calendarEnd;
  }

  public void setCalendarEnd(Calendar calendarEnd) {
    this.calendarEnd = calendarEnd;
  }

  public boolean isAllDay() {
    return isAllDay;
  }

  public void setAllDay(boolean allDay) {
    isAllDay = allDay;
  }

  public String getCategory() {
    return category;
  }

  public void setCategory(String category) {
    this.category = category;
  }
}
