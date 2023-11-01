package it.uniba.dib.sms222334.Utils;

import android.content.Context;
import android.content.res.Resources;

import androidx.annotation.IntRange;
import androidx.annotation.NonNull;

import com.google.firebase.Timestamp;

import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import it.uniba.dib.sms222334.R;

public final class DateUtilities {

    public static final String TAG="DateUtilities";

    /**
     * Method to extract age expressed in text.
     *
     * @param date birthdate.
     * @param context context of the application.
     *
     * @return ageInMonth age expressed in text.
     */
    public static String  calculateAge(Date date, Context context){
        Calendar birthdate = Calendar.getInstance();
        Calendar today = Calendar.getInstance();

        String stringResources;

        birthdate.setTime(date);

        int age = (today.get(Calendar.YEAR) - birthdate.get(Calendar.YEAR))*365;

        age+=today.get(Calendar.DAY_OF_YEAR) - birthdate.get(Calendar.DAY_OF_YEAR);

        if(age<0){
            return "";
        }

        if (age==0){
            return context.getString(R.string.born_today);

        } else if (age<30) {
            stringResources=age==1?context.getString(R.string.day):context.getString(R.string.days);
        }
        else if (age<365) {
            age=(int)Math.floor(age/30);
            stringResources=age==1?context.getString(R.string.month):context.getString(R.string.months);
        }
        else{
            age=(int)Math.floor(age/365);
            stringResources=age==1?context.getString(R.string.year):context.getString(R.string.years);
        }

        return age+" "+stringResources;
    }

    /**
     * Method to verify validity of date based on the minimun age
     *
     * @param date birthdate.
     * @param minimunAgeDays minimun days age accepted.
     *
     * @return if boolean check.
     */
    public static Boolean validateDate(Date date, @IntRange(from = 0, to = 36500) int minimunAgeDays) {
        Calendar birthdate = Calendar.getInstance();
        birthdate.setTime(date);

        Calendar today = Calendar.getInstance();

        int age = (today.get(Calendar.YEAR) - birthdate.get(Calendar.YEAR)) * 365;

        age += today.get(Calendar.DAY_OF_YEAR) - birthdate.get(Calendar.DAY_OF_YEAR);

        return age >= minimunAgeDays;

    }

    public static Date parseAgeString(String ageString, Context context) {
        if (ageString.isEmpty())
            return null;

        Calendar today = Calendar.getInstance();

        int ageValue = 0;
        String[] parts = ageString.split(" ");
        if (parts.length != 2)
            return null;

        try {
            ageValue = Integer.parseInt(parts[0]);
        } catch (NumberFormatException e) {
            return null;
        }

        String ageUnit = parts[1].toLowerCase();

        if (ageUnit.equals(context.getString(R.string.day).toLowerCase()) || ageUnit.equals(context.getString(R.string.days).toLowerCase()))
            today.add(Calendar.DAY_OF_YEAR, -ageValue);
        else if (ageUnit.equals(context.getString(R.string.month).toLowerCase()) || ageUnit.equals(context.getString(R.string.months).toLowerCase()))
            today.add(Calendar.MONTH, -ageValue);
        else if (ageUnit.equals(context.getString(R.string.year).toLowerCase()) || ageUnit.equals(context.getString(R.string.years).toLowerCase()))
            today.add(Calendar.YEAR, -ageValue);
        else
            return null;

        return today.getTime();
    }

    public static String getTimeAgoString(@NonNull Timestamp timestamp,Context context) {
        Timestamp now=Timestamp.now();

        long timeDifference=now.getSeconds()-timestamp.getSeconds();

        long seconds = TimeUnit.SECONDS.toSeconds(timeDifference);
        long minutes = TimeUnit.SECONDS.toMinutes(timeDifference);
        long hours = TimeUnit.SECONDS.toHours(timeDifference);
        long days = TimeUnit.SECONDS.toDays(timeDifference);
        long weeks = days/7;
        long months = weeks/4;
        long years = months/12;

        if(seconds <= 1){
            return context.getString(R.string.now);
        }
        else if (seconds < 60) {
            return seconds+" "+context.getString(R.string.seconds)+" "+context.getString(R.string.ago);
        }
        else if (minutes == 1) {
            return minutes+" "+context.getString(R.string.minute)+" "+context.getString(R.string.ago);
        }
        else if (minutes < 60) {
            return minutes+" "+context.getString(R.string.minutes)+" "+context.getString(R.string.ago);
        }
        else if (hours == 1) {
            return hours+" "+context.getString(R.string.hour)+" "+context.getString(R.string.ago);
        }
        else if (hours < 24) {
            return hours+" "+context.getString(R.string.hours)+" "+context.getString(R.string.ago);
        }
        else if(days == 1){
            return days+" "+context.getString(R.string.day)+" "+context.getString(R.string.ago);
        }
        else if(days < 30){
            return days+" "+context.getString(R.string.days)+" "+context.getString(R.string.ago);
        }
        else if(weeks == 1){
            return weeks+" "+context.getString(R.string.week)+" "+context.getString(R.string.ago);
        }
        else if(weeks < 4){
            return weeks+" "+context.getString(R.string.weeks)+" "+context.getString(R.string.ago);
        }
        else if(months == 1){
            return months+" "+context.getString(R.string.month)+" "+context.getString(R.string.ago);
        }
        else if(months < 12){
            return months+" "+context.getString(R.string.months)+" "+context.getString(R.string.ago);
        }
        else if(years == 1){
            return years+" "+context.getString(R.string.year)+" "+context.getString(R.string.ago);
        }
        else{
            return years+" "+context.getString(R.string.years)+" "+context.getString(R.string.ago);
        }
    }

}
