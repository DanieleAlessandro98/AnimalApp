package it.uniba.dib.sms222334.Utils;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.provider.Settings;
import android.widget.Toast;
import androidx.core.app.ActivityCompat;
import it.uniba.dib.sms222334.R;

public class MyLocationManager {
    private static MyLocationManager instance;

    private Context context;
    private boolean isTracking;
    private Location currentLocation;
    private LocationListener locationListener;
    private LocationManager locationManager;

    private MyLocationManager(Context context) {
        this.context = context;
        isTracking = false;

        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                currentLocation = location;
            }
        };
    }

    public static MyLocationManager getInstance(Context context) {
        if (instance == null)
            instance = new MyLocationManager(context);

        return instance;
    }

    public void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            return;

        if (isTracking)
            return;

        if (!isGPSProviderEnabled())
            return;

        isTracking = true;
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0f, locationListener);
    }

    public Location getLocation() {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            return null;

        if (!isGPSProviderEnabled()) {
            showGPSDisabledDialog();
            return null;
        }

        if (isTracking && currentLocation == null) {
            showLocationNotAvailable();
            return null;
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

    private void showLocationNotAvailable() {
        Toast.makeText(context, context.getString(R.string.location_not_available), Toast.LENGTH_SHORT).show();
    }

    private void showGPSDisabledDialog() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
        alertDialog.setMessage(context.getString(R.string.location_gps_disabled_message));
        alertDialog.setPositiveButton(context.getString(R.string.settings), (dialog, which) -> {
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            context.startActivity(intent);
        });
        alertDialog.setNegativeButton(context.getString(R.string.location_gps_disabled_cancel), (dialog, which) -> {});
        alertDialog.show();
    }
}
