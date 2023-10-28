package it.uniba.dib.sms222334.Utils.Permissions;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.pm.PackageManager;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.ActivityResultRegistry;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

public class PermissionManager {
    private static PermissionManager instance;

    private PermissionManager() {
    }

    public static PermissionManager getInstance() {
        if (instance == null) {
            instance = new PermissionManager();
        }

        return instance;
    }

    public ActivityResultLauncher<String[]> registerPermissionLauncher(PermissionInterface permissionInterface) {
        return permissionInterface.getFragment().registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), result ->
                result.forEach((permission, granted) -> {
                    AndroidPermission androidPermission = AndroidPermission.findAndroidEnumFromManifestPermission(permission);
                    if (granted)
                        permissionInterface.permissionGranted(androidPermission);
                    else
                        permissionInterface.permissionNotGranted(androidPermission);
                }
                ));
    }

    public void checkAndRequestPermission(Activity activity, PermissionInterface permissionInterface, AndroidPermission permission) {
        String permissionString = AndroidPermission.findManifestStringFromAndroidPermission(permission);

        if (isPermissionGranted(activity, permissionString))
            permissionInterface.permissionGranted(permission);
        else {
            if (shouldShowPermissionExplanation(activity, permissionString))
                showPermissionExplanation(permissionInterface, permission);
            else
                requestPermission(permissionInterface, permission);
        }
    }

    private boolean isPermissionGranted(Context context, String permission) {
        return ActivityCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED;
    }

    private boolean shouldShowPermissionExplanation(Activity activity, String permission) {
        return ActivityCompat.shouldShowRequestPermissionRationale(activity, permission);
    }

    private void showPermissionExplanation(PermissionInterface permissionInterface, AndroidPermission permission) {
        permissionInterface.showPermissionExplanation(permission);
    }

    private void requestPermission(PermissionInterface permissionInterface, AndroidPermission permission) {
        permissionInterface.requestPermission(permission);
    }
}
