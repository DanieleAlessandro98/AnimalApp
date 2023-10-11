package it.uniba.dib.sms222334.Models;

import android.graphics.Bitmap;
import android.util.Log;

import com.google.firebase.firestore.GeoPoint;

import java.util.ArrayList;

import it.uniba.dib.sms222334.Database.Dao.ReportDao;
import it.uniba.dib.sms222334.Database.Dao.User.PrivateDao;
import it.uniba.dib.sms222334.Database.Dao.User.UserCallback;
import it.uniba.dib.sms222334.Database.DatabaseCallbackResult;
import it.uniba.dib.sms222334.Utils.AnimalSpecies;
import it.uniba.dib.sms222334.Utils.ReportType;

public class Report extends Document {
    private ReportType type;
    private AnimalSpecies animalSpecies;
    private String description;
    private Float latitude;
    private Float longitude;
    private Bitmap reportPhoto;

    private String animalName;
    private int animalAge;

    public ReportType getType() {
        return type;
    }

    public void setType(ReportType type) {
        this.type = type;
    }

    public AnimalSpecies getAnimalSpecies() {
        return animalSpecies;
    }

    public void setAnimalSpecies(AnimalSpecies animalSpecies) {
        this.animalSpecies = animalSpecies;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Float getLatitude() {
        return latitude;
    }

    public void setLatitude(Float latitude) {
        this.latitude = latitude;
    }

    public Float getLongitude() {
        return longitude;
    }

    public void setLongitude(Float longitude) {
        this.longitude = longitude;
    }

    public Bitmap getReportPhoto() {
        return reportPhoto;
    }

    public void setReportPhoto(Bitmap reportPhoto) {
        this.reportPhoto = reportPhoto;
    }

    public String getAnimalName() {
        return animalName;
    }

    public void setAnimalName(String animalName) {
        this.animalName = animalName;
    }

    public int getAnimalAge() {
        return animalAge;
    }

    public void setAnimalAge(int animalAge) {
        this.animalAge = animalAge;
    }

    public Report(String firebaseID, ReportType type, AnimalSpecies animalSpecies, String description, Float latitude, Float longitude, Bitmap reportPhoto, String animalName, int animalAge) {
        super(firebaseID);
        this.type = type;
        this.animalSpecies = animalSpecies;
        this.description = description;
        this.latitude = latitude;
        this.longitude = longitude;
        this.reportPhoto = reportPhoto;
        this.animalName = animalName;
        this.animalAge = animalAge;
    }

    public static class Builder {
        private String bID;
        private ReportType bType;
        private AnimalSpecies bAnimalSpecies;
        private String bDescription;
        private Float bLatitude;
        private Float bLongitude;
        private Bitmap bReportPhoto;

        private String bAnimalName;
        private int bAnimalAge;

        private Builder(String bID, ReportType bType, AnimalSpecies bAnimalSpecies, String bDescription, Float bLatitude, Float bLongitude, Bitmap bReportPhoto) {
            this.bID = bID;
            this.bType = bType;
            this.bAnimalSpecies = bAnimalSpecies;
            this.bDescription = bDescription;
            this.bLatitude = bLatitude;
            this.bLongitude = bLongitude;
            this.bReportPhoto = bReportPhoto;
        }

        public static Builder create(String bID, ReportType bType, AnimalSpecies bAnimalSpecies, String bDescription, Float bLatitude, Float bLongitude, Bitmap bReportPhoto) {
            return new Builder(bID, bType, bAnimalSpecies, bDescription, bLatitude, bLongitude, bReportPhoto);
        }

        public Builder setAnimalName(String bAnimalName) {
            this.bAnimalName = bAnimalName;
            return this;
        }

        public Builder setAge(int bAnimalAge) {
            this.bAnimalAge = bAnimalAge;
            return this;
        }

        public Report build() {
            return new Report(bID, bType, bAnimalSpecies, bDescription, bLatitude, bLongitude, bReportPhoto, bAnimalName, bAnimalAge);
        }
    }

    public GeoPoint getLocation() {
        return new GeoPoint(latitude, longitude);
    }

    public void createReport(DatabaseCallbackResult callbackPresenter) {
        ReportDao reportDao = new ReportDao();
        reportDao.createReport(this, new DatabaseCallbackResult() {

            @Override
            public void onDataRetrieved(Object result) {
                callbackPresenter.onDataRetrieved(result);
            }

            @Override
            public void onDataRetrieved(ArrayList results) {
            }

            @Override
            public void onDataNotFound() {
            }

            @Override
            public void onDataQueryError(Exception e) {
                callbackPresenter.onDataQueryError(e);
            }
        });
    }
}
