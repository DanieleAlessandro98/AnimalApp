package it.uniba.dib.sms222334.Utils;

import android.content.Context;
import android.util.Log;

import androidx.annotation.IntRange;

import java.util.Calendar;
import java.util.Date;

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
     * @param minimunAgeYear minimun year age accepted.
     *
     * @return ageInMonth age expressed in text.
     */
    public static Boolean validateAge(Date date,@IntRange(from = 0, to = 100) int minimunAgeYear) {
        Calendar birthdate = Calendar.getInstance();
        birthdate.setTime(date);

        Calendar today = Calendar.getInstance();

        int age = (today.get(Calendar.YEAR) - birthdate.get(Calendar.YEAR)) * 365;

        age += today.get(Calendar.DAY_OF_YEAR) - birthdate.get(Calendar.DAY_OF_YEAR);

        if(age<0)
            return false;

        age=(int)Math.floor(age/365);

        return age >= minimunAgeYear;

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

    public static String parseAgeDate(Date date, Context context) {
        if (date == null) {
            return "";
        }

        Calendar today = Calendar.getInstance();
        today.setTime(new Date());

        Calendar birthDate = Calendar.getInstance();
        birthDate.setTime(date);

        int years = today.get(Calendar.YEAR) - birthDate.get(Calendar.YEAR);
        int months = today.get(Calendar.MONTH) - birthDate.get(Calendar.MONTH);
        int days = today.get(Calendar.DAY_OF_MONTH) - birthDate.get(Calendar.DAY_OF_MONTH);

        if (days < 0) {
            months--;
            today.add(Calendar.MONTH, -1); // Move one month back
            days += today.getActualMaximum(Calendar.DAY_OF_MONTH); // Add the maximum days in the previous month
        }

        if (months < 0) {
            years--;
            months += 12; // Add 12 months to get a positive value
        }

        StringBuilder ageString = new StringBuilder();

        if (years > 0) {
            ageString.append(years);
            ageString.append(" ");
            ageString.append(years == 1 ? context.getString(R.string.year) : context.getString(R.string.years));
        }

        if (months > 0) {
            if (ageString.length() > 0) {
                ageString.append(" ");
            }
            ageString.append(months);
            ageString.append(" ");
            ageString.append(months == 1 ? context.getString(R.string.month) : context.getString(R.string.months));
        }

        if (days > 0) {
            if (ageString.length() > 0) {
                ageString.append(" ");
            }
            ageString.append(days);
            ageString.append(" ");
            ageString.append(days == 1 ? context.getString(R.string.day) : context.getString(R.string.days));
        }

        return ageString.toString();
    }
}
