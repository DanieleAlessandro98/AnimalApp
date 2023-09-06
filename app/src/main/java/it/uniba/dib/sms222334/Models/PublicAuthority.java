package it.uniba.dib.sms222334.Models;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.common.util.ArrayUtils;
import com.google.firebase.firestore.GeoPoint;

import java.util.ArrayList;
import java.util.Date;
import java.util.ArrayList;

import it.uniba.dib.sms222334.Database.Dao.User.PrivateDao;
import it.uniba.dib.sms222334.Database.Dao.User.PublicAuthorityDao;
import it.uniba.dib.sms222334.Database.Dao.User.UserCallback;
import it.uniba.dib.sms222334.Utils.UserRole;

public class PublicAuthority extends User implements Owner, Parcelable {
    private GeoPoint legalSite;        // sede
    private Integer NBeds;  // posti letto

    private ArrayList<Animal> listAnimal;
    private ArrayList<Expense> listExpense;


    public PublicAuthority(String id, String name, String email, String password, long phone, Bitmap photo, GeoPoint legalSite, Integer nBeds) {
        super(id, name, email, password, phone, photo);

        this.legalSite = legalSite;
        this.NBeds = nBeds;

        listAnimal = new ArrayList<>();
        listExpense = new ArrayList<>();
    }

    public void setLegalSite(GeoPoint legalSite) {
        this.legalSite = legalSite;
    }

    public void setNBeds(Integer NBeds) {
        this.NBeds = NBeds;
    }

    public static class Builder {
        private String bID;
        private String bName;
        private String bEmail;
        private String bPassword;
        private long bPhone;
        private Bitmap bPhoto;

        private GeoPoint bLegalSite;
        private Integer bNBeds;

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

        public Builder setNBeds(final Integer bNBeds) {
            this.bNBeds = bNBeds;
            return this;
        }

        public PublicAuthority build() {
            return new PublicAuthority(bID, bName, bEmail, bPassword, bPhone, bPhoto, bLegalSite, bNBeds);
        }
    }

    public GeoPoint getLegalSite() {
        return legalSite;
    }

    public Integer getNBeds() {
        return NBeds;
    }

    @Override
    public UserRole getRole() {
        return UserRole.PUBLIC_AUTHORITY;
    }

    @Override
    public void updateProfile() {

    }

    @Override
    public void addAnimal(Animal animal) {
        this.listAnimal.add(0,animal);
        notifyItemLoaded();
    }

    @Override
    public void removeAnimal(Animal animal) {
        final int index=listAnimal.indexOf(animal);
        listAnimal.remove(animal);
        notifyItemRemoved(index);
    }

    @Override
    public void updateAnimal(Animal animal, boolean profilePictureFlag) {
        final int index=listAnimal.indexOf(animal);

        Animal animalToReplace=this.listAnimal.get(index);

        animalToReplace.assign(animal,profilePictureFlag);

        notifyItemUpdated(index);
    }

    @Override
    public void addExpense(Expense Expense) {
        this.listExpense.add(Expense);
    }

    @Override
    public void removeExpense(Expense Expense) {
        for (Expense a : listExpense) {
            if (a.getFirebaseID().compareTo(Expense.getFirebaseID()) == 0) {
                listExpense.remove(a);
            }
        }
    }

    @Override
    public ArrayList<Animal> getAnimalList() {
        return this.listAnimal;
    }

    @Override
    public ArrayList<Expense> getExpenseList() {
        return this.listExpense;
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
        dest.writeParcelable(getPhoto(),flags);
        dest.writeDouble(legalSite.getLatitude());
        dest.writeDouble(legalSite.getLongitude());
        dest.writeInt(this.NBeds);
        dest.writeList(getAnimalList());
        dest.writeList(getExpenseList());
    }

    protected PublicAuthority(Parcel in) {
        super(in.readString(), in.readString(), in.readString(), in.readString(), in.readLong(), in.readParcelable(Bitmap.class.getClassLoader()));

        double latitude=in.readDouble();
        double longitude=in.readDouble();
        this.legalSite=new GeoPoint(latitude,longitude);

        this.NBeds = in.readInt();
        in.readArrayList(Animal.class.getClassLoader());
        in.readArrayList(Expense.class.getClassLoader());
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

// TODO: Da ultimare bisogna controllare il metodo da creare nel Dao dell'authority
    public void registerAuthority(UserCallback.UserRegisterCallback callback) {

        // Crea un'istanza di PublicAuthorityDao
        PublicAuthorityDao pubblicAuthorityDao = new PublicAuthorityDao();

        // Chiamata al metodo di creazione di PublicAuthorityDao per salvare la PA nel database
        pubblicAuthorityDao.createPublicAuthority(this, callback);

    }
}
