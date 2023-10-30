package it.uniba.dib.sms222334.Models;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import it.uniba.dib.sms222334.Database.Dao.VisitDao;
import it.uniba.dib.sms222334.Utils.UserRole;

public class Visit extends Document implements Serializable {

    public enum visitType{DEWORMING,VACCINATION,STERILIZATION,SURGERY,CONTROL}
    public enum diagnosisType{POSITIVE,NEGATIVE,NULL}

    public enum visitState{EXECUTED,NOT_EXECUTED,BE_REVIEWED}
    private String name;
    private String IDowner;
    private visitType type;
    private visitState state;   // stato

    private String doctorID;
    private Date date;
    private diagnosisType Diagnosis;
    private String medicalNotes;

    private Animal animal;

    private Visit(String id,String IDowner,String name,visitType type,Animal animal, Date date,visitState state, diagnosisType diagnosis,String doctorName, String medicalNotes) {
        super(id);

        this.name = name;
        this.state = state;
        this.type=type;
        this.date=date;
        this.doctorID =doctorName;
        this.Diagnosis = diagnosis;
        this.medicalNotes = medicalNotes;
        this.animal=animal;
        this.IDowner = IDowner;
    }

    public String getIDowner(){return IDowner;}
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public visitType getType() {
        return type;
    }

    public String getDoctorFirebaseID(){
        return doctorID;
    }

    public void setDoctorFirebaseID(String doctorID){
        this.doctorID =doctorID;
    }

    public void setType(visitType type) {
        this.type = type;
    }

    public visitState getState() {
        return state;
    }

    public void setState(visitState state) {
        this.state = state;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public diagnosisType getDiagnosis() {
        return Diagnosis;
    }

    public void setDiagnosis(diagnosisType diagnosis) {
        Diagnosis = diagnosis;
    }

    public String getMedicalNotes() {
        return medicalNotes;
    }

    public void setMedicalNotes(String medicalNotes) {
        this.medicalNotes = medicalNotes;
    }

    public Animal getAnimal() {
        return animal;
    }

    public void setAnimal(Animal animal) {
        this.animal = animal;
    }

    public static class Builder{
        private String bID;
        private String Bname;

        private visitType Btype;

        private Date Bdate;
        private visitState Bstate=visitState.NOT_EXECUTED;   // stato
        private diagnosisType BDiagnosis=diagnosisType.NULL;

        private String BdoctorName="";
        private String BmedicalNotes="";

        private Animal banimal;
        private String bIDowner;

        private Builder(final String id,final String name, final visitType type, final Date date){
            this.bID = id;
            this.Bname=name;
            this.Btype=type;
            this.Bdate=date;
        }

        public static Builder create(final String id,final String name, final visitType type, final Date date){
            return new Builder(id,name,type,date);
        }

        public static Builder createFrom(Visit visit){
            return new Builder(visit.getFirebaseID(),visit.getName(),visit.getType(),visit.getDate())
                    .setDoctorName(visit.getDoctorFirebaseID())
                    .setState(visit.getState())
                    .setDiagnosis(visit.getDiagnosis())
                    .setMedicalNotes(visit.getMedicalNotes())
                    .setIDowner(visit.getIDowner());

        }

        public Builder setIDowner(final String IDowner){
            this.bIDowner=IDowner;
            return this;
        }

        public Builder setName(final String Name){
            this.Bname=Name;
            return this;
        }

        public Builder setType(final visitType type){
            this.Btype=type;
            return this;
        }

        public Builder setDate(final Date date){
            this.Bdate=date;
            return this;
        }

        public Builder setAnimal(final Animal animal){
            this.banimal=animal;
            return this;
        }

        public Builder setDoctorName(final String doctorName){
            this.BdoctorName=doctorName;
            return this;
        }

        public Builder setState(final visitState State){
            this.Bstate=State;
            return this;
        }

        public Builder setDiagnosis(diagnosisType Diagnosis){
            this.BDiagnosis=Diagnosis;
            return this;
        }

        public Builder setMedicalNotes(final String medicalNotes){
            this.BmedicalNotes=medicalNotes;
            return this;
        }

        public Visit build(){
            return new Visit(bID,bIDowner,Bname,Btype,banimal,Bdate,Bstate,BDiagnosis,BdoctorName,BmedicalNotes);
        }
    }

    public boolean createVisit(Visit visit){
        VisitDao dao = new VisitDao();
        return dao.createVisit(visit);
    }
    public void delete() {
        VisitDao visitDao = new VisitDao();
        visitDao.deleteVisit(this);
    }

    public boolean editVisit(String idAnimal,String name){
        VisitDao dao = new VisitDao();
        dao.editVisit(this,idAnimal,name);
        return false;
    }

    public static void ViewVisit(UserRole idProfile,final VisitDao.OnVisitListener listener){
        VisitDao dao = new VisitDao();
        dao.viewVisitListDao(idProfile, new VisitDao.OnVisitListener() {
            @Override
            public void onGetVisitListener(List<Visit> visitList) {
                listener.onGetVisitListener(visitList);
            }
        });
    }

}
