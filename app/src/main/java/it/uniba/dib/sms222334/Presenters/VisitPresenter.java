package it.uniba.dib.sms222334.Presenters;

import java.util.Date;

import it.uniba.dib.sms222334.Models.VISIT;
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

    public Visit createVisit(Visit.visitType visit_type, Date date, String visitName){
        if(visit_type != null && date != null && visitName != null){
            Visit visit = Visit.Builder.create("",visitName,visit_type,date).build();
            return visit;
        }else{
            System.out.println("error in the parametro of create visit");
            return null;
        }
    }

    public boolean action_edit(String idVisit,String visitType, String animalChooser, String dateVisit, String doctor_name){

        if(isAlphabet(visitType) && isAlphabet(animalChooser)){
            //TODO chiamare il metodo della model per modificare la visita
            return true;
        }else{
            //TODO fare qualcosa per visualizzare errore nella modifica
            return false;
        }
    }

    public boolean removeVisit(String idvisita){
        if (isAlphaNumeric(idvisita)){
            // TODO chiamare il metodo della model per eliminare la visita
            return true;
        }else{
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
