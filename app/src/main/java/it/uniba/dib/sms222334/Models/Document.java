package it.uniba.dib.sms222334.Models;

public class Document {
    private String firebaseID;

    public Document(String firebaseID) {
        this.firebaseID = firebaseID;
    }

    public String getFirebaseID() {
        return firebaseID;
    }

    public void setFirebaseID(String firebaseID) {
        this.firebaseID=firebaseID;
    }
}
