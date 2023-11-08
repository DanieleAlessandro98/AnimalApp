package it.uniba.dib.sms222334.Views.RecycleViews.RequestReport;

import android.content.Context;
import android.location.Location;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.GeoPoint;

import it.uniba.dib.sms222334.Fragmets.HomeFragment;
import it.uniba.dib.sms222334.Models.Animal;
import it.uniba.dib.sms222334.Models.Document;
import it.uniba.dib.sms222334.Models.Report;
import it.uniba.dib.sms222334.Models.Request;
import it.uniba.dib.sms222334.Models.SessionManager;
import it.uniba.dib.sms222334.Models.User;
import it.uniba.dib.sms222334.Presenters.AnimalPresenter;
import it.uniba.dib.sms222334.Presenters.RequestPresenter;
import it.uniba.dib.sms222334.R;
import it.uniba.dib.sms222334.Utils.CoordinateUtilities;
import it.uniba.dib.sms222334.Utils.LocationTracker;
import it.uniba.dib.sms222334.Views.AnimalAppDialog;

public class MenuViewHolder extends RecyclerView.ViewHolder{
    private HomeFragment fragment;

    private Document requestReport;

    private TextView type;
    private ImageView photo;

    private Button editButton;
    private Button shareButton;
    private Button deleteButton;
    private Button callButton;

    public MenuViewHolder(View view, HomeFragment fragment){
        super(view);

        this.fragment = fragment;

        type = itemView.findViewById(R.id.type);
        photo = itemView.findViewById(R.id.image);

        editButton= itemView.findViewById(R.id.edit_button);
        shareButton= itemView.findViewById(R.id.share_button);
        deleteButton= itemView.findViewById(R.id.delete_button);
        callButton= itemView.findViewById(R.id.call_button);

        editButton.setOnClickListener(v -> {
            fragment.editRequestReport(requestReport);
        });

        shareButton.setOnClickListener(v -> {
            fragment.sharePositionRequestReport(requestReport);
        });

        deleteButton.setOnClickListener(v -> {
            final AnimalAppDialog deleteDialog=new AnimalAppDialog(fragment.getContext());

            String warningText;
            if (requestReport instanceof Report)
                warningText = fragment.getContext().getString(R.string.delete_report_warning);
            else
                warningText = fragment.getContext().getString(R.string.delete_request_warning);
            deleteDialog.setContentView(warningText, AnimalAppDialog.DialogType.CRITICAL);

            deleteDialog.setConfirmAction(d -> {
                fragment.deleteRequestReport(requestReport);
                deleteDialog.cancel();
            });

            deleteDialog.setUndoAction(d -> deleteDialog.cancel());
            deleteDialog.show();
        });

        callButton.setOnClickListener(v -> {
            fragment.callRequestUser(requestReport);
        });
    }

    public void bind(Request request) {
        this.requestReport = request;
        this.type.setText(Request.getRequestTypeString(request.getType(), fragment.getContext()));
        Animal animal = request.getAnimal();
        if (animal != null)
            this.photo.setImageBitmap(animal.getPhoto());

        setButtonsVisibility(request.getUser());
    }

    public void bind(Report report) {
        this.requestReport = report;
        this.type.setText(report.getReportTypeString(report.getType(), fragment.getContext()));
        this.photo.setImageBitmap(report.getReportPhoto());

        setButtonsVisibility(report.getUser());
    }

    private void setButtonsVisibility(User user) {
        if (!SessionManager.getInstance().isLogged() || user == null || !SessionManager.getInstance().getCurrentUser().getFirebaseID().equals(user.getFirebaseID())) {
            editButton.setVisibility(View.GONE);
            deleteButton.setVisibility(View.GONE);

            if (user != null && user.getPhone() != null)
                callButton.setVisibility(View.VISIBLE);
            else
                callButton.setVisibility(View.GONE);
        }
        else {
            editButton.setVisibility(View.VISIBLE);
            deleteButton.setVisibility(View.VISIBLE);
            callButton.setVisibility(View.GONE);
        }
    }
}
