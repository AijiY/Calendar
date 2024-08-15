package com.example.mytodo;

import java.util.Calendar;

public class Plan {
  private String title;
  private String description;
  private Calendar CalendarStart;
  private Calendar CalendarEnd;
  private boolean isAllDay;

  public Plan(String title, String description, Calendar CalendarStart, Calendar CalendarEnd, boolean isAllDay) {
    this.title = title;
    this.description = description;
    this.CalendarStart = CalendarStart;
    this.CalendarEnd = CalendarEnd;
    this.isAllDay = isAllDay;
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
}
