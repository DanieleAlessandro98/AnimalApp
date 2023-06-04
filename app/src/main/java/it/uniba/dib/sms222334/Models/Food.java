package it.uniba.dib.sms222334.Models;

import android.os.Parcel;
import android.os.Parcelable;
import android.view.animation.AnimationUtils;

import java.util.LinkedList;

public class Food extends Document implements Parcelable {
    private String name;

    private Food(String id, String name) {
        super(id);

        this.name = name;
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

        public Builder setName(final String name){
            this.bName=name;
            return this;
        }

        public static Builder create(final String id, final String name){
            return new Builder(id, name);
        }

        public Food build(){
            return new Food(bID, bName);
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
}
