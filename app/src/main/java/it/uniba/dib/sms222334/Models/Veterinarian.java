package it.uniba.dib.sms222334.Models;

import android.graphics.Bitmap;

import it.uniba.dib.sms222334.Utils.UserRole;

public class Veterinarian extends User {
    private String legalSite; //sede
    private float latitude;
    private float longitude;
    //array<visite>

    public Veterinarian(String id, String name, String email, String password, long phone, Bitmap photo, String legalSite, float latitude, float longitude) {
        super(id, name, email, password, phone, photo);

        this.legalSite = legalSite;
        this.latitude = latitude;
        this.longitude = longitude;
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

        public Veterinarian build() {
            return new Veterinarian(bID, bName, bEmail, bPassword, bPhone, bPhoto, bLegalSite, bLatitude, bLongitude);
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

    @Override
    public UserRole getRole() {
        return UserRole.VETERINARIAN;
    }
}
