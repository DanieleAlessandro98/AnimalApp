package it.uniba.dib.sms222334.Utils;

import android.content.Context;
import android.util.Patterns;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import it.uniba.dib.sms222334.R;

public class Validations {
    public static boolean isValidName(String name) {
        return name.matches("[a-zA-Z]+");
    }

    public static boolean isValidSurname(String surname) {
        return surname.matches("[a-zA-Z]+");
    }

    public static boolean isValidEmail(String email) {
        return (!email.isEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches());
    } //todo: creare controllo sull'unicità dell'email
    public static boolean isValidPassword(String password) {
        return (password.length() >= 6);
    }
    public static boolean isValidDateBirth(Date dateBirth) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        dateFormat.setLenient(false);

        try {
            Date minDate = dateFormat.parse("01/01/1900");
            Date maxDate = new Date();

            if (dateBirth.before(minDate) || dateBirth.after(maxDate)) {
                return false;
            }

            return true;
        } catch (ParseException e) {
            return false;
        }
    }
    public static boolean isValidPhone(String phone) {
        return phone.matches("[0-9]+");
    }

    public static boolean isValidCompanyName(String company) {
        return (company.length() >= 4 && company.matches("[a-zA-Z]+"));
    }

    public static boolean isValidDescription(String reportDescription) {
        return (reportDescription.length() >= 2);
    }

    public static int isValidAgeString(String ageString, Context context) {
        if (ageString.isEmpty())
            return 0;

        String[] parts = ageString.split(" ");
        if (parts.length != 2)
            return 1;

        try {
            Integer.parseInt(parts[0]);
        } catch (NumberFormatException e) {
            return 2;
        }

        String ageUnit = parts[1].toLowerCase();

        String[] validUnits = new String[]{
                context.getString(R.string.day).toLowerCase(),
                context.getString(R.string.days).toLowerCase(),
                context.getString(R.string.month).toLowerCase(),
                context.getString(R.string.months).toLowerCase(),
                context.getString(R.string.year).toLowerCase(),
                context.getString(R.string.years).toLowerCase()
        };

        for (String validUnit : validUnits) {
            if (ageUnit.equals(validUnit))
                return 0;
        }

        return 3;
    }

    public static boolean isValidBedsRequest(String beds) {
        if (beds == null || beds.isEmpty())
            return false;

        try {
            int bedsValue = Integer.parseInt(beds);
            if (bedsValue <= 0)
                return false;

            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

}
