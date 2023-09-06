package it.uniba.dib.sms222334.Presenters;

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



public class RegisterPresenter implements UserCallback.UserRegisterCallback{
    private RegisterFragment registerFragment;

    StringBuilder errorMessage = new StringBuilder();

    public RegisterPresenter(RegisterFragment fragment){
        this.registerFragment = fragment;

    }

    //Prendo i dati dal fragment e li Controllo
    public void checkPrivateRegistration(String name, String surname, String email, String password, Long phone, Date birthDate, String taxIDCode){

        StringBuilder errorMessage = new StringBuilder();

        // Validazione del nome e del cognome
        if (!name.matches("[a-zA-Z ]+")) {
            errorMessage.append("Il nome non deve contenere numeri o caratteri speciali. ");
            return;
        }

        if (!surname.matches("[a-zA-Z ]+")) {
            errorMessage.append("Il cognome non deve contenere numeri o caratteri speciali. ");
            return;
        }

        String phoneStr = phone.toString();
        if (!phoneStr.matches("[0-9]+")) {
            errorMessage.append("Il numero di telefono non deve contenere lettere. ");
            return;
        }

        if (!DateUtilities.validateAge(birthDate, 18)){
            errorMessage.append("Devi avere 18 anni. ");
            return;
        }

        // Validazione dell'email
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            errorMessage.append("L'indirizzo email non è valido. ");
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

    // TODO: Check sui dati dell'autority
    public void checkAuthorityRegistration(String companyName, String emailA, String passwordA, Long phoneA, String site){

        // Validazione del COMPANYNAME
        if (!companyName.matches("[a-zA-Z ]+")) {
            errorMessage.append("Il nome non deve contenere numeri o caratteri speciali. ");
            return;
        }

        String phoneStr = phoneA.toString();
        if (!phoneStr.matches("[0-9]+")) {
            errorMessage.append("Il numero di telefono non deve contenere lettere. ");
            return;
        }

        // Validazione dell'email
        if (!Patterns.EMAIL_ADDRESS.matcher(emailA).matches()) {
            errorMessage.append("L'indirizzo email non è valido. ");
            return;
        }

        GeoPoint TEST = new GeoPoint(110,4);

        // Crea un'istanza di PublicAuthority utilizzando il Builder
        PublicAuthority authorityUser = PublicAuthority.Builder.create("", companyName, emailA)
                .setPassword(passwordA)
                .setPhone(phoneA)
                .setLegalSite(TEST)
                .build();

        // Tutti i dati sono validi, procedi con la registrazione
        authorityUser.registerAuthority(companyName, emailA, passwordA, phoneA, TEST);
    }

    public void checkVeterinarianRegistration(String companyName, String emailB, String passwordB, Long phoneB, String siteB){

        // Validazione del COMPANYNAME
        if (!companyName.matches("[a-zA-Z ]+")) {
            errorMessage.append("Il nome non deve contenere numeri o caratteri speciali. ");
            return;
        }

        String phoneStr = phoneB.toString();
        if (!phoneStr.matches("[0-9]+")) {
            errorMessage.append("Il numero di telefono non deve contenere lettere. ");
            return;
        }

        // Validazione dell'email
        if (!Patterns.EMAIL_ADDRESS.matcher(emailB).matches()) {
            errorMessage.append("L'indirizzo email non è valido. ");
            return;
        }

        GeoPoint TEST = new GeoPoint(110,4);

        // Crea un'istanza di PublicAuthority utilizzando il Builder
        Veterinarian authorityUser = Veterinarian.Builder.create("", companyName, emailB)
                .setPassword(passwordB)
                .setPhone(phoneB)
                .setLegalSite(TEST)
                .build();

        // Tutti i dati sono validi, procedi con la registrazione
        authorityUser.registerVeterinarian(companyName, emailB, passwordB, phoneB, TEST);
    }
//Da implementare l'interfaccia tra fragment e presenter
    @Override
    public void onRegisterSuccess() {
        registerFragment.onRegisterSuccess();
    }

    @Override
    public void onRegisterFail() {
        registerFragment.onRegisterFail();
    }
};


