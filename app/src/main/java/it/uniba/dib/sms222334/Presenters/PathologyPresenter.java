package it.uniba.dib.sms222334.Presenters;

import java.util.ArrayList;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import it.uniba.dib.sms222334.Database.Dao.PathologyDao;
import it.uniba.dib.sms222334.Models.Pathology;

/**
 * this class handles the create, view, remove and check the date for Pathology
 */
public class PathologyPresenter {

    private String idAnimal,name;
    public static Pathology pathology;

    public PathologyPresenter() {
    }

    // this is the method that check if are error in the name and call the method for create the pathology
    public Pathology action_create(String idAnimal, String name){
        if(name != null && isAlphaNumeric(idAnimal)){
            pathology = Pathology.Builder.create(idAnimal,name).build();
            //TODO ho fatto passare per parametro perché non sto capendo come prendere i paramentri dalla Build
            if(Pathology.createPathology(idAnimal,name)) {
                return pathology;
            }
        }else{
            System.out.println(name+"   the string is not currect");
        }
        return null;
    }

    // this is che method that check if exist the pathology, if exist then call the delete method
    public boolean action_delete(String idPathology, String name){
        if (idPathology != null && name != null){
            pathology = Pathology.Builder.create(idPathology,name).build();
            return pathology.deletePathology(idPathology);
        }else{
            System.out.println("the Pathology is not Exist");
            return false;
        }
    }

    public static void action_getPathology(String idAnimal,final PathologyDao.OnPathologyListListener listener){
        if (idAnimal != null){
            Pathology.getPathology(idAnimal, new PathologyDao.OnPathologyListListener() {
                @Override
                public void onPathologyListReady(ArrayList<Pathology> listPathology) {
                    listener.onPathologyListReady(listPathology);
                }
            });
        }
    }

    private boolean isAlphabet(String s){
        return s != null && s.matches("^[a-zA-Z]*$");
    }

    public static boolean isAlphaNumeric(String s) {
        return s != null && s.matches("^[a-zA-Z0-9]*$");
    }

    private boolean checkIfExist(String idAnimal){
        //TODO chiamare un metodo del model per verificare l'esistenza della patologia
        boolean value = true;
        if (value){
            return true;
        }else{
            return false;
        }
    }



    public String getIdAnimal() {
        return idAnimal;
    }

    public void setIdAnimal(String idAnimal) {
        this.idAnimal = idAnimal;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
