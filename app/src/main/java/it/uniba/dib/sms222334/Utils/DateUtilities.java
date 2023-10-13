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
            return stringResources=context.getString(R.string.born_today);

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
}
