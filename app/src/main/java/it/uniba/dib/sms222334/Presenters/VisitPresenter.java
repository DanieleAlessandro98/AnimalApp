package it.uniba.dib.sms222334.Presenters;

import android.app.Dialog;
import android.util.Log;

import java.util.Date;
import java.util.List;

import it.uniba.dib.sms222334.Database.Dao.VisitDao;
import it.uniba.dib.sms222334.Fragmets.ListFragment;
import it.uniba.dib.sms222334.Fragmets.ProfileFragment;
import it.uniba.dib.sms222334.Models.Animal;
import it.uniba.dib.sms222334.Models.Visit;
import it.uniba.dib.sms222334.Utils.UserRole;

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

    public void createVisit(Dialog editDialog, Visit.visitType visit_type, Animal animal, Date date, String visitName, String doctorID){
        if(visit_type != null && date != null && visitName != null && animal != null && doctorID != null){
            VisitDao.OnVisitCreateListener listener = new VisitDao.OnVisitCreateListener() {
                @Override
                public void onCreateVisit(Visit visit) {
                    if (listFragment != null) {

                        listFragment.refresh(visit);
                    }
                    editDialog.cancel();
                }
                @Override
                public void onFailureCreateVisit() {
                    Log.w("W","Errore creazione visita");
                }
            };

            Visit visit = Visit.Builder.create("",visitName,visit_type,date).build();
            visit.setAnimal(animal);
            visit.setDoctorFirebaseID(doctorID);
            visit.createVisit(listener);
        }else{
            System.out.println("error in the parametro of create visit");
        }
    }

    public void action_edit(Visit visit,String idAnimal,String name,VisitDao.OnVisitEditListener listener){
        if(visit != null && idAnimal != null && name != null){
            visit.editVisit(idAnimal,name,listener);
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


    public void action_view(UserRole idProfile,final VisitDao.OnVisitListener listener){
        if (idProfile != null){
            Visit.ViewVisit(idProfile, new VisitDao.OnVisitListener() {
                @Override
                public void onGetVisitListener(List<Visit> visitList) {
                    listener.onGetVisitListener(visitList);
                }
            });
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
