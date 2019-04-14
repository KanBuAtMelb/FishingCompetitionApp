package com.example.fishingtest.Model;

import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

public class Common {

    public Common() {
    }

    private final static String TAG = "Common";

    public static Competition currentItem = null;

    // Used in Competition and User classes
    public static String NA = "NA";


    // Used in User class
    public static String user_member = "Member";
    public static String user_admin = "Administrator";


    // Convert Time String "dd/mm/yyyy" to Date in AEST time zone, e.g. "14/03/2019 20:50 GMT+08:00"
    public static long timeToCompStart(String compTime_str){

        long diff = -1L;
        DateFormat df = new SimpleDateFormat("dd/MM/yy HH:mm z");
        df.setTimeZone(TimeZone.getTimeZone("GMT"));
        Date compDate = new Date();
        try {
            // Convert string into Date
            compDate = df.parse(compTime_str);
            System.out.println("Converted by df2 = " + df.format(compDate));
        } catch (Exception e) {
            e.printStackTrace();
        }

        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT"),Locale.getDefault());
        Date currentLocalTime = calendar.getTime();
        TimeZone uTimeZone= calendar.getTimeZone();

        if(uTimeZone.inDaylightTime(new Date())) {
            diff = compDate.getTime() -(currentLocalTime.getTime()+uTimeZone.getDSTSavings());
        }else {
            diff = compDate.getTime() -currentLocalTime.getTime();
        }

//        long diffSeconds = diff / 1000;
//        long diffMinutes = diff / (60 * 1000) % 60;
//        long diffHours = diff / (60 * 60 * 1000) % 24;
//        long diffDays = diff / (24 * 60 * 60 * 1000);


        return diff;

    };


}
