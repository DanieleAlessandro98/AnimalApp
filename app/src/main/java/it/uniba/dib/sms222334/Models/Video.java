package it.uniba.dib.sms222334.Models;

import android.provider.MediaStore;

import com.google.firebase.Timestamp;

public class Video extends Media{

    String video;
    public Video(String path, Timestamp timestamp) {
        super(path,timestamp);

        this.video="https://firebasestorage.googleapis.com/v0/b/animalapp-717fd.appspot.com/o/videos%2Fdefault.mp4?alt=media&token=a381764a-7fcc-42ef-9e25-9a6f32cca4b7";
    }

    public String getVideo() {
        return video;
    }

    public void setVideo(String video) {
        this.video = video;
    }
}
