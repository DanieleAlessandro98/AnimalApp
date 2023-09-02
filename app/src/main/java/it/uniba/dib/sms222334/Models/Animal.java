package it.uniba.dib.sms222334.Models;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.Nullable;

import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.ArrayList;

import it.uniba.dib.sms222334.Database.AnimalAppDB;

public class Animal extends Document implements Parcelable {


    //in carico, smarrito, adottato, assistico, randagio
    public enum stateList{LOST,IN_CHARGE,ADOPTED,ASSISTED,STRAY}

    public final static String PHOTO_PATH="/images/profiles/animals/";
    private String name;

    private String ownerReference;
    private Date birthDate;
    private stateList state;
    private String microchip;
    private String species;
    private String race;
    private Bitmap photo;
    private ArrayList<String> videos;

    private ArrayList<String> photos;

    private ArrayList<Visit> visits;

    private ArrayList<Pathology> pathologies;

    private ArrayList<Food> foods;

    private ArrayList<Expense> expenses;

    private ArrayList<Relation> relations;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOwnerReference() {
        return ownerReference;
    }

    public void setOwnerReference(String owner) {
        this.ownerReference = owner;
    }

    public Date getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(Date birthDate) {
        this.birthDate = birthDate;
    }

    public stateList getState() {
        return state;
    }

    public void setState(stateList state) {
        this.state = state;
    }

    public String getMicrochip() {
        return microchip;
    }

    public void setMicrochip(String microchip) {
        this.microchip = microchip;
    }

    public String getSpecies() {
        return species;
    }

    public void setSpecies(String species) {
        this.species = species;
    }

    public String getRace() {
        return race;
    }

    public void setRace(String race) {
        this.race = race;
    }

    public Bitmap getPhoto() {
        return photo;
    }

    public void setPhoto(Bitmap photo) {
        this.photo = photo;
    }

    public ArrayList<String> getPhotos() {
        return photos;
    }

    public void setPhotos(ArrayList<String> photos) {
        this.photos = photos;
    }

    public ArrayList<String> getVideos() {
        return videos;
    }

    public void setVideos(ArrayList<String> videos) {
        this.videos = videos;
    }

    public ArrayList<Visit> getVisits() {
        return visits;
    }

    public void setVisits(ArrayList<Visit> visits) {
        this.visits = visits;
    }

    public ArrayList<Pathology> getPathologies() {
        return pathologies;
    }

    public void setPathologies(ArrayList<Pathology> pathologies) {
        this.pathologies = pathologies;
    }

    public ArrayList<Food> getFoods() {
        return foods;
    }

    public void setFoods(ArrayList<Food> foods) {
        this.foods = foods;
    }

    public ArrayList<Expense> getExpenses() {
        return expenses;
    }

    public void setExpenses(ArrayList<Expense> expenses) {
        this.expenses = expenses;
    }

    public ArrayList<Relation> getRelations() {
        return relations;
    }

    public void setRelations(ArrayList<Relation> relations) {
        this.relations = relations;
    }

    public int getVisitNumber(){
        return this.getVisits().size();
    }

    public int getPathologiesNumber(){
        return this.getPathologies().size();
    }

    private Animal(String id, String name, String ownerReference, Date birthDate, stateList state, String species, String race, Bitmap photo, String microchip){
        super(id);

        this.name = name;
        this.ownerReference = ownerReference;
        this.birthDate = birthDate;
        this.state = state;
        this.species = species;
        this.race = race;

        if(photo!=null){
            this.photo = photo;
        }

        this.microchip = microchip;
        this.videos=new ArrayList<>();
        this.photos=new ArrayList<>();
        this.foods =new ArrayList<>();
        this.pathologies=new ArrayList<>();
        this.visits=new ArrayList<>();
        this.expenses=new ArrayList<>();
        this.relations=new ArrayList<>();
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if(this==obj) return true;

        if (obj == null || getClass() != obj.getClass()) return false;

        return this.getFirebaseID().compareTo(((Animal)obj).getFirebaseID())==0;
    }

    public void assign(Animal animal, boolean profilePictureFlag){
        setName(animal.getName());
        setMicrochip(animal.getMicrochip());
        setBirthDate(animal.getBirthDate());
        setFirebaseID(animal.getFirebaseID());
        setExpenses(animal.getExpenses());
        setFoods(animal.getFoods());
        setOwnerReference(animal.getOwnerReference());
        setPathologies(animal.getPathologies());
        if(profilePictureFlag)//ho inserito questo check perchè causa Fatal signal 4 (SIGILL) quando la foto è la stessa
            setPhoto(animal.getPhoto());
        setPhotos(animal.getPhotos());
        setRace(animal.getRace());
        setRelations(animal.getRelations());
        setState(animal.getState());
        setSpecies(animal.getSpecies());
        setVideos(animal.getVideos());
        setVisits(animal.getVisits());
    }

