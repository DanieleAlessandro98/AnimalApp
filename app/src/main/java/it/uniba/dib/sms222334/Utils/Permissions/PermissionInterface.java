package it.uniba.dib.sms222334.Utils.Permissions;

import androidx.fragment.app.Fragment;

public interface PermissionInterface<T extends Enum<T>> {
    Fragment getFragment();
    void registerPermissionLauncher();
    void requestPermission(T permission);
    void launchPermissionHandler(T permission);
    void showPermissionExplanation(T permission);
    void permissionGranted(T permission);
    void permissionNotGranted(T permission);
}
