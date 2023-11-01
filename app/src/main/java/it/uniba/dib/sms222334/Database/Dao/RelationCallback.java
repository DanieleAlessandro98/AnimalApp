package it.uniba.dib.sms222334.Database.Dao;

import it.uniba.dib.sms222334.Models.Relation;

public class RelationCallback {
    public interface createRelationCallback{
        void createSuccesfully(String idRelation);
        void createFailure();
    }
}
