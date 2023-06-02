package it.uniba.dib.sms222334.Presenters;

import android.graphics.Bitmap;
import android.net.Uri;

import java.io.IOException;
import java.util.Date;

import it.uniba.dib.sms222334.Database.Dao.AuthenticationCallbackResult;
import it.uniba.dib.sms222334.Database.Dao.MediaDao;
import it.uniba.dib.sms222334.Fragmets.ProfileFragment;
import it.uniba.dib.sms222334.Models.Authentication;
import it.uniba.dib.sms222334.Models.Private;
import it.uniba.dib.sms222334.Models.SessionManager;
import it.uniba.dib.sms222334.Models.User;
import it.uniba.dib.sms222334.Utils.Media;
import it.uniba.dib.sms222334.Utils.UserRole;
import it.uniba.dib.sms222334.Utils.Validations;

public class UserPresenter implements AuthenticationCallbackResult.LoginOrLogoutCompletedListener {

    private ProfileFragment profileView;
    private User profileModel;

    public UserPresenter(ProfileFragment profileView) {
        this.profileView = profileView;
        this.profileModel = SessionManager.getInstance().getCurrentUser();
    }

    public void initUserData() {
        this.profileModel = SessionManager.getInstance().getCurrentUser();

        UserRole userRole = profileModel.getRole();

        switch (userRole) {
            case PRIVATE:
                profileView.onInitPrivateData((Private) profileModel);
                break;

            case PUBLIC_AUTHORITY:
            case VETERINARIAN:
                // ...
                break;
        }
    }

    public void updateProfile(String name, String surname, Date birthDate, String taxID, long phone, String email, String password) {
        if (!Validations.isValidName(name)) {
            profileView.showInvalidInput(1);
            return;
        }
        if (!Validations.isValidSurname(surname)) {
            profileView.showInvalidInput(2);
            return;
        }
        if (!Validations.isValidDateBirth(birthDate)) {
            profileView.showInvalidInput(3);
            return;
        }
        if (!Validations.isValidEmail(email)) {
            profileView.showInvalidInput(4);
            return;
        }
        if (!Validations.isValidPassword(password)) {
            profileView.showInvalidInput(5);
            return;
        }

        UserRole userRole = profileModel.getRole();

        switch (userRole) {
            case PRIVATE:
                Private.Builder updatedPrivate=Private.Builder.
                        create(
                                profileModel.getFirebaseID(),
                                name,
                                email)
                        .setPassword(password)
                        .setPhone(phone)
                        .setSurname(surname)
                        .setBirthDate(birthDate)
                        .setTaxIdCode(taxID);

                if (!profileModel.getPhoto().sameAs(profileView.getPhotoPicked())) {
                    MediaDao.PhotoUploadListener listener = new MediaDao.PhotoUploadListener() {
                        @Override
                        public void onPhotoUploaded() {
                            profileModel.setPhoto(profileView.getPhotoPicked());
                            updatedPrivate.setPhoto(profileView.getPhotoPicked());

                            User updatedUser = updatedPrivate.build();

                            SessionManager.getInstance().updateCurrentUser(updatedUser);
                            updatedUser.updateProfile();

                            profileView.showUpdateSuccessful();
                        }

                        @Override
                        public void onPhotoUploadFailed(Exception exception) {
                            profileView.showPhotoUpdateError();
                        }
                    };

                    MediaDao mediaDao = new MediaDao();
                    mediaDao.uploadPhoto(profileModel.getPhoto(), profileModel.getFirebaseID() + Media.PROFILE_PHOTO_EXTENSION, listener);
                }
                break;

            case PUBLIC_AUTHORITY:
            case VETERINARIAN:
                // ...
                break;
        }
    }

    public void deleteProfile() {
        if ((!SessionManager.getInstance().isLogged()) || profileModel == null)
            return;

        Authentication authentication = new Authentication(this);
        authentication.delete();
    }

    public void editPhoto() {
        if (!profileView.isPhotoPickerAvailable()) {
            profileView.showPhotoPickerNotAvailable();
            return;
        }

        if (!profileView.isPhotoPermissionGranted()) {
            if (profileView.shouldShowRequestPhotoPermission()) {
                profileView.showRequestPhotoPermission();
            } else {
                profileView.requestPhotoPermission();
            }

            return;
        }

        profileView.onLaunchPhotoPicker();
    }

    public void pickPhoto(Uri uri) {
        try {
            Bitmap bitmap = Media.getBitmapFromUri(uri, profileView.getContext());
            profileView.setPhotoPicked(bitmap);
        } catch (IOException e) {
            profileView.showPhotoUpdateError();
            e.printStackTrace();
        }
    }

    @Override
    public void onLoginOrLogoutCompleted(boolean isSuccessful) {
        if (isSuccessful) {
            profileModel.deleteProfile();
            profileView.showLoginSuccessful();
        } else {
            profileView.showLogoutError();
        }
    }
}
