package it.uniba.dib.sms222334.Presenters;

import android.util.Log;

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
    public static Pathology pathology;

    public PathologyPresenter() {
    }

    // this is the method that check if are error in the name and call the method for create the pathology
    public void action_create(String idAnimal, String name,PathologyDao.OnPathologyCreateListener listener){
        if(name != null && isAlphaNumeric(idAnimal)){
            pathology = Pathology.Builder.create("",idAnimal,name).build();
            pathology.createPathology(pathology,name,listener);
        }else{
            Log.w("W","The creation is failure");
        }
    }

    public static boolean isAlphaNumeric(String s) {
        return s != null && s.matches("^[a-zA-Z0-9]*$");
    }
}
