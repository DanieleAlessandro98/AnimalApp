package it.uniba.dib.sms222334.Presenters;

import android.graphics.Bitmap;
import android.net.Uri;

import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import it.uniba.dib.sms222334.Database.Dao.MediaDao;
import it.uniba.dib.sms222334.Database.Dao.ReportDao;
import it.uniba.dib.sms222334.Database.DatabaseCallbackResult;
import it.uniba.dib.sms222334.Fragmets.HomeFragment;
import it.uniba.dib.sms222334.Models.Animal;
import it.uniba.dib.sms222334.Models.Private;
import it.uniba.dib.sms222334.Models.PublicAuthority;
import it.uniba.dib.sms222334.Models.Report;
import it.uniba.dib.sms222334.Models.SessionManager;
import it.uniba.dib.sms222334.Models.User;
import it.uniba.dib.sms222334.Utils.AnimalSpecies;
import it.uniba.dib.sms222334.Utils.AnimalStates;
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

    public void onAdd(int selectedPositionReport, int selectedPositionSpecies, String description, String name, String age, float latitude, float longitude, Animal animal, boolean isShowAnimalProfile) {
        if (selectedPositionReport < 0 || selectedPositionReport >= ReportType.values().length)
            return;

        if (selectedPositionSpecies < 0 || selectedPositionSpecies >= AnimalSpecies.values().length)
            return;

        ReportType type = ReportType.values()[selectedPositionReport];

        if (type == ReportType.LOST && name.equals("")) {
            reportFragment.showInvalidReportSelectedAnimal();
            return;
        }

        if (reportFragment.getPhotoPicked() == null) {
            reportFragment.showPhotoUpdateError();
            return;
        }

        if (!Validations.isValidDescription(description)) {
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

        AnimalSpecies species = AnimalSpecies.values()[selectedPositionSpecies];

        Report reportModel = Report.Builder.create("",
                SessionManager.getInstance().getCurrentUser(),
                type,
                species,
                description, new GeoPoint(latitude, longitude),
                reportFragment.getPhotoPicked())
                .setAnimalName(name)
                .setAnimalAge(DateUtilities.parseAgeString(age, reportFragment.getContext()))
                .setAnimal(animal)
                .setShowAnimalProfile(isShowAnimalProfile)
                .build();

        reportModel.createReport(new DatabaseCallbackResult() {
            @Override
            public void onDataRetrieved(Object result) {
                MediaDao.PhotoUploadListener listener = new MediaDao.PhotoUploadListener() {
                    @Override
                    public void onPhotoUploaded() {
                        reportFragment.showReportCreateSuccessful();
                        reportFragment.loadReportsAndRequests();
                    }

                    @Override
                    public void onPhotoUploadProgress(UploadTask.TaskSnapshot snapshot) {

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
                reportFragment.showReportCreateError();
            }
        });
    }

    public List<Animal> getMyAnimalNames(boolean showEmptyAnimal) {
        User user = SessionManager.getInstance().getCurrentUser();
        ArrayList<Animal> myAnimalNames = new ArrayList<>();

        if (!SessionManager.getInstance().isLogged())
            return myAnimalNames;

        if (showEmptyAnimal)
            myAnimalNames.add(Animal.Builder.create("", AnimalStates.ADOPTED).build());

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

    public void getReportList(DatabaseCallbackResult callback) {
        ReportDao reportDao = new ReportDao();
        reportDao.getAllReports(callback);
    }


    public void delete(Report requestReport) {
        requestReport.deleteReport(new DatabaseCallbackResult() {
            @Override
            public void onDataRetrieved(Object result) {
                boolean resultDelete = (boolean) result;
                if (resultDelete)
                    reportFragment.showDocumentDeleteSuccessful(requestReport);
                else
                    reportFragment.showDocumentDeleteError(requestReport);
            }

            @Override
            public void onDataRetrieved(ArrayList results) {

            }

            @Override
            public void onDataNotFound() {

            }

            @Override
            public void onDataQueryError(Exception e) {

            }
        });
    }

    public void onEdit(Report report, String description, String name, String age, float latitude, float longitude) {
        if (!Validations.isValidDescription(description)) {
            reportFragment.showInvalidReportDescription();
            return;
        }

        if (report.getType() == ReportType.LOST && name.equals("")) {
            reportFragment.showInvalidReportSelectedAnimal();
            return;
        }

        if (!age.isEmpty()) {
            int validationCode = Validations.isValidAgeString(age, reportFragment.getContext());
            if (validationCode != 0) {
                reportFragment.showInvalidAge(validationCode);
                return;
            }
        }

        report.setDescription(description);
        report.setAnimalName(name);
        report.setAnimalAge(DateUtilities.parseAgeString(age, reportFragment.getContext()));
        report.setLocation(new GeoPoint(latitude, longitude));

        if (!report.getReportPhoto().sameAs(reportFragment.getPhotoPicked())) {
            MediaDao.PhotoUploadListener listener = new MediaDao.PhotoUploadListener() {
                @Override
                public void onPhotoUploaded() {
                    report.setReportPhoto(reportFragment.getPhotoPicked());

                    report.updateReport(new DatabaseCallbackResult() {
                        @Override
                        public void onDataRetrieved(Object result) {
                            boolean resultValue = (boolean) result;
                            if (resultValue)
                                reportFragment.showReportUpdateSuccessful(report);
                            else
                                reportFragment.showReportUpdateError();
                        }

                        @Override
                        public void onDataRetrieved(ArrayList results) {

                        }

                        @Override
                        public void onDataNotFound() {

                        }

                        @Override
                        public void onDataQueryError(Exception e) {

                        }
                    });
                }

                @Override
                public void onPhotoUploadProgress(UploadTask.TaskSnapshot snapshot) {

                }

                @Override
                public void onPhotoUploadFailed(Exception exception) {
                    reportFragment.showReportUpdateError();
                }
            };

            MediaDao mediaDao = new MediaDao();
            mediaDao.uploadPhoto(reportFragment.getPhotoPicked(), Media.REPORT_PHOTO_PATH, report.getFirebaseID() + Media.PROFILE_PHOTO_EXTENSION, listener);
        } else {
            report.updateReport(new DatabaseCallbackResult() {
                @Override
                public void onDataRetrieved(Object result) {
                    boolean resultValue = (boolean) result;
                    if (resultValue)
                        reportFragment.showReportUpdateSuccessful(report);
                    else
                        reportFragment.showReportUpdateError();
                }

                @Override
                public void onDataRetrieved(ArrayList results) {

                }

                @Override
                public void onDataNotFound() {

                }

                @Override
                public void onDataQueryError(Exception e) {

                }
            });
        }
    }
}
