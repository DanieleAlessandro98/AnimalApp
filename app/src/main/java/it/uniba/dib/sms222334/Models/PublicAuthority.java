package it.uniba.dib.sms222334.Models;

import android.graphics.Bitmap;
import android.media.Image;


public class PublicAuthority extends Owner{


    public final static int ROLE=1;
    private String email;
    private String password;
    private int role;
    private String company_name;  // denominazione sociale
    private String legalSite;        // sede
    private Bitmap logo;

    private float latitude;

    private float longitude;
    private int Nbeds;  // posti letto
    private int telephone;

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

    public int getRole() {
        return role;
    }

    public void setRole(int role) {
        this.role = role;
    }

    public String getCompany_name() {
        return company_name;
    }

    public void setCompany_name(String company_name) {
        this.company_name = company_name;
    }

    public String getlegalSite() {
        return legalSite;
    }

    public void setlegalSite(String site) {
        this.legalSite = site;
    }

    public Bitmap getLogo() {
        return logo;
    }

    public void setLogo(Bitmap logo) {
        this.logo = logo;
    }

    public float getLatitude() {
        return latitude;
    }

    public void setLatitude(float latitude) {
        this.latitude = latitude;
    }

    public float getLongitude() {
        return longitude;
    }

    public void setLongitude(float longitude) {
        this.longitude = longitude;
    }

    public int getNbeds() {
        return Nbeds;
    }

    public void setNbeds(int nbeds) {
        Nbeds = nbeds;
    }

    public int getTelephone() {
        return telephone;
    }

    public void setTelephone(int telephone) {
        this.telephone = telephone;
    }

    private PublicAuthority(String email, String password, int role, String company_name, String site, Bitmap logo, float latitude, float longitude, int nbeds, int telephone) {
        this.email = email;
        this.password = password;
        this.role = role;
        this.company_name = company_name;
        this.legalSite = site;
        this.logo = logo;
        Nbeds = nbeds;
        this.telephone = telephone;
    }

    public static class Builder{
        private String bemail;
        private String bpassword;
        private int brole;
        private String bcompany_name;  // denominazione sociale
        private String bsite;        // sede
        private Bitmap blogo;
        private int bNbeds;  // posti letto
        private int btelephone;

        private float blatitude;

        private float blongitude;

        private Builder(final String email, final String password){
            this.bemail=email;
            this.bpassword=password;
        }

        public static Builder create(final String name, final String surname){
            return new Builder(name,surname);
        }

        public Builder setEmail(final String email){
            this.bemail=email;
            return this;
        }

        public Builder setPassword(final String password){
            this.bpassword=password;
            return this;
        }

        public Builder setRole(int role){
            this.brole=role;
            return this;
        }

        public Builder setCompany_name(final String bcompany_name){
            this.bcompany_name=bcompany_name;
            return this;
        }

        public Builder setLatitude(final float latitude){
            this.blatitude=latitude;
            return this;
        }

        public Builder setLongitude(final float longitude){
            this.blongitude=longitude;
            return this;
        }

        public Builder setSite(final String bsite){
            this.bsite=bsite;
            return this;
        }

        public Builder setLogo(final Bitmap blogo){
            this.blogo=blogo;
            return this;
        }

        public Builder setNbeds(final int bNbeds){
            this.bNbeds=bNbeds;
            return this;
        }

        public Builder setTelephone(final int btelephone){
            this.btelephone=btelephone;
            return this;
        }

        public PublicAuthority build(){
            return new PublicAuthority(bemail,bpassword,brole,bcompany_name,bsite,blogo,blatitude,blongitude,bNbeds,btelephone);
        }
    }
}
