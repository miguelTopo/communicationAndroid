package co.edu.udistrital.communicationapp.model;

import java.io.File;
import java.util.List;

import co.edu.udistrital.communicationapp.enums.Role;

public class User extends AuditObject {

    public User() {
    }

    public User(String id) {
        this.id = id;
    }

    public String id;

    public String name;

    public String email;

    public String photo;

    public File file;

    public String mobilePhone;

    public String countryCode;

    public String langPreferences;

    public UserLangPreferences userLangPreferences;

    public List<UserContact> userContactList;

    public List<String> userContactIdList;

    public List<Role> roleList;
}
