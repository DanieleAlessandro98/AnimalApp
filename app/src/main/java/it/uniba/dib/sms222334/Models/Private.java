package it.uniba.dib.sms222334.Models;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import java.time.temporal.TemporalAccessor;
import java.util.ArrayList;
import java.util.Date;

import it.uniba.dib.sms222334.Database.Dao.User.PrivateDao;
import it.uniba.dib.sms222334.Utils.UserRole;

public class Private extends User implements Owner, Parcelable {
    private String surname;
    private Date birthDate;
    private String taxIDCode; //codice_fiscale

    private ArrayList<Animal> listAnimal;
    private ArrayList<Expense> listExpense;

    public Private(String id, String name, String email, String password, Long phone, Bitmap photo, String surname, Date birthDate, String taxIDCode) {
        super(id, name, email, password, phone, photo);

        this.surname = surname;
        this.birthDate = birthDate;
        this.taxIDCode = taxIDCode;

        listAnimal = new ArrayList<>();
        listExpense = new ArrayList<>();
    }


    public static class Builder{
        private String bID;
        private String bName;
        private String bEmail;
        private String bPassword;
        private Long bPhone;
        private Bitmap bPhoto;

        private String bSurname;
        private Date bBirthDate;
        private String bTaxID;
        //ArrayList<Animali>

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

        public Builder setPhone(final Long phone){
            this.bPhone=phone;
            return this;
        }

        public Builder setPhoto(final Bitmap photo){
            this.bPhoto=photo;
            return this;
        }

        public Builder setSurname(final String surname) {
            this.bSurname = surname;
            return this;
        }

        public Builder setBirthDate(final Date birthDate) {
            this.bBirthDate = birthDate;
            return this;
        }

        public Builder setTaxIdCode(final String taxIDCode) {
            this.bTaxID = taxIDCode;
            return this;
        }

        public Private build() {
            return new Private(bID, bName, bEmail, bPassword, bPhone, bPhoto, bSurname, bBirthDate, bTaxID);
        }
    }

    public String getSurname() {
        return surname;
    }

    public Date getBirthDate() {
        return birthDate;
    }

    public String getTaxIDCode() {
        return taxIDCode;
    }

    @Override
    public UserRole getRole() {
        return UserRole.PRIVATE;
    }

    @Override
    public void updateProfile() {
        PrivateDao privateDao = new PrivateDao();
        privateDao.updatePrivate(this,null);
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
        dest.writeString(surname);
        dest.writeSerializable(birthDate);
        dest.writeString(taxIDCode);
        dest.writeList(getAnimalList());
        dest.writeList(getExpenseList());
    }

    protected Private(Parcel in) {
        super(in.readString(), in.readString(), in.readString(), in.readString(), in.readLong(), in.readParcelable(Bitmap.class.getClassLoader()));
        surname = in.readString();
        birthDate = (Date) in.readSerializable();
        taxIDCode = in.readString();
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
}
