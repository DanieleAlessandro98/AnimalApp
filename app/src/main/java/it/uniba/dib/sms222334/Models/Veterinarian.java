package it.uniba.dib.sms222334.Models;

import android.media.Image;

import java.util.zip.CheckedOutputStream;

public class Veterinarian extends Document {

    public final static int ROLE=2;
    private String email;
    private String password;
    private int role;
    private String companyName; //denominazione sociale
    private String legal_site; //sede
    private int telephone;
    private String logo;

    //array<visite>


    private Veterinarian(String email, String password, int role, String companyName, String legal_site, int telephone, String logo) {
        super();

        this.email = email;
        this.password = password;
        this.role = role;
        this.companyName = companyName;
        this.legal_site = legal_site;
        this.telephone = telephone;
        this.logo = logo;
    }

    public static class Builder{
        private String bemail;
        private String bpassword;
        private int brole;

        private String bcompanyName;

        private String blegal_site;

        private int btelephone;


        private String blogo;

        private Builder(final String email, final String password){
            this.bemail=email;
            this.bpassword=password;
        }

        public static Veterinarian.Builder create(final String email, final String password){
            return new Veterinarian.Builder(email,password);
        }

        public Veterinarian.Builder setRole(int role){
            this.brole=role;
            return this;
        }

        public Veterinarian.Builder setCompanyName(final String companyName){
            this.bcompanyName=companyName;
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

        public Veterinarian.Builder setLogo(final String logo){
            this.blogo=logo;
            return this;
        }

        public Veterinarian build(){
            return new Veterinarian(bemail,bpassword,brole,bcompanyName,blegal_site,btelephone,blogo);
        }
    }
}
