package it.uniba.dib.sms222334.Models;


public class Pathology extends Document{
    private Animal animal;
    private String name;

    private Pathology(Animal animal, String name) {
        this.animal = animal;
        this.name = name;
    }

    public Animal getAnimal() {
        return animal;
    }

    public void setAnimal(Animal animal) {
        this.animal = animal;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public static class Builder {
        private Animal bAnimal;
        private String bName;

        private Builder(final Animal Animal, final String name){
            this.bAnimal=Animal;
            this.bName=name;
        }

        public static Builder create(final Animal Animal, final String name){
            return new Builder(Animal,name);
        }

        public Builder setName(final String name){
            this.bName=name;
            return this;
        }

        public Builder setAnimal(final Animal animal){
            this.bAnimal=animal;
            return this;
        }

        public Pathology build(){
            return new Pathology(bAnimal, bName);
        }
    }
}
