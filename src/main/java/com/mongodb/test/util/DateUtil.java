package com.mongodb.test.util;

import lombok.extern.slf4j.Slf4j;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/***
 * @author zhulei
 * @date 2020-04-28 11:35
 **/
@Slf4j
public class DateUtil {
    public static final String FORMAT = "yyyy-MM-dd HH:mm:ss";

    /**
     * 格式化字符串
     *
     * @param date
     * @param format
     * @return
     */
    public static String format(Date date, String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(date);
    }

    /**
     * 格式化本地 字符串
     *
     * @param date
     * @return
     */
    public static String format(Date date) {
        return format(date, FORMAT);
    }

    /**
     * 格式化当前时间
     *
     * @return
     */
    public static String format() {
        return format(new Date());
    }

    /**
     * 将字符串转换成 时间
     *
     * @param dateStr
     * @param pattern
     * @return
     */
    public static Date parse(String dateStr, String pattern) {
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        Date date = null;
        try {
            date = sdf.parse(dateStr);
        } catch (ParseException e) {
            log.error("",e);
        }
        return date;
    }

    /**
     * @param dateStr
     * @return
     */
    public static Date parse(String dateStr) {
        return parse(dateStr, FORMAT);
    }

    /**
     * 获取 时间毫秒 2018-05-20 12:01:00
     *
     * @param time
     * @return
     */
    public static Long getDateMinute(Long time) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time);
        calendar.set(GregorianCalendar.SECOND, 0);
        calendar.set(GregorianCalendar.MILLISECOND, 0);
        return calendar.getTimeInMillis();
    }

    /**
     * 增加时间
     *
     * @param date
     * @param field
     * @param value
     * @return
     */
    public static Date add(Date date, int field, int value) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(field, value);
        return calendar.getTime();
    }

    /**
     * 增加几天
     *
     * @param date
     * @param value
     * @return
     */
    public static Date addDay(Date date, int value) {
        return add(date, Calendar.DAY_OF_YEAR, value);
    }

    /**
     * 增加几个小时
     *
     * @param date
     * @param value
     * @return
     */
    public static Date addMinute(Date date, int value) {
        return add(date, Calendar.MINUTE, value);
    }

    /**
     * 增加几个小时
     *
     * @param date
     * @param value
     * @return
     */
    public static Date addHour(Date date, int value) {
        return add(date, Calendar.HOUR_OF_DAY, value);
    }

    /***
     * 根据字符串获取事件 有时区显示的
     * @param converDate yyyy-MM-dd HH:mm:ss.SSS
     * @return
     */
    public static Date parseTimeZone(String converDate) {
        String dataStr = converDate.replace("000Z", "").replace("T", "");
        Date date = parse(dataStr, "yyyy-MM-ddHH:mm:ss.SSS");
        long resultDate = date.getTime();
        resultDate += getLongOffset(resultDate);
        return new Date(resultDate);
    }

    private final static TimeZone timeZone = TimeZone.getTimeZone("UTC");

    /***
     * 获取偏移北京时间
     * @param date
     * @return
     */
    public static long getLongOffset(long date) {
        return timeZone.getOffset(date);
    }

}
