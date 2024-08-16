package com.example.mytodo.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DateUtils {
  public static Date addMonths(Date date, int amount) {
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(date);
    calendar.add(Calendar.MONTH, amount);
    calendar.set(Calendar.DAY_OF_MONTH, 1); // 月の最初の日に設定
    return calendar.getTime();
  }

  public static Date addWeeks(Date date, int amount) {
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(date);
    calendar.add(Calendar.WEEK_OF_YEAR, amount);
    calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY); // 週の最初の日に設定
    return calendar.getTime();
  }

  public static Date addDays(Date date, int amount) {
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(date);
    calendar.add(Calendar.DAY_OF_MONTH, amount);
    return calendar.getTime();
  }

  public static String getOrdinalDate(int dayOfMonth) {
    String[] suffixes = {"th", "st", "nd", "rd"};
    int j = dayOfMonth % 10;
    int k = dayOfMonth % 100;
    String suffix;

    if (j == 1 && k != 11) {
      suffix = suffixes[1];
    } else if (j == 2 && k != 12) {
      suffix = suffixes[2];
    } else if (j == 3 && k != 13) {
      suffix = suffixes[3];
    } else {
      suffix = suffixes[0];
    }

    return dayOfMonth + suffix;
  }

  public static String getWeekDayRange(Date startDate, Date endDate) {
    SimpleDateFormat dateFormat = new SimpleDateFormat("d", Locale.getDefault());
    int startDay = Integer.parseInt(dateFormat.format(startDate));
    int endDay = Integer.parseInt(dateFormat.format(endDate));

    return getOrdinalDate(startDay) + "-" + getOrdinalDate(endDay);
  }

  public static boolean isSameDay(Date date1, Date date2) {
    Calendar cal1 = Calendar.getInstance();
    Calendar cal2 = Calendar.getInstance();
    cal1.setTime(date1);
    cal2.setTime(date2);
    return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
        cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR);
  }

  public static String formatDate(Date date) {
    SimpleDateFormat sdf = new SimpleDateFormat("d'st'", Locale.ENGLISH);
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(date);
    int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

    // 日付のサフィックス (st, nd, rd, th) を決定
    if (dayOfMonth >= 11 && dayOfMonth <= 13) {
      sdf = new SimpleDateFormat("d'th'", Locale.ENGLISH);
    } else {
      switch (dayOfMonth % 10) {
        case 1: sdf = new SimpleDateFormat("d'st'", Locale.ENGLISH); break;
        case 2: sdf = new SimpleDateFormat("d'nd'", Locale.ENGLISH); break;
        case 3: sdf = new SimpleDateFormat("d'rd'", Locale.ENGLISH); break;
        default: sdf = new SimpleDateFormat("d'th'", Locale.ENGLISH); break;
      }
    }

    return sdf.format(date);
  }
}
