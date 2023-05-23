package it.uniba.dib.sms222334.Models;

import android.graphics.Bitmap;

public class Segnalation extends Document{
    public enum segnalationType{LOST,FIND,IN_DANGER}
    private String AnimalName,Species,Age,CreatorName,Description;
    private segnalationType type;
    private Float latitude;
    private Float longitude;
    private Bitmap creatorPhoto, segnalationPhoto;

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

    public segnalationType getType() {
        return type;
    }

    public void setType(segnalationType type) {
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

    public Bitmap getsegnalationPhoto() {
        return segnalationPhoto;
    }

    public void setsegnalationPhoto(Bitmap segnalationPhoto) {
        this.segnalationPhoto = segnalationPhoto;
    }

    private Segnalation(String animalName, String species, String age, String creatorName, String description, segnalationType type, Float latitude, Float longitude, Bitmap creatorPhoto, Bitmap segnalationPhoto) {
        this.AnimalName = animalName;
        this.Species = species;
        this.Age=age;
        this.CreatorName = creatorName;
        this.Description = description;
        this.type = type;
        this.latitude = latitude;
        this.longitude = longitude;
        this.creatorPhoto = creatorPhoto;
        this.segnalationPhoto=segnalationPhoto;
    }

    public static class Builder{
        String bAnimalName, bSpecies,bAge, bCreatorName, bDescription;
        segnalationType btype;
        public Float blatitude;
        public Float blongitude;
        Bitmap bcreatorPhoto, bsegnalationPhoto;

        private Builder(final segnalationType type,Float latitude, Float longitude,Bitmap segnalationPhoto){
            this.blatitude=latitude;
            this.blongitude=longitude;
            this.btype=type;
        }

        public static Builder create(final segnalationType type,Float latitude, Float longitude,Bitmap segnalationPhoto){
            return new Builder(type,latitude,longitude,segnalationPhoto);
        }

        public  Builder setAnimalName(String AnimalName) {
            this.bAnimalName = AnimalName;
            return this;
        }

        public  Builder setSpecies(String Species) {
            this.bSpecies = Species;
            return this;
        }

        public  Builder setAge(String Age) {
            this.bAge = Age;
            return this;
        }

        public  Builder setCreatorName(String CreatorName) {
            this.bCreatorName = CreatorName;
            return this;
        }

        public  Builder setDescription(String Description) {
            this.bDescription = Description;
            return this;
        }

        public  Builder setType( segnalationType type) {
            this.btype = type;
            return this;
        }

        public  Builder setLatitude(Float latitude) {
            this.blatitude = latitude;
            return this;
        }

        public  Builder setLongitude(Float longitude) {
            this.blongitude = longitude;
            return this;
        }

        public  Builder setCreatorPhoto(Bitmap creatorPhoto) {
            this.bcreatorPhoto = creatorPhoto;
            return this;
        }

        public  Builder setSegnalationPhoto(Bitmap segnalationPhoto) {
            this.bsegnalationPhoto = segnalationPhoto;
            return this;
        }

        public Segnalation build(){
            return new Segnalation(bAnimalName,bSpecies,bAge,bCreatorName,bDescription,btype,blatitude,blongitude,bcreatorPhoto,bsegnalationPhoto);
        }
    }
}
