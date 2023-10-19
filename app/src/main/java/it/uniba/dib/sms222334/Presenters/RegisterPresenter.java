package it.uniba.dib.sms222334.Presenters;

import android.util.Log;
import android.util.Patterns;

import java.util.Date;

import com.google.firebase.firestore.GeoPoint;
import it.uniba.dib.sms222334.Activity.RegisterActivity;
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

    //Prendo i dati dal fragment e li Controllo
    public void checkPrivateRegistration(String name, String surname, String email, String password, Long phone, Date birthDate, String taxIDCode){

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

        // Crea un'istanza di Private utilizzando il Builder
        Private privateUser = Private.Builder.create("", name, email)
                .setSurname(surname)
                .setPassword(password)
                .setPhone(phone)
                .setBirthDate(birthDate)
                .setTaxIdCode(taxIDCode)
                .build();

        // Tutti i dati sono validi, procedi con la registrazione
        privateUser.registerPrivate( this);
        }

    public void checkAuthorityRegistration(String companyName, String emailA, String passwordA, Long phoneA, String site){
        //Richiama il metodo isValidCompanyName e in se il controllo non va a buon fine richiama il messaggio di errore creato nel registerFragment
        if (!Validations.isValidCompanyName(companyName)) {
            registerFragment.showInvalidCompanyName();
            return;
        }

        //Richiama il metodo isvalidEmail e in se il controllo non va a buon fine richiama il messaggio di errore creato nel registerFragment
        if (!Validations.isValidEmail(emailA)) {
            registerFragment.showInvalidEmail();
            return;
        }

        //Richiama il metodo isValidPassword e in se il controllo non va a buon fine richiama il messaggio di errore creato nel registerFragment
        if (!Validations.isValidPassword(passwordA)) {
            registerFragment.showInvalidPassword();
            return;
        }

        String phoneStr = phoneA.toString();
        //Richiama il metodo isValidPhone e in se il controllo non va a buon fine richiama il messaggio di errore creato nel registerFragment
        if (!Validations.isValidPhone(phoneStr)) {
            registerFragment.showInvalidPhone();
            return;
        }

        //Variabile di test per la geolocalizzazione
        GeoPoint TEST = new GeoPoint(-90,90);

        // Crea un'istanza di PublicAuthority utilizzando il Builder
        PublicAuthority authorityUser = PublicAuthority.Builder.create("", companyName, emailA)
                .setPassword(passwordA)
                .setPhone(phoneA)
                .setLegalSite(TEST)
                .build();

        // Tutti i dati sono validi, procedi con la registrazione
        authorityUser.registerAuthority(this);
    }

    public void checkVeterinarianRegistration(String companyName, String emailB, String passwordB, Long phoneB, String siteB){
        //Richiama il metodo isValidCompanyName e in se il controllo non va a buon fine richiama il messaggio di errore creato nel registerFragment
        if (!Validations.isValidCompanyName(companyName)) {
            registerFragment.showInvalidCompanyName();
            return;
        }

        //Richiama il metodo isvalidEmail e in se il controllo non va a buon fine richiama il messaggio di errore creato nel registerFragment
        if (!Validations.isValidEmail(emailB)) {
            registerFragment.showInvalidEmail();
            return;
        }

        //Richiama il metodo isValidPassword e in se il controllo non va a buon fine richiama il messaggio di errore creato nel registerFragment
        if (!Validations.isValidPassword(passwordB)) {
            registerFragment.showInvalidPassword();
            return;
        }

        String phoneStr = phoneB.toString();
        //Richiama il metodo isValidPhone e in se il controllo non va a buon fine richiama il messaggio di errore creato nel registerFragment
        if (!Validations.isValidPhone(phoneStr)) {
            registerFragment.showInvalidPhone();
            return;
        }

        //Variabile di test per la geolocalizzazione
        GeoPoint TEST = new GeoPoint(-90,90);

        // Crea un'istanza di PublicAuthority utilizzando il Builder
        Veterinarian authorityUser = Veterinarian.Builder.create("", companyName, emailB)
                .setPassword(passwordB)
                .setPhone(phoneB)
                .setLegalSite(TEST)
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


