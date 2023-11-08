package it.uniba.dib.sms222334.Models;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.google.firebase.Timestamp;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import it.uniba.dib.sms222334.Database.Dao.Animal.AnimalDao;
import it.uniba.dib.sms222334.Database.Dao.VisitDao;
import it.uniba.dib.sms222334.Database.DatabaseCallbackResult;
import it.uniba.dib.sms222334.Utils.UserRole;

public class Visit extends Document implements Parcelable {

    public enum visitType{DEWORMING,VACCINATION,STERILIZATION,SURGERY,CONTROL}
    public enum diagnosisType{POSITIVE,NEGATIVE,NULL}

    public enum visitState{EXECUTED,NOT_EXECUTED,BE_REVIEWED}
    private String name;
    private String IDowner;
    private visitType type;
    private visitState state;
    private String doctorID;

    private String doctorName;
    private Timestamp date;
    private diagnosisType Diagnosis;
    private String medicalNotes;

    private Animal animal;

    private Visit(String id,String IDowner,String doctorId,String name,visitType type,Animal animal, Timestamp date,visitState state, diagnosisType diagnosis,String doctorName, String medicalNotes) {
        super(id);

        this.name = name;
        this.state = state;
        this.type = type;
        this.date = date;
        this.doctorName = doctorName;
        this.doctorID = doctorId;
        this.Diagnosis = diagnosis;
        this.medicalNotes = medicalNotes;
        this.animal = animal;
        this.IDowner = IDowner;
    }

    public String getIDowner(){return IDowner;}
    public String getName() {
        return name;
    }

    public String getDoctorID() {
        return doctorID;
    }

    public void setDoctorID(String doctorID) {
        this.doctorID = doctorID;
    }

    public String getDoctorName() {
        return doctorName;
    }

    public void setDoctorName(String doctorName) {
        this.doctorName = doctorName;
    }

    public void setName(String name) {
        this.name = name;
    }

    public visitType getType() {
        return type;
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

    public Timestamp getDate() {
        return date;
    }

    public void setDate(Timestamp date) {
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

        private Timestamp Bdate;
        private visitState Bstate=visitState.NOT_EXECUTED;   // stato
        private diagnosisType BDiagnosis=diagnosisType.NULL;

        private String BdoctorName="";

        private String BdoctorId;
        private String BmedicalNotes="";

        private Animal banimal;
        private String bIDowner;

        private Builder(final String id,final String name, final visitType type, final Timestamp date){
            this.bID = id;
            this.Bname=name;
            this.Btype=type;
            this.Bdate=date;
        }

        public static Builder create(final String id,final String name, final visitType type, final Timestamp date){
            return new Builder(id,name,type,date);
        }

        public static Builder createFrom(Visit visit){
            return new Builder(visit.getFirebaseID(),visit.getName(),visit.getType(),visit.getDate())
                    .setDoctorName(visit.getDoctorName())
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

        public Builder setDate(final Timestamp date){
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

        public Builder setDoctorId(final String doctorId){
            this.BdoctorId=doctorId;
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
            return new Visit(bID,bIDowner,BdoctorId,Bname,Btype,banimal,Bdate,Bstate,BDiagnosis,BdoctorName,BmedicalNotes);
        }
    }

    public void createVisit(VisitDao.OnVisitCreateListener listener){
        VisitDao dao = new VisitDao();
       dao.createVisit(this,listener);
    }
    public void delete() {
        VisitDao visitDao = new VisitDao();
        visitDao.deleteVisit(this);
    }

    public void editVisit(VisitDao.OnVisitEditListener listener){
        VisitDao dao = new VisitDao();
        dao.editVisit(this,listener);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(getFirebaseID());
        dest.writeString(name);
        dest.writeString(IDowner);
        dest.writeString(doctorID);
        dest.writeString(medicalNotes);
        dest.writeInt(this.state.ordinal());
        dest.writeInt(this.Diagnosis.ordinal());
        dest.writeInt(this.type.ordinal());
        dest.writeParcelable(getDate(),flags);
        dest.writeString(animal.getFirebaseID());
    }

    protected Visit(Parcel in) {
        super(in.readString());

        name = in.readString();
        IDowner = in.readString();
        doctorID = in.readString();
        medicalNotes = in.readString();
        this.state=visitState.values()[in.readInt()];
        this.Diagnosis=diagnosisType.values()[in.readInt()];
        this.type=visitType.values()[in.readInt()];
        this.date=in.readParcelable(Timestamp.class.getClassLoader());


        //TODO temporary bad solution
        new AnimalDao().getAnimalByReference(AnimalDao.collectionAnimal.document(in.readString()), IDowner, new DatabaseCallbackResult<Animal>() {
            @Override
            public void onDataRetrieved(Animal result) {
                animal=result;
            }

            @Override
            public void onDataRetrieved(ArrayList<Animal> results) {

            }

            @Override
            public void onDataNotFound() {

            }

            @Override
            public void onDataQueryError(Exception e) {

            }
        });
    }

    public static final Creator<Visit> CREATOR = new Creator<Visit>() {
        @Override
        public Visit createFromParcel(Parcel in) {
            return new Visit(in);
        }

        @Override
        public Visit[] newArray(int size) {
            return new Visit[size];
        }
    };

}
