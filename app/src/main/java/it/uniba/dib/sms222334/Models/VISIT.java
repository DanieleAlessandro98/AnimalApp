package it.uniba.dib.sms222334.Models;

public class VISIT {

    private String visitType;
    private String animalChooser;
    private String date;
    private String doctorName;

    public VISIT(String visitType, String animalChooser, String date, String doctorName) {
        this.visitType = visitType;
        this.animalChooser = animalChooser;
        this.date = date;
        this.doctorName = doctorName;
    }

    public boolean createVisit(){
        //TODO chiamare il metodo della dao per la creazione della visita
        return true;
    }

    public void deleteVisit(){
        //TODO chiamare il metodo della dao per la eliminazione della visita
    }

    public void editVisit(){
        //TOOO chiamare il metodo ddella dao per la eliminazione della visita
    }


    public String getVisitType() {
        return visitType;
    }

    public void setVisitType(String visitType) {
        this.visitType = visitType;
    }

    public String getAnimalChooser() {
        return animalChooser;
    }

    public void setAnimalChooser(String animalChooser) {
        this.animalChooser = animalChooser;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDoctorName() {
        return doctorName;
    }

    public void setDoctorName(String doctorName) {
        this.doctorName = doctorName;
    }
}
