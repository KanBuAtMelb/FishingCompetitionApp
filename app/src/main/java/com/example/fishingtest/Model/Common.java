package com.example.fishingtest.Model;

import android.util.Log;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

public class Common {

    public Common() {
    }

    private final static String TAG = "Common";

    public static Competition currentItem = null;

//    public static List<Competition> compList = new ArrayList<>();
//    public static List<Competition> compsBydate = new ArrayList<>();
//    public static List<Competition> compsByName = new ArrayList<>();
//    public static List<Competition> compsByReward = new ArrayList<>();
//    public static List<Competition> compsByType = new ArrayList<>();


    // Used in Competition and User classes
    public static String NA = "NA";
    public static Date NA_Date = new Date(2019,1,1);
    public static int NA_Integer = -1;
    public static int EMPTY_SPINNER = 0;
    public static String EMPTY = "";


    // Used in User class
    public static String user_member = "Member";
    public static String user_admin = "Administrator";


    // Check the time to the competition date
    // competition date given in the format of  "14/03/2019 20:50 GMT+08:00"
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


    public static Date formattingDate(String compDate){
        // For Competition info
        DateFormat df = new SimpleDateFormat("dd/MM/yy HH:mm z");
        df.setTimeZone(TimeZone.getTimeZone("GMT"));
        Date date = new Date();
        try {
        // Convert string into Date
        date = df.parse("20/04/19 23:50 GMT+08:00");
//        System.out.println("Converted by df2 = " + df.format(compDate));
        } catch (Exception e) {
            e.printStackTrace();
        }

      return date;
    };

}
