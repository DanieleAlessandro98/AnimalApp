package it.uniba.dib.sms222334.Models;

import android.os.Parcel;
import android.os.Parcelable;

import it.uniba.dib.sms222334.Database.Dao.RelationDao;

public class Relation extends Document implements Parcelable{

    public enum relationType{FRIEND,INCOMPATIBLE,COHABITEE}

    relationType category;
    Animal animal;

    private Relation(String id,Animal animal,relationType relationType){
        super(id);
        this.animal=animal;
        this.category= relationType;
    }

    public boolean createRelation(){
        RelationDao dao = new RelationDao();
        return dao.createRelation(this.category,this.animal);
    }

    public Relation.relationType getRelationType() {
        return category;
    }

    public void setRelationType(Relation.relationType relationType) {
        this.category = relationType;
    }

    public Animal getAnimal() {
        return animal;
    }

    public void setAnimal(Animal animal) {
        this.animal = animal;
    }

    public static boolean deleteRelation(){
        return true;
    }
    public static class Builder{
        private String bID;
        private relationType bcategory;
        private Animal banimal;

        private Builder(String id,final relationType type, Animal animal){
            this.bID=id;
            this.banimal=animal;
            this.bcategory=type;
        }

        public static Builder create(String id,final relationType type, Animal animal){
            return new Builder(id,type,animal);
        }

        public Builder setAnimal(Animal animal){
            this.banimal=animal;
            return this;
        }

        public Builder setRelationType(relationType type){
            this.bcategory=type;
            return this;
        }

        public Relation build(){
            return new Relation(bID,banimal,bcategory);
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(getFirebaseID());
        dest.writeInt(getRelationType().ordinal());
        dest.writeParcelable(getAnimal(),flags);
    }

    protected Relation(Parcel in) {
        super(in.readString());

        this.category = relationType.values()[in.readInt()];
        this.animal=in.readParcelable(Animal.class.getClassLoader());
    }

    public static final Parcelable.Creator<Relation> CREATOR = new Parcelable.Creator<Relation>() {
        @Override
        public Relation createFromParcel(Parcel in) {
            return new Relation(in);
        }

        @Override
        public Relation[] newArray(int size) {
            return new Relation[size];
        }
    };
}
