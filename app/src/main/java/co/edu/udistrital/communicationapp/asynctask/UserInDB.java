package co.edu.udistrital.communicationapp.asynctask;

import android.content.Intent;
import android.os.AsyncTask;

import co.edu.udistrital.communicationapp.ContactActivity;
import co.edu.udistrital.communicationapp.RegisterActivity;
import co.edu.udistrital.communicationapp.application.CommunicationApplication;
import co.edu.udistrital.communicationapp.rest.UserService;

public class UserInDB extends AsyncTask<String, Void, Boolean> {

    private UserService userService;

    public UserInDB() {
    }

    @Override
    protected Boolean doInBackground(String... strings) {
        return getUserService().userExistInDB(strings[0]);
    }

    @Override
    protected void onPostExecute(Boolean userExistInDB) {
        super.onPostExecute(userExistInDB);
        try {
            Intent intent;
            if (userExistInDB)
                intent = new Intent(CommunicationApplication.getAppContext(), ContactActivity.class);
            else
                intent = new Intent(CommunicationApplication.getAppContext(), RegisterActivity.class);
            CommunicationApplication.getAppContext().startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private UserService getUserService() {
        if (userService == null)
            userService = new UserService();
        return userService;

    }
}
