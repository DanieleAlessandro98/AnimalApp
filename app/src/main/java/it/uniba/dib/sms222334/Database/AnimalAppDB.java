package it.uniba.dib.sms222334.Database;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;
import it.uniba.dib.sms222334.Models.Animal;

public class AnimalAppDB {
    public static final String DATABASE_NAME = "AnimalApp";

    public static final String AUTHORITY="it.uniba.dib.sms222334";

    public static class Private implements BaseColumns {
        public static final String TABLE_NAME = "Private";

        public static final String COLUMN_NAME_NAME = "name";
        public static final String COLUMN_NAME_SURNAME = "surname";
        public static final String COLUMN_NAME_EMAIL = "email";
        public static final String COLUMN_NAME_PASSWORD = "password";
        public static final String COLUMN_NAME_PHONE_NUMBER = "phone_number";
        public static final String COLUMN_NAME_LOCATION = "location";
        public static final String COLUMN_NAME_BIRTH_DATE = "birthdate";
        public static final String COLUMN_NAME_TAX_ID = "tax_id_code";
        public static final String COLUMN_NAME_PHOTO = "photo";
        public static final String COLUMN_NAME_ANIMALS = "animalList";
    }

    public static class PublicAuthority implements BaseColumns {
        public static final String PATH="public_authority";

        public static final Uri CONTENT_URI=Uri.parse(ContentResolver.SCHEME_CONTENT+"://"
                +AUTHORITY+"/"+PATH);

        public static final String MIME_TYPE_DIR=ContentResolver.CURSOR_DIR_BASE_TYPE+"/vnd."+PATH;

        public static final String MIME_TYPE_ITEM=ContentResolver.CURSOR_ITEM_BASE_TYPE+"/vnd."+PATH;
        public static final String TABLE_NAME = "PublicAuthority";
        public static final String COLUMN_NAME_EMAIL = "email";
        public static final String COLUMN_NAME_PASSWORD = "password";
        public static final String COLUMN_NAME_COMPANY_NAME = "company_name";
        public static final String COLUMN_NAME_SITE = "site";
        public static final String COLUMN_NAME_LOGO = "logo";
        public static final String COLUMN_NAME_BEDS_NUMBER = "nbeds";
        public static final String COLUMN_NAME_PHONE_NUMBER = "phone_number";
        public static final String COLUMN_NAME_ANIMALS = "animalList";
    }

    public static class Veterinarian implements BaseColumns {
        public static final String PATH="veterinarian";

        public static final Uri CONTENT_URI=Uri.parse(ContentResolver.SCHEME_CONTENT+"://"
        +AUTHORITY+"/"+PATH);

        public static final String MIME_TYPE_DIR=ContentResolver.CURSOR_DIR_BASE_TYPE+"/vnd."+PATH;

        public static final String MIME_TYPE_ITEM=ContentResolver.CURSOR_ITEM_BASE_TYPE+"/vnd."+PATH;

        public static final String TABLE_NAME = "Veterinarian";

        public static final String COLUMN_NAME_EMAIL = "email";
        public static final String COLUMN_NAME_PASSWORD = "password";
        public static final String COLUMN_NAME_COMPANY_NAME = "company_name";
        public static final String COLUMN_NAME_SITE = "site";
        public static final String COLUMN_NAME_LOGO = "logo";
        public static final String COLUMN_NAME_PHONE_NUMBER = "phone_number";
    }

    public static class Animal implements BaseColumns {
        public static final String TABLE_NAME = "Animal";
        public static final String COLUMN_NAME_NAME = "name";
        public static final String COLUMN_NAME_OWNER = "ownerID";
        public static final String COLUMN_NAME_BIRTH_DATE = "birthdate";
        public static final String COLUMN_NAME_STATE = "state";
        public static final String COLUMN_NAME_SPECIES = "species";
        public static final String COLUMN_NAME_RACE = "race";
        public static final String COLUMN_NAME_PHOTO = "photo";
        public static final String COLUMN_NAME_MICROCHIP = "microchip";
        public static class Images implements BaseColumns{
            public static final String COLUMN_NAME = "images";

            public static final String COLUMN_PATH = "path";

            public static final String COLUMN_TIMESTAMP = "timestamp";
        }

        public static class Videos implements BaseColumns{
            public static final String COLUMN_NAME = "videos";

            public static final String COLUMN_PATH = "path";

