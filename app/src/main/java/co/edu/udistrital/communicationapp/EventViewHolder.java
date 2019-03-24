package co.edu.udistrital.communicationapp;

import android.view.View;

import java.util.Calendar;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import co.edu.udistrital.communicationapp.enums.EventType;

public class EventViewHolder extends RecyclerView.ViewHolder {

    public Calendar date;

    public EventType eventType;

    public String reiterativeInfo;

    public boolean active;

    public EventViewHolder(@NonNull View itemView) {
        super(itemView);
        //Aqui especificar el recycler a utilizar de la vista
    }


}
