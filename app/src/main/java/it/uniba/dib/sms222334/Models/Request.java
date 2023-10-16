package it.uniba.dib.sms222334.Models;

import java.util.ArrayList;

import it.uniba.dib.sms222334.Database.Dao.RequestDao;
import it.uniba.dib.sms222334.Database.DatabaseCallbackResult;
import it.uniba.dib.sms222334.Utils.AnimalSpecies;
import it.uniba.dib.sms222334.Utils.RequestType;


public class Request extends Document{
    private String userID;
    RequestType type;
    private String description;

    private AnimalSpecies animalSpecies;
    private String animalID;
    private int nBeds;

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public RequestType getType() {
        return type;
    }

    public void setType(RequestType type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public AnimalSpecies getAnimalSpecies() {
        return animalSpecies;
    }

    public void setAnimalSpecies(AnimalSpecies animalSpecies) {
        this.animalSpecies = animalSpecies;
    }

    public String getAnimalID() {
        return animalID;
    }

    public void setAnimalID(String animalID) {
        this.animalID = animalID;
    }

    public int getNBeds() {
        return nBeds;
    }

    public void setNBeds(int nBeds) {
        this.nBeds = nBeds;
    }

    private Request(String id, String userID, RequestType type, String description, AnimalSpecies animalSpecies, String animalID, int nBeds) {
        super(id);

        this.userID = userID;
        this.type = type;
        this.description = description;
        this.animalSpecies = animalSpecies;
        this.animalID = animalID;
        this.nBeds = nBeds;
    }

    public static class Builder{
        private String bID;
        private String bUserID;
        RequestType bType;
        private String bDescription;

        private AnimalSpecies bAnimalSpecies;
        private String bAnimalID;
        private int bNBeds;

        private Builder(String bID, String bUserID, RequestType bType, String bDescription) {
            this.bID = bID;
            this.bUserID = bUserID;
            this.bType = bType;
            this.bDescription = bDescription;
        }

        public static Builder create(final String bID, String bUserID, final RequestType bType, String bDescription) {
            return new Builder(bID, bUserID, bType, bDescription);
        }

        public Builder setAnimalSpecies(AnimalSpecies bAnimalSpecies) {
            this.bAnimalSpecies = bAnimalSpecies;
            return this;
        }

        public Builder setAnimalID(String bAnimalID) {
            this.bAnimalID = bAnimalID;
            return this;
        }

        public Builder setNBeds(int bNBeds) {
            this.bNBeds = bNBeds;
            return this;
        }

        public Request build() {
            return new Request(bID, bUserID, bType, bDescription, bAnimalSpecies, bAnimalID, bNBeds);
        }
    }

    public void createRequest(DatabaseCallbackResult callbackPresenter) {
        RequestDao requestDao = new RequestDao();
        requestDao.createRequest(this, new DatabaseCallbackResult() {

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
