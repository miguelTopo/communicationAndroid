package co.edu.udistrital.communicationapp.model;

import com.google.gson.annotations.JsonAdapter;

import java.io.Serializable;
import java.util.Calendar;
import java.util.List;

import co.edu.udistrital.communicationapp.enums.EventReiterativeType;
import co.edu.udistrital.communicationapp.enums.EventType;
import co.edu.udistrital.communicationapp.gson.CalendarCustomSerializer;

public class Event extends AuditObject implements Serializable {

    private String id;

    private String userId;

    @JsonAdapter(CalendarCustomSerializer.class )
    private Calendar date;

    private EventType eventType;

    private String description;

    private EventReiterativeType eventReiterativeType;

    private List<Integer> rememberDays;

    private boolean active;

    //Get and set
    public Calendar getDate() {
        return date;
    }

    public void setDate(Calendar date) {
        this.date = date;
    }

    public EventType getEventType() {
        return eventType;
    }

    public void setEventType(EventType eventType) {
        this.eventType = eventType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public EventReiterativeType getEventReiterativeType() {
        return eventReiterativeType;
    }

    public void setEventReiterativeType(EventReiterativeType eventReiterativeType) {
        this.eventReiterativeType = eventReiterativeType;
    }

    public List<Integer> getRememberDays() {
        return rememberDays;
    }

    public void setRememberDays(List<Integer> rememberDays) {
        this.rememberDays = rememberDays;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserId(String userId) {
        return this.userId;
    }

    public String getId() {
        return id;
    }

    public String getUserId() {
        return userId;
    }

    public boolean isActive() {
        return active;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
