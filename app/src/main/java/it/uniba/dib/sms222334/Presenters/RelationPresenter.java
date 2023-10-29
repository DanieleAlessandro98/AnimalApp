package it.uniba.dib.sms222334.Presenters;

import java.util.ArrayList;
import java.util.List;

import it.uniba.dib.sms222334.Database.Dao.RelationDao;
import it.uniba.dib.sms222334.Models.Animal;
import it.uniba.dib.sms222334.Models.Relation;

public class RelationPresenter {

    public RelationPresenter(){

    }
    private Relation relation;


    public Relation createRelation(String idMyAnimal, Relation.relationType relationCategory, Animal animal) {
        if (animal != null   && relationCategory != null) {
            relation = Relation.Builder.create("", relationCategory, animal).build();
            relation.createRelation(relation, idMyAnimal, new RelationDao.OnRelationCreated() {
                @Override
                public void onRelationCreatedListener(Relation relation) {
                }
            });
            return relation;
        }else{
            return null;
        }
    }

    public void deleteRelation(Relation relation){
        if (relation != null) {
            Relation.deleteRelation(relation);
        }
    }

    public void action_getRelation(String ownerID,String idAnimal,final RelationDao.OnRelationAnimalListener listener){
        Relation.getListRelation(ownerID,idAnimal, new RelationDao.OnRelationAnimalListener() {
            @Override
            public void onRelationAnimalListener(ArrayList<Relation> relationList, List<Animal> animalList) {
                listener.onRelationAnimalListener(relationList,animalList);
            }
        });
    }

    public static boolean isAlphaNumeric(String s) {
        return s != null && s.matches("^[a-zA-Z0-9]*$");
    }

}
