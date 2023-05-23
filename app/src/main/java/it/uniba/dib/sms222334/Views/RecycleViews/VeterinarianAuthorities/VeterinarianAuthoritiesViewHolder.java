package it.uniba.dib.sms222334.Views.RecycleViews.VeterinarianAuthorities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.location.Location;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import it.uniba.dib.sms222334.Models.PublicAuthority;
import it.uniba.dib.sms222334.Models.Request;
import it.uniba.dib.sms222334.Models.Segnalation;
import it.uniba.dib.sms222334.Models.Veterinarian;
import it.uniba.dib.sms222334.R;
import it.uniba.dib.sms222334.Views.CoordinateUtilities;

public class VeterinarianAuthoritiesViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private static final String TAG="VeterinarianAuthoritiesViewHolder";

        TextView companyName,legalSite,distance;
        ImageView profilePhoto,profileType;

        Float latidute,longitude;

        Context context;

        public interface OnItemClickListener{
            void OnItemClick(int position);
        }

        OnItemClickListener onItemClickListener;

        public VeterinarianAuthoritiesViewHolder(@NonNull View itemView,Context context) {
            super(itemView);

            this.context=context;

            distance=itemView.findViewById(R.id.distance_text);
            companyName=itemView.findViewById(R.id.company_name);
            legalSite=itemView.findViewById(R.id.legal_site_text);
            profilePhoto=itemView.findViewById(R.id.profile_photo);
            profileType=itemView.findViewById(R.id.profile_type);

            itemView.setOnClickListener(this);
        }

        public void setOnItemClickListener(OnItemClickListener listener){
            this.onItemClickListener= listener;
        }

        public void bind(Veterinarian veterinarian){
             this.latidute=veterinarian.getLatitude();
             this.longitude=veterinarian.getLongitude();
             this.companyName.setText(veterinarian.getCompanyName());
             this.legalSite.setText(veterinarian.getLegal_site());

            Bitmap logo=veterinarian.getLogo();
            if(logo==null){
                this.profilePhoto.setImageDrawable(context.getDrawable(R.drawable.baseline_profile_24));
            }
            else{
                this.profilePhoto.setImageBitmap(veterinarian.getLogo());
            }

             this.profileType.setImageDrawable(context.getDrawable(R.drawable.health));
             this.profileType.setColorFilter(context.getResources().getColor(R.color.main_green,null), PorterDuff.Mode.SRC_ATOP);
        }

        @SuppressLint("ResourceAsColor")
        public void bind(PublicAuthority publicAuthority){
            this.latidute=publicAuthority.getLatitude();
            this.longitude=publicAuthority.getLongitude();
            this.companyName.setText(publicAuthority.getCompany_name());
            this.legalSite.setText(publicAuthority.getlegalSite());

            Bitmap logo=publicAuthority.getLogo();
            if(logo==null){
                this.profilePhoto.setImageDrawable(context.getDrawable(R.drawable.baseline_profile_24));
            }
            else{
                this.profilePhoto.setImageBitmap(publicAuthority.getLogo());
            }

            this.profileType.setImageDrawable(context.getDrawable(R.drawable.paw));
            this.profileType.setColorFilter(context.getResources().getColor(R.color.main_green,null), PorterDuff.Mode.SRC_ATOP);
        }

        public void setDistance(Location devicePosition) {
            if(devicePosition!=null){
                this.distance.setText(CoordinateUtilities.calculateDistance(this.latidute
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
