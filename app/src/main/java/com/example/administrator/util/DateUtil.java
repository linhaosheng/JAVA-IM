package com.example.administrator.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


/**
 * Created by Administrator on 2015/9/19.
 */
public class DateUtil {
    private static final String FORMAT="yyyy-MM-dd HH:mm:ss";

    public static Date str2Date(String str){
        return str2Date(str,null);
    }

    /**
     * 解析日期
     * @param str
     * @param format
     * @return
     */

    public static Date str2Date(String str,String format){
        if(str==null||str.length()==0){
            return null;
        }
            if(format==null||format.length()==0){
                format=FORMAT;
            }
        Date date=null;
        try{
           // SimpleDateFormat 允许以为日期-时间格式化选择任何用户指定的方式启动
             SimpleDateFormat sdf=new SimpleDateFormat(format);
            date=sdf.parse(str);
        }catch (Exception e){
            e.printStackTrace();
        }
        return date;
    }

    /**
     * 获取一个Calendar日期对象
     * @param str
     * @param format
     * @return
     */
    public static Calendar str2Calender(String str,String format){

        Date date=str2Date(str,format);
        if(date==null){
            return null;
        }
        Calendar calendar=Calendar.getInstance();
        calendar.setTime(date);
        return calendar;
    }

    public static String date2Str(Calendar calendar,String format){
        if(calendar==null){
            return null;
        }
        return date2Str(calendar.getTime(),format);
    }

    public static String date2Str(Date date,String format){
        if(date==null){
            return null;
        }
        if(format==null || format.length()==0){
            format=FORMAT;
        }
        SimpleDateFormat sdf=new SimpleDateFormat(format);
        String s=sdf.format(date);
        return s;
    }
    /**
     * 获取到当天的时间
     * @return
     */
    public static String getCurDate(){
        Calendar calendar=Calendar.getInstance();
        calendar.setTime(new Date());
        return  calendar.get(Calendar.YEAR)+"-"+(calendar.get(Calendar.MONTH)+1) +"-"
                + calendar.get(Calendar.DAY_OF_MONTH) +"-"+calendar.get(Calendar.HOUR_OF_DAY)+ ":"
                + calendar.get(Calendar.MINUTE)+ ":" +calendar.get(Calendar.SECOND);
    }
}
