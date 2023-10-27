package it.uniba.dib.sms222334.Models;

import android.os.Parcel;
import android.os.Parcelable;

import it.uniba.dib.sms222334.Database.Dao.Animal.AnimalCallbacks;
import it.uniba.dib.sms222334.Database.Dao.Animal.ExpenseDao;
import it.uniba.dib.sms222334.Database.Dao.User.UserCallback;

public class Expense extends Document implements Parcelable{

    public enum expenseType{ACCESSORY,FOOD,HEALTH}
    private String note;
    private Double price;
    private expenseType category; //categorie per le spese

    private String animalID;

    private Expense(String id, String note, Double price, expenseType category, String animalID) {
        super(id);

        this.note = note;
        this.price = price;
        this.category = category;
        this.animalID = animalID;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public expenseType getCategory() {
        return category;
    }

    public void setCategory(expenseType category) {
        this.category = category;
    }

    public String getAnimalID() {
        return animalID;
    }

    public void setAnimalID(String animalID) {
        this.animalID = animalID;
    }

    public static class Builder {
        private String bID;
        private String bnote;
        private Double bprice;
        private expenseType bcategory;

        private String banimalID;

        private Builder(final String id, final Double price){
            this.bID = id;
            this.bprice=price;
        }


        public static Builder create(final String id, final Double price){
            return new Builder(id, price);
        }


        public Builder setCategory(final expenseType category){
            this.bcategory=category;
            return this;
        }

        public Builder setNote(final String note){
            this.bnote=note;
            return this;
        }

        public Builder setAnimalID(final String animalID){
            this.banimalID=animalID;
            return this;
        }


        public Expense build(){
            return new Expense(bID, bnote,bprice,bcategory,banimalID);
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(getFirebaseID());
        dest.writeString(getNote());
        dest.writeDouble(getPrice());
        dest.writeInt(getCategory().ordinal());
        dest.writeString(getAnimalID());
    }

    protected Expense(Parcel in) {
        super(in.readString());

        this.note = in.readString();
        this.price = in.readDouble();
        this.category = expenseType.values()[in.readInt()];
        this.animalID= in.readString();
    }

    public static final Parcelable.Creator<Expense> CREATOR = new Parcelable.Creator<Expense>() {
        @Override
        public Expense createFromParcel(Parcel in) {
            return new Expense(in);
        }

        @Override
        public Expense[] newArray(int size) {
            return new Expense[size];
        }
    };

    public void delete(AnimalCallbacks.eliminationCallback callback) {
        new ExpenseDao().deleteExpense(this,callback);
    }
}
