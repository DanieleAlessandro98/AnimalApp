package it.uniba.dib.sms222334.Models;

import android.graphics.Bitmap;

import java.util.Date;
import java.util.LinkedList;

import it.uniba.dib.sms222334.Database.Dao.PrivateDao;
import it.uniba.dib.sms222334.Utils.UserRole;

public class Private extends User implements Owner {
    private String surname;
    private Date birthDate;
    private String taxIDCode; //codice_fiscale

    private LinkedList<Animal> listAnimal;

    public Private(String id, String name, String email, String password, long phone, Bitmap photo, String surname, Date birthDate, String taxIDCode) {
        super(id, name, email, password, phone, photo);

        this.surname = surname;
        this.birthDate = birthDate;
        this.taxIDCode = taxIDCode;

        listAnimal = new LinkedList<>();
    }

    public static class Builder {
        private String bID;
        private String bName;
        private String bEmail;
        private String bPassword;
        private long bPhone;
        private Bitmap bPhoto;

        private String bSurname;
        private Date bBirthDate;
        private String bTaxID;
        //ArrayList<Animali>

        private Builder(final String id, final String name, final String email) {
            this.bID = id;
            this.bName = name;
            this.bEmail = email;
        }

        public static Builder create(final String id, final String name, final String email) {
            return new Builder(id, name, email);
        }

        public Builder setName(final String name){
            this.bName=name;
            return this;
        }

        public Builder setEmail(final String email){
            this.bEmail=email;
            return this;
        }

        public Builder setPassword(final String password){
            this.bPassword=password;
            return this;
        }

        public Builder setPhone(final long phone){
            this.bPhone=phone;
            return this;
        }

        public Builder setPhoto(final Bitmap photo){
            this.bPhoto=photo;
            return this;
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

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public void setBirthDate(Date birthDate) {
        this.birthDate = birthDate;
    }

    public void setTaxIDCode(String taxIDCode) {
        this.taxIDCode = taxIDCode;
    }

    @Override
    public UserRole getRole() {
        return UserRole.PRIVATE;
    }

    @Override
    public void updateProfile() {
        PrivateDao privateDao = new PrivateDao();
        privateDao.updatePrivate(this);
    }

    @Override
    public void deleteProfile() {
        for (Animal animal : listAnimal) {
            animal.delete();
        }

        PrivateDao privateDao = new PrivateDao();
        privateDao.deletePrivate(this);
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
}
