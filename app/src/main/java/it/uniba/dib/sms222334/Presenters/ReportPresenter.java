package it.uniba.dib.sms222334.Presenters;

import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import it.uniba.dib.sms222334.Database.Dao.MediaDao;
import it.uniba.dib.sms222334.Database.DatabaseCallbackResult;
import it.uniba.dib.sms222334.Fragmets.HomeFragment;
import it.uniba.dib.sms222334.Models.Animal;
import it.uniba.dib.sms222334.Models.Private;
import it.uniba.dib.sms222334.Models.PublicAuthority;
import it.uniba.dib.sms222334.Models.Report;
import it.uniba.dib.sms222334.Models.SessionManager;
import it.uniba.dib.sms222334.Models.User;
import it.uniba.dib.sms222334.Utils.AnimalSpecies;
import it.uniba.dib.sms222334.Utils.DateUtilities;
import it.uniba.dib.sms222334.Utils.Media;
import it.uniba.dib.sms222334.Utils.ReportType;
import it.uniba.dib.sms222334.Utils.Validations;

public class ReportPresenter {
    private final HomeFragment reportFragment;

    public ReportPresenter(HomeFragment reportFragment) {
        this.reportFragment = reportFragment;
    }

    public void pickPhoto(Uri uri) {
        try {
            Bitmap bitmap = Media.getBitmapFromUri(uri, reportFragment.getContext());
            reportFragment.setPhotoPicked(bitmap);
        } catch (IOException e) {
            reportFragment.showPhotoUpdateError();
            e.printStackTrace();
        }
    }

    public void onAdd(int selectedPositionReport, int selectedPositionSpecies, String description, String name, String age, String animalID, boolean isShowAnimalProfile) {
        if (reportFragment.getPhotoPicked() == null) {
            reportFragment.showPhotoUpdateError();
            return;
        }

        if (selectedPositionReport < 0 || selectedPositionReport >= ReportType.values().length)
            return;

        if (selectedPositionSpecies < 0 || selectedPositionSpecies >= AnimalSpecies.values().length)
            return;

        if (!Validations.isValidReportDescription(description)) {
            reportFragment.showInvalidReportDescription();
            return;
        }

        if (!age.isEmpty()) {
            int validationCode = Validations.isValidAgeString(age, reportFragment.getContext());
            if (validationCode != 0) {
                reportFragment.showInvalidAge(validationCode);
                return;
            }
        }

        ReportType type = ReportType.values()[selectedPositionReport];
        AnimalSpecies species = AnimalSpecies.values()[selectedPositionSpecies];

        Report reportModel = Report.Builder.create("",
                type,
                species,
                description,
                0f,
                0f,
                reportFragment.getPhotoPicked())
                .setAnimalName(name)
                .setAnimalAge(DateUtilities.parseAgeString(age, reportFragment.getContext()))
                .setAnimalID(animalID)
                .setShowAnimalProfile(isShowAnimalProfile)
                .build();

        reportModel.createReport(new DatabaseCallbackResult() {
            @Override
            public void onDataRetrieved(Object result) {
                MediaDao.PhotoUploadListener listener = new MediaDao.PhotoUploadListener() {
                    @Override
                    public void onPhotoUploaded() {
                        reportFragment.showCreateSuccessful();
                    }

                    @Override
                    public void onPhotoUploadFailed(Exception exception) {
                        reportFragment.showPhotoUpdateError();
                    }
                };

                MediaDao mediaDao = new MediaDao();
                mediaDao.uploadPhoto(reportModel.getReportPhoto(), Media.REPORT_PHOTO_PATH, result + Media.PROFILE_PHOTO_EXTENSION, listener);
            }

            @Override
            public void onDataRetrieved(ArrayList results) {

            }

            @Override
            public void onDataNotFound() {

            }

            @Override
            public void onDataQueryError(Exception e) {
                reportFragment.showCreateError();
            }
        });
    }

    public List<Animal> getMyAnimalNames() {
        User user = SessionManager.getInstance().getCurrentUser();
        ArrayList<Animal> myAnimalNames = new ArrayList<>();

        if (!SessionManager.getInstance().isLogged())
            return myAnimalNames;

        switch (user.getRole()) {
            case PRIVATE:
                for (Animal animal : ((Private) user).getAnimalList())
                    myAnimalNames.add(animal);
                break;

            case PUBLIC_AUTHORITY:
                for (Animal animal : ((PublicAuthority) user).getAnimalList())
                    myAnimalNames.add(animal);
                break;

            default:
                break;
        }

        return myAnimalNames;
    }

}
