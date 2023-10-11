package it.uniba.dib.sms222334.Presenters;

import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import it.uniba.dib.sms222334.Database.Dao.MediaDao;
import it.uniba.dib.sms222334.Database.DatabaseCallbackResult;
import it.uniba.dib.sms222334.Fragmets.HomeFragment;
import it.uniba.dib.sms222334.Models.Report;
import it.uniba.dib.sms222334.Utils.AnimalSpecies;
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

    public void onAdd(int selectedPositionReport, int selectedPositionSpecies, String description, String name, String age) {
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

        ReportType type = ReportType.values()[selectedPositionReport];
        AnimalSpecies species = AnimalSpecies.values()[selectedPositionSpecies];

        Report reportModel = Report.Builder.create("", type, species, description, 0f, 0f, reportFragment.getPhotoPicked()).build();
        if (!name.isEmpty())
            reportModel.setAnimalName(name);

        int ageValue;
        try {
            ageValue = Integer.parseInt(age);
        } catch (NumberFormatException e) {
            ageValue = -1;
        }
        reportModel.setAnimalAge(ageValue);

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
        //test();
    }

    private void test() {
        String addressToConvert = "Via Giuseppe Capruzzi, Bari, Italia";
        Geocoder geocoder = new Geocoder(reportFragment.getContext(), Locale.getDefault());

        try {
            // Ottieni una lista di risultati
            List<Address> addresses = geocoder.getFromLocationName(addressToConvert, 1);

            if (addresses != null && !addresses.isEmpty()) {
                Address address = addresses.get(0);
                double latitude = address.getLatitude();
                double longitude = address.getLongitude();

                // Ora puoi utilizzare latitude e longitude
                Log.d("test", "Latitudine: " + latitude + ", Longitudine: " + longitude);
            } else {
                // L'indirizzo non è stato trovato
                Log.d("test", "Indirizzo non trovato");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


}
