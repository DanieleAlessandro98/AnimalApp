package it.uniba.dib.sms222334.Presenters;

import android.graphics.Bitmap;
import android.net.Uri;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.Date;
import it.uniba.dib.sms222334.Database.Dao.Authentication.AuthenticationCallbackResult;
import it.uniba.dib.sms222334.Database.Dao.Authentication.AuthenticationDao;
import it.uniba.dib.sms222334.Database.Dao.MediaDao;
import it.uniba.dib.sms222334.Database.Dao.User.PrivateDao;
import it.uniba.dib.sms222334.Database.Dao.User.PublicAuthorityDao;
import it.uniba.dib.sms222334.Database.DatabaseCallbackResult;
import it.uniba.dib.sms222334.Fragmets.ProfileFragment;
import it.uniba.dib.sms222334.Models.Authentication;
import it.uniba.dib.sms222334.Models.Owner;
import it.uniba.dib.sms222334.Models.Private;
import it.uniba.dib.sms222334.Models.PublicAuthority;
import it.uniba.dib.sms222334.Models.SessionManager;
import it.uniba.dib.sms222334.Models.User;
import it.uniba.dib.sms222334.Models.Veterinarian;
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

    public static void getOwnerList(String emailMatched, DatabaseCallbackResult<Owner> callback){
        new PrivateDao().getPrivatesByEmail(emailMatched, callback);
        new PublicAuthorityDao().getPublicAuthoritiesByEmail(emailMatched,callback);
    }

    public void initUserData() {
        UserRole userRole = profileModel.getRole();

        switch (userRole) {
            case PRIVATE:
                profileView.onInitPrivateData((Private) profileModel);
                break;

            case PUBLIC_AUTHORITY:
                profileView.onInitAuthorityData((PublicAuthority) profileModel);
                break;

            case VETERINARIAN:
                profileView.onInitVeterinarianData((Veterinarian) profileModel);
                break;
        }
    }

    public void mailcheckUpdateProfile(String name, String surname, Date birthDate, String taxID, String phone, String email, String password, String site, String companyname ) {
        if (!Validations.isValidEmail(email)) {
            profileView.showInvalidInput(4);
            return;
        }

        AuthenticationDao authenticationDao = new AuthenticationDao();

        if(SessionManager.getInstance().getCurrentUser().getEmail().compareTo(email) != 0){
            authenticationDao.isEmailUnique(email, result -> {
                if (result==true){
                    profileView.showInvalidInput(6);
                }else{
                    updateProfile(name, surname, birthDate, taxID, phone, email, password, site, companyname);
                }
            });
        }
        else{
            updateProfile(name, surname, birthDate, taxID, phone, email, password, site, companyname);
        }
    }

    public void updateProfile(String name, String surname, Date birthDate, String taxID, String phone, String email, String password, String location, String companyname) {

        if (!Validations.isValidPassword(password)) {
            profileView.showInvalidInput(5);
            return;
        }

        GeoPoint locationValue = Validations.isValidLocation(location, profileView.getContext());
        if (locationValue == null) {
            profileView.showInvalidInput(7);
            return;
        }

        switch (profileModel.getRole()) {
            case PRIVATE:
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
                ((Private) profileModel).setName(name);
                ((Private) profileModel).setSurname(surname);
                ((Private) profileModel).setBirthDate(birthDate);
                ((Private) profileModel).setTaxIDCode(taxID);
                break;

            case PUBLIC_AUTHORITY:
                if (!Validations.isValidCompanyName(companyname)) {
                    profileView.showInvalidInput(1);
                    return;
                }

                ((PublicAuthority) profileModel).setName(companyname);
                GeoPoint fakesite = new GeoPoint(-90, 90); //TODO: implementare il geopoint
                ((PublicAuthority) profileModel).setLocation(fakesite);
                break;

            case VETERINARIAN:
                if (!Validations.isValidCompanyName(companyname)) {
                    profileView.showInvalidInput(1);
                    return;
                }
                ((Veterinarian) profileModel).setName(companyname);
                fakesite = new GeoPoint(-90, 90); //TODO: implementare il geopoint
                ((Veterinarian) profileModel).setLocation(fakesite);
                break;
        }

        profileModel.setEmail(email);
        profileModel.setPassword(password);
        profileModel.setPhone(Long.parseLong(phone));
        profileModel.setLocation(new GeoPoint(locationValue.getLatitude(), locationValue.getLongitude()));

        Authentication authentication = new Authentication(new AuthenticationCallbackResult.UpdateAuthentication() {
            @Override
            public void onUpdateSuccessful() {
                if ((profileView.getPhotoPicked() != null) && (!profileModel.getPhoto().sameAs(profileView.getPhotoPicked()))) {
                    MediaDao.PhotoUploadListener listener = new MediaDao.PhotoUploadListener() {
                        @Override
                        public void onPhotoUploaded() {
                            profileModel.setPhoto(profileView.getPhotoPicked());
                            profileModel.updateProfile(true);
                            profileView.showUpdateSuccessful(profileModel);
                        }

                        @Override
                        public void onPhotoUploadFailed(Exception exception) {
                            profileView.showPhotoUpdateError();
                        }

                        @Override
                        public void onPhotoUploadProgress(UploadTask.TaskSnapshot snapshot) {

                        }
                    };

                    MediaDao mediaDao = new MediaDao();
                    mediaDao.uploadPhoto(profileView.getPhotoPicked(), Media.PROFILE_PHOTO_PATH, profileModel.getFirebaseID() + Media.PROFILE_PHOTO_EXTENSION, listener);
                } else {
                    profileModel.updateProfile(false);
                    profileView.showUpdateSuccessful(profileModel);
                }
            }

            @Override
            public void onUpdateFailure() {

            }
        });

        authentication.updateUserAuth(email, password);
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
