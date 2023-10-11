package it.uniba.dib.sms222334.Views.RecycleViews.Animal;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Calendar;
import java.util.Date;

import it.uniba.dib.sms222334.Models.Animal;
import it.uniba.dib.sms222334.R;
import it.uniba.dib.sms222334.Utils.DateUtilities;

public class AnimalViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private static final String TAG="AnimalViewHolder";

        TextView animalName,SpeciesAge,visitNumber,pathologiesNumber;
        ImageView animalPhoto,visitIcon,pathologiesIcon,dangerIcon;

        Context context;

        public interface OnItemClickListener{
            void OnItemClick(int position);
        }

        OnItemClickListener onItemClickListener;

        public AnimalViewHolder(@NonNull View itemView,Context context) {
            super(itemView);

            this.context=context;

            animalName=itemView.findViewById(R.id.animal_name);
            SpeciesAge=itemView.findViewById(R.id.species_age_text);
            visitNumber=itemView.findViewById(R.id.visit_number);
            pathologiesNumber=itemView.findViewById(R.id.pathologies_number);
            animalPhoto=itemView.findViewById(R.id.animal_photo);
            visitIcon=itemView.findViewById(R.id.visit_icon);
            pathologiesIcon=itemView.findViewById(R.id.pathologies_icon);
            dangerIcon=itemView.findViewById(R.id.report_icon);

            itemView.setOnClickListener(this);
        }

        public void setOnItemClickListener(OnItemClickListener listener){
            this.onItemClickListener= listener;
        }

        public void bind(Animal animal){
             this.animalName.setText(animal.getName());
             this.SpeciesAge.setText(animal.getSpecies()+", "+ DateUtilities.calculateAge(animal.getBirthDate(),context));

             if(animal.getVisitNumber()==0){
                 this.visitNumber.setText("");
                 this.visitIcon.setVisibility(View.GONE);
             }
             else{
                 this.visitNumber.setText(animal.getVisitNumber());
             }

             if(animal.getPathologiesNumber()==0){
                 this.pathologiesNumber.setText("");
                 this.pathologiesIcon.setVisibility(View.GONE);
             }
             else{
                 this.pathologiesNumber.setText(animal.getPathologiesNumber());
             }

             Bitmap animalPhoto=animal.getPhoto();
             if(animalPhoto==null){
                 this.animalPhoto.setImageDrawable(context.getDrawable(R.drawable.default_profile_image));
             }
             else{
                 this.animalPhoto.setImageBitmap(animal.getPhoto());
             }


             //TODO if there is a visit in at least one day
             this.dangerIcon.setVisibility(View.VISIBLE);

        }


        @Override
        public void onClick(View view) {
            if(this.onItemClickListener!=null){
                this.onItemClickListener.OnItemClick(getLayoutPosition());
            }
        }
}
