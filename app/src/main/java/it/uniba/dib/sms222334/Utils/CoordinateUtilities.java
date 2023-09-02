package it.uniba.dib.sms222334.Utils;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.util.Log;

import com.google.firebase.firestore.GeoPoint;
import com.google.type.LatLng;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public final class CoordinateUtilities {

    public static final int EARTH_RADIUS_KILOMETERS=6371;
    public static final int EARTH_RADIUS_METERS=6371000;
    public static final int EARTH_RADIUS_MILES=3963;

    public static final int NO_METRICS=001;

    public static final int WITH_METRICS=002;

    public static String calculateDistance(double lat1,double lat2,double lon1,double lon2,int mode){
        // The math module contains a function
        // named toRadians which converts from
        // degrees to radians.

        lon1 = Math.toRadians(lon1);
        lon2 = Math.toRadians(lon2);
        lat1 = Math.toRadians(lat1);
        lat2 = Math.toRadians(lat2);

        // Haversine formula
        double dlon = lon2 - lon1;
        double dlat = lat2 - lat1;
        double a = Math.pow(Math.sin(dlat / 2), 2)
                + Math.cos(lat1) * Math.cos(lat2)
                * Math.pow(Math.sin(dlon / 2),2);

        double c = ( 2 * Math.asin(Math.sqrt(a)));

        if(mode==WITH_METRICS){
            return(c * EARTH_RADIUS_METERS)>1000?((int)(c*EARTH_RADIUS_KILOMETERS)+"km"):((int)(c*EARTH_RADIUS_METERS))+"m";
        }
        else if(mode==NO_METRICS){
            return((int)(c * EARTH_RADIUS_METERS)+"");
        }
        else{
            throw new IllegalArgumentException("Inserted mode is not known");
        }
    }

    public static String getAddressFromLatLng(Context context, GeoPoint location) {
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        String address = "";

        try {
            List<Address> addresses = geocoder.getFromLocation(
                    location.getLatitude(), location.getLongitude(), 1);

            if (addresses != null && addresses.size() > 0) {
                Address fetchedAddress = addresses.get(0);
                address = fetchedAddress.getLocality();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return address;
    }
}
