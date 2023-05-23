package it.uniba.dib.sms222334.Models;

import android.view.animation.AnimationUtils;

public class Food extends Document{

    private Animal animal;
    private String name;

    private Food(String name, Animal animal) {
        this.name = name;
        this.animal=animal;
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
        private String bName;

        private Animal bAnimal;

        private Builder( final String name, final Animal animal){
            this.bName=name;
            this.bAnimal=animal;
        }

        public Builder setName(final String name){
            this.bName=name;
            return this;
        }

        public Builder setAnimal(final Animal animal){
            this.bAnimal=animal;
            return this;
        }

        public static Builder create(final String name, final Animal animal){
            return new Builder(name, animal);
        }

        public Food build(){
            return new Food(bName, bAnimal);
        }
    }
}
