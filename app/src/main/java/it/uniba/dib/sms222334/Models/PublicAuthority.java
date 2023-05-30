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

    public PublicAuthority(String id, String name, String email, String password, long phone, Bitmap photo, String legalSite, float latitude, float longitude, int nBeds) {
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
        private long bPhone;
        private Bitmap bPhoto;

        private String bLegalSite;
        private float bLatitude;
        private float bLongitude;
        private int bNBeds;

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
    public void updateProfile() {

    }

    @Override
    public void deleteProfile() {

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
