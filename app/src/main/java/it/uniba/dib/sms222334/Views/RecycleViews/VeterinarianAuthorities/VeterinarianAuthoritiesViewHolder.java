package it.uniba.dib.sms222334.Views.RecycleViews.VeterinarianAuthorities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.location.Location;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.GeoPoint;

import it.uniba.dib.sms222334.Models.Document;
import it.uniba.dib.sms222334.Models.PublicAuthority;
import it.uniba.dib.sms222334.Models.Veterinarian;
import it.uniba.dib.sms222334.R;
import it.uniba.dib.sms222334.Utils.CoordinateUtilities;
import it.uniba.dib.sms222334.Utils.LocationTracker;

public class VeterinarianAuthoritiesViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private static final String TAG="VeterinarianAuthoritiesViewHolder";

        TextView companyName,legalSite,distance;
        ImageView profilePhoto,profileType;

        double latidute,longitude;

        Context context;

        private Document pubVetDocument;

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
            this.pubVetDocument = veterinarian;

            this.latidute=veterinarian.getLocation().getLatitude();
            this.longitude=veterinarian.getLocation().getLongitude();
            this.companyName.setText(veterinarian.getName());

            this.legalSite.setText(CoordinateUtilities.getAddressFromLatLng(context,veterinarian.getLocation(), true));

            Bitmap logo=veterinarian.getPhoto();
            if(logo==null){
                this.profilePhoto.setImageDrawable(context.getDrawable(R.drawable.default_profile_image));
            }
            else{
                this.profilePhoto.setImageBitmap(veterinarian.getPhoto());
            }

            this.profileType.setImageDrawable(context.getDrawable(R.drawable.health));
            this.profileType.setColorFilter(context.getResources().getColor(R.color.main_green,null), PorterDuff.Mode.SRC_ATOP);

            setDistance();
        }

        @SuppressLint("ResourceAsColor")
        public void bind(PublicAuthority publicAuthority){
            this.pubVetDocument = publicAuthority;

            this.latidute=publicAuthority.getLocation().getLatitude();
            this.longitude=publicAuthority.getLocation().getLongitude();
            this.companyName.setText(publicAuthority.getName());
            this.legalSite.setText(publicAuthority.getLocation().toString());

            this.legalSite.setText(CoordinateUtilities.getAddressFromLatLng(context,publicAuthority.getLocation(), true));

            Bitmap logo=publicAuthority.getPhoto();
            if(logo==null){
                this.profilePhoto.setImageDrawable(context.getDrawable(R.drawable.default_profile_image));
            }
            else{
                this.profilePhoto.setImageBitmap(publicAuthority.getPhoto());
            }

            this.profileType.setImageDrawable(context.getDrawable(R.drawable.paw_icon));
            this.profileType.setColorFilter(context.getResources().getColor(R.color.main_green,null), PorterDuff.Mode.SRC_ATOP);

            setDistance();
        }

    private void setDistance() {
        LocationTracker.LocationState state = LocationTracker.getInstance(context).checkLocationState();
        switch (state) {
            case PERMISSION_NOT_GRANTED:
            case PROVIDER_DISABLED:
                this.distance.setText("0 km");
                break;

            case LOCATION_IS_NOT_TRACKING:
                LocationTracker.getInstance(context).startLocationTracking();
                this.distance.setText("... km");
                break;

            case LOCATION_IS_TRACKING_AND_NOT_AVAILABLE:
            case LOCATION_IS_TRACKING_AND_AVAILABLE:
                Location devicePosition = LocationTracker.getInstance(context).getLocation(false);
                if (devicePosition != null) {

                    float distance;
                    if (pubVetDocument instanceof PublicAuthority) {
                        distance = CoordinateUtilities.calculateDistance(new GeoPoint(devicePosition.getLatitude(), devicePosition.getLongitude()), ((PublicAuthority) pubVetDocument).getLocation());
                        ((PublicAuthority) pubVetDocument).setDistance(distance);
                    } else {
                        distance = CoordinateUtilities.calculateDistance(new GeoPoint(devicePosition.getLatitude(), devicePosition.getLongitude()), ((Veterinarian) pubVetDocument).getLocation());
                        ((Veterinarian) pubVetDocument).setDistance(distance);
                    }

                    this.distance.setText(CoordinateUtilities.formatDistance(distance));
                } else {
                    this.distance.setText("... km");
                }
                break;
        }
    }

    @Override
        public void onClick(View view) {
            if(this.onItemClickListener!=null){
                this.onItemClickListener.OnItemClick(getLayoutPosition());
            }
        }
}
