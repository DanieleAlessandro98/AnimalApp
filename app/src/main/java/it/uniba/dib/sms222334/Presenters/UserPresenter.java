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

public class UserPresenter implements AuthenticationCallbackResult.LogoutCompletedListener {

    private ProfileFragment profileView;
    private User profileModel;

    public UserPresenter(ProfileFragment profileView) {
        this.profileView = profileView;
        this.profileModel = SessionManager.getInstance().getCurrentUser();
    }

    public void initUserData() {
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

        profileModel.setName(name);
        profileModel.setEmail(email);
        profileModel.setPassword(password);
        profileModel.setPhone(phone);

        switch (profileModel.getRole()) {
            case PRIVATE:
                ((Private)profileModel).setSurname(surname);
                ((Private)profileModel).setBirthDate(birthDate);
                ((Private)profileModel).setTaxIDCode(taxID);
                break;

            case PUBLIC_AUTHORITY:
            case VETERINARIAN:
                // ...
                break;
        }

        if (!profileModel.getPhoto().sameAs(profileView.getPhotoPicked())) {
            MediaDao.PhotoUploadListener listener = new MediaDao.PhotoUploadListener() {
                @Override
                public void onPhotoUploaded() {
                    profileModel.setPhoto(profileView.getPhotoPicked());
                    profileModel.updateProfile();
                    profileView.showUpdateSuccessful();
                }

                @Override
                public void onPhotoUploadFailed(Exception exception) {
                    profileView.showPhotoUpdateError();
                }
            };

            MediaDao mediaDao = new MediaDao();
            mediaDao.uploadPhoto(profileView.getPhotoPicked(), profileModel.getFirebaseID() + Media.PROFILE_PHOTO_EXTENSION, listener);
        }
        else {
            profileModel.updateProfile();
            profileView.showUpdateSuccessful();
        }
    }

    public void deleteProfile() {
        if ((!SessionManager.getInstance().isLogged()) || profileModel == null)
            return;

        Authentication authentication = new Authentication(this);
        authentication.delete();
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
    public void onLogoutCompleted(boolean isSuccessful) {
        if (isSuccessful) {
            profileModel.deleteProfile();
            profileView.showLogoutSuccessful();
        } else {
            profileView.showLogoutError();
        }
    }
}
