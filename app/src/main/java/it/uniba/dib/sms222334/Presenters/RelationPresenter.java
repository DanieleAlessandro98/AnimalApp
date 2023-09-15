package it.uniba.dib.sms222334.Presenters;

import it.uniba.dib.sms222334.Models.Animal;
import it.uniba.dib.sms222334.Models.Relation;

public class RelationPresenter {

    public RelationPresenter(){

    }
    private Relation relation;


    public Relation createRelation(String id, Relation.relationType relationCategory, Animal animal) {
        if (animal != null  && isAlphaNumeric(id)) {
            relation = Relation.Builder.create(id, relationCategory, animal).build();
            if (relation.createRelation()) {
                return relation;
            }else{
                return null;
            }
        }else{
            return null;
        }
    }

    public boolean deleteRelation(String idAnimal1,String idAnimal2){
        if (isAlphaNumeric(idAnimal1)) {
            return Relation.deleteRelation(idAnimal1,idAnimal2);
        }else{
            return false;
        }
    }

    private boolean isAlphabet(String s){
        return s != null && s.matches("^[a-zA-Z]*$");
    }

    public static boolean isAlphaNumeric(String s) {
        return s != null && s.matches("^[a-zA-Z0-9]*$");
    }

}
