package it.uniba.dib.sms222334.Presenters;

import android.util.Patterns;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import it.uniba.dib.sms222334.Database.AnimalAppDB;
import it.uniba.dib.sms222334.Fragmets.ProfileFragment;
import it.uniba.dib.sms222334.Models.Private;
import it.uniba.dib.sms222334.Models.SessionManager;
import it.uniba.dib.sms222334.Models.User;
import it.uniba.dib.sms222334.Utils.UserRole;

public class UserPresenter {

    private ProfileFragment profileView;
    private User profileModel;

    public UserPresenter(ProfileFragment profileView) {
        this.profileView = profileView;
        this.profileModel = SessionManager.getInstance().getCurrentUser();
    }

    public void initUserData() {
        UserRole userRole = profileModel.getRole();

        switch (userRole) {
            case PRIVATE:
                profileView.onInitPrivateData((Private) profileModel);
                break;

            case PUBLIC_AUTHORITY:
            case VETERINARIAN:
                // ...
                break;
        }
    }

    public void updateProfile(String name, String surname, Date birthDate, String taxID, long phone, String email, String password) {
        if (!isValidName(name)) {
            profileView.showInvalidInput(1);
            return;
        }
        if (!isValidSurname(surname)) {
            profileView.showInvalidInput(2);
            return;
        }
        if (!isValidDateBirth(birthDate)) {
            profileView.showInvalidInput(3);
            return;
        }
        if (!isValidEmail(email)) {
            profileView.showInvalidInput(4);
            return;
        }
        if (!isValidPassword(password)) {
            profileView.showInvalidInput(5);
            return;
        }

        UserRole userRole = profileModel.getRole();

        switch (userRole) {
            case PRIVATE:
                Private.Builder updatedPrivate=Private.Builder.
                        create(
                                profileModel.getFirebaseID(),
                                name,
                                email) //TODO: photo
                        .setPassword(password)
                        .setPhone(phone)
                        .setSurname(surname)
                        .setBirthDate(birthDate)
                        .setTaxIdCode(taxID);

                SessionManager.getInstance().updateCurrentUser(updatedPrivate.build());
                this.profileModel = SessionManager.getInstance().getCurrentUser();
                this.profileModel.updateProfile();
                break;

            case PUBLIC_AUTHORITY:
            case VETERINARIAN:
                // ...
                break;
        }

        profileView.showUpdateSuccessful();
    }

    private boolean isValidName(String name) {
        return name.matches("[a-zA-Z]+");
    }

    private boolean isValidSurname(String surname) {
        return surname.matches("[a-zA-Z]+");
    }

    private boolean isValidEmail(String email) {
        return (!email.isEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches());
    }

    private boolean isValidPassword(String password) {
        return (password.length() >= 6);
    }

    private boolean isValidDateBirth(Date dateBirth) {
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
}
