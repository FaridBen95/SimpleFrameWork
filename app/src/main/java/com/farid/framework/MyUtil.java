package com.farid.framework;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class MyUtil {
    public static final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

    public static String caller(){
        try{
            throw new Exception("the caller is");
        }
        catch (Exception e){
            return e.getStackTrace()[0].getFileName().replace(".java","");
        }
    }

    public static String getCurrentDate(){
        SimpleDateFormat gmtFormat = new SimpleDateFormat();
        gmtFormat.applyPattern(DEFAULT_DATE_FORMAT);
        TimeZone gmtTime =  TimeZone.getDefault();
        gmtFormat.setTimeZone(gmtTime);
        return gmtFormat.format(new Date());
    }
}
