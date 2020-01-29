package com.saltechdigital.pizzeria.utils;

import android.util.Log;

import org.joda.time.LocalDateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.ArrayList;
import java.util.List;

public class DateUtils {

    public static List<String> getMonthName(String date){
        DateTimeFormatter monthPattern = DateTimeFormat.forPattern("MMM");
        DateTimeFormatter yearPattern = DateTimeFormat.forPattern("YYY");
        DateTimeFormatter dayPattern = DateTimeFormat.forPattern("dd");
        DateTimeFormatter dayPat = DateTimeFormat.forPattern("EEE");
        DateTimeFormatter pattern = DateTimeFormat.forPattern("Y-M-d H:m:s");

        DateTimeFormatter hourPattern = DateTimeFormat.forPattern("H");
        DateTimeFormatter minutePattern = DateTimeFormat.forPattern("m");
        DateTimeFormatter secondsPattern = DateTimeFormat.forPattern("s");

        if (date == null)
            date = "2019-01-26 12:00:00";
        LocalDateTime dateTime = pattern.parseLocalDateTime(date);

        String month = dateTime.toString(monthPattern);
        String year = dateTime.toString(yearPattern);
        String day = dateTime.toString(dayPattern);
        String full = dateTime.toString();
        String dayname = dateTime.toString(dayPat);
        String hour = dateTime.toString(hourPattern);
        String minute = dateTime.toString(minutePattern);
        String seconds = dateTime.toString(secondsPattern);

        List<String> returnable = new ArrayList<>();
        returnable.add(day);
        returnable.add(month);
        returnable.add(year);

        returnable.add(hour);
        returnable.add(minute);
        returnable.add(seconds);

        returnable.add(full);
        returnable.add(dayname);

        return returnable;
    }

    public static String simpleDate(String date){
        DateTimeFormatter monthPattern = DateTimeFormat.forPattern("MMM");
        DateTimeFormatter yearPattern = DateTimeFormat.forPattern("YY");
        DateTimeFormatter dayPattern = DateTimeFormat.forPattern("EEEEEEEE");
        DateTimeFormatter pattern = DateTimeFormat.forPattern("Y-M-d H:m:s");

        if (date == null)
            date = "2019-01-26 12:00:00";
        LocalDateTime dateTime = pattern.parseLocalDateTime(date);

        return dateTime.toString(dayPattern)+", "+dateTime.toString(monthPattern)+" "+dateTime.toString(yearPattern);
    }

    public static String simpleHour(String date){
        DateTimeFormatter hourPattern = DateTimeFormat.forPattern("HH");
        DateTimeFormatter minutePattern = DateTimeFormat.forPattern("mm");
        DateTimeFormatter secondPattern = DateTimeFormat.forPattern("ss");
        DateTimeFormatter pattern = DateTimeFormat.forPattern("Y-M-d H:m:s");

        if (date == null)
            date = "2019-01-26 12:00:00";
        LocalDateTime dateTime = pattern.parseLocalDateTime(date);

        return dateTime.toString(hourPattern)+":"+dateTime.toString(minutePattern)+":"+dateTime.toString(secondPattern);
    }
}

