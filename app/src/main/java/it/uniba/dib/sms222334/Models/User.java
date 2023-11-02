package it.uniba.dib.sms222334.Models;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.google.android.gms.common.util.ArrayUtils;

import java.io.ByteArrayOutputStream;
import java.io.Serializable;
import java.nio.ByteBuffer;
import java.util.Arrays;

import it.uniba.dib.sms222334.Database.Dao.User.UserCallback;
import it.uniba.dib.sms222334.Utils.UserRole;

public abstract class User extends Document implements UserCallback.UserStateListener {

    private String name;
    private String email;
    private String password;
    private Long phone;
    private Bitmap photo;

    private String photoPath;

    private UserCallback.UserStateListener userCallback;

    public User(String id, String name, String email, String password, Long phone, Bitmap photo) {
        super(id);
        this.name = name;
        this.email = email;
        this.password = password;
        this.phone = phone;

        if(photo!=null){
            this.photo = photo;
            this.photoPath="/images/profiles/users/"+id+".jpg";
        }

    }

    public void setUserLoadingCallBack(UserCallback.UserStateListener callBack){
        this.userCallback=callBack;
    }


    @Override
    public void notifyItemLoaded() {
        if(this.userCallback!=null)
            this.userCallback.notifyItemLoaded();
    }

    @Override
    public void notifyItemUpdated(int position) {
        if(this.userCallback!=null)
            this.userCallback.notifyItemUpdated(position);
    }

    @Override
    public void notifyItemRemoved(int position) {
        if(this.userCallback!=null)
            this.userCallback.notifyItemRemoved(position);
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

    public String getPhotoPath() {
        return this.photoPath;
    }

    public abstract UserRole getRole();
    public abstract void updateProfile(boolean isPhotoChanged);
    public abstract void deleteProfile();
}
