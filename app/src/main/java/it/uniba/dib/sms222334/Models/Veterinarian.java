package it.uniba.dib.sms222334.Models;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.firestore.GeoPoint;

import java.util.ArrayList;
import java.util.List;

import it.uniba.dib.sms222334.Database.Dao.Animal.AnimalCallbacks;
import it.uniba.dib.sms222334.Database.Dao.User.PublicAuthorityDao;
import it.uniba.dib.sms222334.Database.Dao.User.UserCallback;
import it.uniba.dib.sms222334.Database.Dao.User.UserDao;
import it.uniba.dib.sms222334.Database.Dao.User.VeterinarianDao;
import it.uniba.dib.sms222334.Utils.UserRole;

public class Veterinarian extends User implements Parcelable
                                                ,AnimalCallbacks.visitCallback{
    private ArrayList<Visit> visitList;
    private ArrayList<Animal> animalList;

    private float distance;

    public ArrayList<Visit> getVisitList() {
        return visitList;
    }

    public void setVisitList(ArrayList<Visit> visitList) {
        this.visitList = visitList;
    }

    public ArrayList<Animal> getAnimalList() {
        return animalList;
    }

    public void setAnimalList(ArrayList<Animal> animalList) {
        this.animalList = animalList;
    }

    public float getDistance() {
        return distance;
    }

    public void setDistance(float distance) {
        this.distance = distance;
    }

    public Veterinarian(String id, String name, String email, String password, long phone, Bitmap photo, GeoPoint legalSite) {
        super(id, name, email, password, phone, photo, legalSite);
        this.visitList=new ArrayList<>();
        this.animalList=new ArrayList<>();
    }

    public static void getVeterinarianAndPublicAuthority(UserCallback.UserFindCallback listener){
        UserDao dao = new UserDao();
        dao.getVeterinariansAndPublicAuthorities(listener);
    }

    public static class Builder {
        private String bID;
        private String bName;
        private String bEmail;
        private String bPassword;
        private long bPhone;
        private Bitmap bPhoto;

        private GeoPoint bLegalSite;

        private Builder(final String id, final String name, final String email) {
            this.bID = id;
            this.bName = name;
            this.bEmail = email;
        }

        public static Builder create(final String id, final String name, final String email) {
            return new Builder(id, name, email);
        }

        public Builder setName(final String name){
            this.bName=name;
            return this;
        }

        public Builder setEmail(final String email){
            this.bEmail=email;
            return this;
        }

        public Builder setPassword(final String password){
            this.bPassword=password;
            return this;
        }

        public Builder setPhone(final long phone){
            this.bPhone=phone;
            return this;
        }

        public Builder setPhoto(final Bitmap photo){
            this.bPhoto=photo;
            return this;
        }

        public Builder setLegalSite(final GeoPoint legalSite) {
            this.bLegalSite = legalSite;
            return this;
        }

        public Veterinarian build() {
            return new Veterinarian(bID, bName, bEmail, bPassword, bPhone, bPhoto, bLegalSite);
        }
    }


    AnimalCallbacks.visitCallback visitCallback=null;

    public void setVisitCallback(AnimalCallbacks.visitCallback visitCallback){
        this.visitCallback=visitCallback;
    }


    public void addVisit(Visit visit){
        this.visitList.add(0,visit);

        notifyVisitLoaded();

        if(this.animalList.isEmpty()){
            addAnimal(visit.getAnimal());
            return;
        }

        for(Animal animal:this.animalList){
            if(visit.getAnimal().getFirebaseID().compareTo(animal.getFirebaseID()) == 0)
                return;
        }

        addAnimal(visit.getAnimal());
    }

    public void removeVisit(Visit visit){
        final int index=this.visitList.indexOf(visit);
        this.visitList.remove(visit);

        notifyVisitRemoved(index);

        Animal animal=visit.getAnimal();

        for(Visit visit1: this.visitList){
            if(visit1.getAnimal().getFirebaseID().compareTo(animal.getFirebaseID()) == 0)
                return;
        }

        removeAnimal(animal);
    }


    private void addAnimal(Animal animal){
        this.animalList.add(0,animal);
        notifyItemLoaded();
    }

    private void removeAnimal(Animal animal){
        int index=this.animalList.indexOf(animal);
        this.animalList.remove(animal);
        notifyItemRemoved(index);
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
    public UserRole getRole() {
        return UserRole.VETERINARIAN;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(getFirebaseID());
        dest.writeString(getName());
        dest.writeString(getEmail());
        dest.writeString(getPassword());
        dest.writeLong(getPhone());
        //dest.writeParcelable(getPhoto(),flags);
        dest.writeDouble(getLocation().getLatitude());
        dest.writeDouble(getLocation().getLongitude());
    }

    protected Veterinarian(Parcel in) {
        super(in.readString(), in.readString(), in.readString(), in.readString(), in.readLong(), in.readParcelable(Bitmap.class.getClassLoader()), new GeoPoint(in.readDouble(),in.readDouble()));
    }

    public static final Creator<Private> CREATOR = new Creator<Private>() {
        @Override
        public Private createFromParcel(Parcel in) {
            return new Private(in);
        }

        @Override
        public Private[] newArray(int size) {
            return new Private[size];
        }
    };

    public void updateProfile(boolean isPhotoChanged) {
        VeterinarianDao veterinarianDao = new VeterinarianDao();
        veterinarianDao.updateVeterinarian(this, new UserCallback.UserUpdateCallback() {
            @Override
            public void notifyUpdateSuccesfull() {
            }

            @Override
            public void notifyUpdateFailed() {

            }
        });
    }

    public void registerVeterinarian(UserCallback.UserRegisterCallback callback) {

        // Crea un'istanza di PublicAuthorityDao
        VeterinarianDao veterinarianDao = new VeterinarianDao();

        // Chiamata al metodo di creazione di VeterinarianDao per salvare il Veterinarian nel database
        veterinarianDao.createVeterinarian(this, callback);
    }
    @Override
    public void deleteProfile() {
        for (Visit visit : visitList) {
            visit.delete();
        }

        VeterinarianDao veterinarianDao = new VeterinarianDao();
        veterinarianDao.deleteVeterinarian(this);
    }
}
