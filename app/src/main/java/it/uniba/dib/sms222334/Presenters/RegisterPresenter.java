package it.uniba.dib.sms222334.Presenters;

import android.util.Patterns;

import java.util.Date;

import com.google.firebase.firestore.GeoPoint;

import it.uniba.dib.sms222334.Database.Dao.Authentication.AuthenticationDao;
import it.uniba.dib.sms222334.Database.Dao.User.UserCallback;
import it.uniba.dib.sms222334.Fragmets.RegisterFragment;
import it.uniba.dib.sms222334.Models.Private;
import it.uniba.dib.sms222334.Models.PublicAuthority;
import it.uniba.dib.sms222334.Models.Veterinarian;
import it.uniba.dib.sms222334.Utils.DateUtilities;
import it.uniba.dib.sms222334.Utils.Validations;


public class RegisterPresenter implements UserCallback.UserRegisterCallback{
    private final String TAG="RegisterPresenter";
    private RegisterFragment registerFragment;

    public RegisterPresenter(RegisterFragment fragment){
        this.registerFragment = fragment;
    }

    public void checkEmail(String email, AuthenticationDao.FindSameEmail emailfind){
        AuthenticationDao authenticationDao = new AuthenticationDao();
        authenticationDao.isEmailUnique(email, emailfind);
    }

    //Prendo i dati dal fragment e li Controllo
    public void checkPrivateRegistration(String name, String surname, String email, String password, Long phone, Date birthDate, String taxIDCode, String location){

        //Richiama il metodo isValidName e in se il controllo non va a buon fine richiama il messaggio di errore creato nel registerFragment
        if (!Validations.isValidName(name)) {
            registerFragment.showInvalidName();
            return;
        }

        //Richiama il metodo isValidSurname e in se il controllo non va a buon fine richiama il messaggio di errore creato nel registerFragment
        if (!Validations.isValidSurname(surname)) {
            registerFragment.showInvalidSurname();
            return;
        }

        //Richiama il metodo isvalidEmail e in se il controllo non va a buon fine richiama il messaggio di errore creato nel registerFragment
        if (!Validations.isValidEmail(email)) {
            registerFragment.showInvalidEmail();
            return;
        }

        //Richiama il metodo isValidPassword e in se il controllo non va a buon fine richiama il messaggio di errore creato nel registerFragment
        if (!Validations.isValidPassword(password)) {
            registerFragment.showInvalidPassword();
            return;
        }

        String phoneStr = phone.toString();
        //Richiama il metodo isvalidEmail e in se il controllo non va a buon fine richiama il messaggio di errore creato nel registerFragment
        if (!Validations.isValidPhone(phoneStr)) {
            registerFragment.showInvalidPhone();
            return;
        }

        //Richiama il metodo isValidDateBirth e in se il controllo non va a buon fine richiama il messaggio di errore creato nel registerFragment
        if (!Validations.isValidDateBirth(birthDate)) {
            registerFragment.showInvalidDateBirth();
            return;
        }

        GeoPoint locationValue = Validations.isValidLocation(location, registerFragment.getContext());
        if (locationValue == null) {
            registerFragment.showInvalidLocation();
            return;
        }

        // Crea un'istanza di Private utilizzando il Builder
        Private privateUser = Private.Builder.create("", name, email)
                .setSurname(surname)
                .setPassword(password)
                .setPhone(phone)
                .setBirthDate(birthDate)
                .setTaxIdCode(taxIDCode)
                .setLocation(locationValue)
                .build();

        // Tutti i dati sono validi, procedi con la registrazione
        privateUser.registerPrivate( this);
        }

    public void checkAuthorityRegistration(String companyName, String email, String password, Long phone, String location){
        //Richiama il metodo isValidCompanyName e in se il controllo non va a buon fine richiama il messaggio di errore creato nel registerFragment
        if (!Validations.isValidCompanyName(companyName)) {
            registerFragment.showInvalidCompanyName();
            return;
        }

        //Richiama il metodo isvalidEmail e in se il controllo non va a buon fine richiama il messaggio di errore creato nel registerFragment
        if (!Validations.isValidEmail(email)) {
            registerFragment.showInvalidEmail();
            return;
        }

        //Richiama il metodo isValidPassword e in se il controllo non va a buon fine richiama il messaggio di errore creato nel registerFragment
        if (!Validations.isValidPassword(password)) {
            registerFragment.showInvalidPassword();
            return;
        }

        String phoneStr = phone.toString();
        //Richiama il metodo isValidPhone e in se il controllo non va a buon fine richiama il messaggio di errore creato nel registerFragment
        if (!Validations.isValidPhone(phoneStr)) {
            registerFragment.showInvalidPhone();
            return;
        }

        GeoPoint locationValue = Validations.isValidLocation(location, registerFragment.getContext());
        if (locationValue == null) {
            registerFragment.showInvalidLocation();
            return;
        }

        // Crea un'istanza di PublicAuthority utilizzando il Builder
        PublicAuthority authorityUser = PublicAuthority.Builder.create("", companyName, email)
                .setPassword(password)
                .setPhone(phone)
                .setLocation(locationValue)
                .build();

        // Tutti i dati sono validi, procedi con la registrazione
        authorityUser.registerAuthority(this);
    }

    public void checkVeterinarianRegistration(String companyName, String email, String password, Long phoneB, String location){
        //Richiama il metodo isValidCompanyName e in se il controllo non va a buon fine richiama il messaggio di errore creato nel registerFragment
        if (!Validations.isValidCompanyName(companyName)) {
            registerFragment.showInvalidCompanyName();
            return;
        }

        //Richiama il metodo isvalidEmail e in se il controllo non va a buon fine richiama il messaggio di errore creato nel registerFragment
        if (!Validations.isValidEmail(email)) {
            registerFragment.showInvalidEmail();
            return;
        }

        //Richiama il metodo isValidPassword e in se il controllo non va a buon fine richiama il messaggio di errore creato nel registerFragment
        if (!Validations.isValidPassword(password)) {
            registerFragment.showInvalidPassword();
            return;
        }

        String phoneStr = phoneB.toString();
        //Richiama il metodo isValidPhone e in se il controllo non va a buon fine richiama il messaggio di errore creato nel registerFragment
        if (!Validations.isValidPhone(phoneStr)) {
            registerFragment.showInvalidPhone();
            return;
        }

        GeoPoint locationValue = Validations.isValidLocation(location, registerFragment.getContext());
        if (locationValue == null) {
            registerFragment.showInvalidLocation();
            return;
        }

        // Crea un'istanza di PublicAuthority utilizzando il Builder
        Veterinarian authorityUser = Veterinarian.Builder.create("", companyName, email)
                .setPassword(password)
                .setPhone(phoneB)
                .setLocation(locationValue)
                .build();

        // Tutti i dati sono validi, procedi con la registrazione
        authorityUser.registerVeterinarian(this);
    }

    @Override
    public void onRegisterSuccess() {
        registerFragment.onRegisterSuccess();
    }

    @Override
    public void onRegisterFail() {
        registerFragment.onRegisterFail();
    }
};


