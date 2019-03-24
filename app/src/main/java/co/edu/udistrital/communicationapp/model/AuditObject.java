package co.edu.udistrital.communicationapp.model;

import java.io.Serializable;
import java.util.Date;

import co.edu.udistrital.communicationapp.enums.State;
import co.edu.udistrital.communicationapp.util.DateUtil;

public class AuditObject implements Serializable{

    public Date dateCreation;

    public Date dateUpdate;

    public State state;

    protected void initObject(boolean isNew) {
        if (isNew) {
            this.dateCreation = DateUtil.getCurrentDate();
            this.state = State.ACTIVE;
        } else
            this.dateUpdate = DateUtil.getCurrentDate();
    }

}
