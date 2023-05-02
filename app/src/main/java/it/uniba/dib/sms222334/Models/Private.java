package it.uniba.dib.sms222334.Models;

import android.media.Image;
import android.provider.ContactsContract;

public class Private extends Owner{
    private String name;
    private String surname;
    private String email;
    private String password;
    private int role;
    private String tax_id_code; //codice_fiscale
    private Image photo;


    private Private(String name, String surname, String email, String password, int role, String tax_id_code, Image photo) {
        this.name = name;
        this.surname = surname;
        this.email = email;
        this.password = password;
        this.role = role;
        this.tax_id_code = tax_id_code;
        this.photo = photo;
    }

    public static class Builder{
        private String bname;
        private String bsurname;
        private String bemail;
        private String bpassword;
        private int brole;
        private String btax_id_code; //codice_fiscale
        private Image bphoto;
        //ArrayList<Animali>

        private Builder(final String name, final String surname){
            this.bname=name;
            this.bsurname=surname;
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

        public Builder setTaxIdCode(final String tax_id_code){
            this.btax_id_code=tax_id_code;
            return this;
        }

        public Builder setPhoto(final Image photo){
            this.bphoto=photo;
            return this;
        }

        public Private build(){
            return new Private(bname,bsurname,bemail,bpassword,brole,btax_id_code,bphoto);
        }
    }
}
