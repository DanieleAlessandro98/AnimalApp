package it.uniba.dib.sms222334.Models;

import android.media.Image;
import android.provider.MediaStore;

import java.util.LinkedList;

public class Animal extends Document {

    //in carico, smarrito, adottato, assistico, randagio
    public static enum stateList{LOST,IN_CHARGE,ADOPTED,ASSISTED,STRAY}
    private String name;
    private Owner owner;
    private int age;
    private int state;
    private String species;
    private String race;
    private Image photo;

    private LinkedList<String> images;

    private LinkedList<String> videos;
    private String microchip;

    //arraylist<visite>
    //arraylist<patologia>
    //arraylist<cibo>
    //arraylist<video>
    //arraylist<foto>
    //arraylist<spesa>
    //arraylist<relazioni>

    private Animal(String name, Owner owner, int age, int state, String species, String race, Image photo, String microchip){
        this.name = name;
        this.owner = owner;
        this.age = age;
        this.state = state;
        this.species = species;
        this.race = race;
        this.photo = photo;
        this.microchip = microchip;
    }

    public static class Builder{
        private String bname;
        private Owner bowner;
        private int bage;
        private int bstate;
        private String bspecies;
        private String brace;
        private Image bphoto;
        private String bmicrochip;

        private Builder(final int state){
            this.bstate=state;
        }

        public static Animal.Builder create(final int state){
            return new Animal.Builder(state);
        }

        public Animal.Builder setName(final String name){
            this.bname=name;
            return this;
        }

        public Animal.Builder setOwner(final Owner owner){
            this.bowner=owner;
            return this;
        }

        public Animal.Builder setAge(int age){
            this.bage=age;
            return this;
        }

        public Animal.Builder setSpecies(final String species){
            this.bspecies=species;
            return this;
        }

        public Animal.Builder setMicrochip(final String microchip){
            this.bmicrochip=microchip;
            return this;
        }

        public Animal.Builder setPhoto(final Image photo){
            this.bphoto=photo;
            return this;
        }

        public Animal build(){
            return new Animal(bname,bowner,bage,bstate,bspecies,brace,bphoto,bmicrochip);
        }
    }
}
