package it.uniba.dib.sms222334.Models;

import android.graphics.Bitmap;
import android.provider.ContactsContract;

import java.util.ArrayList;
import java.util.Date;

import it.uniba.dib.sms222334.Utils.UserRole;

public class Private extends Owner{
    private String name;
    private String surname;
    private String email;
    private String password;

    private long phoneNumber;

    private Date date;
    private UserRole role;
    private String tax_id_code; //codice_fiscale
    private String photo;

    private Private(String id, String name, String surname, String email, String password, Date date, UserRole role, long phoneNumber, String tax_id_code, String photo) {
        super(id);

        this.name = name;
        this.surname = surname;
        this.email = email;
        this.password = password;
        this.role = role;
        this.date=date;
        this.phoneNumber=phoneNumber;
        this.tax_id_code = tax_id_code;
        this.photo = photo;
    }

    public static class Builder{
        private String bID;
        private String bname;
        private String bsurname;
        private String bemail;
        private String bpassword;

        private Date bdate;
        private UserRole brole;

        private long bphoneNumber;
        private String btax_id_code; //codice_fiscale
        private String bphoto;
        //ArrayList<Animali>

        private Builder(final String id, final String name, final String surname){
            this.bID = id;
            this.bname=name;
            this.bsurname=surname;
        }

        public static Builder create(final String id, final String name, final String surname){
            return new Builder(id,name,surname);
        }

        public Builder setEmail(final String email){
            this.bemail=email;
            return this;
        }

        public Builder setPassword(final String password){
            this.bpassword=password;
            return this;
        }

        public Builder setSurname(final String surname){
            this.bsurname=surname;
            return this;
        }

        public Builder setDate(final Date date){
            this.bdate=date;
            return this;
        }

        public Builder setPhoneNumber(long phoneNumber){
            this.bphoneNumber=phoneNumber;
            return this;
        }

        public Builder setTaxIdCode(final String tax_id_code){
            this.btax_id_code=tax_id_code;
            return this;
        }

        public Builder setPhoto(final String photo){
            this.bphoto=photo;
            return this;
        }

        public Private build(){
            return new Private(bID,bname,bsurname,bemail,bpassword,bdate, UserRole.PRIVATE,bphoneNumber,btax_id_code,bphoto);
        }
    }

    public String getName() {
        return name;
    }

    public String getSurname() {
        return surname;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public long getPhoneNumber() {
        return phoneNumber;
    }

    public Date getDate() {
        return date;
    }

    public UserRole getRole() {
        return role;
    }

    public String getTax_id_code() {
        return tax_id_code;
    }

    public String getPhoto() {
        return photo;
    }
}
