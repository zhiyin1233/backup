package com.yiziton.dataweb.salesindex;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * @author HuangHuai on 2019-07-18 15:34
 */

public class DateUtil {
    public static final long oneDayMS    = 24 * 60 * 60 * 1000; //一天时间的毫秒数
    public static final long oneHourMS   = 60 * 60 * 1000; //一小时毫秒数
    public static final long oneMinuteMS = 60 * 1000; //一分钟毫秒数

    public static final int              PATTERN_YYYYMMDD     = 1;                   //"yyyyMMdd";
    public static final String           STR_PATTERN_YYYYMMDD = "yyyyMMdd";
    public static final SimpleDateFormat SHORT_FORMAT         = new SimpleDateFormat(STR_PATTERN_YYYYMMDD);


    public static final int              PATTERN_YYYY_MM_DD     = 2;                 //"yyyy-MM-dd";
    public static final String           STR_PATTERN_YYYY_MM_DD = "yyyy-MM-dd";
    public static final SimpleDateFormat SHORT_FORMAT2          = new SimpleDateFormat(STR_PATTERN_YYYY_MM_DD);

    public static final SimpleDateFormat SHORT_FORMAT4 = new SimpleDateFormat("yyyy/MM/dd");
    public static final SimpleDateFormat SHORT_FORMAT5 = new SimpleDateFormat("yyyy/MM");


    public static final String           STR_PATTERN_YYYY_MM = "yyyy-MM";
    public static final SimpleDateFormat SHORT_FORMAT3       = new SimpleDateFormat(STR_PATTERN_YYYY_MM);


    public static final int              LONG_PATTERN     = 3;                       //"yyyy-MM-dd HH:mm:ss";
    public static final String           STR_LONG_PATTERN = "yyyy-MM-dd HH:mm:ss";
    public static final SimpleDateFormat LONG_FORMAT      = new SimpleDateFormat(STR_LONG_PATTERN);


    public static final int              LONG_PATTERN_2     = 4;                     //"yyyy-MM-dd HH:mm:ss.SSS";
    public static final String           STR_LONG_PATTERN_2 = "yyyy-MM-dd HH:mm:ss.SSS";
    public static final SimpleDateFormat LONG_FORMAT2       = new SimpleDateFormat(STR_LONG_PATTERN_2);


    public static SimpleDateFormat getFormat(String pattern) {
        switch (pattern) {
            case STR_PATTERN_YYYYMMDD:
                return SHORT_FORMAT;
            case STR_PATTERN_YYYY_MM_DD:
                return SHORT_FORMAT2;
            case STR_LONG_PATTERN:
                return LONG_FORMAT;
            case STR_LONG_PATTERN_2:
                return LONG_FORMAT2;
            default:
                return LONG_FORMAT;
        }
    }


