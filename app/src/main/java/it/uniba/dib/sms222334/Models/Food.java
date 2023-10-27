package it.uniba.dib.sms222334.Models;

import android.os.Parcel;
import android.os.Parcelable;

import it.uniba.dib.sms222334.Database.Dao.Animal.AnimalCallbacks;
import it.uniba.dib.sms222334.Database.Dao.Animal.FoodDao;

public class Food extends Document implements Parcelable {
    private String name;

    private String animalID;

    private Food(String id, String name, String animalID) {
        super(id);

        this.name = name;
        this.animalID= animalID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAnimalID() {
        return animalID;
    }

    public void setAnimalID(String animalID) {
        this.animalID = animalID;
    }

    public static class Builder {
        private String bID;
        private String bName;

        private String bAnimalID;

        private Builder(final String id, final String name){
            this.bID = id;
            this.bName=name;
        }

        public Builder setName(final String name){
            this.bName=name;
            return this;
        }

        public Builder setAnimalID(final String animalID){
            this.bAnimalID=animalID;
            return this;
        }

        public static Builder create(final String id, final String name){
            return new Builder(id, name);
        }

        public Food build(){
            return new Food(bID, bName,bAnimalID);
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

    protected Food(Parcel in) {
        super(in.readString());

        this.name = in.readString();
    }

    public static final Creator<Food> CREATOR = new Creator<Food>() {
        @Override
        public Food createFromParcel(Parcel in) {
            return new Food(in);
        }

        @Override
        public Food[] newArray(int size) {
            return new Food[size];
        }
    };

    public void delete(AnimalCallbacks.eliminationCallback callback) {
        new FoodDao().deleteFood(this,callback);
    }
}
