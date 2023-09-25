package it.uniba.dib.sms222334.Presenters;

import java.util.Date;
import java.util.List;

import it.uniba.dib.sms222334.Database.Dao.VisitDao;
import it.uniba.dib.sms222334.Models.Animal;
import it.uniba.dib.sms222334.Models.Visit;
import it.uniba.dib.sms222334.Utils.UserRole;

/**
 * this Class handles the create, view, edit, remove and check Exist or currect of the visit
 */
public class VisitPresenter {

    private String idAnimale,idVeterinario,name,stato,diagnosi,NoteMediche;

    public VisitPresenter() {
    }

    private boolean isAlphabet(String s){
        return s != null && s.matches("^[a-zA-Z]*$");
    }

    public static boolean isAlphaNumeric(String s) {
        return s != null && s.matches("^[a-zA-Z0-9]*$");
    }

    public Visit createVisit(Visit.visitType visit_type, Animal animal, Date date, String visitName,String doctorID){
        if(visit_type != null && date != null && visitName != null && animal != null && doctorID != null){
            Visit visit = Visit.Builder.create(animal.getFirebaseID(),visitName,visit_type,date).build();
            visit.setAnimal(animal);
            visit.setDoctorFirebaseID(doctorID);

            if(visit.createVisit(visit)){
                System.out.println("creazione finita");
                return visit;
            }else{
                return null;
            }
        }else{
            System.out.println("error in the parametro of create visit");
            return null;
        }
    }

    public boolean action_edit(Visit visit,String idAnimal,String name){

        if(visit != null){
            if (Visit.editVisit(visit,idAnimal,name)) {
                return true;
            }else{
                return false;
            }
        }else{
            return false;
        }
    }

    public boolean removeVisit(String idAnimal, String TypeVisit){
        if (isAlphaNumeric(idAnimal) && TypeVisit != null){
            if (Visit.removeVisit(idAnimal,TypeVisit)) {
                return true;
            }else{
                return false;
            }
        }else{
            // TODO fare qualcosa per visualizzare errore nella eliminazione
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
