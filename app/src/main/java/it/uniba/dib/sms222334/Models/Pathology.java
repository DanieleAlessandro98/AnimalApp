package it.uniba.dib.sms222334.Models;


import it.uniba.dib.sms222334.Database.Dao.PathologyDao;

public class Pathology extends Document{
    private Animal animal;
    private String name;

    private Pathology(String id, Animal animal, String name) {
        super(id);

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
        private String bID;
        private Animal bAnimal;
        private String bName;

        private Builder(final String id, final Animal Animal, final String name){
            this.bID = id;
            this.bAnimal=Animal;
            this.bName=name;
        }

        public static Builder create(final String id, final Animal Animal, final String name){
            return new Builder(id, Animal,name);
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
            return new Pathology(bID, bAnimal, bName);
        }
    }

    public void delete() {
        PathologyDao pathologyDao = new PathologyDao();
        pathologyDao.deletePathology(this);
    }
}
