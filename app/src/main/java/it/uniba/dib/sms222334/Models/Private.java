package it.uniba.dib.sms222334.Models;

import android.graphics.Bitmap;

import java.util.Date;
import java.util.LinkedList;

import it.uniba.dib.sms222334.Utils.UserRole;

public class Private extends User implements Owner {
    private String surname;
    private Date birthDate;
    private String taxIDCode; //codice_fiscale

    private LinkedList<Animal> listAnimal;
    private LinkedList<Expense> listExpense;

    public Private(String id, String name, String email, String password, int phone, Bitmap photo, String surname, Date birthDate, String taxIDCode) {
        super(id, name, email, password, phone, photo);

        this.surname = surname;
        this.birthDate = birthDate;
        this.taxIDCode = taxIDCode;

        listAnimal = new LinkedList<>();
        listExpense = new LinkedList<>();
    }

    public static class Builder {
        private String bID;
        private String bName;
        private String bEmail;
        private String bPassword;
        private int bPhone;
        private Bitmap bPhoto;

        private String bSurname;
        private Date bBirthDate;
        private String bTaxID;
        //ArrayList<Animali>

        private Builder(final String id, final String name, final String email, final String password, final int phone, final Bitmap photo) {
            this.bID = id;
            this.bName = name;
            this.bEmail = email;
            this.bPassword = password;
            this.bPhone = phone;
            this.bPhoto = photo;
        }

        public static Builder create(final String id, final String name, final String email, final String password, final int phone, final Bitmap photo) {
            return new Builder(id, name, email, password, phone, photo);
        }

        public Builder setSurname(final String surname) {
            this.bSurname = surname;
            return this;
        }

        public Builder setBirthDate(final Date birthDate) {
            this.bBirthDate = birthDate;
            return this;
        }

        public Builder setTaxIdCode(final String taxIDCode) {
            this.bTaxID = taxIDCode;
            return this;
        }

        public Private build() {
            return new Private(bID, bName, bEmail, bPassword, bPhone, bPhoto, bSurname, bBirthDate, bTaxID);
        }
    }

    public String getSurname() {
        return surname;
    }

    public Date getBirthDate() {
        return birthDate;
    }

    public String getTaxIDCode() {
        return taxIDCode;
    }

    @Override
    public UserRole getRole() {
        return UserRole.PRIVATE;
    }

    @Override
    public void addAnimal(Animal animal) {
        this.listAnimal.add(animal);
    }

    @Override
    public void removeAnimal(Animal animal) {
        for (Animal a : listAnimal) {
            if (a.getFirebaseID().compareTo(animal.getFirebaseID()) == 0) {
                listAnimal.remove(a);
            }
        }
    }

    @Override
    public void addExpense(Expense Expense) {
        this.listExpense.add(Expense);
    }

    @Override
    public void removeExpense(Expense Expense) {
        for (Expense a : listExpense) {
            if (a.getFirebaseID().compareTo(Expense.getFirebaseID()) == 0) {
                listExpense.remove(a);
            }
        }
    }
}
