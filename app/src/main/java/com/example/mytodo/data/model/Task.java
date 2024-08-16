package com.example.mytodo.data.model;

import java.util.Calendar;

public class Task {
  private String title;
  private String description;
  private Calendar calendarStart;
  private boolean finished;

  public Task(String title, String description, Calendar calendarStart) {
    this.title = title;
    this.description = description;
    this.calendarStart = calendarStart;
    finished = false;
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
    return calendarStart;
  }

  public void setCalendarStart(Calendar calendarStart) {
    this.calendarStart = calendarStart;
  }

  public boolean getFinished() {
    return finished;
  }

  public void setFinished(boolean finished) {
    this.finished = finished;
  }
}
