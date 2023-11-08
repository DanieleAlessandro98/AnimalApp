package it.uniba.dib.sms222334.Models;

import android.content.Context;
import android.graphics.Bitmap;

import com.google.firebase.firestore.GeoPoint;

import java.util.ArrayList;
import java.util.Date;

import it.uniba.dib.sms222334.Database.Dao.ReportDao;
import it.uniba.dib.sms222334.Database.DatabaseCallbackResult;
import it.uniba.dib.sms222334.Presenters.ReportPresenter;
import it.uniba.dib.sms222334.R;
import it.uniba.dib.sms222334.Utils.AnimalSpecies;
import it.uniba.dib.sms222334.Utils.ReportType;

public class Report extends Document {
    private User user;
    private ReportType type;
    private AnimalSpecies animalSpecies;
    private String description;
    private GeoPoint location;
    private Bitmap reportPhoto;

    private Animal animal;
    private String animalName;
    private Date animalAge;
    private boolean showAnimalProfile;

    private float distance;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

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

    public GeoPoint getLocation() {
        return location;
    }

    public void setLocation(GeoPoint location) {
        this.location = location;
    }

    public Bitmap getReportPhoto() {
        return reportPhoto;
    }

    public void setReportPhoto(Bitmap reportPhoto) {
        this.reportPhoto = reportPhoto;
    }

    public Animal getAnimal() {
        return animal;
    }

    public void setAnimal(Animal animal) {
        this.animal = animal;
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

    public boolean isShowAnimalProfile() {
        return showAnimalProfile;
    }

    public void setShowAnimalProfile(boolean showAnimalProfile) {
        this.showAnimalProfile = showAnimalProfile;
    }

    public float getDistance() {
        return distance;
    }

    public void setDistance(float distance) {
        this.distance = distance;
    }

    private Report(String firebaseID, User user, ReportType type, AnimalSpecies animalSpecies, String description, GeoPoint location, Bitmap reportPhoto, Animal animal, String animalName, Date animalAge, boolean isShowAnimalProfile) {
        super(firebaseID);

        this.user = user;
        this.type = type;
        this.animalSpecies = animalSpecies;
        this.description = description;
        this.location = location;
        this.reportPhoto = reportPhoto;
        this.animal = animal;
        this.animalName = animalName;
        this.animalAge = animalAge;
        this.showAnimalProfile = isShowAnimalProfile;
    }

    public static class Builder {
        private String bID;
        private User bUser;
        private ReportType bType;
        private AnimalSpecies bAnimalSpecies;
        private String bDescription;
        private GeoPoint bLocation;
        private Bitmap bReportPhoto;

        private Animal bAnimal;
        private String bAnimalName;
        private Date bAnimalAge;
        private boolean bIsShowAnimalProfile;

        private Builder(String bID, User bUser, ReportType bType, AnimalSpecies bAnimalSpecies, String bDescription, GeoPoint bLocation, Bitmap bReportPhoto) {
            this.bID = bID;
            this.bUser = bUser;
            this.bType = bType;
            this.bAnimalSpecies = bAnimalSpecies;
            this.bDescription = bDescription;
            this.bLocation = bLocation;
            this.bReportPhoto = bReportPhoto;
        }

        public static Builder create(String bID, User bUser, ReportType bType, AnimalSpecies bAnimalSpecies, String bDescription, GeoPoint bLocation, Bitmap bReportPhoto) {
            return new Builder(bID, bUser, bType, bAnimalSpecies, bDescription, bLocation, bReportPhoto);
        }

        public Builder setAnimal(Animal bAnimal) {
            this.bAnimal = bAnimal;
            return this;
        }

        public Builder setAnimalName(String bAnimalName) {
            this.bAnimalName = bAnimalName;
            return this;
        }

        public Builder setAnimalAge(Date bAnimalAge) {
            this.bAnimalAge = bAnimalAge;
            return this;
        }

        public Builder setShowAnimalProfile(boolean bIsShowAnimalProfile) {
            this.bIsShowAnimalProfile = bIsShowAnimalProfile;
            return this;
        }

        public Report build() {
            return new Report(bID, bUser, bType, bAnimalSpecies, bDescription, bLocation, bReportPhoto, bAnimal, bAnimalName, bAnimalAge, bIsShowAnimalProfile);
        }
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

    public void deleteReport(DatabaseCallbackResult callbackPresenter) {
        ReportDao reportDao = new ReportDao();
        reportDao.deleteReport(this, callbackPresenter);
    }

    public void updateReport(DatabaseCallbackResult callbackPresenter) {
        ReportDao reportDao = new ReportDao();
        reportDao.updateReport(this, callbackPresenter);
    }

    public static String getReportTypeString(ReportType type, Context context) {
        switch (type) {
            case FIND:
                return context.getString(R.string.find_animal_report_name);
            case LOST:
                return context.getString(R.string.lost_animal_report_name);
            case IN_DANGER:
                return context.getString(R.string.danger_animal_report_name);
            default:
                return "";
        }
    }
}
