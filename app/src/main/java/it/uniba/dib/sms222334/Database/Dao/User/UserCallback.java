package it.uniba.dib.sms222334.Database.Dao.User;

import it.uniba.dib.sms222334.Models.User;

public final class UserCallback {

    public interface UserRegisterCallback {

        void onRegisterSuccess();

        void onRegisterFail();
    }

    public interface UserStateListener {
        void notifyItemLoaded();

        void notifyItemUpdated(int position);

        void notifyItemRemoved(int position);
    }

    public interface UserUpdateCallback {
        void notifyUpdateSuccesfull();

        void notifyUpdateFailed();
    }
}
