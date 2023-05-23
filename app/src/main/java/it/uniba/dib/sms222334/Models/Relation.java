package it.uniba.dib.sms222334.Models;

public class Relation {

    public enum relationType{FRIEND,INCOMPATIBLE,COHABITEE}

    relationType relationType;
    Animal animal;

    private Relation(Animal animal,relationType relationType){
        this.animal=animal;
        this.relationType= relationType;
    }

    public Relation.relationType getRelationType() {
        return relationType;
    }

    public void setRelationType(Relation.relationType relationType) {
        this.relationType = relationType;
    }

    public Animal getAnimal() {
        return animal;
    }

    public void setAnimal(Animal animal) {
        this.animal = animal;
    }

    public static class Builder{
        relationType brelationType;
        Animal banimal;

        private Builder(final relationType type, Animal animal){
            this.banimal=animal;
            this.brelationType=type;
        }

        public static Builder create(final relationType type, Animal animal){
            return new Builder(type,animal);
        }

        public Builder setAnimal(Animal animal){
            this.banimal=animal;
            return this;
        }

        public Builder setRelationType(relationType type){
            this.brelationType=type;
            return this;
        }

        public Relation build(){
            return new Relation(banimal,brelationType);
        }
    }


}
