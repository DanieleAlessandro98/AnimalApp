package it.uniba.dib.sms222334.Views.RecycleViews.RequestReport;

import android.content.Context;
import android.graphics.Bitmap;
import android.location.Location;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.GeoPoint;

import it.uniba.dib.sms222334.Models.Animal;
import it.uniba.dib.sms222334.Models.Request;
import it.uniba.dib.sms222334.R;
import it.uniba.dib.sms222334.Utils.CoordinateUtilities;
import it.uniba.dib.sms222334.Utils.DateUtilities;
import it.uniba.dib.sms222334.Utils.LocationTracker;
import it.uniba.dib.sms222334.Utils.RequestType;

public class RequestViewHolder extends RecyclerView.ViewHolder {
    private Context context;

    private Request request;

    private TextView userName;
    private ImageView userPhoto;
    private TextView type;
    private TextView description;
    private TextView distance;
    private ImageView photo;
    private TextView animalName;
    private TextView animalSpeciesAndAge;

    public RequestViewHolder(View itemView, Context context) {
        super(itemView);

        this.context = context;

        userName = itemView.findViewById(R.id.creator_name);
        userPhoto = itemView.findViewById(R.id.profile_picture);
        type = itemView.findViewById(R.id.type);
        description = itemView.findViewById(R.id.description_text);
        distance = itemView.findViewById(R.id.distance_text);
        photo = itemView.findViewById(R.id.image);
        animalName = itemView.findViewById(R.id.name_text);
        animalSpeciesAndAge = itemView.findViewById(R.id.species_age_text);
    }

    public void bind(Request request) {
        this.request = request;

        this.userName.setText(request.getUser().getName());

        Bitmap creator = request.getUser().getPhoto();
        if (creator == null)
            this.userPhoto.setImageDrawable(context.getDrawable(R.drawable.default_profile_image));
        else
            this.userPhoto.setImageBitmap(creator);

        this.type.setText(Request.getRequestTypeString(request.getType(), context));
        this.description.setText(request.getDescription());

        Animal animal = request.getAnimal();
        if (animal != null) {
            this.photo.setImageBitmap(animal.getPhoto());
            this.animalName.setText(animal.getName());
            this.animalSpeciesAndAge.setText(animal.getSpeciesString(animal.getSpecies(), context) + (animal.getBirthDate() == null ? "" : (", " + DateUtilities.calculateAge(animal.getBirthDate(), context))));
        } else {
            this.animalName.setText("");
            this.animalSpeciesAndAge.setText("");
        }

        if (request.getType() == RequestType.OFFER_BEDS) {
            this.animalName.setText(context.getString(R.string.description_offer_beds_request) + request.getNBeds());
        }
    }

    public void updateDistance(Location devicePosition) {
        if (devicePosition != null) {
            float distance = CoordinateUtilities.calculateDistance(new GeoPoint(devicePosition.getLatitude(), devicePosition.getLongitude()), request.getLocation());
            request.setDistance(distance);
            this.distance.setText(CoordinateUtilities.formatDistance(distance));
        } else {
            this.distance.setText("... km");
        }
    }
}
