package it.uniba.dib.sms222334.Models;

import android.graphics.Bitmap;

import it.uniba.dib.sms222334.Utils.UserRole;

public abstract class User extends Document {

    private String name;
    private String email;
    private String password;
    private long phone;
    private Bitmap photo;

    public User(String id, String name, String email, String password, long phone, Bitmap photo) {
        super(id);
        this.name = name;
        this.email = email;
        this.password = password;
        this.phone = phone;
        this.photo = photo;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public long getPhone() {
        return phone;
    }

    public Bitmap getPhoto() {
        return photo;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setPhone(long phone) {
        this.phone = phone;
    }

    public void setPhoto(Bitmap photo) {
        this.photo = photo;
    }

    public abstract UserRole getRole();
    public abstract void updateProfile();
    public abstract void deleteProfile();
}
