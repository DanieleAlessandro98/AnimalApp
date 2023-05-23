package it.uniba.dib.sms222334.Fragmets;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.Calendar;
import java.util.Date;

import it.uniba.dib.sms222334.Models.Visit;
import it.uniba.dib.sms222334.R;

public class VisitFragment extends Fragment {

    Button editButton,deleteButton,backButton;
    TextView visitState,visitName,date,examType,diagnosisType,medicalNote,doctorName;

    ProfileFragment.Type profileType;

    Visit visit;

    public VisitFragment(Visit visit,ProfileFragment.Type profileType){
        this.visit=visit;
        this.profileType=profileType;
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View layout= inflater.inflate(R.layout.visit_fragment,container,false);

        //TODO passare il bundle del profyleType durante la rotazione del dispositivo(causa crash)

        backButton=layout.findViewById(R.id.back_button);

        backButton.setOnClickListener(v -> getParentFragmentManager().popBackStack());

        if(this.profileType== ProfileFragment.Type.VETERINARIAN){
            editButton = layout.findViewById(R.id.edit_button);
            editButton.setOnClickListener(v -> launchEditDialogForVeterinarian());
            editButton.setVisibility(View.VISIBLE);
        }
        else{
            deleteButton = layout.findViewById(R.id.delete_button);
            deleteButton.setOnClickListener(v -> deleteVisit());
            deleteButton.setVisibility(View.VISIBLE);
        }

        visitState= layout.findViewById(R.id.visit_state);
        visitName= layout.findViewById(R.id.name);
        date= layout.findViewById(R.id.date_text_view);
        examType= layout.findViewById(R.id.exam_type_text_view);
        diagnosisType= layout.findViewById(R.id.diagnosis_type_text_view);
        medicalNote= layout.findViewById(R.id.medical_note_text_view);
        doctorName= layout.findViewById(R.id.doctor_name_text_view);

        bindVisit();

        return layout;
    }

    public void bindVisit(){
        this.visitState.setText(visit.getState().toString());
        this.visitName.setText(visit.getName());
        Date visitDate=visit.getDate();
        this.date.setText(visitDate.getDay()+"/"+(visitDate.getMonth()+1)+"/"+visitDate.getYear());
        this.examType.setText(visit.getType().toString());
        if(visit.getDiagnosis() != Visit.diagnosisType.NULL){
            this.diagnosisType.setText(visit.getDiagnosis().toString());
        }
        else{
            this.diagnosisType.setText("");
        }
        this.medicalNote.setText(visit.getMedicalNotes());
        this.doctorName.setText(visit.getDoctorName());
    }

    public void deleteVisit(){

    }

    public void launchEditDialogForVeterinarian(){
        Visit editVisit=Visit.Builder.createFrom(this.visit).build();


        final Dialog editDialog=new Dialog(getContext());
        editDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        editDialog.setContentView(R.layout.visit_edit);

        ImageButton datePickerButton=editDialog.findViewById(R.id.date_picker_button);

        TextView dateTextView=editDialog.findViewById(R.id.date_text_view);
        Date visitDate= editVisit.getDate();
        dateTextView.setText(visitDate.getDay()+"/"+(visitDate.getMonth()+1)+"/"+visitDate.getYear());

        datePickerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar c = Calendar.getInstance();

                int year = c.get(Calendar.YEAR);
                int month = c.get(Calendar.MONTH);
                int day = c.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        getContext(),
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                                dateTextView.setText(dayOfMonth + "/" + (month+1) + "/" + year);
                                editVisit.setDate(new Date(year,month+1,dayOfMonth));
                            }
                            }, year, month, day);

                datePickerDialog.show();
            }
        });

        EditText medicalNote=editDialog.findViewById(R.id.medical_note_edit_text);
        medicalNote.setText(editVisit.getMedicalNotes());

        EditText doctorName=editDialog.findViewById(R.id.doctor_name);
        doctorName.setText(editVisit.getDoctorName());

        Button backButton= editDialog.findViewById(R.id.back_button);

        backButton.setOnClickListener(v -> editDialog.cancel());

        Button saveButton= editDialog.findViewById(R.id.save_button);

        Spinner diagnosiSpinner= editDialog.findViewById(R.id.diagnosis_spinner);
        ArrayAdapter<CharSequence> diagnosisAdapter= ArrayAdapter.createFromResource(getContext(),R.array.diagnosis_type,
                android.R.layout.simple_list_item_1);
        diagnosisAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        diagnosiSpinner.setAdapter(diagnosisAdapter);
        diagnosiSpinner.setSelection(editVisit.getDiagnosis().ordinal());


        Spinner examStateSpinner= editDialog.findViewById(R.id.exam_state_spinner);
        ArrayAdapter<CharSequence> examAdapter= ArrayAdapter.createFromResource(getContext(),R.array.visit_state,
                android.R.layout.simple_spinner_item);
        examAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        examStateSpinner.setAdapter(examAdapter);
        examStateSpinner.setSelection(editVisit.getState().ordinal());

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editVisit.setDoctorName(doctorName.getText().toString());
                editVisit.setMedicalNotes(medicalNote.getText().toString());
                editVisit.setDiagnosis(Visit.diagnosisType.values()[diagnosiSpinner.getSelectedItemPosition()]);
                editVisit.setState(Visit.visitState.values()[examStateSpinner.getSelectedItemPosition()]);
                saveVisit(editVisit);
                editDialog.cancel();
            }
        });

        editDialog.show();
        editDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        editDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        editDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        editDialog.getWindow().setGravity(Gravity.BOTTOM);

    }

    private void saveVisit(Visit visit) {
        this.visit=visit;
        bindVisit();
    }
}
