package it.uniba.dib.sms222334.Models;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.Nullable;

import com.google.firebase.firestore.DocumentSnapshot;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Date;

import it.uniba.dib.sms222334.Database.AnimalAppDB;
import it.uniba.dib.sms222334.Database.Dao.Animal.AnimalCallbacks;
import it.uniba.dib.sms222334.Database.Dao.Animal.AnimalDao;
import it.uniba.dib.sms222334.Database.Dao.MediaDao;
import it.uniba.dib.sms222334.Database.Dao.User.UserCallback;
import it.uniba.dib.sms222334.R;
import it.uniba.dib.sms222334.Utils.AnimalSpecies;
import it.uniba.dib.sms222334.Utils.AnimalStates;
import it.uniba.dib.sms222334.Utils.ReportType;
import it.uniba.dib.sms222334.Utils.UserRole;

public class Animal extends Document implements Parcelable
        ,AnimalCallbacks.foodCallback
        ,AnimalCallbacks.expensesCallback
        ,AnimalCallbacks.visitCallback
        ,AnimalCallbacks.pathologyCallback
        ,AnimalCallbacks.changeDataCallback{

    public final static String PHOTO_PATH="/images/profiles/animals/";

    public final static String VIDEO_PATH="/videos/";
    private String name;

    private String ownerReference;
    private Date birthDate;
    private AnimalStates state;
    private String microchip;
    private AnimalSpecies species;
    private String race;
    private Bitmap photo;
    private ArrayList<Video> videos;

    private ArrayList<Photo> photos;

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

    public AnimalStates getState() {
        return state;
    }

    public void setState(AnimalStates state) {
        this.state = state;
    }

    public String getMicrochip() {
        return microchip;
    }

    public void setMicrochip(String microchip) {
        this.microchip = microchip;
    }

    public AnimalSpecies getSpecies() {
        return species;
    }

    public static String getSpeciesString(AnimalSpecies species, Context context) {
        switch (species) {
            case DOG:
                return context.getString(R.string.dog_species_name);
            case CAT:
                return context.getString(R.string.cat_species_name);
            case FISH:
                return context.getString(R.string.fish_species_name);
            case BIRD:
                return context.getString(R.string.bird_species_name);
            case RABBIT:
                return context.getString(R.string.rabbit_species_name);
            default:
                return "";
        }
    }

    public void setSpecies(AnimalSpecies species) {
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

    public ArrayList<Photo> getPhotos() {
        return photos;
    }

    public void setPhotos(ArrayList<Photo> photos) {
        this.photos = photos;
    }

    public ArrayList<Video> getVideos() {
        return videos;
    }

    public void setVideos(ArrayList<Video> videos) {
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

    private Animal(String id, String name, String ownerReference, Date birthDate, AnimalStates state, AnimalSpecies species, String race, Bitmap photo, String microchip){
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

    AnimalCallbacks.foodCallback foodCallback= null;

    AnimalCallbacks.expensesCallback expensesCallback=null;

    AnimalCallbacks.visitCallback visitCallback=null;

    AnimalCallbacks.pathologyCallback pathologyCallback=null;

    AnimalCallbacks.changeDataCallback changeDataCallback=null;

    public void setChangeDataCallback(AnimalCallbacks.changeDataCallback callBack){
        this.changeDataCallback=callBack;
    }

    public void setFoodCallback(AnimalCallbacks.foodCallback foodCallback){
        this.foodCallback= foodCallback;
    }

    public void setExpensesCallback(AnimalCallbacks.expensesCallback expensesCallback){
        this.expensesCallback= expensesCallback;
    }

    public void setVisitCallback(AnimalCallbacks.visitCallback visitCallback){
        this.visitCallback=visitCallback;
    }

    public void setPathologyCallback(AnimalCallbacks.pathologyCallback pathologyCallback){
        this.pathologyCallback=pathologyCallback;
    }

    @Override
    public void notifyFoodLoaded() {
        if(foodCallback!=null)
            foodCallback.notifyFoodLoaded();
    }

    @Override
    public void notifyFoodRemoved(int position) {
        if(foodCallback!=null)
            foodCallback.notifyFoodRemoved(position);
    }

    @Override
    public void notifyExpensesLoaded() {
        if(expensesCallback!=null)
            expensesCallback.notifyExpensesLoaded();
    }

    @Override
    public void notifyExpensesRemoved(int position) {
        if(expensesCallback!=null)
            expensesCallback.notifyExpensesRemoved(position);
    }

    @Override
    public void notifyVisitLoaded() {
        if(visitCallback!=null)
            visitCallback.notifyVisitLoaded();
    }

    @Override
    public void notifyVisitRemoved(int position) {
        if(visitCallback!=null)
            visitCallback.notifyVisitRemoved(position);
    }

    @Override
    public void notifyPathologyLoaded() {
        if(pathologyCallback!=null)
            pathologyCallback.notifyPathologyLoaded();
    }

    @Override
    public void notifyPathologyRemoved(int position) {
        if(pathologyCallback!=null)
            pathologyCallback.notifyPathologyRemoved(position);
    }

    @Override
    public void onDataChanged(Animal animal) {
        if(changeDataCallback!=null)
            changeDataCallback.onDataChanged(animal);
    }

    public static class Builder{
        private String bID;
        private String bname;
        private String bownerReference;

        private Date bbirthDate;
        private AnimalStates bstate;
        private AnimalSpecies bspecies;
        private String brace;
        private Bitmap bphoto;
        private String bmicrochip;

        private Builder(final String id, final AnimalStates state){
            this.bID = id;
            this.bstate=state;
        }

        public static Builder create(final String id, final AnimalStates state){
            return new Builder(id, state);
        }

        public static Builder fromDocumentSnapshot(DocumentSnapshot document) {
            return Builder.
                    create(document.getId(), AnimalStates.values()[Math.toIntExact(document.getLong(AnimalAppDB.Animal.COLUMN_NAME_STATE))])
                    .setName(document.getString(AnimalAppDB.Animal.COLUMN_NAME_NAME))
                    .setBirthDate(document.getDate(AnimalAppDB.Animal.COLUMN_NAME_BIRTH_DATE))
                    .setSpecies(AnimalSpecies.values()[Math.toIntExact(document.getLong(AnimalAppDB.Animal.COLUMN_NAME_STATE))])
                    .setMicrochip(document.getString(AnimalAppDB.Animal.COLUMN_NAME_MICROCHIP))
                    .setRace(document.getString(AnimalAppDB.Animal.COLUMN_NAME_RACE));
        }

        public Builder setId(final String id){
            this.bID=id;
            return this;
        }

        public Builder setState(final AnimalStates state){
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

        public Builder setSpecies(final AnimalSpecies species){
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

    public void addImage(Photo image) {
        photos.add(image);
    }

    public void addVideo(Video video) {
        videos.add(video);
    }

    public void addFood(Food food) {
        foods.add(0,food);
        notifyFoodLoaded();
    }

    public void addVisit(Visit visit){
        visits.add(0,visit);
        notifyVisitLoaded();
        onDataChanged(this);
    }

    public void removeVisit(Visit visit){
        final int index=visits.indexOf(visit);
        visits.remove(visit);
        notifyVisitRemoved(index);
        onDataChanged(this);
    }

    public void addExpense(Expense expense) {
        expenses.add(0,expense);
        notifyExpensesLoaded();
    }

    public void removeFood(Food food){
        final int index=foods.indexOf(food);
        foods.remove(food);
        notifyFoodRemoved(index);
    }

    public void removeExpense(Expense expense){
        final int index=expenses.indexOf(expense);
        expenses.remove(expense);
        notifyExpensesRemoved(index);
    }

    public void addPathology(Pathology pathology) {
        pathologies.add(0,pathology);
        notifyPathologyLoaded();
        onDataChanged(this);
    }

    public void removePathology(Pathology pathology){
        final int index=pathologies.indexOf(pathology);
        pathologies.remove(pathology);
        notifyPathologyRemoved(index);
        onDataChanged(this);
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
        dest.writeInt(getSpecies().ordinal());
        dest.writeString(getRace());
        //dest.writeParcelable(getPhoto(),flags); //TODO trovare il modo di salvare anche la foto perchè troppo grande
        dest.writeString(getMicrochip());
        dest.writeList(getVideos());
        dest.writeList(getPhotos());
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
        this.state = AnimalStates.values()[in.readInt()];
        this.species = AnimalSpecies.values()[in.readInt()];
        this.race = in.readString();
        //this.photo = in.readParcelable(Bitmap.class.getClassLoader());
        this.microchip = in.readString();
        this.photos=in.readArrayList(Photo.class.getClassLoader());
        this.videos=in.readArrayList(Video.class.getClassLoader());
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

    public void delete(@Nullable AnimalCallbacks.eliminationCallback callback) {

        AnimalDao animalDao = new AnimalDao();

        for (Visit visit : visits) {
            visit.delete();
        }

        for (Pathology pathology : pathologies) {
            pathology.delete(null);
        }

        for (Food food : foods) {
            food.delete(null);
        }

        for (Expense expense : expenses) {
            expense.delete(null);
        }

        for(Relation relation: relations){
            // TODO: Cancellare relazioni
        }
        for(Video video: videos)
            video.delete(new MediaDao.MediaDeleteListener() {
                @Override
                public void mediaDeletedSuccessfully() {
                }
                @Override
                public void mediaDeletedFailed(Exception exception) {
                }
            });

        for(Photo photo: photos)
            photo.delete(new MediaDao.MediaDeleteListener() {
                @Override
                public void mediaDeletedSuccessfully() {
                }

                @Override
                public void mediaDeletedFailed(Exception exception) {
                }
            });
        //TODO: fix check asincrono del session manager(Entra qui anche nell'eliminazione causando nul pointer exception)
        if (SessionManager.getInstance().isLogged())        {
            animalDao.deleteAnimal(this, new AnimalCallbacks.eliminationCallback() {
                @Override
                public void eliminatedSuccesfully() {

                }
                @Override
                public void failedElimination() {

                }
            });
        }
        else
        {
            animalDao.deleteAnimal(this);
        }
}

    @Override
    public String toString() {
        return name;
    }

    public static AnimalStates findAnimalStateByReport(User user, ReportType reportType, boolean isDeletingReport) {
        if (isDeletingReport) {
            if (user != null) {
                if (user.getRole() == UserRole.PRIVATE)
                    return AnimalStates.ADOPTED;
                else if (user.getRole() == UserRole.PUBLIC_AUTHORITY)
                    return AnimalStates.IN_CHARGE;
            }
        }
        else if (reportType == ReportType.LOST)
            return AnimalStates.LOST;
        else if (reportType == ReportType.IN_DANGER)
            return AnimalStates.IN_DANGER;

        return AnimalStates.NULL;
    }
}
