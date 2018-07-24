package co.edu.udistrital.communicationapp.rest;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import co.edu.udistrital.communicationapp.values.FieldName;

public class UserService  {

    private static final String USER_IN_DB = "/user/userInDB";

    public boolean userExistInDB(String mobilePhone) {
        if (mobilePhone == null || mobilePhone.trim().isEmpty())
            return true;
        Map<String,String> map = new HashMap<String,String>();
        map.put("mobilePhone", mobilePhone);
        String json = ApplicationService.executePost(USER_IN_DB, map);
        try {
            JSONObject jsonObject = new JSONObject(json);
            JSONObject user = jsonObject.getJSONObject(ObjectAttr.ID);
            try {
                return user.getBoolean(FieldName.EXIST_IN_DB);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return true;
        }
        return true;
    }
}