    public static class Builder{
        private String bID;
        private String bname;
        private String bownerReference;

        private Date bbirthDate;
        private stateList bstate;
        private String bspecies;
        private String brace;
        private Bitmap bphoto;
        private String bmicrochip;

        private Builder(final String id, final stateList state){
            this.bID = id;
            this.bstate=state;
        }

        public static Builder create(final String id, final stateList state){
            return new Builder(id, state);
        }

        public static Builder fromDocumentSnapshot(DocumentSnapshot document) {
            return Builder.
                    create(document.getId(), stateList.values()[Math.toIntExact(document.getLong(AnimalAppDB.Animal.COLUMN_NAME_STATE))])
                    .setName(document.getString(AnimalAppDB.Animal.COLUMN_NAME_NAME))
                    .setBirthDate(document.getDate(AnimalAppDB.Animal.COLUMN_NAME_BIRTH_DATE))
                    .setSpecies(document.getString(AnimalAppDB.Animal.COLUMN_NAME_SPECIES))
                    .setMicrochip(document.getString(AnimalAppDB.Animal.COLUMN_NAME_MICROCHIP))
                    .setRace(document.getString(AnimalAppDB.Animal.COLUMN_NAME_RACE));
        }

        public Builder setId(final String id){
            this.bID=id;
            return this;
        }

        public Builder setState(final stateList state){
            this.bstate=state;
            return this;
        }

        public Builder setName(final String name){
            this.bname=name;
            return this;
        }

        public Builder setOwner(final String ownerReference){
            this.bownerReference=ownerReference;
            return this;
        }

        public Builder setBirthDate(Date birthDate){
            this.bbirthDate=birthDate;
            return this;
        }

        public Builder setSpecies(final String species){
            this.bspecies=species;
            return this;
        }

        public Builder setMicrochip(final String microchip){
            this.bmicrochip=microchip;
            return this;
        }

        public Builder setPhoto(final Bitmap photo){
            this.bphoto=photo;
            return this;
        }

        public Builder setRace(final String race){
            this.brace=race;
            return this;
        }

        public Animal build(){
            return new Animal(bID,bname,bownerReference,bbirthDate,bstate,bspecies,brace,bphoto,bmicrochip);
        }
    }

    public void addImage(String image) {
        photos.add(image);
    }

    public void addVideo(String video) {
        videos.add(video);
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(getFirebaseID());
        dest.writeString(getName());



        dest.writeString(getOwnerReference());
        dest.writeSerializable(birthDate);
        dest.writeInt(getState().ordinal());
        dest.writeString(getSpecies());
        dest.writeString(getRace());
        dest.writeParcelable(getPhoto(),flags);
        dest.writeString(getMicrochip());
        dest.writeStringList(getVideos());
        dest.writeStringList(getPhotos());
        dest.writeList(getFoods());
        dest.writeList(getPathologies());
        dest.writeList(getVisits());
        dest.writeList(getExpenses());
        dest.writeList(getRelations());
    }

    protected Animal(Parcel in) {
        super(in.readString());

        this.name = in.readString();
        this.ownerReference = in.readString();
        this.birthDate = (Date)in.readSerializable();
        this.state = stateList.values()[in.readInt()];
        this.species = in.readString();
        this.race = in.readString();
        this.photo = in.readParcelable(Bitmap.class.getClassLoader());
        this.microchip = in.readString();
        in.readStringList(this.videos);
        in.readStringList(this.photos);
        this.foods =in.readArrayList(Food.class.getClassLoader());
        this.pathologies=in.readArrayList(Pathology.class.getClassLoader());
        this.visits=in.readArrayList(Visit.class.getClassLoader());
        this.expenses=in.readArrayList(Expense.class.getClassLoader());
        this.relations=in.readArrayList(Relation.class.getClassLoader());
    }

    public static final Creator<Animal> CREATOR = new Creator<Animal>() {
        @Override
        public Animal createFromParcel(Parcel in) {
            return new Animal(in);
        }

        @Override
        public Animal[] newArray(int size) {
            return new Animal[size];
        }
    };
}
