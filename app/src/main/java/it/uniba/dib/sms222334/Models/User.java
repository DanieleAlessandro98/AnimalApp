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

    public abstract UserRole getRole();
}
