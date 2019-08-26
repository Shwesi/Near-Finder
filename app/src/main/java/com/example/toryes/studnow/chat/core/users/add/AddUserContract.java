package  com.example.toryes.studnow.chat.core.users.add;

import android.content.Context;

import com.example.toryes.studnow.bin.User;
import com.example.toryes.studnow.bin.Users;
import com.google.firebase.auth.FirebaseUser;

/**
 * Author: Kartik Sharma
 * Created on: 8/28/2016 , 11:06 AM
 * Project: FirebaseChat
 */

public interface AddUserContract {
    interface View {
        void onAddUserSuccess(String message);

        void onAddUserFailure(String message);
    }

    interface Presenter {
        void addUser(Context context, FirebaseUser firebaseUser, User users);
    }

    interface Interactor {
        void addUserToDatabase(Context context, FirebaseUser firebaseUser, User users);
    }

    interface OnUserDatabaseListener {
        void onSuccess(String message);

        void onFailure(String message);
    }
}
