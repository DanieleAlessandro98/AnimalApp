package it.uniba.dib.sms222334.Models;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import java.util.ArrayList;
import java.util.Map;

import javax.annotation.Nullable;

import it.uniba.dib.sms222334.Database.Dao.Animal.AnimalCallbacks;
import it.uniba.dib.sms222334.Database.Dao.PathologyDao;

public class Pathology extends Document implements Parcelable{
    private String name;
    private String idAnimal;
    private String id;
    private Pathology(String id,String idAnimal,String name) {
        super(id);
        this.idAnimal = idAnimal;
        this.name = name;
    }

    public void createPathology (Pathology pathology, String name, PathologyDao.OnPathologyCreateListener listener){
        PathologyDao dao = new PathologyDao();
        dao.createPathology(pathology,name,listener);
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getIdAnimal() {
        return idAnimal;
    }
    public void setIdAnimal(String idAnimal) {
        this.idAnimal = idAnimal;
    }
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }

    public static class Builder {
        private String bID;
        private String bidAnimal;
        private String bName;

        private Builder(final String id,final String idAnimal ,final String name){
            this.bID = id;
            this.bidAnimal = idAnimal;
            this.bName=name;
        }

        public static Builder create(final String id,final String idAnimal, final String name){
            return new Builder(id,idAnimal,name);
        }
        
        public Builder setName(final String name){
            this.bName=name;
            return this;
        }

        public Builder setIdAnimal(final String idAnimal){
            this.bidAnimal=idAnimal;
            return this;
        }

        public Pathology build(){
            return new Pathology(bID,bidAnimal,bName);
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

    public void delete(@Nullable AnimalCallbacks.eliminationCallback callback) {
        PathologyDao pathologyDao = new PathologyDao();
        pathologyDao.deletePathology(this,callback);
    }
}
