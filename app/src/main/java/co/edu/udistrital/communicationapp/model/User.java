package co.edu.udistrital.communicationapp.model;

import java.util.Date;
import java.util.List;

import co.edu.udistrital.communicationapp.enums.State;

public class User {

    public String id;

    public String name;

    public String email;

    public String mobilePhone;

    public Date dateCreation;

    public State state;

    public List<UserContact> userContacts;
}
