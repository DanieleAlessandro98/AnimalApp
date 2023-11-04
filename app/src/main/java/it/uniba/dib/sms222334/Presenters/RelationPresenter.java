package it.uniba.dib.sms222334.Presenters;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import it.uniba.dib.sms222334.Database.Dao.RelationDao;
import it.uniba.dib.sms222334.Models.Animal;
import it.uniba.dib.sms222334.Models.Relation;

public class RelationPresenter {

    public RelationPresenter(){

    }


    public void createRelation(String idMyAnimal, Relation.relationType relationCategory, Animal animal,RelationDao.OnRelationCreateListener listener) {
        if (animal != null && idMyAnimal != null  && relationCategory != null) {
            Relation relation = Relation.Builder.create("", relationCategory, animal).build();
            relation.createRelation(relation, idMyAnimal,listener);
        }else{
            Log.w("W","the variable is null");
        }
    }

    public void deleteRelation(Relation relation){
        if (relation != null) {
            Relation.deleteRelation(relation);
        }
    }

    public void action_getRelation(String ownerID,String idAnimal,final RelationDao.OnRelationAnimalListener listener){
        Relation.getListRelation(ownerID,idAnimal, listener);
    }
}
