package it.uniba.dib.sms222334.Utils;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;
import androidx.core.app.ActivityCompat;
import it.uniba.dib.sms222334.R;

public class LocationTracker {
    public enum LocationState {
        NULL,
        PERMISSION_NOT_GRANTED,
        PROVIDER_DISABLED,
        LOCATION_IS_NOT_TRACKING,
        LOCATION_IS_TRACKING_AND_NOT_AVAILABLE,
        LOCATION_IS_TRACKING_AND_AVAILABLE,
    }

    private static LocationTracker instance;

    private Context context;
    private boolean isTracking;
    private Location currentLocation;
    private LocationListener locationListener;
    private LocationManager locationManager;
    private NotifyLocationChanged notifyLocationChanged;

    private LocationTracker(Context context) {
        this.context = context;
        isTracking = false;

        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                currentLocation = location;

                if (notifyLocationChanged != null)
                    notifyLocationChanged.locationChanged();
            }
        };
    }

    public static LocationTracker getInstance(Context context) {
        if (instance == null)
            instance = new LocationTracker(context);

        return instance;
    }

    public LocationState checkLocationState() {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            return LocationState.PERMISSION_NOT_GRANTED;

        if (!isGPSProviderEnabled())
            return LocationState.PROVIDER_DISABLED;

        if (!isTracking)
            return LocationState.LOCATION_IS_NOT_TRACKING;

        if (isTracking && currentLocation == null)
            return LocationState.LOCATION_IS_TRACKING_AND_NOT_AVAILABLE;

        if (isTracking && currentLocation != null)
            return LocationState.LOCATION_IS_TRACKING_AND_AVAILABLE;

        return LocationState.NULL;
    }

    public void startLocationTracking() {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            return;

        if (isTracking)
            return;

        isTracking = true;
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0f, locationListener);
    }

    public void setNotifyLocationChangedListener(NotifyLocationChanged notifyLocationChanged) {
        this.notifyLocationChanged = notifyLocationChanged;
    }

    public Location getLocation(boolean showWarning) {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            return null;

        if (showWarning) {
            if (isTracking && currentLocation == null)
                showLocationNotAvailable();
        }

        return currentLocation;
    }

    public void stopLocationUpdates() {
        if (!isTracking)
            return;

        isTracking = false;
        locationManager.removeUpdates(locationListener);
    }

    private boolean isGPSProviderEnabled() {
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    public void showLocationNotAvailable() {
        Toast.makeText(context, context.getString(R.string.location_not_available), Toast.LENGTH_SHORT).show();
    }

    public interface NotifyLocationChanged {
        void locationChanged();
    }
}
