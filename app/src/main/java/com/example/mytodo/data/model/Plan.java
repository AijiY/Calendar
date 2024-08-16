package com.example.mytodo.data.model;

import java.util.Calendar;

public class Plan {
  private String title;
  private String details;
  private Calendar CalendarStart;
  private Calendar CalendarEnd;
  private boolean isAllDay;
  private String category;

  public Plan(String title, String details, Calendar CalendarStart, Calendar CalendarEnd, boolean isAllDay, String category) {
    this.title = title;
    this.details = details;
    this.CalendarStart = CalendarStart;
    this.CalendarEnd = CalendarEnd;
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
    return CalendarStart;
  }

  public void setCalendarStart(Calendar calendarStart) {
    CalendarStart = calendarStart;
  }

  public Calendar getCalendarEnd() {
    return CalendarEnd;
  }

  public void setCalendarEnd(Calendar calendarEnd) {
    CalendarEnd = calendarEnd;
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
