package com.unicorn.csp.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class DateUtils {

    public static final SimpleDateFormat DEFAULT_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
    public static final SimpleDateFormat DATE_FORMAT_DATE = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);



    public static String getFormatDateString(Date date, SimpleDateFormat dateFormat) {
        return dateFormat.format(date);
    }



    public static String getFormatDateString(Date date) {
        return getFormatDateString(date, DEFAULT_DATE_FORMAT);
    }

}