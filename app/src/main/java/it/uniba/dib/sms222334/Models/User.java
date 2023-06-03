package it.uniba.dib.sms222334.Models;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.google.android.gms.common.util.ArrayUtils;

import java.io.ByteArrayOutputStream;
import java.io.Serializable;
import java.nio.ByteBuffer;
import java.util.Arrays;

import it.uniba.dib.sms222334.Utils.UserRole;

public abstract class User extends Document{

    private String name;
    private String email;
    private String password;
    private Long phone;
    private Bitmap photo;

    public User(String id, String name, String email, String password, Long phone, Bitmap photo) {
        super(id);
        this.name = name;
        this.email = email;
        this.password = password;
        this.phone = phone;

        if(photo!=null)
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

    public Long getPhone() {
        return phone;
    }

    public Bitmap getPhoto() {
        return this.photo;
    }

    public abstract UserRole getRole();
}
