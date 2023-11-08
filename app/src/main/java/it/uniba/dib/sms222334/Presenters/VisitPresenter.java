package it.uniba.dib.sms222334.Presenters;

import android.app.Dialog;
import android.util.Log;

import com.google.firebase.Timestamp;

import java.util.Date;

import it.uniba.dib.sms222334.Database.Dao.VisitDao;
import it.uniba.dib.sms222334.Fragmets.ListFragment;
import it.uniba.dib.sms222334.Fragmets.ProfileFragment;
import it.uniba.dib.sms222334.Models.Animal;
import it.uniba.dib.sms222334.Models.User;
import it.uniba.dib.sms222334.Models.Veterinarian;
import it.uniba.dib.sms222334.Models.Visit;

/**
 * this Class handles the create, view, edit, remove and check Exist or currect of the visit
 */
public class VisitPresenter {

    private String idAnimale,idVeterinario,name,stato,diagnosi,NoteMediche;
    private ProfileFragment profileFragment;
    private ListFragment listFragment;

    public VisitPresenter() {}

    public VisitPresenter( ListFragment listFragment) {
        this.listFragment = listFragment;
    }

    public void createVisit(Dialog editDialog, Visit.visitType visit_type, Animal animal, Timestamp date, String visitName, Veterinarian veterinarian){
        if(visit_type != null && date != null && visitName != null && animal != null){
            VisitDao.OnVisitCreateListener listener = new VisitDao.OnVisitCreateListener() {
                @Override
                public void onCreateVisit(Visit visit) {
                    veterinarian.addVisit(visit);
                    animal.addVisit(visit);
                    editDialog.cancel();
                }
                @Override
                public void onFailureCreateVisit() {
                    Log.w("W","Errore creazione visita");
                }
            };

            Visit visit = Visit.Builder.create("",visitName,visit_type,date).build();
            visit.setAnimal(animal);
            visit.setDoctorID(veterinarian.getFirebaseID());
            visit.createVisit(listener);
        }else{
            System.out.println("error in the parametro of create visit");
        }
    }

    public void action_edit(Visit visit,VisitDao.OnVisitEditListener listener){
        if(visit != null){
            visit.editVisit(listener);
        }
    }


    public boolean removeVisit(Visit visit){
        if (visit != null){
            visit.delete();
            return true;
        } else {
            Log.w("W","the class visit is null");
            return false;
        }
    }

    public String getIdAnimale() {
        return idAnimale;
    }

    public void setIdAnimale(String idAnimale) {
        this.idAnimale = idAnimale;
    }

    public String getIdVeterinario() {
        return idVeterinario;
    }

    public void setIdVeterinario(String idVeterinario) {
        this.idVeterinario = idVeterinario;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStato() {
        return stato;
    }

    public void setStato(String stato) {
        this.stato = stato;
    }

    public String getDiagnosi() {
        return diagnosi;
    }

    public void setDiagnosi(String diagnosi) {
        this.diagnosi = diagnosi;
    }

    public String getNoteMediche() {
        return NoteMediche;
    }

    public void setNoteMediche(String noteMediche) {
        NoteMediche = noteMediche;
    }
}
