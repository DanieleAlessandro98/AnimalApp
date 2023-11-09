package it.uniba.dib.sms222334.Fragmets;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
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

import com.google.firebase.Timestamp;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import it.uniba.dib.sms222334.Database.Dao.VisitDao;
import it.uniba.dib.sms222334.Models.SessionManager;
import it.uniba.dib.sms222334.Models.Visit;
import it.uniba.dib.sms222334.Presenters.VisitPresenter;
import it.uniba.dib.sms222334.R;
import it.uniba.dib.sms222334.Utils.UserRole;

public class VisitFragment extends Fragment {

    Button editButton,deleteButton,backButton;
    TextView visitState,visitName,date,examType,diagnosisType,medicalNote,doctorName;

    UserRole userRole;

    Visit visit;

    public VisitFragment(){

    }

    public static VisitFragment newInstance(Visit visit) {
        VisitFragment myFragment = new VisitFragment();

        Bundle args = new Bundle();
        args.putParcelable("visit", visit);
        myFragment.setArguments(args);

        return myFragment;
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View layout= inflater.inflate(R.layout.visit_fragment,container,false);

        this.visit = (getArguments().getParcelable("visit"));
        this.userRole = SessionManager.getInstance().getCurrentUser().getRole();

        backButton=layout.findViewById(R.id.back_button);

        backButton.setOnClickListener(v -> getParentFragmentManager().popBackStack());

        if(this.userRole== UserRole.VETERINARIAN){
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

        Date visitDate= visit.getDate().toDate();
        Calendar calendar=Calendar.getInstance();
        calendar.setTime(visitDate);
        this.date.setText(calendar.get(Calendar.DAY_OF_MONTH)+"/"+calendar.get(Calendar.MONTH)+"/"+calendar.get(Calendar.YEAR));

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
        Visit editVisit=this.visit;

        final Dialog editDialog=new Dialog(getContext());
        editDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        editDialog.setContentView(R.layout.visit_edit);

        ImageButton datePickerButton=editDialog.findViewById(R.id.date_picker_button);

        TextView dateTextView=editDialog.findViewById(R.id.date_text_view);

        Date visitDate= editVisit.getDate().toDate();
        Calendar calendar=Calendar.getInstance();
        calendar.setTime(visitDate);
        dateTextView.setText(calendar.get(Calendar.DAY_OF_MONTH)+"/"+calendar.get(Calendar.MONTH)+"/"+calendar.get(Calendar.YEAR));

        datePickerButton.setOnClickListener(v -> {
            final Calendar c = Calendar.getInstance();

            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    getContext(),
                    (view, year1, month1, dayOfMonth) -> {
                        dateTextView.setText(dayOfMonth + "/" + (month1 +1) + "/" + year1);

                        c.set(year1,month1, dayOfMonth);

                        editVisit.setDate(new Timestamp(c.getTime()));
                    }, year, month, day);

            datePickerDialog.show();
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

        Spinner examStateSpinner= editDialog.findViewById(R.id.exam_state_spinner);
        ArrayAdapter<CharSequence> examAdapter= ArrayAdapter.createFromResource(getContext(),R.array.visit_state,
                android.R.layout.simple_spinner_item);
        examAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        examStateSpinner.setAdapter(examAdapter);

        saveButton.setOnClickListener(v -> {
            editVisit.setDoctorName(doctorName.getText().toString());
            editVisit.setMedicalNotes(medicalNote.getText().toString());
            editVisit.setDiagnosis(Visit.diagnosisType.values()[diagnosiSpinner.getSelectedItemPosition()]);
            editVisit.setState(Visit.visitState.values()[examStateSpinner.getSelectedItemPosition()]);

            VisitPresenter presenter = new VisitPresenter();
            presenter.action_edit(editVisit, new VisitDao.OnVisitEditListener() {
                @Override
                public void onSuccessEdit() {
                    Log.i("I", "update fatto");
                    saveVisit(editVisit);
                    editDialog.cancel();
                }

                @Override
                public void onFailureEdit() {
                    System.out.println("fallito");
                }
            });
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
