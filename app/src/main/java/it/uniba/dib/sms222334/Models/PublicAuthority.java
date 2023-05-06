package it.uniba.dib.sms222334.Models;

import android.media.Image;

public class PublicAuthority extends Owner{

    public static int ROLE=1;
    private String email;
    private String password;
    private int role;
    private String company_name;  // denominazione sociale
    private String site;        // sede
    private Image logo;
    private int Nbeds;  // posti letto
    private int telephone;

    private PublicAuthority(String email, String password, int role, String company_name, String site, Image logo, int nbeds, int telephone) {
        this.email = email;
        this.password = password;
        this.role = role;
        this.company_name = company_name;
        this.site = site;
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
        private Image blogo;
        private int bNbeds;  // posti letto
        private int btelephone;

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

        public Builder setSite(final String bsite){
            this.bsite=bsite;
            return this;
        }

        public Builder setLogo(final Image blogo){
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
            return new PublicAuthority(bemail,bpassword,brole,bcompany_name,bsite,blogo,bNbeds,btelephone);
        }
    }
}
