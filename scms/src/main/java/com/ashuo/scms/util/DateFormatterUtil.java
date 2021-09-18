package com.ashuo.scms.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class DateFormatterUtil {

    private static final DateTimeFormatter localDateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private static final SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public static String localDateTimeToString(LocalDateTime dateTime) {
        return dateTime.format(localDateTimeFormatter);
    }

    public static LocalDateTime stringToLocalDateTime(String strDateTime) {
        return LocalDateTime.parse(strDateTime, localDateTimeFormatter);
    }

    public static String dateToString(Date dateTime) {
        return formatter.format(dateTime);
    }

    public static Date stringToDate(String strDateTime) throws ParseException {
        return formatter.parse(strDateTime);
    }

}
