package it.uniba.dib.sms222334.Presenters;

import java.util.List;

import it.uniba.dib.sms222334.Database.Dao.User.UserCallback;
import it.uniba.dib.sms222334.Database.Dao.User.VeterinarianDao;
import it.uniba.dib.sms222334.Models.User;
import it.uniba.dib.sms222334.Models.Veterinarian;

public class VeterinarianPresenter {

    public void action_getVeterinarian(UserCallback.UserFindCallback listener){
        Veterinarian.getVeterinarianAndPublicAuthority(listener);
    }

}
