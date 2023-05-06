package it.uniba.dib.sms222334.Models;

public class Visit extends Document{
    private String name;
    private String state;   // stato
    private String Diagnosis;
    private String medicalNotes;

    private Visit(String name, String state, String diagnosis, String medicalNotes) {
        this.name = name;
        this.state = state;
        Diagnosis = diagnosis;
        this.medicalNotes = medicalNotes;
    }

    public static class Builder{
        private String Bname;
        private String Bstate;   // stato
        private String BDiagnosis;
        private String BmedicalNotes;

        private Builder(final String name, final String state){
            this.Bname=name;
            this.Bstate=state;
        }

        public static Builder create(final String name, final String surname){
            return new Builder(name,surname);
        }

        public Builder setName(final String Name){
            this.Bname=Name;
            return this;
        }

        public Builder setState(final String State){
            this.Bstate=State;
            return this;
        }

        public Builder setDiagnosis(String Diagnosis){
            this.BDiagnosis=Diagnosis;
            return this;
        }

        public Builder setMedicalNotes(final String medicalNotes){
            this.BmedicalNotes=medicalNotes;
            return this;
        }

        public Visit build(){
            return new Visit(Bname,Bstate,BDiagnosis,BmedicalNotes);
        }
    }
}

