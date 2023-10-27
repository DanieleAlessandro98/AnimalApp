package it.uniba.dib.sms222334.Views.RecycleViews.RequestReport;

import android.content.Context;
import android.graphics.Bitmap;
import android.location.Location;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import it.uniba.dib.sms222334.Models.Animal;
import it.uniba.dib.sms222334.Models.Request;
import it.uniba.dib.sms222334.Models.Report;
import it.uniba.dib.sms222334.Models.User;
import it.uniba.dib.sms222334.R;
import it.uniba.dib.sms222334.Utils.CoordinateUtilities;
import it.uniba.dib.sms222334.Utils.DateUtilities;
import it.uniba.dib.sms222334.Utils.RequestType;

public class RequestReportViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private static final String TAG="RequestReportViewHolder";

        TextView Distance,Type, Name,SpeciesAge,CreatorName,Description;
        ImageView creatorPhoto,image;

        Float latidute,longitude;

        Context context;

        public interface OnItemClickListener{
            void OnItemClick(int position);
        }

        OnItemClickListener onItemClickListener;

        public RequestReportViewHolder(@NonNull View itemView, Context context) {
            super(itemView);

            this.context=context;

            Distance=itemView.findViewById(R.id.distance_text);
            Type=itemView.findViewById(R.id.type);
            Name =itemView.findViewById(R.id.name_text);
            SpeciesAge=itemView.findViewById(R.id.species_age_text);
            CreatorName=itemView.findViewById(R.id.creator_name);
            Description=itemView.findViewById(R.id.description_text);
            creatorPhoto=itemView.findViewById(R.id.profile_picture);
            image=itemView.findViewById(R.id.image);

            itemView.setOnClickListener(this);
        }

        public void setOnItemClickListener(OnItemClickListener listener){
            this.onItemClickListener= listener;
        }

        public void bind(Request request){
            this.Type.setText(Request.getRequestTypeString(request.getType(), context));
            this.CreatorName.setText(request.getUser().getName());
            this.Description.setText(request.getDescription());

            Bitmap creator = request.getUser().getPhoto();
            if(creator==null)
                this.creatorPhoto.setImageDrawable(context.getDrawable(R.drawable.default_profile_image));
            else
                this.creatorPhoto.setImageBitmap(creator);

            Animal animal = request.getAnimal();
            if (animal != null) {
                this.Name.setText(animal.getName());
                this.SpeciesAge.setText(animal.getSpeciesString(animal.getSpecies(), context) + (animal.getBirthDate()==null?"":(", "+ DateUtilities.calculateAge(animal.getBirthDate(), context))));
                this.image.setImageBitmap(animal.getPhoto());
            } else {
                this.Name.setText("");
                this.SpeciesAge.setText("");
            }

            if (request.getType() == RequestType.OFFER_BEDS) {
                this.Name.setText(context.getString(R.string.description_offer_beds_request) + request.getNBeds());
            }

            /*
             this.latidute=request.getLatitude();
             this.longitude=request.getLongitude();

             */
        }

        public void bind(Report report){
            this.Type.setText(report.getRequestTypeString(report.getType(), context));
            this.SpeciesAge.setText(Animal.getSpeciesString(report.getAnimalSpecies(), context) + (report.getAnimalAge()==null?"":(", "+ DateUtilities.calculateAge(report.getAnimalAge(), context))));
            this.Description.setText(report.getDescription());
            this.image.setImageBitmap(report.getReportPhoto());

            User user = report.getUser();
            if (user != null) {
                this.CreatorName.setText(user.getName());
                this.creatorPhoto.setImageBitmap(user.getPhoto());
            } else {
                this.CreatorName.setText(context.getString(R.string.user_not_logged_report));
                this.creatorPhoto.setImageDrawable(context.getDrawable(R.drawable.default_profile_image));
            }

            String animalName = report.getAnimalName();
            if (!animalName.equals(""))
                this.Name.setText(animalName);
            else
                this.Name.setText(context.getString(R.string.animal_name_unknown_report));

            /*

            this.latidute=report.getLatitude();
            this.longitude=report.getLongitude();

             */
        }

        public void setDistance(Location devicePosition) {
            if(devicePosition!=null){
                this.Distance.setText(CoordinateUtilities.calculateDistance(this.latidute
                        ,devicePosition.getLatitude()
                        ,this.longitude
                        ,devicePosition.getLongitude(),CoordinateUtilities.WITH_METRICS));
            }
        }


        @Override
        public void onClick(View view) {
            if(this.onItemClickListener!=null){
                this.onItemClickListener.OnItemClick(getLayoutPosition());
            }
        }
}
