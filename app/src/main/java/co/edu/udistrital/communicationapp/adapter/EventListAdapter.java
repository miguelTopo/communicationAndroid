package co.edu.udistrital.communicationapp.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import java.util.LinkedList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import co.edu.udistrital.communicationapp.EventActivity;
import co.edu.udistrital.communicationapp.R;
import co.edu.udistrital.communicationapp.application.CommunicationApplication;
import co.edu.udistrital.communicationapp.enums.DateStringFormat;
import co.edu.udistrital.communicationapp.model.Event;
import co.edu.udistrital.communicationapp.rest.EventService;
import co.edu.udistrital.communicationapp.util.DateUtil;
import co.edu.udistrital.communicationapp.util.ToastMessage;


public class EventListAdapter extends RecyclerView.Adapter<EventListAdapter.EventViewHolder> {

    private final LinkedList<Event> eventList;
    private LayoutInflater inflater;
    private EventService eventService;

    public EventListAdapter(Context context, LinkedList<Event> eventList) {
        inflater = LayoutInflater.from(context);
        this.eventList = eventList;
    }

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = inflater.inflate(R.layout.event_recyclerview, parent, false);
        return new EventViewHolder(itemView, this);
    }

    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
        Event e = this.eventList.get(position);
        holder.eventHour.setText(DateUtil.getStringHour24H(e.getDate()));
        holder.eventReiterativeInfo.setText(getReiterativeInfo(e));
        holder.eventDescription.setText(e.getDescription());
        holder.eventActive.setChecked(e.isActive());
    }

    private String getReiterativeInfo(Event event) {
        if (event == null || event.getEventType() == null)
            return "";
        switch (event.getEventType()) {
            case SPECIFIC_DATE:
                return DateUtil.getStringDate(event.getDate(), DateStringFormat.DD_MM_YYYY);
            case REITERATIVE:
                return getReiterativeTypeInfo(event);
            default:
                break;
        }
        return "";
    }

    private String getReiterativeTypeInfo(Event event) {
        if (event == null || event.getEventType() == null)
            return "";
        Context context = CommunicationApplication.getAppContext();
        switch (event.getEventReiterativeType()) {
            case HOURLY:
                return context.getString(R.string.event_frequency_hour);
            case WEEKLY:
                if (event.getRememberDays() == null || event.getRememberDays().isEmpty())
                    return "";
                return context.getString(R.string.event_frequency_week) + " " + DateUtil.getDayOfWeekShortName(event.getRememberDays().get(0));
            case EVERY_DAY:
                return context.getString(R.string.event_frequency_day);
            case ANNUALY:
                return context.getString(R.string.event_frequency_year) + " " + DateUtil.getStringDate(event.getDate(), DateStringFormat.DD_MM_YYYY);
            case MONTHLY:
                return context.getString(R.string.event_frequency_month) + " " + DateUtil.getStringDate(event.getDate(), DateStringFormat.DD_MM_YYYY);
            case SPECIFIC_DAYS:
                if (event.getRememberDays() == null || event.getRememberDays().isEmpty())
                    return "";
                String dateConcat = "";
                for (Integer day : event.getRememberDays()) {
                    dateConcat += " " + DateUtil.getDayOfWeekShortName(day);
                }
                return dateConcat;
            default:
                break;
        }
        return "";
    }

    @Override
    public int getItemCount() {
        return this.eventList.size();
    }

    private EventService getEventService() {
        if (this.eventService == null)
            this.eventService = new EventService();
        return this.eventService;
    }

    public class EventViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public final TextView eventHour;
        public final TextView eventReiterativeInfo;
        public final TextView eventDescription;
        public final Switch eventActive;
        public final EventListAdapter adapter;

        public EventViewHolder(View itemView, EventListAdapter adapter) {
            super(itemView);
            eventHour = itemView.findViewById(R.id.event_hour);
            eventReiterativeInfo = itemView.findViewById(R.id.event_reiterative_info);
            eventActive = itemView.findViewById(R.id.event_active);
            eventDescription = itemView.findViewById(R.id.event_description);

            eventActive.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                    Event e = eventList.get(getAdapterPosition());
                    if (e.isActive() != isChecked)
                        changeEventState(e.getId(), isChecked);
                }
            });
            this.adapter = adapter;
            itemView.setOnClickListener(this);
        }

        private void changeEventState(String eventId, boolean isChecked) {
            boolean success = getEventService().changeEventActive(eventId, isChecked);
            if (success)
                ToastMessage.addInfo(isChecked ? R.string.event_info_state_active_update : R.string.event_info_state_inactive_update);
            else
                ToastMessage.addError(R.string.core_error_response);
        }

        @Override
        public void onClick(View view) {
            System.out.println("Haciendo click a un evento");
            // Get the position of the item that was clicked.
            int position = getLayoutPosition();
            Event event = eventList.get(position);
            Intent eventIntent = new Intent(CommunicationApplication.getInstance(), EventActivity.class);
            eventIntent.putExtra("eventObject", event);
            CommunicationApplication.getInstance().startActivity(eventIntent);
        }
    }

}
