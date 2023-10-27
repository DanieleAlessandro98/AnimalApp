package it.uniba.dib.sms222334.Models;

import android.content.Context;

import com.google.firebase.firestore.GeoPoint;

import java.util.ArrayList;

import it.uniba.dib.sms222334.Database.Dao.RequestDao;
import it.uniba.dib.sms222334.Database.DatabaseCallbackResult;
import it.uniba.dib.sms222334.R;
import it.uniba.dib.sms222334.Utils.AnimalSpecies;
import it.uniba.dib.sms222334.Utils.RequestType;


public class Request extends Document{
    private User user;
    private RequestType type;
    private String description;
    private GeoPoint location;

    private AnimalSpecies animalSpecies;
    private Animal animal;
    private int nBeds;

    private float distance;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
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

    public GeoPoint getLocation() {
        return location;
    }

    public void setLocation(GeoPoint location) {
        this.location = location;
    }

    public AnimalSpecies getAnimalSpecies() {
        return animalSpecies;
    }

    public void setAnimalSpecies(AnimalSpecies animalSpecies) {
        this.animalSpecies = animalSpecies;
    }

    public Animal getAnimal() {
        return animal;
    }

    public void setAnimal(Animal animal) {
        this.animal = animal;
    }

    public int getNBeds() {
        return nBeds;
    }

    public void setNBeds(int nBeds) {
        this.nBeds = nBeds;
    }

    public float getDistance() {
        return distance;
    }

    public void setDistance(float distance) {
        this.distance = distance;
    }

    private Request(String id, User user, RequestType type, String description, GeoPoint location, AnimalSpecies animalSpecies, Animal animal, int nBeds) {
        super(id);

        this.user = user;
        this.type = type;
        this.description = description;
        this.location = location;
        this.animalSpecies = animalSpecies;
        this.animal = animal;
        this.nBeds = nBeds;
    }

    public static class Builder{
        private String bID;
        private User bUser;
        RequestType bType;
        private String bDescription;
        private GeoPoint bLocation;

        private AnimalSpecies bAnimalSpecies;
        private Animal bAnimal;
        private int bNBeds;

        private Builder(String bID, User bUser, RequestType bType, String bDescription, GeoPoint bLocation) {
            this.bID = bID;
            this.bUser = bUser;
            this.bType = bType;
            this.bDescription = bDescription;
            this.bLocation = bLocation;
        }

        public static Builder create(final String bID, User bUser, final RequestType bType, String bDescription, GeoPoint bLocation) {
            return new Builder(bID, bUser, bType, bDescription, bLocation);
        }

        public Builder setAnimalSpecies(AnimalSpecies bAnimalSpecies) {
            this.bAnimalSpecies = bAnimalSpecies;
            return this;
        }

        public Builder setAnimal(Animal bAnimal) {
            this.bAnimal = bAnimal;
            return this;
        }

        public Builder setNBeds(int bNBeds) {
            this.bNBeds = bNBeds;
            return this;
        }

        public Request build() {
            return new Request(bID, bUser, bType, bDescription, bLocation, bAnimalSpecies, bAnimal, bNBeds);
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

    public static String getRequestTypeString(RequestType type, Context context) {
        switch (type) {
            case FIND_ANIMAL:
                return context.getString(R.string.find_animal_request_name);
            case OFFER_ANIMAL:
                return context.getString(R.string.offer_animal_request_name);
            case OFFER_BEDS:
                return context.getString(R.string.offer_beds_request_name);
            default:
                return "";
        }
    }
}
