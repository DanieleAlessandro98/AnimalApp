package it.uniba.dib.sms222334.Models;

import android.graphics.Bitmap;

import java.util.zip.CheckedOutputStream;

import it.uniba.dib.sms222334.Utils.UserRole;

public class Veterinarian extends Document {
    private String email;
    private String password;
    private UserRole role;
    private String companyName; //denominazione sociale
    private String legal_site; //sede
    private int telephone;
    private float latitude;
    private float longitude;
    private Bitmap logo;

    //array<visite>

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getLegal_site() {
        return legal_site;
    }

    public void setLegal_site(String legal_site) {
        this.legal_site = legal_site;
    }

    public int getTelephone() {
        return telephone;
    }

    public void setTelephone(int telephone) {
        this.telephone = telephone;
    }

    public Float getLatitude() {
        return latitude;
    }

    public void setLatitude(Float latitude) {
        this.latitude = latitude;
    }

    public Float getLongitude() {
        return longitude;
    }

    public void setLongitude(Float longitude) {
        this.longitude = longitude;
    }

    public Bitmap getLogo() {
        return logo;
    }

    public void setLogo(Bitmap logo) {
        this.logo = logo;
    }

    private Veterinarian(String id, String email, String password, UserRole role, String companyName, String legal_site,float latitude,float longitude, int telephone, Bitmap logo) {
        super(id);

        this.email = email;
        this.password = password;
        this.role = role;
        this.companyName = companyName;
        this.legal_site = legal_site;
        this.telephone = telephone;
        this.logo = logo;
        this.latitude=latitude;
        this.longitude=longitude;
    }

    public static class Builder{
        private String bID;
        private String bemail;
        private String bpassword;
        private UserRole brole;

        private String bcompanyName;

        private String blegal_site;

        private int btelephone;

        private float blatitude;

        private float blongitude;

        private Bitmap blogo;

        private Builder(final String id, final String email, final String password){
            this.bID = id;
            this.bemail=email;
            this.bpassword=password;
        }

        public static Veterinarian.Builder create(final String id, final String email, final String password){
            return new Veterinarian.Builder(id,email,password);
        }

        public Veterinarian.Builder setRole(UserRole role){
            this.brole=role;
            return this;
        }

        public Veterinarian.Builder setCompanyName(final String companyName){
            this.bcompanyName=companyName;
            return this;
        }

        public Veterinarian.Builder setLatitude(final float latitude){
            this.blatitude=latitude;
            return this;
        }

        public Veterinarian.Builder setLongitude(final float longitude){
            this.blongitude=longitude;
            return this;
        }

        public Veterinarian.Builder setLegalSite(final String legalSite){
            this.blegal_site=legalSite;
            return this;
        }

        public Veterinarian.Builder setTelephone(final int telephone){
            this.btelephone=telephone;
            return this;
        }

        public Veterinarian.Builder setLogo(final Bitmap logo){
            this.blogo=logo;
            return this;
        }

        public Veterinarian build(){
            return new Veterinarian(bID,bemail,bpassword,brole,bcompanyName,blegal_site,blatitude,blongitude,btelephone,blogo);
        }
    }
}
