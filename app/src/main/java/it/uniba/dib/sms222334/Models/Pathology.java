package it.uniba.dib.sms222334.Models;


public class Pathology extends Document{
    private int animalID;
    private String name;

    private Pathology(int animalID, String name) {
        this.animalID = animalID;
        this.name = name;
    }

    public static class Builder {
        private int bAnimalID;
        private String bName;

        private Builder(final int animalID, final String name){
            this.bAnimalID=animalID;
            this.bName=name;
        }

        public static Builder create(final int animalID, final String name){
            return new Builder(animalID,name);
        }

        public Pathology build(){
            return new Pathology(bAnimalID, bName);
        }
    }
}
