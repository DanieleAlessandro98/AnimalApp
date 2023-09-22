package it.uniba.dib.sms222334.Models;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import java.util.ArrayList;
import java.util.Map;

import it.uniba.dib.sms222334.Database.Dao.PathologyDao;

public class Pathology extends Document implements Parcelable{
    private String name;
    //TODO chiedere come si crea la classe nel present
    private Pathology(String id, String name) {
        super(id);

        this.name = name;
    }

    public static boolean createPathology (String idAnimal, String name){
        PathologyDao dao = new PathologyDao();
        return dao.createPathology(idAnimal,name);
    }

    public boolean deletePathology(String idPathology){
        PathologyDao dao = new PathologyDao();
        return dao.deleteAnimalPathology(idPathology);
    }


    public static void getPathology(String idAnimal,final PathologyDao.OnPathologyListListener listener){
        PathologyDao dao = new PathologyDao();
        dao.getListPathology(idAnimal, new PathologyDao.OnPathologyListListener() {
            @Override
            public void onPathologyListReady(ArrayList<Pathology> listPathology) {
                listener.onPathologyListReady(listPathology);
            }
        });
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public static class Builder {
        private String bID;
        private String bName;

        private Builder(final String id, final String name){
            this.bID = id;
            this.bName=name;
        }

        public static Builder create(final String id, final String name){
            return new Builder(id,name);
        }
        
        public Builder setName(final String name){
            this.bName=name;
            return this;
        }

        public Pathology build(){
            return new Pathology(bID, bName);
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(getFirebaseID());
        dest.writeString(getName());
    }

    protected Pathology(Parcel in) {
        super(in.readString());

        this.name = in.readString();
    }

    public static final Parcelable.Creator<Pathology> CREATOR = new Parcelable.Creator<Pathology>() {
        @Override
        public Pathology createFromParcel(Parcel in) {
            return new Pathology(in);
        }

        @Override
        public Pathology[] newArray(int size) {
            return new Pathology[size];
        }
    };

    public void delete() {
        PathologyDao pathologyDao = new PathologyDao();
        pathologyDao.deletePathology(this);
    }
}
