package it.uniba.dib.sms222334.Presenters;

import java.util.List;

import it.uniba.dib.sms222334.Database.Dao.User.VeterinarianDao;
import it.uniba.dib.sms222334.Models.Veterinarian;

public class VeterinarianPresenter {

    public void action_getVeterinarian(final VeterinarianDao.OnVeterinarianListener listener){
        Veterinarian.getVeterinarian(new VeterinarianDao.OnVeterinarianListener() {
            @Override
            public void onGetVeterinarianListener(List<Veterinarian> veterinarianList) {
                listener.onGetVeterinarianListener(veterinarianList);
            }
        });
    }

}
