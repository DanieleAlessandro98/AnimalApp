package it.uniba.dib.sms222334.Models;

import android.graphics.Bitmap;
import android.provider.MediaStore;

import java.util.LinkedList;

public class Animal extends Document {

    //in carico, smarrito, adottato, assistico, randagio
    public enum stateList{LOST,IN_CHARGE,ADOPTED,ASSISTED,STRAY}
    private String name;
    private Owner owner;
    private int age;
    private stateList state;
    private String microchip;
    private String species;
    private String race;
    private Bitmap photo;


    private LinkedList<String> videos;

    private LinkedList<String> photos;

    private LinkedList<Visit> visits;

    private LinkedList<Pathology> pathologies;

    private LinkedList<Food> foods;

    private LinkedList<Expense> expenses;
    
    //arraylist<relazioni>


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Owner getOwner() {
        return owner;
    }

    public void setOwner(Owner owner) {
        this.owner = owner;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public stateList getState() {
        return state;
    }

    public void setState(stateList state) {
        this.state = state;
    }

    public String getMicrochip() {
        return microchip;
    }

    public void setMicrochip(String microchip) {
        this.microchip = microchip;
    }

    public String getSpecies() {
        return species;
    }

    public void setSpecies(String species) {
        this.species = species;
    }

    public String getRace() {
        return race;
    }

    public void setRace(String race) {
        this.race = race;
    }

    public Bitmap getPhoto() {
        return photo;
    }

    public void setPhoto(Bitmap photo) {
        this.photo = photo;
    }

    public LinkedList<String> getPhotos() {
        return photos;
    }

    public void setPhotos(LinkedList<String> photos) {
        this.photos = photos;
    }

    public LinkedList<String> getVideos() {
        return videos;
    }

    public void setVideos(LinkedList<String> videos) {
        this.videos = videos;
    }

    public LinkedList<Visit> getVisits() {
        return visits;
    }

    public void setVisits(LinkedList<Visit> visits) {
        this.visits = visits;
    }

    public LinkedList<Pathology> getPathologies() {
        return pathologies;
    }

    public void setPathologies(LinkedList<Pathology> pathologies) {
        this.pathologies = pathologies;
    }

    public LinkedList<Food> getFoods() {
        return foods;
    }

    public void setFoods(LinkedList<Food> foods) {
        this.foods = foods;
    }

    public LinkedList<Expense> getExpenses() {
        return expenses;
    }

    public void setExpenses(LinkedList<Expense> expenses) {
        this.expenses = expenses;
    }
    
    public int getVisitNumber(){
        return this.getVisits().size();
    }

    public int getPathologiesNumber(){
        return this.getPathologies().size();
    }

    private Animal(String name, Owner owner, int age, stateList state, String species, String race, Bitmap photo, String microchip){
        this.name = name;
        this.owner = owner;
        this.age = age;
        this.state = state;
        this.species = species;
        this.race = race;
        this.photo = photo;
        this.microchip = microchip;

        this.videos=new LinkedList<>();
        this.photos=new LinkedList<>();
        this.foods =new LinkedList<>();
        this.pathologies=new LinkedList<>();
        this.visits=new LinkedList<>();
        this.expenses=new LinkedList<>();

    }

    public static class Builder{
        private String bname;
        private Owner bowner;
        private int bage;
        private stateList bstate;
        private String bspecies;
        private String brace;
        private Bitmap bphoto;
        private String bmicrochip;

        private Builder(final stateList state){
            this.bstate=state;
        }

        public static Animal.Builder create(final stateList state){
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

        public Animal.Builder setPhoto(final Bitmap photo){
            this.bphoto=photo;
            return this;
        }

        public Animal build(){
            return new Animal(bname,bowner,bage,bstate,bspecies,brace,bphoto,bmicrochip);
        }
    }
}
