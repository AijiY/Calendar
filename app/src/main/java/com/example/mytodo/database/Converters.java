package com.example.mytodo.database;

import androidx.room.TypeConverter;
import java.util.Calendar;

public class Converters {
  @TypeConverter
  public static long fromCalendar(Calendar calendar) {
    return calendar != null ? calendar.getTimeInMillis() : 0;
  }

  @TypeConverter
  public static Calendar toCalendar(long timestamp) {
    Calendar calendar = Calendar.getInstance();
    calendar.setTimeInMillis(timestamp);
    return calendar;
  }
}
