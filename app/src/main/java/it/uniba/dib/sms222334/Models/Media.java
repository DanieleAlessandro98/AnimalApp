package it.uniba.dib.sms222334.Models;

import com.google.firebase.Timestamp;

import it.uniba.dib.sms222334.Database.Dao.MediaDao;

public class Media implements Comparable<Media>{

    private String path;

    private Timestamp timestamp;

    public Media(String path, Timestamp timestamp) {
        this.path = path;
        this.timestamp = timestamp;
    }

    @Override
    public int compareTo(Media o) {
        return timestamp.compareTo(o.getTimestamp());
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public void delete(MediaDao.MediaDeleteListener listener){
        new MediaDao().deleteMedia(getPath(), listener);
    }
}
