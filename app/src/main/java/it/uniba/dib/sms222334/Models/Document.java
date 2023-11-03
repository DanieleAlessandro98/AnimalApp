package it.uniba.dib.sms222334.Models;

import android.os.Parcel;
import android.os.Parcelable;

public class Document implements Parcelable {
    private String firebaseID;
    private boolean showMenu;

    public Document(String firebaseID) {
        this.firebaseID = firebaseID;
    }

    public String getFirebaseID() {
        return firebaseID;
    }

    public void setFirebaseID(String firebaseID) {
        this.firebaseID = firebaseID;
    }

    public boolean isShowMenu() {
        return showMenu;
    }

    public void setShowMenu(boolean showMenu) {
        this.showMenu = showMenu;
    }

    // Implementazione dei metodi Parcelable
    protected Document(Parcel in) {
        firebaseID = in.readString();
        showMenu = in.readByte() != 0;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(firebaseID);
        dest.writeByte((byte) (showMenu ? 1 : 0));
    }

    public static final Creator<Document> CREATOR = new Creator<Document>() {
        @Override
        public Document createFromParcel(Parcel in) {
            return new Document(in);
        }

        @Override
        public Document[] newArray(int size) {
            return new Document[size];
        }
    };
}
