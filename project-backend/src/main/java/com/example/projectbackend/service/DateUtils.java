package com.example.projectbackend.service;

import java.time.DayOfWeek;
import java.time.LocalDate;

public final class DateUtils {

    private DateUtils() {
    }

    public static String toWeekday(LocalDate date) {
        DayOfWeek dayOfWeek = date.getDayOfWeek();
        switch (dayOfWeek) {
            case MONDAY:
                return "周一";
            case TUESDAY:
                return "周二";
            case WEDNESDAY:
                return "周三";
            case THURSDAY:
                return "周四";
            case FRIDAY:
                return "周五";
            case SATURDAY:
                return "周六";
            case SUNDAY:
                return "周日";
            default:
                return "";
        }
    }
}