            public static final String COLUMN_TIMESTAMP = "timestamp";
        }

        public static String getAnimalUri(it.uniba.dib.sms222334.Models.Animal animal){
            Uri.Builder builder = new Uri.Builder();
            builder.scheme("http");
            builder.authority("animal");
            builder.appendPath("view");
            builder.appendQueryParameter("id", animal.getFirebaseID());

            return builder.build().toString();
        }
    }

    public static class Visit implements BaseColumns {
        public static final String TABLE_NAME = "Visit";

        public static final String COLUMN_NAME_NAME = "name";
        public static final String COLUMN_NAME_STATE = "state";
        public static final String COLUMN_NAME_DIAGNOSIS = "diagnosis";
        public static final String COLUMN_NAME_MEDICAL_NOTE = "medical_note";

        public static final String COLUMN_NAME_TYPE = "Visit Type";

        public static final String COLUMN_NAME_DOCTOR_NAME = "doctor name";

        public static final String COLUMN_NAME_DOCTOR_ID = "idDoctor";

        public static final String COLUMN_NAME_OWNER_ID = "idOwner";

        public static final String COLUMN_NAME_DATE = "Date";
        public static final String COLUMN_NAME_ANIMAL = "animalID";
    }

    public static class Expense implements BaseColumns {
        public static final String TABLE_NAME = "Expense";

        public static final String COLUMN_NAME_NOTE = "note";
        public static final String COLUMN_NAME_PRICE = "price";
        public static final String COLUMN_NAME_CATEGORY = "category";
        public static final String COLUMN_NAME_ANIMAL = "animalID";
    }

    public static class Food implements BaseColumns {
        public static final String TABLE_NAME = "Food";

        public static final String COLUMN_NAME_NAME = "name";
        public static final String COLUMN_NAME_ANIMAL = "animalID";
    }

    public static class Pathology implements BaseColumns {
        public static final String TABLE_NAME = "Pathology";

        public static final String COLUMN_NAME_NAME = "name";
        public static final String COLUMN_NAME_ANIMAL = "animalID";
    }

    public static class Relation implements BaseColumns{
        public static final String TABLE_NAME = "Relation";
        public static final String COLUMN_NAME_IDANIMAL1 = "idAnimal1";
        public static final String COLUMN_NAME_IDANIMAL2 = "idAnimal2";
        public static final String COLUMN_NAME_REFERENCE = "Relation";
    }

    public static class Photo implements BaseColumns {
        public static final String TABLE_NAME = "Photo";

        public static final String COLUMN_NAME_PHOTO = "photo";
        public static final String COLUMN_NAME_ANIMAL = "animalID";
    }

    public static class Video implements BaseColumns {
        public static final String TABLE_NAME = "Video";

        public static final String COLUMN_NAME_VIDEO = "video";
        public static final String COLUMN_NAME_ANIMAL = "animalID";
    }

    public static class Report implements BaseColumns {
        public static final String TABLE_NAME = "Report";

        public static final String COLUMN_NAME_USER_ID = "user_id";
        public static final String COLUMN_NAME_TYPE = "type";
        public static final String COLUMN_NAME_ANIMAL_SPECIES = "animal_species";
        public static final String COLUMN_NAME_DESCRIPTION = "description";
        public static final String COLUMN_NAME_LOCATION = "location";
        public static final String COLUMN_NAME_PHOTO = "photo";
        public static final String COLUMN_NAME_ANIMAL_NAME = "animal_name";
        public static final String COLUMN_NAME_ANIMAL_AGE = "animal_age";
        public static final String COLUMN_NAME_ANIMAL_ID = "animal_id";
        public static final String COLUMN_NAME_SHOW_ANIMAL_PROFILE = "show_animal_profile";
    }

    public static class Request implements BaseColumns {
        public static final String TABLE_NAME = "Request";

        public static final String COLUMN_NAME_USER_ID = "user_id";
        public static final String COLUMN_NAME_TYPE = "type";
        public static final String COLUMN_NAME_DESCRIPTION = "description";
        public static final String COLUMN_NAME_LOCATION = "location";
        public static final String COLUMN_NAME_ANIMAL_SPECIES = "animal_species";
        public static final String COLUMN_NAME_ANIMAL_ID = "animal_id";
        public static final String COLUMN_NAME_BEDS_NUMBER = "beds_number";
    }
}
