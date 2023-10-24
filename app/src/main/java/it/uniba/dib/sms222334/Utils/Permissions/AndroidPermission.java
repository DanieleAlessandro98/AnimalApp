package it.uniba.dib.sms222334.Utils.Permissions;

public enum AndroidPermission {
    ACCESS_FINE_LOCATION;

    public static String findManifestStringFromAndroidPermission(AndroidPermission permission) {
        switch (permission) {
            case ACCESS_FINE_LOCATION:
                return android.Manifest.permission.ACCESS_FINE_LOCATION;

            default:
                return null;
        }
    }

    public static AndroidPermission findAndroidEnumFromManifestPermission(String permission) {
        switch (permission) {
            case android.Manifest.permission.ACCESS_FINE_LOCATION:
                return ACCESS_FINE_LOCATION;

            default:
                return null;
        }
    }
}
