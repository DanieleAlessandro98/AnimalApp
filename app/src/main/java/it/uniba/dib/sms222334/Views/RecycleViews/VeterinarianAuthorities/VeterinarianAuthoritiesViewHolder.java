package it.uniba.dib.sms222334.Views.RecycleViews.VeterinarianAuthorities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import it.uniba.dib.sms222334.Models.PublicAuthority;
import it.uniba.dib.sms222334.Models.Veterinarian;
import it.uniba.dib.sms222334.R;
import it.uniba.dib.sms222334.Utils.CoordinateUtilities;

public class VeterinarianAuthoritiesViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private static final String TAG="VeterinarianAuthoritiesViewHolder";

        TextView companyName,legalSite,distance;
        ImageView profilePhoto,profileType;

        double latidute,longitude;

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
            this.latidute=veterinarian.getLocation().getLatitude();
            this.longitude=veterinarian.getLocation().getLongitude();
            this.companyName.setText(veterinarian.getName());

            this.legalSite.setText(CoordinateUtilities.getAddressFromLatLng(context,veterinarian.getLocation()));

            Bitmap logo=veterinarian.getPhoto();
            if(logo==null){
                this.profilePhoto.setImageDrawable(context.getDrawable(R.drawable.default_profile_image));
            }
            else{
                this.profilePhoto.setImageBitmap(veterinarian.getPhoto());
            }

            this.profileType.setImageDrawable(context.getDrawable(R.drawable.health));
            this.profileType.setColorFilter(context.getResources().getColor(R.color.main_green,null), PorterDuff.Mode.SRC_ATOP);
        }

        @SuppressLint("ResourceAsColor")
        public void bind(PublicAuthority publicAuthority){
            this.latidute=publicAuthority.getLocation().getLatitude();
            this.longitude=publicAuthority.getLocation().getLongitude();
            this.companyName.setText(publicAuthority.getName());
            this.legalSite.setText(publicAuthority.getLocation().toString());

            this.legalSite.setText(CoordinateUtilities.getAddressFromLatLng(context,publicAuthority.getLocation()));

            Bitmap logo=publicAuthority.getPhoto();
            if(logo==null){
                this.profilePhoto.setImageDrawable(context.getDrawable(R.drawable.default_profile_image));
            }
            else{
                this.profilePhoto.setImageBitmap(publicAuthority.getPhoto());
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
