package it.uniba.dib.sms222334.Models;

import android.graphics.Bitmap;

import it.uniba.dib.sms222334.Utils.UserRole;

public class Veterinarian extends User {
    private String legalSite; //sede
    private float latitude;
    private float longitude;
    //array<visite>

    public Veterinarian(String id, String name, String email, String password, int phone, Bitmap photo, String legalSite, float latitude, float longitude) {
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
        private int bPhone;
        private Bitmap bPhoto;

        private String bLegalSite;
        private float bLatitude;
        private float bLongitude;

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
