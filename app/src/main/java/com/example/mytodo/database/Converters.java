package com.example.mytodo.database;

import androidx.room.TypeConverter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class Converters {
  private static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
  private static final SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT, Locale.getDefault());

  @TypeConverter
  public static String fromCalendar(Calendar calendar) {
    return calendar != null ? sdf.format(calendar.getTime()) : null;
  }

  @TypeConverter
  public static Calendar toCalendar(String dateString) {
    Calendar calendar = Calendar.getInstance();
    try {
      if (dateString != null) {
        calendar.setTime(sdf.parse(dateString));
      } else {
        calendar = null;
      }
    } catch (ParseException e) {
      e.printStackTrace();
      calendar = null;
    }
    return calendar;
  }
}
