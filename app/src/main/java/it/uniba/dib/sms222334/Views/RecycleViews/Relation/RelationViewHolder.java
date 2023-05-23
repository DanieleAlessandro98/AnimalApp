package it.uniba.dib.sms222334.Views.RecycleViews.Relation;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import it.uniba.dib.sms222334.Models.Expense;
import it.uniba.dib.sms222334.Models.Relation;
import it.uniba.dib.sms222334.R;

public class RelationViewHolder extends RecyclerView.ViewHolder{
        private static final String TAG="RelationViewHolder";

        TextView relationType,animalName,animalSpecieAge;

        ImageView animalPhoto;

        Context context;

        String[] relationTypeArray;

        public RelationViewHolder(@NonNull View itemView,Context context) {
            super(itemView);

            this.context=context;

            relationTypeArray = context.getResources().getStringArray(R.array.relation_type);

            relationType=itemView.findViewById(R.id.relation_type);
            animalName=itemView.findViewById(R.id.animal_name);
            animalSpecieAge=itemView.findViewById(R.id.species_age_text);
            animalPhoto=itemView.findViewById(R.id.animal_photo);
        }


        @SuppressLint("ResourceType")
        public void bind(Relation relation){
             this.relationType.setText(relationTypeArray[relation.getRelationType().ordinal()]);

             this.animalName.setText(relation.getAnimal().getName());

             this.animalSpecieAge.setText(relation.getAnimal().getSpecies()+", "+relation.getAnimal().getAge());

             this.animalPhoto.setImageBitmap(relation.getAnimal().getPhoto());

            switch (relation.getRelationType()){
                case COHABITEE:
                    this.relationType.setBackgroundTintList(ColorStateList.valueOf(context.getColor(R.color.purple_red)));
                    break;
                case FRIEND:
                    this.relationType.setBackgroundTintList(ColorStateList.valueOf(context.getColor(R.color.main_green)));
                    break;
                case INCOMPATIBLE:
                    this.relationType.setBackgroundTintList(ColorStateList.valueOf(context.getColor(R.color.soft_red)));
            }
        }
}
