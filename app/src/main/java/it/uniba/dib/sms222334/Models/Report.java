package it.uniba.dib.sms222334.Models;

import android.graphics.Bitmap;

import com.google.firebase.firestore.GeoPoint;

import java.util.ArrayList;
import java.util.Date;

import it.uniba.dib.sms222334.Database.Dao.Animal.AnimalDao;
import it.uniba.dib.sms222334.Database.Dao.ReportDao;
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
    private Date animalAge;
    private String animalID;
    private boolean showAnimalProfile;

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

    public Date getAnimalAge() {
        return animalAge;
    }

    public void setAnimalAge(Date animalAge) {
        this.animalAge = animalAge;
    }

    public String getAnimalID() {
        return animalID;
    }

    public void setAnimalID(String animalID) {
        this.animalID = animalID;
    }

    public boolean isShowAnimalProfile() {
        return showAnimalProfile;
    }

    public void setShowAnimalProfile(boolean showAnimalProfile) {
        this.showAnimalProfile = showAnimalProfile;
    }

    private Report(String firebaseID, ReportType type, AnimalSpecies animalSpecies, String description, Float latitude, Float longitude, Bitmap reportPhoto, String animalName, Date animalAge, String animalID, boolean isShowAnimalProfile) {
        super(firebaseID);

        this.type = type;
        this.animalSpecies = animalSpecies;
        this.description = description;
        this.latitude = latitude;
        this.longitude = longitude;
        this.reportPhoto = reportPhoto;
        this.animalName = animalName;
        this.animalAge = animalAge;
        this.animalID = animalID;
        this.showAnimalProfile = isShowAnimalProfile;
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
        private Date bAnimalAge;
        private String bAnimalID;
        private boolean bIsShowAnimalProfile;

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

        public Builder setAnimalAge(Date bAnimalAge) {
            this.bAnimalAge = bAnimalAge;
            return this;
        }

        public Builder setAnimalID(String bAnimalID) {
            this.bAnimalID = bAnimalID;
            return this;
        }

        public Builder setShowAnimalProfile(boolean bIsShowAnimalProfile) {
            this.bIsShowAnimalProfile = bIsShowAnimalProfile;
            return this;
        }

        public Report build() {
            return new Report(bID, bType, bAnimalSpecies, bDescription, bLatitude, bLongitude, bReportPhoto, bAnimalName, bAnimalAge, bAnimalID, bIsShowAnimalProfile);
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
                if (type == ReportType.LOST) {
                    AnimalDao animalDao = new AnimalDao();
                    animalDao.updateStateToLost(animalID);
                }

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
