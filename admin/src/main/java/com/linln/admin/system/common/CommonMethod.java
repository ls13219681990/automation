package com.linln.admin.system.common;

import com.linln.common.enums.StatusEnum;
import com.linln.common.utils.CommonUtil;
import com.linln.common.utils.ResultVoUtil;
import com.linln.common.utils.StatusUtil;
import com.linln.common.vo.ResultVo;
import com.linln.component.actionLog.annotation.EntityParam;
import com.linln.component.thymeleaf.utility.DictUtil;
import com.linln.modules.system.domain.AcquisitionInstrument;
import com.linln.modules.system.domain.Menu;
import com.linln.modules.system.service.AcquisitionInstrumentService;
import com.linln.modules.system.service.MenuService;
import net.sf.json.JSONObject;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Month;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author SWICS
 * @date 2018/8/14
 */
@Controller
@RequestMapping("/system/clientTree")
public class CommonMethod {
    // 常量

    /**
     * 获取当前时间
     *
     * @return yyyy-MM-dd HH:mm:ss
     */
    public static String getDate() {
        String returnStr = null;
        returnStr = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        return returnStr;
    }

    public static String getDateMm() {
        String returnStr = null;
        returnStr = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSSSS").format(new Date());
        return returnStr;
    }


    /**
     * 获取当前时间戳yyyyMMddHHmmssSSS
     *
     * @return
     */
    public static String getCurrentSystemDate() {
        String returnStr = null;
        returnStr = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date());
        return returnStr;
    }

    /**
     * 获取当前时间数字yyMMdd（6位）的16进制字符串5位
     *
     * @return
     */
    public static String getCurrentYMD() {
        String returnStr = null;
        returnStr = new SimpleDateFormat("yyMMdd").format(new Date());
        return Integer.toHexString(Integer.parseInt(returnStr));
    }

    /**
     * 判断传入的时间是不是昨天
     *
     * @param paramDate
     * @return
     * @throws ParseException
     */
    public static boolean judgeYesterday(String paramDate) throws ParseException {
        Date Yesterday = null;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Yesterday = sdf.parse(paramDate);

        Date date = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String todayStr = format.format(date);
        //得到今天零时零分零秒这一时刻
        Date today = format.parse(todayStr);
        //比较
        System.out.println(today.getTime() - Yesterday.getTime());
        if ((today.getTime() - Yesterday.getTime()) < 86400000) {
            return true;
        }
        return false;
    }

    /**
     * 取得当月天数
     */
    public static int getCurrentMonthLastDay() {
        Calendar a = Calendar.getInstance();
        a.set(Calendar.DATE, 1);//把日期设置为当月第一天
        a.roll(Calendar.DATE, -1);//日期回滚一天，也就是最后一天
        int maxDate = a.get(Calendar.DATE);
        return maxDate;
    }

    private static Calendar getCalendar() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        return cal;
    }

    public static Date getCurrentMonday() {
        return getCalendar().getTime();
    }

    public static Date getLastWeekMonday() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(getCurrentMonday());
        cal.add(Calendar.DATE, -7);
        return cal.getTime();
    }


    public static Date getLastSundayEndDay() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(getLastWeekMonday());
        cal.add(Calendar.DAY_OF_WEEK, 6);
        Calendar todayEnd = new GregorianCalendar();
        todayEnd.setTime(cal.getTime());
        todayEnd.set(Calendar.HOUR_OF_DAY, 23);
        todayEnd.set(Calendar.MINUTE, 59);
        todayEnd.set(Calendar.SECOND, 59);
        todayEnd.set(Calendar.MILLISECOND, 999);
        return todayEnd.getTime();
    }

    /**
     * 获取上周一的时间
     *
     * @return
     */
    public Date lastMonday() {

        Calendar preWeekMondayCal = Calendar.getInstance();

        preWeekMondayCal.add(Calendar.DATE, -6);
        return preWeekMondayCal.getTime();
    }


    /**
     * 根据日期字符串判断当月第几周
     * @param str
     * @return
     * @throws Exception
     */
    public static int getWeek(Date str) throws Exception{

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(str);
        //第几周
        int week = calendar.get(Calendar.WEEK_OF_MONTH);
        //第几天，从周日开始
        int day = calendar.get(Calendar.DAY_OF_WEEK);
        return week;
    }

    /**
     * 判断传入的时间是不是上周
     *
     * @param paramDate
     * @return
     * @throws ParseException
     */
    public static boolean LastWeek(String paramDate) throws ParseException {

        Boolean flag = false;

        try {

            if(CommonMethod.getDate().substring(5, 7).equals(paramDate.substring(5,7))){//先判断是同一月
                if(CommonMethod.getDate().substring(0, 4).equals(paramDate.substring(0,4))){//再判断是同一年
                    Date date = new Date();//获取当前时间    
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(date);
                    calendar.add(Calendar.DAY_OF_MONTH,-7); //当前时间减去一天，即一天前的时间
                    int current = getWeek(calendar.getTime());//判断当前是第几周

                    SimpleDateFormat sdf= new SimpleDateFormat("yyyy-MM-dd");
                    Date date1 =sdf.parse(paramDate);
                    Calendar calendar1 = Calendar.getInstance();
                    calendar1.setTime(date1);
                    int value = getWeek(calendar1.getTime());//判断传入的时间是第几周

                    if(value >= current){// 主要思路 判断当前时间是第几周  和传入的时间是第几周  进行比较
                        flag = true;
                    }
                }

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return flag;
    }


    /**
     * 上月的最后一天
     *
     * @return
     */
    public static Date LastMonthEndDate() {

        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");

        Calendar calendar = Calendar.getInstance();

        int month = calendar.get(Calendar.MONTH);

        calendar.set(Calendar.MONTH, month - 1);

        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));

        return calendar.getTime();
    }

    /**
     * 上月的第一天
     *
     * @return
     */
    public static Date fastMonthEndDate() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

        Calendar calendar = Calendar.getInstance();

        calendar.add(Calendar.MONTH, -1);

        calendar.set(Calendar.DAY_OF_MONTH, 1);
        return calendar.getTime();

    }


    //获取上月的天数
    public static int countDaysInMonth(Month month) {
        return LocalDate.now()
                .withMonth(month.getValue())
                .lengthOfMonth();
    }

    /**
     * 获取两个时间节点之间的月份列表
     **/
    private static List<String> getMonthBetween(String minDate, String maxDate) {
        // 返回的日期集合
        List<String> days = new ArrayList<String>();

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date start = dateFormat.parse(minDate);
            Date end = dateFormat.parse(maxDate);

            Calendar tempStart = Calendar.getInstance();
            tempStart.setTime(start);

            Calendar tempEnd = Calendar.getInstance();
            tempEnd.setTime(end);
            tempEnd.add(Calendar.DATE, +1);// 日期加1(包含结束)
            while (tempStart.before(tempEnd)) {
                days.add(dateFormat.format(tempStart.getTime()));
                tempStart.add(Calendar.DAY_OF_YEAR, 1);
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }

        return days;
    }


    /**
     * 判断传入的时间是不是上月
     *
     * @param paramDate
     * @return
     * @throws ParseException
     */
    public static boolean LastMonth(String paramDate) throws ParseException {


        Boolean flag = false;

        Date date = stringToDate(paramDate);


        Date startDate = fastMonthEndDate();
        Date endDate = LastMonthEndDate();

        String start = dateToString(startDate).substring(0, 10);
        String end = dateToString(endDate).substring(0, 10);
        List<String> monthBetween = getMonthBetween(start, end);


        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.MONTH, -1);//当前时间前去一个月，即一个月前的时间    

        String s = dateToString(calendar.getTime()).substring(0, 10);
        for (String d : monthBetween) {
            if (d.equals(s)) {
                return true;
            }
        }
        return flag;

    }

    /**
     * 获取当前时间
     *
     * @return yyyy-MM-dd HH:mm:ss
     */
    public static Date getTime() {
        String returnStr = null;
        returnStr = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        // System.out.println(returnStr);
        try {
            return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(returnStr);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 获取当前时间
     *
     * @return yyyy-MM-dd
     */
    public static String getCurrentDate() {
        String returnStr = null;
        returnStr = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        // System.out.println(returnStr);
        try {
            return returnStr;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 获取当前月
     *
     * @return yyyy-MM
     */
    public static String getCurrentMonth() {
        String returnStr = null;
        returnStr = new SimpleDateFormat("yyyy-MM").format(new Date());
        try {
            return returnStr;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 获取当前年
     *
     * @return yyyy
     */
    public static String getCurrentYear() {
        String returnStr = null;
        returnStr = new SimpleDateFormat("yyyy").format(new Date());
        try {
            return returnStr;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 获取当前时间
     *
     * @return yyyy-MM-dd HH:mm:ss
     */
    public final static Timestamp getTimeStamp() {
        Timestamp dateTime = new Timestamp(getTime().getTime());// Timestamp类型,timeDate.getTime()返回一个long型
        return dateTime;
    }

    /**
     * 将时间转换为字段
     *
     * @param s
     * @return
     * @throws ParseException
     */
    public static Date stringToDate(String s) {
        try {
            if (s == null)
                s = "";
            return !s.trim().equals("") ? new SimpleDateFormat("yyyy-MM-dd").parse(s) : null;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * method 将字符串类型的日期转换为一个timestamp（时间戳记java.sql.Timestamp）
     *
     * @param dateString 需要转换为timestamp的字符串 yyyy-MM-dd kk:mm:ss.SSS
     * @return dataTime timestamp
     */
    public final static Timestamp string2Time(String dateString) {
        try {
            DateFormat dateFormat;
            dateFormat = new SimpleDateFormat("yyyy-MM-dd kk:mm:ss.SSS", Locale.ENGLISH);// 设定格式
            // dateFormat = new SimpleDateFormat("yyyy-MM-dd kk:mm:ss",
            // Locale.ENGLISH);
//			dateFormat.setLenient(false);
            Date timeDate = (Date) dateFormat.parse(dateString);// util类型
            Timestamp dateTime = new Timestamp(timeDate.getTime());// Timestamp类型,timeDate.getTime()返回一个long型
            return dateTime;
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
            return null;
        }
    }

    /**
     * method 将字符串类型的日期转换为一个timestamp（时间戳记java.sql.Timestamp）
     *
     * @param dateString 需要转换为timestamp的字符串yyyy-MM-dd kk:mm:ss
     * @return dataTime timestamp
     */
    public final static Timestamp string2Time1(String dateString) {
        try {
//			logger.debug("日期0: "+dateString);
//			DateFormat dateFormat;
//			dateFormat = new SimpleDateFormat("yyyy-MM-dd kk:mm:ss", Locale.ENGLISH);// 设定格式
            if (!dateString.contains(":")) {
                dateString += " 00:00:00";
            }
            Date dt = new Date();
            String[] parts = dateString.replace(" ", "-").replace(":", "-").replace(".", "-").split("-");

            if (parts.length >= 3) {
                int years = Integer.parseInt(parts[0]);
                int months = Integer.parseInt(parts[1]) - 1;
                int days = Integer.parseInt(parts[2]);
                int hours = Integer.parseInt(parts[3]);
                int minutes = Integer.parseInt(parts[4]);
                int seconds = Integer.parseInt(parts[5]);

                GregorianCalendar gc = new GregorianCalendar(years, months,
                        days, hours, minutes, seconds);

                dt = gc.getTime();
            }
//			return dt;
            // dateFormat = new SimpleDateFormat("yyyy-MM-dd kk:mm:ss",
            // Locale.ENGLISH);
//			dateFormat.setLenient(false);
//			java.util.Date timeDate = dateFormat.parse(dateString);// util类型
//			java.sql.Timestamp dateTime = new java.sql.Timestamp(timeDate.getTime());// Timestamp类型,timeDate.getTime()返回一个long型
            Timestamp dateTime = new Timestamp(dt.getTime());// Timestamp类型,timeDate.getTime()返回一个long型
            return dateTime;
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
            return null;
        }
    }

    /**
     * yyyy-MM-dd 00:00:00
     *
     * @param submitTimeFrom
     * @return
     */
    public static Timestamp toStartTime(Timestamp submitTimeFrom) {
        String start = timeToString(submitTimeFrom);
        start += " 00:00:00";
        return string2Time1(start);
    }

    /**
     * yyyy-MM-dd 23:59:59
     *
     * @param submitTimeFrom
     * @return
     */
    public static Timestamp toEndTime(Timestamp submitTimeFrom) {
        String end = timeToString(submitTimeFrom);
        end += " 23:59:59";
        return string2Time1(end);
    }

    /**
     * yyyy-MM-dd HH:mm:ss method 将字符串类型的日期转换为一个Date（java.sql.Date）
     *
     * @param dateString 需要转换为Date的字符串
     * @return dataTime Date
     */
    public final static Date string2Date(String dateString) {
        try {
            if (dateString == null)
                dateString = "";
            return !dateString.trim().equals("") ? new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(dateString) : null;
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
            return null;
        }
    }


    /**
     * @param d yyyy-MM-dd
     * @return yyyy-MM-dd
     */
    public static String dateToString(Date d) {
        String result = null;
        if (d != null) {
            SimpleDateFormat bartDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            result = bartDateFormat.format(d);
        }
        return result;
    }

    /**
     * @param d yyyy-MM-dd HH:mm:ss
     * @return yyyy-MM-dd HH:mm:ss
     */
    public static String dateToString2(Date d) {
        String result = null;
        if (d != null) {
            SimpleDateFormat bartDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            result = bartDateFormat.format(d);
        }
        return result;
    }

    /**
     * @param t yyyy-MM-dd
     * @return yyyy-MM-dd
     */
    public static String timeToString(Timestamp t) {
        Date d = new Date(t.getTime());
        Calendar cal = Calendar.getInstance();
        cal.setTime(d);
        String dStr = dateToString(cal.getTime());
        return dStr;
    }

    /**
     * @param t yyyy-MM-dd HH:mm:ss
     * @return yyyy-MM-dd HH:mm:ss
     */
    public static String timeToString2(Timestamp t) {
        Date d = new Date(t.getTime());
        Calendar cal = Calendar.getInstance();
        cal.setTime(d);
        String dStr = dateToString2(cal.getTime());
        return dStr;
    }

    /**
     * @param input
     * @return 空返回true，非空返回false
     */
    public static boolean isNull(String input) {
        if (input != null && !input.equals("")) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * 时间增加n天
     *
     * @param s 初始时间
     * @param n 增加天数
     * @return
     */
    public static String addDay(String s, int n) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

            Calendar cd = Calendar.getInstance();
            cd.setTime(sdf.parse(s));
            cd.add(Calendar.DATE, n);// 增加一天
            // cd.add(Calendar.MONTH, n);//增加一个月
            return sdf.format(cd.getTime());
        } catch (Exception e) {
            return null;
        }

    }

    /**
     * 时间增加n月
     *
     * @param s 初始时间
     * @param n 增加月数
     * @return
     */
    public static String addMonth(String s, int n) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

            Calendar cd = Calendar.getInstance();
            cd.setTime(sdf.parse(s));
//			cd.add(Calendar.DATE, n);// 增加一天
            cd.add(Calendar.MONTH, n);//增加一个月
            return sdf.format(cd.getTime());
        } catch (Exception e) {
            return null;
        }

    }


    /**
     * 时间增加n年
     *
     * @param s 初始时间
     * @param n 增加天数
     * @return
     */
    public static String addYear(String s, int n) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

            Calendar cd = Calendar.getInstance();
            cd.setTime(sdf.parse(s));
            cd.add(Calendar.YEAR, n);// 增加一年
            // cd.add(Calendar.MONTH, n);//增加一个月
            return sdf.format(cd.getTime());
        } catch (Exception e) {
            return null;
        }

    }

    /**
     * 时间增加n天
     *
     * @param s 初始时间
     * @param n 增加天数
     * @return
     */
    public static Timestamp addDay(Timestamp s, int n) {
        try {

            Calendar cd = Calendar.getInstance();
            cd.setTime(s);
            cd.add(Calendar.DATE, n);// 增加一天
            // cd.add(Calendar.MONTH, n);//增加一个月
            return string2Time(dateToString(cd.getTime()) + " 00:00:00.0");
        } catch (Exception e) {
            return null;
        }
    }

    public static String addHour(String s, int n) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

            if (!s.contains(":")) {
                s += " 00:00:00";
            }

            Calendar cd = Calendar.getInstance();
            cd.setTime(sdf.parse(s));
            cd.add(Calendar.HOUR, n);// 增加一小时
            // cd.add(Calendar.MONTH, n);//增加一个月
            return sdf.format(cd.getTime());
        } catch (Exception e) {
            return null;
        }

    }

    public static Timestamp addHour(Timestamp s, int n) {
        try {

            Calendar cd = Calendar.getInstance();
            cd.setTime(s);
            cd.add(Calendar.HOUR, n);// 增加一天
            // cd.add(Calendar.MONTH, n);//增加一个月
            return string2Time1(dateToString2(cd.getTime()));
        } catch (Exception e) {
            return null;
        }

    }

    /**
     * 将长时间格式字符串转换为时间 yyyy-MM-dd HH:mm:ss
     *
     * @param strDate
     * @return
     */
    public static Date strToDateLong(String strDate) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        ParsePosition pos = new ParsePosition(0);
        Date strtodate = formatter.parse(strDate, pos);
        return strtodate;
    }

    /**
     * 计算两个时间相差的天数
     *
     * @param Stringdate1 被减数
     * @param Stringdate2
     * @return
     */
    public static int differentDaysByMillisecond(String Stringdate1, String Stringdate2) {

        Date date1 = strToDateLong(Stringdate1);
        Date date2 = strToDateLong(Stringdate2);
        int days = (int) ((date2.getTime() - date1.getTime()) / (1000 * 3600 * 24));
        return days;
    }


    /**
     * 计算两个时间相差？小时
     *
     * @param startT
     * @param endT
     * @return
     */
    public static Long diffHour(Timestamp startT, Timestamp endT) {

        long l = endT.getTime() - startT.getTime();
        long hour = (l / (60 * 60 * 1000));

        return hour;
    }

    public static Long TimeDiff(String pBeginTime, String pEndTime) throws Exception {

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm");
        Long beginL = format.parse(pBeginTime).getTime();
        Long endL = format.parse(pEndTime).getTime();
        Long day = (endL - beginL) / 86400000; //天
        Long hour = ((endL - beginL) % 86400000) / 3600000;//小时
        Long min = ((endL - beginL) % 86400000 % 3600000) / 60000;//分
        return hour;
    }

    /**
     * 计算两个时间相差？分
     *
     * @param startT
     * @param endT
     * @return
     */
    public static Long diffMin(Timestamp startT, Timestamp endT) {

        long l = endT.getTime() - startT.getTime();
        long min = l / (60 * 1000);

        return min;
    }


    /**
     * 两个时间相差距离多少天多少小时多少分多少秒
     *
     * @param starttime 时间参数 1 格式：1990-01-01 12:00:00
     * @param endtime   时间参数 2 格式：2009-01-01 12:00:00
     * @return String 返回值为：xx天xx小时xx分xx秒
     */
    public static String getDistanceTime(String starttime, String endtime) {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date one;
        Date two;
        long day = 0;
        long hour = 0;
        long min = 0;
        long sec = 0;
        try {
            one = df.parse(starttime);
            two = df.parse(endtime);
            long time1 = one.getTime();
            long time2 = two.getTime();
            long diff;
            if (time1 < time2) {
                diff = time2 - time1;
            } else {
                diff = time1 - time2;
            }

            day = diff / (24 * 60 * 60 * 1000);
            hour = (diff / (60 * 60 * 1000) - day * 24);
            min = ((diff / (60 * 1000)) - day * 24 * 60 - hour * 60);
            sec = (diff / 1000 - day * 24 * 60 * 60 - hour * 60 * 60 - min * 60);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return day + "天" + hour + "小时" + min + "分" + sec + "秒";
    }


    public static Long getDatePoor(String nowDate, String endDate) {

        long nd = 1000 * 24 * 60 * 60;
        long nh = 1000 * 60 * 60;
        long nm = 1000 * 60;

        long min = 0L;


        SimpleDateFormat dfs = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date begin = dfs.parse(nowDate);
            Date end = dfs.parse(endDate);
            // long ns = 1000;
            // 获得两个时间的毫秒时间差异
            long diff = end.getTime() - begin.getTime();
            // 计算差多少天
            long day = diff / nd;
            // 计算差多少小时
            long hour = diff % nd / nh;
            // 计算差多少分钟
            min = diff / nm;

            //min = diff % nd % nh / nm;
            // 计算差多少秒//输出结果
            // long sec = diff % nd % nh % nm / ns;
            //return day + "天" + hour + "小时" + min + "分钟";

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return min;

    }


    /**
     * 计算两个时间相差？秒
     *
     * @param startT
     * @param endT
     * @return
     */
    public static Long diffSec(Timestamp startT, Timestamp endT) {

        long l = endT.getTime() - startT.getTime();
        long min = l / (1000);

        return min;
    }

    /**
     * 当前时间减去多少分钟
     *
     * @param date   当前时间
     * @param number 分钟数
     * @return
     */
    public static String getTimeMinuteAdditionAndSubtraction(String date, Long number) {

        String formatDate = "";
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date currentDate = df.parse(date);// 解析成日期格式

            long time = number * 60 * 1000;//30分钟

            currentDate.setTime(currentDate.getTime() - time);// 减去4分钟以后的时间
            formatDate = df.format(currentDate);// 再将该时间转换成字符串格式
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return formatDate;
    }

    /**
     * 获取本月开始日期
     *
     * @return String
     **/
    public static String getMonthStart() {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MONTH, 0);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        Date time = cal.getTime();
        return new SimpleDateFormat("yyyy-MM-dd").format(time) + " 00：00：00";
    }

    /**
     * 获取本月最后一天
     *
     * @return String
     **/
    public static String getMonthEnd() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
        Date time = cal.getTime();
        return new SimpleDateFormat("yyyy-MM-dd").format(time) + " 23:59:59";
    }

    /**
     * 获取本周的第一天
     *
     * @return String
     **/
    public static String getWeekStart() {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.WEEK_OF_MONTH, 0);
        cal.set(Calendar.DAY_OF_WEEK, 2);
        Date time = cal.getTime();
        return new SimpleDateFormat("yyyy-MM-dd").format(time) + " 00:00:00";
    }

    /**
     * 获取本周的最后一天
     *
     * @return String
     **/
    public static String getWeekEnd() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_WEEK, cal.getActualMaximum(Calendar.DAY_OF_WEEK));
        cal.add(Calendar.DAY_OF_WEEK, 1);
        Date time = cal.getTime();
        return new SimpleDateFormat("yyyy-MM-dd").format(time) + " 23:59:59";
    }

    public static List getSelectList(String listName) {
        HashMap<String, List> hs = new HashMap<String, List>();
        if (hs.get(listName) == null) {
            setSelectList(listName);
        }
        return hs.get(listName);
    }

    private static void setSelectList(String listName) {

    }

    /**
     * @param hj  被加数字，会被修改
     * @param obj 加数 不会被修改
     * @return 和
     */
    public static BigDecimal getSumByDecimal(BigDecimal hj, Object obj) {
        if (obj != null && !obj.equals("")) {
            hj = hj.add(new BigDecimal(obj.toString()));
        }
        return hj;
    }

    /**
     * 验证是否整数
     *
     * @param str
     * @return
     */
    public static boolean isNumber(String str) {
        if (str == null || "".equals(str)) {
            return false;
        }
        for (int i = str.length(); --i >= 0; ) {
            if (!Character.isDigit(str.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    public static List copyObject(List target, List source) {
        for (int i = 0; i < source.size(); i++) {
            target.add(source.get(i));
        }
        return target;
    }

    public static String getClassFields(Object msg) {
        try {
            java.lang.reflect.Field[] flds = msg.getClass().getDeclaredFields();
            String returnValue = "";
            if (flds != null) {
                for (int i = 0; i < flds.length; i++) {
                    String getMethod = "get" + flds[i].getName().substring(0, 1).toUpperCase() + flds[i].getName().substring(1);
                    Class[] methodParam = null;
                    Object[] params = {};
                    Object retValue = null;
                    // 这里就是调用Bean的get方法，很爽哦，适合写在基类里！！！
                    retValue = msg.getClass().getMethod(getMethod, methodParam).invoke(msg, params);
                    returnValue += flds[i].getName() + ":" + retValue + "；";
                    if (i == 3) {
                        break;
                    }
                }
            }
            return returnValue;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }


    /**
     * 为null的话返回""
     *
     * @param text
     * @return
     */
    public static String formatString(String text) {
        if (text == null) {
            return "";
        }
        return text;
    }

    /**
     * double四舍五入为int
     *
     * @param d
     * @return
     */
    public static int doubleToInt(double d) {
        return Integer.valueOf(new BigDecimal(String.valueOf(d)).setScale(0, BigDecimal.ROUND_HALF_UP).toString());
    }

    /**
     * double四舍五入保留小数位数
     *
     * @param d
     * @param scale 需要保留的小数位数
     * @return
     */
    public static String formatdb(double d, int scale) {

        //DecimalFormat df = new DecimalFormat("#0.00");
        //return df.format(d);
        return new BigDecimal(String.valueOf(d)).setScale(scale, BigDecimal.ROUND_HALF_UP).toString();
    }

    /**
     * double四舍五入保留2位小数
     *
     * @param d
     * @return
     */
    public static String format2db(double d) {

        //DecimalFormat df = new DecimalFormat("#0.00");
        //return df.format(d);
        return new BigDecimal(String.valueOf(d)).setScale(2, BigDecimal.ROUND_HALF_UP).toString();
    }

    /**
     * double四舍五入保留3位小数
     *
     * @param d
     * @return
     */
    public static String format3db(double d) {
//		 DecimalFormat df = new DecimalFormat("#0.000");
//		 return df.format(d);

        return new BigDecimal(String.valueOf(d)).setScale(3, BigDecimal.ROUND_HALF_UP).toString();
    }

    /**
     * 转换字符编码格式,原字符必须为utf-8
     *
     * @param s
     * @param charset
     * @return
     */
    public static String getNewString(String s, String charset) {
        try {
            return new String(s.getBytes("utf-8"), charset);
        } catch (Exception e) {
            return s;
        }
    }

    /**
     * 将字符串类型的日期转换为一个timestamp（时间戳记java.sql.Timestamp）
     *
     * @param dateString 需要转换为timestamp的字符串yyyyMMddkkmmss
     * @return
     */
    public static Timestamp string2Time3(String dateString) {
        try {
            DateFormat dateFormat;
            dateFormat = new SimpleDateFormat("yyyyMMddkkmmss", Locale.ENGLISH);// 设定格式
            // dateFormat = new SimpleDateFormat("yyyy-MM-dd kk:mm:ss",
            // Locale.ENGLISH);
            dateFormat.setLenient(false);
            if (!dateString.contains(":")) {
                dateString += " 00:00:00";
            }
            Date timeDate = dateFormat.parse(dateString);// util类型
            Timestamp dateTime = new Timestamp(timeDate.getTime());// Timestamp类型,timeDate.getTime()返回一个long型
            return dateTime;
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 统一易宝支付宝参数格式
     *
     * @param notify_time yyyy-mm-dd kk:mm:ss
     * @return yyyyMMddkkmmss
     */
    public static String formartNotifyTime(String notify_time) {
        return notify_time.replaceAll(":", "").replaceAll("-", "").replaceAll(" ", "");
    }

    /**
     * 计算日期为星期几
     *
     * @param t
     * @return 1.星期日、2.星期一、3.星期二、4.星期三、5.星期四、6.星期五、7.星期六、
     */
    public static int getWeekByTimeStamp(Timestamp t) {
        Date d = new Date(t.getTime());
        Calendar cal = Calendar.getInstance();
        cal.setTime(d);
        int w = cal.get(Calendar.DAY_OF_WEEK);
        return w;
    }

    /**
     * 获取时间段中间的所有日期
     *
     * @param startT
     * @param endT
     */
    public static List<Timestamp> getTimeStampList(Timestamp startT, Timestamp endT) {

        List<Timestamp> tList = new ArrayList<Timestamp>();
        startT = toStartTime(startT);
        endT = toEndTime(endT);

        Timestamp tempT = startT;
        boolean tag = tempT.before(endT);

        while (tag) {

            tList.add(tempT);

            Date d = new Date(tempT.getTime());
            Calendar cal = Calendar.getInstance();
            cal.setTime(d);
            cal.add(Calendar.DATE, 1);
            String dStr = dateToString(cal.getTime());
            tempT = string2Time1(dStr);

            tag = tempT.before(endT);
        }
        ;

        return tList;
    }


    /**
     * 删除单个文件
     *
     * @param sPath 被删除文件的文件名
     * @return 单个文件删除成功返回true，否则返回false
     */
    public static boolean deleteFile(String sPath) {
        Boolean flag = false;
        File file = new File(sPath);
        // 路径为文件且不为空则进行删除
        if (file.isFile() && file.exists()) {
            file.delete();
            flag = true;
        }
        return flag;
    }

    /**
     * 去除select 到from
     *
     * @param sql
     * @return
     */
    public static String removeSelect(String sql) {
        final int beginPos = sql.toLowerCase().indexOf("from");
        return sql.substring(beginPos);
    }

    private static String removeOrders(String sql) {
        Pattern p = Pattern.compile("order\\s*by[\\w|\\W|\\s|\\S]*",
                Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(sql);
        StringBuffer sb = new StringBuffer();
        while (m.find()) {
            m.appendReplacement(sb, "");
        }
        m.appendTail(sb);
        return sb.toString();
    }

//	/**
//	 *
//	 * @param phones
//	 * @param content
//	 * @param choosePlatfrom  true:用新地址，false:用旧地址
//	 */
//	public static void sendSms(String phones,String content,String choosePlatfrom){
//		if(phones != null && !phones.isEmpty()){
//			content = content.replaceAll("'", "");
//			if(!content.endsWith("【----】")){
//				content = content+"【----】";
//			}
//			System.out.println("发送短信手机号码为 ："+phones+",内容："+content);
//			if("1".equals(BookingConfig.getInstance().getValue("is_send"))){
//				if("false".equals(choosePlatfrom)){
//					sendSmsOld(phones,content);
//					//System.out.println("sendSmsOld");
//				}else{
//					sendSmsNew(phones,content);
//					//System.out.println("sendSmsNew");
//				}
//			}
//		}
//	}

//	public static void sendSmsOld(String phones,String content){
//			SendSMSThread thread = new SendSMSThread();
//			thread.setContent(content);
//			thread.setMobile(phones);
//			thread.run();
//	}
//
//	public static void sendSmsNew(String phones,String content){
//		SendSMSNewThread thread = new SendSMSNewThread();
//		thread.setContent(content);
//		thread.setMobile(phones);
//		thread.run();
//}


    /**
     * 读取url内容
     *
     * @param strUrl
     * @return
     */
    public static StringBuffer readUrl(String strUrl) {
        URL url = null;
        HttpURLConnection connection = null;

        try {
            url = new URL(strUrl);
            connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setRequestMethod("GET");
            connection.setUseCaches(false);
            connection.connect();

            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf-8"));
            StringBuffer buffer = new StringBuffer();
            String line = "";
            while ((line = reader.readLine()) != null) {
                buffer.append(line);
            }
            reader.close();
            return buffer;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }


    /**
     * 验证时间段是否交叉
     *
     * @param startDate //开始时间
     * @param endDate   //结束时间
     * @param oldTimes  //已有的时间段List<Map<>>,[{startDate= , endDate= },{startDate= , endDate= }]
     * @return 交叉的时间段{startDate= , endDate= }，如没有交叉则返回null;
     */
    public static Map CrossValidationPeriod(Timestamp startDate, Timestamp endDate, List oldTimes) {

        Map reMap = new HashMap();

        for (Object o : oldTimes) {
            Map temp = (Map) o;
            Timestamp oldStartDate = toStartTime(string2Time1(temp.get("startDate").toString()));
            Timestamp oldEndDate = toEndTime(string2Time1(temp.get("endDate").toString()));

            if (oldStartDate.after(startDate) && oldStartDate.before(endDate)) {
                reMap.put("startDate", oldStartDate);
                reMap.put("endDate", oldEndDate);
                return reMap;
            } else if (oldEndDate.after(startDate) && oldEndDate.before(endDate)) {
                reMap.put("startDate", oldStartDate);
                reMap.put("endDate", oldEndDate);
                return reMap;
            } else if (startDate.before(oldStartDate) && endDate.after(oldEndDate)) {
                reMap.put("startDate", oldStartDate);
                reMap.put("endDate", oldEndDate);
                return reMap;
            } else if (oldStartDate.before(startDate) && oldEndDate.after(endDate)) {
                reMap.put("startDate", oldStartDate);
                reMap.put("endDate", oldEndDate);
                return reMap;
            } else if (oldStartDate.equals(startDate)) {
                reMap.put("startDate", oldStartDate);
                reMap.put("endDate", oldEndDate);
                return reMap;
            } else if (oldEndDate.equals(endDate)) {
                reMap.put("startDate", oldStartDate);
                reMap.put("endDate", oldEndDate);
                return reMap;
            }

        }

        return null;

    }

    /**
     * 返回指定日期的月的最后一天
     */
    public static String getLastDayOfMonth(String date) {
        SimpleDateFormat datef = new SimpleDateFormat("yyyy-MM-dd");
        Date date1 = CommonMethod.stringToDate(date);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date1);
        calendar.set(calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH), 1);
        calendar.roll(Calendar.DATE, -1);
        return datef.format(calendar.getTime());
    }

    /**
     * 返回指定日期的月的第一天
     */
    public static String getFirstDayOfMonth(String date) {
        SimpleDateFormat datef = new SimpleDateFormat("yyyy-MM-dd");
        Date date1 = CommonMethod.stringToDate(date);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date1);
        calendar.set(calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH), 1);
        return datef.format(calendar.getTime());
    }

    /**
     * 获取key
     *
     * @return
     */
    public static String getNewKey() {
        return UUID.randomUUID().toString().replace("-", "").toUpperCase();
    }

    public static String getOrderDate(Date date, int scale) {
        String orderDate = "";
        int dateSum = 0;
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String dateStr = format.format(date);
        System.out.println(dateStr);
        int year = Integer.valueOf(dateStr.substring(0, 4));
        int month = Integer.valueOf(dateStr.substring(5, 7));
        int day = Integer.valueOf(dateStr.substring(8, 10));
        for (int i = 1; i < month; i++) {
            switch (i) {
                case 1:
                case 3:
                case 5:
                case 7:
                case 8:
                case 10:
                case 12:
                    dateSum += 31;
                    break;
                case 4:
                case 6:
                case 9:
                case 11:
                    dateSum += 30;
                    break;
                case 2:
                    if (((year % 4 == 0) & (year % 100 != 0)) | (year % 400 == 0))
                        dateSum += 29;
                    else dateSum += 28;
            }
        }
        dateSum = dateSum + day;
        orderDate = String.format("%0" + scale + "d", dateSum);
        return orderDate;
    }


    /**
     * double 一维数组转二维数组
     *
     * @param onedouble
     * @return
     */
    public static double[][] doubleTwoArry(double[] onedouble) {
        double[][] arr = new double[1][onedouble.length];
        for (int i = 0; i < onedouble.length; i++) {
            arr[0][i] = onedouble[i];
        }
        return arr;
    }

    /**
     * double四舍五入
     *
     * @param onedouble
     * @return
     */
    public static double roundingFour(double onedouble) {
        String roundingFour = String.format("%.4f", onedouble);
        onedouble = new BigDecimal(roundingFour).setScale(1, BigDecimal.ROUND_HALF_UP).doubleValue();


        return onedouble;
    }


    /**
     * 去除数组中的空值
     *
     * @param strArray
     * @return
     */
    public static String[] removeArrayEmptyTextBackNewArray(String[] strArray) {

        List<String> strList = Arrays.asList(strArray);

        List<String> strListNew = new ArrayList<>();

        for (int i = 0; i < strList.size(); i++) {

            if (strList.get(i) != null && !strList.get(i).equals("")) {

                strListNew.add(strList.get(i));
            }


        }

        String[] strNewArray = strListNew.toArray(new String[strListNew.size()]);

        return strNewArray;

    }


    /***
     * 去除String数组中的空值
     */
    private static String[] deleteArrayNull(String string[]) {
        String strArr[] = string;

        // step1: 定义一个list列表，并循环赋值
        ArrayList<String> strList = new ArrayList<String>();
        for (int i = 0; i < strArr.length; i++) {
            strList.add(strArr[i]);
        }

        // step2: 删除list列表中所有的空值
        while (strList.remove(null)) ;
        while (strList.remove("")) ;

        // step3: 把list列表转换给一个新定义的中间数组，并赋值给它
        String strArrLast[] = strList.toArray(new String[strList.size()]);

        return strArrLast;
    }


    /**
     * byte数组转十六进制的字符串
     *
     * @param byteArray
     * @returnH
     */
    public static String byteArrayTurnHexString(byte[] byteArray) {
        StringBuffer stringBuilder = new StringBuffer(byteArray.length);
        String sTemp;
        for (int i = 0; i < byteArray.length; i++) {
            //bytes[i] = -57;
            //int intvalue = bytes[i];
            byte aByte = byteArray[i];
            //转十六进制
            sTemp = Integer.toHexString(0xFF & byteArray[i]);
            if (sTemp.length() < 2)
                stringBuilder.append(0);
            stringBuilder.append(sTemp.toUpperCase());
        }
        return stringBuilder.toString();
    }

    public static String to16Hex(Date date) {
        Long ab = date.getTime() / 1000;
        String a = Long.toHexString(ab);
        return a;
    }


    /**
     * 数据切割(八个一组)
     *
     * @param dataSource
     * @return
     */
    public static List<Map<String, List<String>>> dataSegmentationAndReplace(String dataSource) {


        /**
         * BB分割法 不用 因为数据中间有可能有BB 例如:535749595430303031BB0584C91F2072BB4403C91F5BB6775D5644000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000C23B0000F9BB4402C91F00000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000D4BB0801C91F00000000D7
         */
       /*
        String substring = dataSource.substring(0, dataSource.indexOf("B"));
        dataSource = dataSource.replaceAll(substring, "");
        if (dataSource.contains("BB0005")) {
            dataSource = dataSource.replace("BB0005", "BB05");
        }
        if (dataSource.contains("BB005")) {
            dataSource = dataSource.replace("BB005", "BB05");
        }
        List<Map<String, List<String>>> dataList = new ArrayList<>();
        String[] dataArray = dataSource.split("BB05");
        dataArray = removeArrayEmptyTextBackNewArray(dataArray);
        for (String value : dataArray) {
            value = "BB05" + value;
            if (value.contains("BBBB")) {
                value = value.replace("BBBB", "B B BB");
            }
            if (value.contains("BBB")) {
                value = value.replace("BBB", "B BB");
            }

            String[] StringArray = value.split("BB");
            StringArray = removeArrayEmptyTextBackNewArray(StringArray);


            List<String> stringList = new ArrayList<>();
            for (String paragraph : StringArray) {
                paragraph = "BB"+paragraph;
                paragraph = paragraph.replace(" ", "");

                if (paragraph.indexOf("BA01") != -1) {
                    paragraph = paragraph.replace("BA01", "BB");
                } else if (value.indexOf("BA00") != -1) {
                    paragraph = paragraph.replace("BA00", "BA");
                }
                stringList.add(paragraph);
            }
            Map<String, List<String>> map = new LinkedHashMap<>();
            for (String string : stringList) {
                List<String> strings = new ArrayList<>();

                String head = string.substring(0, 10);
                Integer integer = Integer.parseInt(string.substring(2, 4), 16);
                string  = string.substring(0, (integer * 2) + 4);

                String data = string.substring(10, string.length());
                for (int k = 0; k < data.length(); ) {
                    if (data.length() - k <= 4) {
                        strings.add(data.substring(k, data.length()));
                    } else {
                        strings.add(data.substring(k, k + 8));
                    }
                    k += 8;
                }
                map.put(head, strings);



            }

            dataList.add(map);
        }

        return dataList;*/


        String substring = dataSource.substring(0, dataSource.indexOf("BB"));

        dataSource = dataSource.replaceAll(substring, "");
        if (dataSource.contains("BB0005")) {
            dataSource = dataSource.replace("BB0005", "BB05");
        }
        if (dataSource.contains("BB005")) {
            dataSource = dataSource.replace("BB005", "BB05");
        }
        String[] dataArray = dataSource.split("BB05");

        dataArray = removeArrayEmptyTextBackNewArray(dataArray);
        List<Map<String, List<String>>> dataList = new ArrayList<>();
        for (String value : dataArray) {

            value = "BB05" + value;

           /* if (value.indexOf("BBBB") != -1) {
                value = value.replace("BBBB", "BB BB");
            }

            if (value.indexOf("BBB") != -1) {
                value = value.replace("BBB", "B BB");
            }*/
            if (value.indexOf("BA01") != -1) {
                value = value.replace("BA01", "BB");
            } else if (value.indexOf("BA00") != -1) {
                value = value.replace("BA00", "BA");
            }
            List<String> stringList = new ArrayList<>();
            for (int i = 0; i < value.length(); ) {
                String head = value.substring(i + 2, i + 4);
                Integer integer = Integer.parseInt(head, 16);

                //不等于求和码的开头的哪个 77
                stringList.add(value.substring(i, (integer * 2) + (i + 4)));
                i = (integer * 2) + (i + 4);
                if (i < value.length() && !"BB".equals(value.substring(i, i + 2))) {
                    break;
                }
            }
            Map<String, List<String>> map = new LinkedHashMap<>();
            //数据切割2  8个字节为一组
            for (String string : stringList) {

                List<String> strings = new ArrayList<>();
                String head = string.substring(0, 10);
                String data = string.substring(10, string.length());
                for (int k = 0; k < data.length(); ) {
                    if (data.length() - k <= 4) {
                        strings.add(data.substring(k, data.length()));
                    } else {
                        strings.add(data.substring(k, k + 8));
                    }
                    k += 8;
                }
                map.put(head, strings);


            }


            dataList.add(map);


        }


        return dataList;












       /*

           //数据切割1 已BB为一个单位
        /*List<String> stringList = new ArrayList<>();
        for (int i = 0; i < dataSource.length(); ) {
            String head = dataSource.substring(i + 2, i + 4);
            Integer integer = Integer.parseInt(head, 16);
            //不等于求和码的开头的哪个 77
            if(integer != 119){
                //bb 转义
                String substring = dataSource.substring(i, (integer * 2) + (i + 4));

                if(substring.indexOf("BA01")!=-1){
                    //包含
                    substring = substring.replace("BA01", "BB");
                    //System.out.println("转换过后的值1====>"+substring);
                    i = (integer * 2) + (i + 4)+2;
                }else if(substring.indexOf("BA00")!=-1){
                    substring = substring.replace("BA00", "BA");
                    //System.out.println("转换过后的值2====>"+substring);
                    i = (integer * 2) + (i + 4)+2;
                }else{
                    i = (integer * 2) + (i + 4);
                }
                stringList.add(substring);
            }

        }*/

        /*for (int j = 0; j < stringList.size(); j++) {

            String substring1 = stringList.get(j).substring(0, 2);
            String substring2 = stringList.get(j).substring(2, stringList.get(j).length());

            if (substring2.contains("BB")) {
                stringList.set(j, substring1 + substring2.replaceAll("BB", "BA01"));

            } else if (substring2.contains("BA")) {
                stringList.set(j, substring1 + substring2.replaceAll("BA", "BA00"));
            }

   //计算所有通道
        Iterator<Map.Entry<String, List<String>>> iterator = map.entrySet().iterator();
        while (iterator.hasNext()) {
            int i = 0;
            Map.Entry<String, List<String>> next = iterator.next();

            List<String> value = next.getValue();
            if (i == 0) {

                //Integer.parseInt("", 16); 16转10进制
                // 等用到实际业务再优化
                for (int j = 1; j < value.size() - 1; j++) {
                    //获取unix时间戳转换成北京时间
                    Integer i1 = Integer.parseInt(value.get(0).substring(0, 2), 16);
                    Integer i2 = Integer.parseInt(value.get(0).substring(2, 4), 16);
                    Integer i3 = Integer.parseInt(value.get(0).substring(4, 6), 16);
                    Integer i4 = Integer.parseInt(value.get(0).substring(6, 8), 16);
                    Long result = Long.valueOf((i4 * 256 * 256 * 256) + (i3 * 256 * 256) + (i2 * 256) + i1) - 28631;
                    String dateTime = CommonMethod.TimeStamp2Date(result.toString());
                    //通道参数计算
                    Integer data1 = Integer.parseInt(value.get(j).substring(0, 2), 16);
                    Integer data2 = Integer.parseInt(value.get(j).substring(2, 4), 16);
                    Integer data3 = Integer.parseInt(value.get(j).substring(4, 6), 16);
                    Integer data4 = Integer.parseInt(value.get(j).substring(6, 8), 16);
                    Double integer = CommonMethod.calculationParameter(data1, data2, data3, data4);
                    System.out.println(integer);
                }
            } else {
                for (int j = 1; j < value.size() - 1; j++) {
                    Integer p1 = Integer.parseInt(value.get(j).substring(0, 2), 16);
                    Integer p2 = Integer.parseInt(value.get(j).substring(2, 4), 16);
                    Integer p3 = Integer.parseInt(value.get(j).substring(4, 6), 16);
                    Integer p4 = Integer.parseInt(value.get(j).substring(6, 8), 16);
                    Double integer = CommonMethod.calculationParameter(p1, p2, p3, p4);
                    System.out.println(integer);
                }
            }
            i++;
        }


        }*/



        /*for (int j = 0; j < retuenList.size(); j++) {

            String frameHead = retuenList.get(j).substring(0, 2);
            String frameTail = retuenList.get(j).substring(2, retuenList.get(j).length());
            //替换 BB\BA
            if (frameTail.contains("BB")) {
                retuenList.set(j, frameHead + frameTail.replaceAll("BB", "BA01"));

            } else if (frameTail.contains("BA")) {
                retuenList.set(j, frameHead + frameTail.replaceAll("BA", "BA00"));
            }

        }*/
    }


    /**
     * 根据传入的MAP获取通道数据  (32位)
     *
     * @param map
     * @return
     */

    public static List<String> getPassagewayDataByMap(Map<String, List<String>> map) {

        List<String> dataList = new ArrayList<>();
        List<String> value = new ArrayList<>();
        Iterator<Map.Entry<String, List<String>>> iterator = map.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, List<String>> next = iterator.next();
            if (Integer.valueOf(next.getKey().substring(5, 6)) < 10) {
                for (String s : next.getValue()) {
                    value.add(s);
                }
            }
        }
        Iterator<String> data = value.iterator();
        while (data.hasNext()) {
            String key = data.next();
            if (key.length() < 8) {
                data.remove();
            } else {
                dataList.add(key);
            }
        }
        return dataList;
    }

    /**
     * 计算参数   计算公式 (p1+p2*256+p3*256*256+p4*256*256*256)/10
     *
     * @return
     */
    public static Double calculationParameter(String parameter) {
        Integer p1 = Integer.parseInt(parameter.substring(0, 2), 16);
        Integer p2 = Integer.parseInt(parameter.toString().substring(2, 4), 16);
        Integer p3 = Integer.parseInt(parameter.toString().substring(4, 6), 16);
        Integer p4 = Integer.parseInt(parameter.toString().substring(6, 8), 16);
        return Double.valueOf(p1 + p2 * 256 + p3 * 256 * 256 + p4 * 256 * 256 * 256);
    }


    public static String byParameterGetTime(String value) {
        Integer i1 = Integer.parseInt(value.substring(0, 2), 16);
        Integer i2 = Integer.parseInt(value.substring(2, 4), 16);
        Integer i3 = Integer.parseInt(value.substring(4, 6), 16);
        Integer i4 = Integer.parseInt(value.substring(6, 8), 16);
        Long result = Long.valueOf((i4 * 256 * 256 * 256) + (i3 * 256 * 256) + (i2 * 256) + i1) - 28631 - 167;
        return CommonMethod.TimeStamp2Date(result.toString());
    }


    /**
     * 将时间戳转换为时间
     *
     * @param str
     * @return
     */
    public static String stampToDate(String str) {
        String res;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        long lt = new Long(str);
        Date date = new Date(lt);
        res = simpleDateFormat.format(date);
        return res;
    }

    /**
     * unix时间戳转换为北京时间
     *
     * @param timestampString
     * @return date
     */
    public static String TimeStamp2Date(String timestampString) {
        Long timestamp = Long.parseLong(timestampString) * 1000;
        String date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(timestamp));
        return date;
    }


    //根据map的value获取map的key
    public static String getKey(Map<String, String> map, String value) {
        String key = "";
        for (Map.Entry<String, String> entry : map.entrySet()) {
            if (value.equals(entry.getValue())) {
                key = entry.getKey();
            }
        }
        return key;
    }

    //根据map的key删除值
    public static Map<String, Object> removeBykey(Map<String, Object> map, String key) {

        for (Iterator<Map.Entry<String, Object>> iterator = map.entrySet().iterator(); iterator.hasNext(); ) {

            if (iterator.next().getKey().equals(key)) {
                iterator.remove();
            }
        }
        return map;
    }

    /**
     * 获取map中第一个key值
     *
     * @param map 数据源
     * @return
     */
    public static String getKeyOrNull(Map<String, List<String>> map) {
        String obj = null;
        for (Map.Entry<String, List<String>> entry : map.entrySet()) {
            obj = entry.getKey();
            if (obj != null) {
                break;
            }
        }
        return obj;
    }


    // 删除ArrayList中重复元素，保持顺序
    public static List removeDuplicateWithOrder(List list) {
        List arrList = new ArrayList(list);
        Set set = new HashSet();
        List newList = new ArrayList();
        for (Iterator iter = arrList.iterator(); iter.hasNext(); ) {
            Object element = iter.next();
            if (set.add(element))
                newList.add(element);
        }
        arrList.clear();
        arrList.addAll(newList);
        System.out.println(" remove duplicate " + list);
        return arrList;

    }

    /**
     * List分页
     *
     * @param <T>
     * @param list
     * @param pageSize
     * @return
     */
    public static <T> List<List<T>> splitList(List<T> list, int pageSize) {

        int listSize = list.size();                                                           //list的大小
        int page = listSize / pageSize;                      //页数
        if (listSize % pageSize > 0) {
            page = page + 1;
        }
        List<List<T>> listArray = new ArrayList<List<T>>();              //创建list数组 ,用来保存分割后的list
        for (int i = 0; i < page; i++) {                                                         //按照数组大小遍历
            List<T> subList = new ArrayList<T>();                               //数组每一位放入一个分割后的list
            for (int j = 0; j < listSize; j++) {                                                 //遍历待分割的list
                int pageIndex = (j + pageSize) / pageSize;   //当前记录的页码(第几页)
                if (pageIndex == (i + 1)) {                                               //当前记录的页码等于要放入的页码时
                    subList.add(list.get(j));                                               //放入list中的元素到分割后的list(subList)
                }

                if (j == (i + 1) * pageSize) {                               //当放满一页时退出当前循环
                    break;
                }
            }
            listArray.add(subList);                                                         //将分割后的list放入对应的数组的位中
        }
        return listArray;
    }


    // 删除ArrayList中重复元素，保持顺序
    public static void removeDuplicateWithOrderNoVoid(List list) {
        Set set = new HashSet();
        List newList = new ArrayList();
        for (Iterator iter = list.iterator(); iter.hasNext(); ) {
            Object element = iter.next();
            if (set.add(element))
                newList.add(element);
        }
        list.clear();
        list.addAll(newList);
        System.out.println(" remove duplicate " + list);
    }

    /**
     * 获取map中第一个数据值
     *
     * @param map 数据源
     * @return
     */
    public static Object getFirstOrNull(Map<String, Object> map) {
        Object obj = null;
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            obj = entry.getValue();
            if (obj != null) {
                break;
            }
        }
        return obj;
    }

    /**
     * 十进制转十六进制
     *
     * @param n
     * @return
     */
    public static String intToHex(int n) {
        StringBuffer s = new StringBuffer();
        String a;
        char[] b = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
        while (n != 0) {
            s = s.append(b[n % 16]);
            n = n / 16;
        }
        a = s.reverse().toString();
        return a;
    }

    /**
     * 字符串每两位一组转化为数组  String 转byte数组
     *
     * @param data
     * @return
     */
    public static byte[] str2HexByte(String data) {
        if (1 == data.length() % 2)
            return null;
        else {
            byte[] li = new byte[data.length() / 2];
            for (int i = 0; i < data.length(); i += 2) {
                int cp1 = data.codePointAt(i);
                int cp2 = data.codePointAt(i + 1);
                li[i / 2] = (byte) (asc2Hex(cp1) << 4 | asc2Hex(cp2));
            }
            return li;
        }
    }

    /**
     * 字符asc码数值转为byte数值
     *
     * @param data
     * @return
     */
    public static int asc2Hex(int data) {
        int li = 0;
        if (data >= 0X30 && data <= 0X39) {//0-9
            li = data - 0X30;
        } else if (data >= 0X41 && data <= 0X5A) {//A-F
            li = data - 0X37;
        } else if (data >= 0X61 && data <= 0X7A) {//a-f
            li = data - 0X57;
        } else {
            li = data;
        }
        return li;
    }


    /**
     * 计算求和码
     *
     * @param code
     * @return
     */
    public static String summationCode(String code) {

        String substring = code.substring(4, code.length());
        int[] data = new int[substring.length() / 2];
        int index = 0;
        for (int i = 0; i < substring.length() - 1; index++) {

            for (int j = i; j < substring.length() - 1; ) {
                j += 2;
                data[index] = Integer.parseInt(substring.substring(i, j), 16);
                break;
            }
            i += 2;
        }
        int v = 0;

        for (int k = 0; k < data.length; k++) {
            v ^= data[k];
        }
        return code += intToHex(v);
    }

    /**
     * 字符串位数不够在前面补零
     *
     * @param str
     * @param strLength
     * @return
     */
    public static String addZeroForNum(String str, int strLength) {
        int strLen = str.length();
        StringBuffer sb = null;
        while (strLen < strLength) {
            sb = new StringBuffer();
            sb.append("0").append(str);// 左补0
            // sb.append(str).append("0");//右补0
            str = sb.toString();
            strLen = str.length();
        }
        return str;
    }


    /**
     * 获取日期当中的数字进行转换十六进制
     */
    public static String getDateString(String date) {
        //2019-10-15 14:31:31
        List<String> dateList = new ArrayList<>();
        dateList.add(date.substring(2, 4));//获取年份
        dateList.add(date.substring(5, 7));
        dateList.add(date.substring(8, 10));
        dateList.add(date.substring(11, 13));
        dateList.add(date.substring(14, 16));
        dateList.add(date.substring(17, 19));
        //获取数字也可用两层循环来获取 index+3
        String dateString = "";
        for (int i = 0; i < dateList.size(); i++) {
            dateString += addZeroForNum(intToHex(Integer.valueOf(dateList.get(i))), 2);
        }

        return dateString;
    }

    /**
     * 传入操作指令 计算求和码 返回操作指令字符串
     *
     * @param code          操作指令
     * @param no            采集仪编号
     * @param num           间隔时间
     * @param openingOrClos 开关指令
     * @return
     */
    public static byte[] getCode(Integer code, String no, int num, int openingOrClos) {
        String defaultCode = "BB0481";
        defaultCode += "C" + code;
        defaultCode += addZeroForNum(String.valueOf(no), 2);
        try {
            switch (code) {
                case 1://时间对齐
                    defaultCode += "01";
                    defaultCode += getDateString(CommonMethod.getDate());
                    break;
                case 3://采集一次数据
                    defaultCode += "00";
                    break;
                case 4://采集时间间隔
                    defaultCode += "01" + addZeroForNum(String.valueOf(openingOrClos), 2);
                    defaultCode += addZeroForNum(intToHex(num), 8);//自动采集间隔数a
                    break;
                case 5://自动采集启停指令   openingOrClos  1  开   0 关   返回数据:BB0D81C51F010000000AFFFF000050
                    defaultCode += String.format("%2d", openingOrClos).replace(" ", "0");
                    break;
            }
            defaultCode = summationCode(defaultCode);//计算求和码
            defaultCode = defaultCode.replace(defaultCode.substring(2, 4), addZeroForNum(intToHex(defaultCode.substring(4, defaultCode.length()).length() / 2), 2).replace(" ", "0"));//计算帧长度
        } catch (Exception e) {
            e.printStackTrace();
        }
        byte[] bytes = str2HexByte(defaultCode);

        return bytes;
    }


    public static void main(String[] args) throws ParseException {



        // System.out.println(button3_Click(value,Axis_azimuth));

        /*Integer aaaaa = 1;
        Integer bbb = 1;
        System.out.println(aaaaa == bbb);


        float v = 9;

        System.out.println("是不是" + v);
        String aaa = "A8813030303030303435373435303731320200";


        String s2 = summationCode(aaa);


        String currentDate = CommonMethod.getCurrentDate() + " 00:00:00";

        Long datePoor = CommonMethod.getDatePoor(currentDate, CommonMethod.getDate());

        long l = 600 % 5;


        Integer j = 3;

        String ssss2 = addZeroForNum(intToHex(j), 2);


        String time = "2019-10-15 14:31:31";

        String substring = time.substring(2, 4);
        String substring1 = time.substring(5, 7);
        String substring2 = time.substring(8, 10);
        String substring3 = time.substring(11, 13);
        String substring4 = time.substring(14, 16);
        String substring5 = time.substring(17, 19);
        String date = CommonMethod.getDate();


        String replace = String.format("%2d", 1).replace(" ", "0");


        int a = 1440;
        String str = intToHex(a);


        int strLen = str.length();
        StringBuffer sb = null;
        while (strLen < 8) {
            sb = new StringBuffer();
            sb.append("0").append(str);// 左补0
            // sb.append(str).append("0");//右补0
            str = sb.toString();
            strLen = str.length();
        }


        String s = String.format("%.8f", intToHex(a));

        String defaultCode = "00000001";


        String s1 = CommonMethod.byParameterGetTime("134E285D");

        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        long epoch = 0;
        try {
            epoch = df.parse(CommonMethod.getDate()).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        String format = String.format("%08X", epoch);

        String str1 = String.valueOf(epoch);
        char[] chars = "0123456789ABCDEF".toCharArray();
        StringBuilder builder = new StringBuilder("");
        byte[] bs = str1.getBytes();
        int bit;
        for (int i = 0; i < bs.length; i++) {
            bit = (bs[i] & 0x0f0) >> 4;
            builder.append(chars[bit]);
            bit = bs[i] & 0x0f;
            builder.append(chars[bit]);
            builder.append(' ');
        }
        System.out.println(builder.toString().trim());


        String aaaa = "1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27,28,29,30,31,32";


        String data = "FFC000";


        String data1 = "42 42 30 34 46 46 43 30 31 46 0D 0A";*/


          /*  String a = "FF";
            String b = "C0";
            String c = "1F";
            int j = Integer.parseInt(a.substring(0, 2), 16);
            int i = Integer.parseInt(b.substring(0, 2), 16);
            int k = Integer.parseInt(c.substring(0, 2), 16);

            int i1 = i ^ j ^ k;


            byte[] bytess = b.getBytes();


            String s = byteArrayTurnHexString(bytess);*/




       /* Double value = 174000D;
        int m = 5;

        String format1 = String.format("%." + m + "f", value / Math.pow(10, m));
        Double aDouble = Double.parseDouble(format1);





        DecimalFormat    df   = new DecimalFormat("0.0000");

        String format2 = df.format(v);


        BigDecimal bd = new BigDecimal(format1);

        double v1 = bd.setScale(m, BigDecimal.ROUND_HALF_UP).doubleValue();
*/


        /* value =  new BigDecimal().setScale(m, BigDecim
        al.ROUND_HALF_UP).doubleValue();*/


        /*Long datePoor = CommonMethod.getDatePoor(CommonMethod.getDate(), time);*/






        /*String substring = CommonMethod.getDate().substring(0, 10);


        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        Date parse = null;

        try {
            parse = sdf.parse(substring);
            Long day = 1440 * 60000L * 15;
            Date newDate = new Date(parse.getTime() -day);
            String time = sdf.format(newDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }


        List<MeasuringSpotData> measuringSpotDataList = new ArrayList<>();



        Double index = 1d;
        Long branch = 60000L;
        int k=0;
        for (int i = 0; i < 15; i++) {
            for (int j = 0; j < 1440; j++) {

                MeasuringSpotData measuringSpot = new MeasuringSpotData();
                measuringSpot.setMeasuringSpotValue(index +k);
                k+=1;


                measuringSpot.setReceiveTime(time);
                measuringSpotDataList.add(measuringSpot);
            }
        }

        System.out.println();*/

/*
        MeasuringSpot measuringSpot = new MeasuringSpot();

        if(measuringSpot.getCalculatedValue() != null){
            System.out.println("金额为零");
        }





        //77D33E5D
        String time = "2019-07-30 14:48:00";
        SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat sdf = new SimpleDateFormat("MMM d, yyyy K:m:s a", Locale.ENGLISH);
        Date d2 = null;
        try {
            d2 = sdf2.parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Long ab = d2.getTime() / 1000;
        String a = Long.toHexString(ab);

        String value = "486B405D";


        Integer i1 = Integer.parseInt(value.substring(0, 2), 16);
        Integer i2 = Integer.parseInt(value.substring(2, 4), 16);
        Integer i3 = Integer.parseInt(value.substring(4, 6), 16);
        Integer i4 = Integer.parseInt(value.substring(6, 8), 16);


        Long result = Long.valueOf((i4 * 256 * 256 * 256) + (i3 * 256 * 256) + (i2 * 256) + i1) - 28631;
        String dateTime = CommonMethod.TimeStamp2Date(result.toString());


        long b = getDatePoor("2019-09-01 10:10:10", "2019-09-02 10:20:10");
        String s = getDistanceTime("2019-09-01 10:10:10", "2019-09-02 10:20:10");

        // String timeMinuteSubtraction = getTimeMinuteSubtraction("2019-09-01 10:10:10");

        String str = "yulv B1B 123456BB05 # yulv@21cn.com";


        String data = "3839383630333137323430323838333131303339BB0584C91F2072BB4403C91F77D33E5D4C4400000000000000000000B659000000000000000000000000000000000000000000000000000000000000000000000000000000000000923C00005BBB4402C91F00000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000D4BB0801C91F00000000D7";

        List<Map<String, List<String>>> maps = dataSegmentationAndReplace(data);
        List<String> passagewayDataByMap = new ArrayList<>();
        for (
                Map<String, List<String>> map : maps) {
            passagewayDataByMap = getPassagewayDataByMap(map);
        }


        System.out.println();*/

    }

}
