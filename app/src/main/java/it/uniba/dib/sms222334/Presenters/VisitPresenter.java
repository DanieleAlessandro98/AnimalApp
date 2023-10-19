package it.uniba.dib.sms222334.Presenters;

import java.util.Date;

import it.uniba.dib.sms222334.Models.Animal;
import it.uniba.dib.sms222334.Models.Visit;

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

    public Visit createVisit(Visit.visitType visit_type, Animal animal, Date date, String visitName){
        if(visit_type != null && date != null && visitName != null && animal != null){
            Visit visit = Visit.Builder.create(animal.getFirebaseID(),visitName,visit_type,date).build();
            visit.setAnimal(animal);

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

    public boolean removeVisit(String idVisit) {
        if (isAlphaNumeric(idVisit)) {
            System.out.println("entrato in if di removeVisit()");
            if (Visit.removeVisit(idVisit)) {
                System.out.println("vero di remove visit");
                return true;
            } else {
                System.out.println("falso di remove visit");
                return false;
            }
        } else {
            // TODO fare qualcosa per visualizzare errore nella eliminazione
            return false;
        }
    }



    public void action_view(String idAnimale){

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
