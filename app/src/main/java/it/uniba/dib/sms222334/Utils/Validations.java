package it.uniba.dib.sms222334.Utils;

import android.util.Patterns;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Validations {
    public static boolean isValidName(String name) {
        return name.matches("[a-zA-Z]+");
    }

    public static boolean isValidSurname(String surname) {
        return surname.matches("[a-zA-Z]+");
    }

    public static boolean isValidEmail(String email) {
        return (!email.isEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches());
    }
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

}
