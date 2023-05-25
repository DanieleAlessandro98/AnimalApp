package it.uniba.dib.sms222334.Models;

public class Document {
    private final String firebaseID;

    public Document(String firebaseID) {
        this.firebaseID = firebaseID;
    }

    public String getFirebaseID() {
        return firebaseID;
    }
}
