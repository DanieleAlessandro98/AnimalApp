package it.uniba.dib.sms222334.Models;

import android.graphics.Bitmap;


public class Request extends Document{
    public enum requestType{FIND_ANIMAL,OFFER_ANIMAL,OFFER_BEDS}
    String AnimalName,Species,Age,CreatorName,Description;
    requestType type;
    public Float latitude;
    public Float longitude;
    Bitmap creatorPhoto, requestPhoto;

    public String getAnimalName() {
        return AnimalName;
    }

    public void setAnimalName(String animalName) {
        AnimalName = animalName;
    }

    public String getSpecies() {
        return Species;
    }

    public void setSpecies(String species) {
        Species = species;
    }

    public String getAge() {
        return Age;
    }

    public void setAge(String age) {
        Age = age;
    }

    public String getCreatorName() {
        return CreatorName;
    }

    public void setCreatorName(String creatorName) {
        CreatorName = creatorName;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public requestType getType() {
        return type;
    }

    public void setType(requestType type) {
        this.type = type;
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

    public Bitmap getCreatorPhoto() {
        return creatorPhoto;
    }

    public void setCreatorPhoto(Bitmap creatorPhoto) {
        this.creatorPhoto = creatorPhoto;
    }

    public Bitmap getRequestPhoto() {
        return requestPhoto;
    }

    public void setRequestPhoto(Bitmap requestPhoto) {
        this.requestPhoto = requestPhoto;
    }

    private Request(String animalName, String species, String age, String creatorName, String description, requestType type, Float latitude, Float longitude, Bitmap creatorPhoto, Bitmap requestPhoto) {
        this.AnimalName = animalName;
        this.Species = species;
        this.Age=age;
        this.CreatorName = creatorName;
        this.Description = description;
        this.type = type;
        this.latitude = latitude;
        this.longitude = longitude;
        this.creatorPhoto = creatorPhoto;
        this.requestPhoto=requestPhoto;
    }

    public static class Builder{
        String bAnimalName, bSpecies,bAge, bCreatorName, bDescription;
        requestType btype;
        public Float blatitude;
        public Float blongitude;
        Bitmap bcreatorPhoto, brequestPhoto;

        private Builder(final requestType type,Float latitude, Float longitude){
            this.blatitude=latitude;
            this.blongitude=longitude;
            this.btype=type;
        }

        public static Builder create(final requestType type,Float latitude, Float longitude){
            return new Builder(type,latitude,longitude);
        }

        public Builder setAnimalName(String AnimalName) {
            this.bAnimalName = AnimalName;
            return this;
        }

        public Builder setSpecies(String Species) {
            this.bSpecies = Species;
            return this;
        }

        public Builder setAge(String Age) {
            this.bAge = Age;
            return this;
        }

        public Builder setCreatorName(String CreatorName) {
            this.bCreatorName = CreatorName;
            return this;
        }

        public Builder setDescription(String Description) {
            this.bDescription = Description;
            return this;
        }

        public Builder setType(requestType type) {
            this.btype = type;
            return this;
        }

        public Builder setLatitude(Float latitude) {
            this.blatitude = latitude;
            return this;
        }

        public Builder setLongitude(Float longitude) {
            this.blongitude = longitude;
            return this;
        }

        public Builder setCreatorPhoto(Bitmap creatorPhoto) {
            this.bcreatorPhoto = creatorPhoto;
            return this;
        }

        public Builder setRequestPhoto(Bitmap requestPhoto) {
            this.brequestPhoto = requestPhoto;
            return this;
        }

        public Request build(){
            return new Request(bAnimalName,bSpecies,bAge,bCreatorName,bDescription,btype,blatitude,blongitude,bcreatorPhoto,brequestPhoto);
        }
    }
}
