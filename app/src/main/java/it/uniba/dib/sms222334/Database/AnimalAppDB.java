package it.uniba.dib.sms222334.Database;

import android.provider.BaseColumns;

public class AnimalAppDB {
    public static final String DATABASE_NAME = "AnimalApp";

    public static class Private implements BaseColumns {
        public static final String TABLE_NAME = "Private";

        public static final String COLUMN_NAME_NAME = "name";
        public static final String COLUMN_NAME_SURNAME = "surname";
        public static final String COLUMN_NAME_EMAIL = "email";
        public static final String COLUMN_NAME_PASSWORD = "password";
        public static final String COLUMN_NAME_PHONE_NUMBER = "phone_number";
        public static final String COLUMN_NAME_BIRTH_DATE = "birthdate";
        public static final String COLUMN_NAME_ROLE = "role";
        public static final String COLUMN_NAME_TAX_ID = "tax_id_code";
        public static final String COLUMN_NAME_PHOTO = "photo";
        public static final String COLUMN_NAME_ANIMALS = "animalList";
    }

    public static class PublicAuthority implements BaseColumns {
        public static final String TABLE_NAME = "PublicAuthority";

        public static final String COLUMN_NAME_EMAIL = "email";
        public static final String COLUMN_NAME_PASSWORD = "password";
        public static final String COLUMN_NAME_ROLE = "role";
        public static final String COLUMN_NAME_COMPANY_NAME = "company_name";
        public static final String COLUMN_NAME_SITE = "site";
        public static final String COLUMN_NAME_LOGO = "logo";
        public static final String COLUMN_NAME_BEDS_NUMBER = "nbeds";
        public static final String COLUMN_NAME_PHONE_NUMBER = "phone_number";
        public static final String COLUMN_NAME_ANIMALS = "animalList";
    }

    public static class Veterinarian implements BaseColumns {
        public static final String TABLE_NAME = "Veterinarian";

        public static final String COLUMN_NAME_EMAIL = "email";
        public static final String COLUMN_NAME_PASSWORD = "password";
        public static final String COLUMN_NAME_ROLE = "role";
        public static final String COLUMN_NAME_COMPANY_NAME = "company_name";
        public static final String COLUMN_NAME_SITE = "site";
        public static final String COLUMN_NAME_LOGO = "logo";
        public static final String COLUMN_NAME_PHONE_NUMBER = "phone_number";
    }

    public static class Animal implements BaseColumns {
        public static final String TABLE_NAME = "Animal";

        public static final String COLUMN_NAME_NAME = "name";
        public static final String COLUMN_NAME_OWNER = "ownerID";
        public static final String COLUMN_NAME_AGE = "age";
        public static final String COLUMN_NAME_STATE = "state";
        public static final String COLUMN_NAME_SPECIES = "species";
        public static final String COLUMN_NAME_RACE = "race";
        public static final String COLUMN_NAME_PHOTO = "photo";
        public static final String COLUMN_NAME_MICROCHIP = "microchip";
    }

    public static class Visit implements BaseColumns {
        public static final String TABLE_NAME = "Visit";

        public static final String COLUMN_NAME_NAME = "name";
        public static final String COLUMN_NAME_STATE = "state";
        public static final String COLUMN_NAME_DIAGNOSIS = "diagnosis";
        public static final String COLUMN_NAME_MEDICAL_NOTE = "medical_note";
        public static final String COLUMN_NAME_ANIMAL = "animalID";
        public static final String COLUMN_NAME_VETERINARIAN = "veterinarianID";
    }

    public static class Expense implements BaseColumns {
        public static final String TABLE_NAME = "Expense";

        public static final String COLUMN_NAME_NAME = "name";
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
}