    /**
     * 将字符串日期使用指定pattter解析成日期
     */
    public static Date parseDate(String callDate, String pattern) {
        try {
            return getFormat(pattern).parse(callDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    static Calendar cal = Calendar.getInstance();

    /**
     * 获得今天的指定时间的毫秒数 <br>
     * 0-23, 24代表明天00：00：00：00.000
     *
     * @param hour   小时，24小时制, 1代表今天凌晨1点，12代表今天中午12点，不传就是中午12点
     * @param minute 分钟
     * @param second 秒
     * @param ms     毫秒数
     * @return 返回毫秒数
     */
    public static long todayMS(Integer hour, Integer minute, Integer second, Integer ms) {
        if (hour == null || hour <= 0) {
            hour = 0; //0代表今天凌晨0点
        } else if (hour >= 24) {
            hour = 24;
            minute = second = ms = 0;
        }

        if (second == null || second <= 0) {
            second = 0;
        }
        if (minute == null || minute <= 0) {
            minute = 0;
        }
        if (ms == null || ms <= 0) {
            ms = 0;
        }
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, hour);  //如果是13就是明天凌晨1点
        cal.set(Calendar.MINUTE, minute);
        cal.set(Calendar.SECOND, second);
        cal.set(Calendar.MILLISECOND, ms);
        return cal.getTimeInMillis();
    }


    public static Date getOneDayBeginning(Date date) {
        Calendar c = Calendar.getInstance();
        if (date != null)
            c.setTimeInMillis(date.getTime());

        c.set(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
        c.setTimeInMillis((c.getTimeInMillis() / 1000) * 1000);
        return c.getTime();
    }

    public static Date getOneDayEnding(Date date) {
        Calendar c = Calendar.getInstance();
        if (date != null)
            c.setTimeInMillis(date.getTime());
        c.set(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH), 24, 0, 0);
        c.setTimeInMillis((c.getTimeInMillis() / 1000) * 1000 - 1);
        return c.getTime();
    }


    /**
     * @param mutiTimes 可变参数，第一个hour，第二个minute，second，millisecond
     *                  如果没有，就是null代表的是0
     */
    public static long todayMS(Integer... mutiTimes) {
        Integer hour = 0;
        Integer minute = 0;
        Integer second = 0;
        Integer millisecond = 0;
        if (mutiTimes != null && mutiTimes.length > 0) {
            if (mutiTimes.length >= 1) {
                hour = mutiTimes[0];
            }
            if (mutiTimes.length >= 2) {
                minute = mutiTimes[1];
            }
            if (mutiTimes.length >= 3) {
                second = mutiTimes[2];
            }
            if (mutiTimes.length >= 4) {
                millisecond = mutiTimes[3];
            }
        }
        return todayMS(hour, minute, second, millisecond);
    }

    /**
     * 获取明天的指定时间的毫秒数(ms:毫秒)
     *
     * @param multiTimes 可变参数，第一个hour，第二个minute，second，millisecond
     *                   如果没有，就是null代表的是0
     */
    public static long nextDayMS(Integer... multiTimes) {
        return todayMS(multiTimes) + oneDayMS;
    }

    /**
     * 根据明天的时分秒（HMS：时分秒），返回date类型
     * e.g: <br>
     * <blockquote><pre>
     *         (1,2,3) ==> Date(201x-xx-xx 01:02:03.0);
     *         (1,2,3,500) ==> Date(201x-xx-xx 01:02:03.500)
     *     </pre></blockquote>
     *
     * @param multiTimes
     * @return
     */
    public static Date nextDayDateFromHMS(Integer... multiTimes) {
        return new Date(todayMS(multiTimes) + oneDayMS);
    }

    public static Date todayDateFromHMS(Integer... multiTimes) {
        return new Date(todayMS(multiTimes));
    }

    /**
     * 获取相对与今天以及今天以后指定时间的毫秒数
     *
     * @param whichDay   0代表今天，1代表明天
     * @param multiTimes 可变参数，第一个hour，第二个minute，second，millisecond
     *                   如果没有，就是null代表的是0
     */
    public static long whichDayMS(Integer whichDay, Integer... multiTimes) {
        return todayMS(multiTimes) + oneDayMS * whichDay;
    }

    public static Date whichDayDateFromHMS(Integer whichDay, Integer... multiTimes) {
        return new Date(todayMS(multiTimes) + oneDayMS * whichDay);
    }

    public static String formatDateLong(long ms) {
        return LONG_FORMAT.format(new Date(ms));
    }

    public static String formatDateLong2(long ms) {
        return LONG_FORMAT2.format(new Date(ms));
    }


    /**
     * 获得指定月份的最大天
     *
     * @param month 为null 获取当月，1代表1月 12代表12月
     */
    public static int getMaxDayOfMonth(Integer month) {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.MONTH, validateMonth(month, c) - 1);
        return c.getActualMaximum(Calendar.DAY_OF_MONTH);
    }

    private static int validateMonth(Integer month, Calendar c) {
        if (month == null) month = c.get(Calendar.MONTH) + 1;
        else if (month < 1) month = 1;
        else if (month > 12) month = 12;
        return month;
    }

    /**
     * 获取指定月第一天：2019-07-01 00:00:00.000
     *
     * @param month 为null 获取当月，1代表1月 12代表12月
     */
    public static Date getFirstDateOfMonth(Integer month) {
        Calendar c = Calendar.getInstance();
        c.set(c.get(Calendar.YEAR), validateMonth(month, c) - 1, 1, 0, 0, 0);
        c.setTimeInMillis((c.getTimeInMillis() / 1000) * 1000);
        return c.getTime();
    }


    /**
     * 得到相对于当前的时间
     *
     * @return
     */
    public static Date getRelativeMonthToCurrent(Integer mon) {
        Calendar c = Calendar.getInstance();
        int m = c.get(Calendar.MONTH);
        c.set(c.get(Calendar.YEAR), m + mon, 1, 0, 0, 0);
        return c.getTime();
    }


    /**
     * 获取指定月最后一天:2019-07-31 23:59:59.999
     *
     * @param month 为null 获取当月，1代表1月 12代表12月
     */
    public static Date getLastDateOfMonth(Integer month) {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.MONTH, validateMonth(month, c) - 1);
        int max = c.getActualMaximum(Calendar.DAY_OF_MONTH);
        c.set(c.get(Calendar.YEAR), c.get(Calendar.MONTH), max, 24, 0, 0);
        c.setTimeInMillis((c.getTimeInMillis() / 1000) * 1000 - 1);
        return c.getTime();
    }

    public static void main(String[] args) throws ParseException {
        System.out.println(LONG_FORMAT2.format(getFirstDateOfMonth(3)));
        System.out.println(LONG_FORMAT2.format(getRelativeMonthToCurrent(0)));

    }

    public static Date getLastDateOfMonth(Date start) {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(start.getTime());
        return getLastDateOfMonth(c.get(Calendar.MONTH)+1);
    }
}
