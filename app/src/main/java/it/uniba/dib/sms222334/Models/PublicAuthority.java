package it.uniba.dib.sms222334.Models;

import android.graphics.Bitmap;

import java.util.LinkedList;

import it.uniba.dib.sms222334.Utils.UserRole;

public class PublicAuthority extends User implements Owner {
    private String legalSite;        // sede
    private float latitude;
    private float longitude;
    private int NBeds;  // posti letto

    private LinkedList<Animal> listAnimal;
    private LinkedList<Expense> listExpense;

    public PublicAuthority(String id, String name, String email, String password, int phone, Bitmap photo, String legalSite, float latitude, float longitude, int nBeds) {
        super(id, name, email, password, phone, photo);

        this.legalSite = legalSite;
        this.latitude = latitude;
        this.longitude = longitude;
        this.NBeds = nBeds;

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

        private String bLegalSite;
        private float bLatitude;
        private float bLongitude;
        private int bNBeds;

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

        public Builder setLegalSite(final String legalSite) {
            this.bLegalSite = legalSite;
            return this;
        }

        public Builder setLatitude(final float latitude) {
            this.bLatitude = latitude;
            return this;
        }

        public Builder setLongitude(final float longitude) {
            this.bLongitude = longitude;
            return this;
        }

        public Builder setNBeds(final int bNBeds) {
            this.bNBeds = bNBeds;
            return this;
        }

        public PublicAuthority build() {
            return new PublicAuthority(bID, bName, bEmail, bPassword, bPhone, bPhoto, bLegalSite, bLatitude, bLongitude, bNBeds);
        }
    }

    public String getLegalSite() {
        return legalSite;
    }

    public float getLatitude() {
        return latitude;
    }

    public float getLongitude() {
        return longitude;
    }

    public int getNBeds() {
        return NBeds;
    }

    @Override
    public UserRole getRole() {
        return UserRole.PUBLIC_AUTHORITY;
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
