package com.ashuo.scms.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateFormatterUtil {

    /*  private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

      public static String dateToString(LocalDateTime dateTime) {
          return dateTime.format(formatter);
      }

      public static LocalDateTime stringToDate(String strDateTime) {
          return LocalDateTime.parse(strDateTime, formatter);
      }

  */

    private static SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public static String dateToString(Date dateTime) {
        return formatter.format(dateTime);
    }

    public static Date stringToDate(String strDateTime) throws ParseException {
        return formatter.parse(strDateTime);
    }

}
