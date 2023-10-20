package it.uniba.dib.sms222334.Models;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.firestore.GeoPoint;

import java.util.ArrayList;
import java.util.List;

import it.uniba.dib.sms222334.Database.Dao.User.PublicAuthorityDao;
import it.uniba.dib.sms222334.Database.Dao.User.UserCallback;
import it.uniba.dib.sms222334.Database.Dao.User.VeterinarianDao;
import it.uniba.dib.sms222334.Utils.UserRole;

public class Veterinarian extends User implements Parcelable{
    private GeoPoint legalSite; //sede

    ArrayList<Visit> visitList;

    public void setLegalSite(GeoPoint legalSite) {
        this.legalSite = legalSite;
    }

    public GeoPoint getLegalSite() {
        return legalSite;
    }

    public ArrayList<Visit> getVisitList() {
        return visitList;
    }

    public void setVisitList(ArrayList<Visit> visitList) {
        this.visitList = visitList;
    }

    public Veterinarian(String id, String name, String email, String password, long phone, Bitmap photo, GeoPoint legalSite) {
        super(id, name, email, password, phone, photo);

        this.legalSite = legalSite;
        this.visitList=new ArrayList<>();
    }

    public static void getVeterinarianAndPublicAuthority(final VeterinarianDao.OnCombinedListener listener){
        VeterinarianDao dao = new VeterinarianDao();
        dao.getVeterinariansAndPublicAuthorities(new VeterinarianDao.OnCombinedListener() {
            @Override
            public void onGetCombinedData(List<User> UserList) {
                listener.onGetCombinedData(UserList);
            }
        });
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
        dest.writeParcelable(getPhoto(),flags);
        dest.writeDouble(legalSite.getLatitude());
        dest.writeDouble(legalSite.getLongitude());
    }

    protected Veterinarian(Parcel in) {
        super(in.readString(), in.readString(), in.readString(), in.readString(), in.readLong(), in.readParcelable(Bitmap.class.getClassLoader()));
        double latitude=in.readDouble();
        double longitude=in.readDouble();

        this.legalSite=new GeoPoint(latitude,longitude);
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

    public void updateProfile() {

    }
    public void registerVeterinarian(UserCallback.UserRegisterCallback callback) {

        // Crea un'istanza di PublicAuthorityDao
        VeterinarianDao veterinarianDao = new VeterinarianDao();

        // Chiamata al metodo di creazione di VeterinarianDao per salvare il Veterinarian nel database
        veterinarianDao.createVeterinarian(this, callback);
    }
    @Override
    public void deleteProfile() {

    }
}
