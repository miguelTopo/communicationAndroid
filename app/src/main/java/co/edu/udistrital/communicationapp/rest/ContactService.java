package co.edu.udistrital.communicationapp.rest;

import java.util.ArrayList;
import java.util.List;

import co.edu.udistrital.communicationapp.model.UserContact;

public class ContactService {

    public List<UserContact> findByMobileNumber() {
        List<UserContact>userList = new ArrayList<>(1);
        userList.add(new UserContact());
        userList.add(new UserContact());
        return userList;
    }
}
