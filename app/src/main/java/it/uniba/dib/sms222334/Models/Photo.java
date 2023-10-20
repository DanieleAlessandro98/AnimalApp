package it.uniba.dib.sms222334.Models;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.google.firebase.Timestamp;

import it.uniba.dib.sms222334.R;

public class Photo extends Media{

    Bitmap photo;
    public Photo(String path, Timestamp timestamp) {
        super(path,timestamp);

        photo=null;
    }

    public Bitmap getPhoto() {
        return photo;
    }

    public void setPhoto(Bitmap photo) {
        this.photo = photo;
    }
}
