package it.uniba.dib.sms222334.Views.RecycleViews.RequestSegnalation;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.RecyclerView;

import it.uniba.dib.sms222334.Models.Request;
import it.uniba.dib.sms222334.Models.Segnalation;
import it.uniba.dib.sms222334.R;
import it.uniba.dib.sms222334.Views.CoordinateUtilities;
import it.uniba.dib.sms222334.Views.AnimalAppEditText;

public class RequestSegnalationViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private static final String TAG="RequestSegnalationViewHolder";

        TextView Distance,Type,AnimalName,SpeciesAge,CreatorName,Description;
        ImageView creatorPhoto,image;

        Float latidute,longitude;

        Context context;

        public interface OnItemClickListener{
            void OnItemClick(int position);
        }

        OnItemClickListener onItemClickListener;

        public RequestSegnalationViewHolder(@NonNull View itemView,Context context) {
            super(itemView);

            this.context=context;

            Distance=itemView.findViewById(R.id.distance_text);
            Type=itemView.findViewById(R.id.type);
            AnimalName=itemView.findViewById(R.id.name_text);
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
            //this.Type selection array string
             this.latidute=request.getLatitude();
             this.longitude=request.getLongitude();
             this.AnimalName.setText(request.getAnimalName());
             this.SpeciesAge.setText(request.getSpecies()+(request.getAge()==null?"":(", "+request.getAge())));
             this.CreatorName.setText(request.getCreatorName());
             this.Description.setText(request.getDescription());
             Bitmap requestPhoto=request.getRequestPhoto();
             if(requestPhoto==null){
                 this.image.setImageDrawable(context.getDrawable(R.drawable.baseline_photo_camera_24));
             }
             else{
                 this.image.setImageBitmap(request.getRequestPhoto());
             }

             this.creatorPhoto.setImageBitmap(request.getCreatorPhoto());
        }

        public void bind(Segnalation segnalation){
            //this.Type selection array string
            this.latidute=segnalation.getLatitude();
            this.longitude=segnalation.getLongitude();
            this.AnimalName.setText(segnalation.getAnimalName());
            this.SpeciesAge.setText(segnalation.getSpecies()+", "+segnalation.getAge());
            this.CreatorName.setText(segnalation.getCreatorName());
            this.Description.setText(segnalation.getDescription());
            this.image.setImageBitmap(segnalation.getsegnalationPhoto());
            this.creatorPhoto.setImageBitmap(segnalation.getCreatorPhoto());
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
