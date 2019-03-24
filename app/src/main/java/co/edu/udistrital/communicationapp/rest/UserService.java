package co.edu.udistrital.communicationapp.rest;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import co.edu.udistrital.communicationapp.ContactActivity;
import co.edu.udistrital.communicationapp.R;
import co.edu.udistrital.communicationapp.application.AppPreferences;
import co.edu.udistrital.communicationapp.application.CommunicationApplication;
import co.edu.udistrital.communicationapp.model.User;
import co.edu.udistrital.communicationapp.navigation.NavigateTo;
import co.edu.udistrital.communicationapp.util.JSONUtil;
import co.edu.udistrital.communicationapp.util.ToastMessage;
import co.edu.udistrital.communicationapp.values.FieldName;
import co.edu.udistrital.communicationapp.values.PreferenceKey;

public class UserService {


    private static final String USER_IN_DB = "/user/userInDB";
    private static final String USER_SINGLE_REGISTER = "/user/userSingleRegister";
    private static final String MULTIPART_USER_SINGLE_REGISTER = "/user/multipartUserSingleRegister";
    private static final String USER_SAVE_LAN_PREFERENCE = "/user/saveLanPreference";
    private static final String UPDATE_LAN_PREFERENCE = "/user/updateLanPreferece";

    public void redirectContactOrRegister(final String mobilePhone) {
        AsyncTask<String, Void, Boolean> task = new AsyncTask<String, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(String... strings) {
                if (mobilePhone == null || mobilePhone.trim().isEmpty())
                    return false;
                Map<String, String> map = new HashMap<String, String>();
                map.put("mobilePhone", mobilePhone);
                String json = ApplicationService.executePost(USER_IN_DB, map);
                try {
                    JSONObject jsonObject = new JSONObject(json);
                    return jsonObject.getBoolean(FieldName.BOOLEAN_RESPONSE);
                } catch (Exception e) {
                    e.printStackTrace();
                    return true;
                }
            }

            @Override
            protected void onPostExecute(Boolean result) {
                super.onPostExecute(result);
                try {
                    if (result)
                        NavigateTo.contactActivity();
                    else
                        NavigateTo.registerActivity();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        task.execute(mobilePhone);
    }

    /**
     * Registra un nuevo usuario en la base de datos y redirecciona a la pantalla de contactos
     */
    public void userRegisterAndContactRedirect(final User user, final File file) {
        if (user == null)
            Toast.makeText(CommunicationApplication.getAppContext(), R.string.register_operation_fail, Toast.LENGTH_SHORT).show();
        AsyncTask<User, Void, User> task = new AsyncTask<User, Void, User>() {
            @Override
            protected User doInBackground(User... users) {
                String json = "";
                if (file != null)
                    json = ApplicationService.executeMultipartTest(MULTIPART_USER_SINGLE_REGISTER, file, JSONUtil.parseToJson(user));
                else
                    json = ApplicationService.executePost(USER_SINGLE_REGISTER, JSONUtil.parseToJson(user));
                try {
                    JSONObject jsonObject = new JSONObject(json);
                    boolean succesResponse = jsonObject.getBoolean(FieldName.BOOLEAN_RESPONSE);
                    if (!succesResponse)
                        return null;
                    return (User) JSONUtil.parseToObject(jsonObject.getString("entity"), User.class);
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }

            @Override
            protected void onPostExecute(User userRegister) {
                super.onPostExecute(userRegister);
                try {
                    if (userRegister != null) {
                        Context context = CommunicationApplication.getAppContext();
                        Intent intent = new Intent(context, ContactActivity.class);
                        AppPreferences.addString(PreferenceKey.MOBILE_PHONE_CONFIGURATION, userRegister.mobilePhone);
                        AppPreferences.addString(PreferenceKey.APP_USER_ID, userRegister.id);
                        AppPreferences.addString(PreferenceKey.DEFAULT_ROLE, userRegister.roleList.get(0).toString());
                        intent.putExtra(FieldName.MOBILE_PHONE, userRegister.mobilePhone);
                        intent.putExtra(FieldName.USER_ID, userRegister.id);
                        context.startActivity(intent);
                    } else
                        ToastMessage.addError(R.string.core_error_response);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        task.execute(user);
    }

    public void saveUserLangPreferencesAndContactRedirect(final User user) {
        if (user == null || user.userLangPreferences == null)
            ToastMessage.addError(R.string.core_error_response);
        AsyncTask<User, Void, String> task = new AsyncTask<User, Void, String>() {
            @Override
            protected String doInBackground(User... users) {
                String json = ApplicationService.executePost(UPDATE_LAN_PREFERENCE, JSONUtil.parseToJson(user));
                try {
                    JSONObject jsonObject = new JSONObject(json);
                    boolean succesResponse = jsonObject.getBoolean(FieldName.BOOLEAN_RESPONSE);
                    if (!succesResponse)
                        return "";
                    return JSONUtil.parseToJson(user.userLangPreferences);
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }

            @Override
            protected void onPostExecute(String langPreferences) {
                super.onPostExecute(langPreferences);
                try {
                    if (langPreferences != null && !langPreferences.trim().isEmpty()) {
                        AppPreferences.addString(PreferenceKey.LANG_USER_PREF, langPreferences);
                        NavigateTo.contactActivity();
                        ToastMessage.addSuccess(R.string.core_success_response);
                    } else
                        ToastMessage.addError(R.string.core_error_response);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        task.execute(user);
    }
}
