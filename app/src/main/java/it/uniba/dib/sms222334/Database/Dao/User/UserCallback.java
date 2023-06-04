package it.uniba.dib.sms222334.Database.Dao.User;

import it.uniba.dib.sms222334.Models.User;

public final class UserCallback {

    public interface UserStateListener{
        void notifyItemLoaded();
        void notifyItemUpdated();
        void notifyItemRemoved();
    }
}
