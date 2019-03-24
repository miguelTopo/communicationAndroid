package co.edu.udistrital.communicationapp;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.TimePicker;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import org.angmarch.views.NiceSpinner;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.appcompat.widget.AppCompatRadioButton;
import androidx.appcompat.widget.Toolbar;
import co.edu.udistrital.communicationapp.application.AppPreferences;
import co.edu.udistrital.communicationapp.constant.EventReiterativeTypeConst;
import co.edu.udistrital.communicationapp.constant.EventTypeConst;
import co.edu.udistrital.communicationapp.enums.DateStringFormat;
import co.edu.udistrital.communicationapp.enums.EventReiterativeType;
import co.edu.udistrital.communicationapp.enums.EventType;
import co.edu.udistrital.communicationapp.model.Event;
import co.edu.udistrital.communicationapp.navigation.NavigateTo;
import co.edu.udistrital.communicationapp.rest.EventService;
import co.edu.udistrital.communicationapp.util.DateUtil;
import co.edu.udistrital.communicationapp.util.ToastMessage;
import co.edu.udistrital.communicationapp.values.PreferenceKey;

public class EventActivity extends AppCompatActivity {

    private EventService eventService;

    private DatePickerDialog datePicker;
    private TimePickerDialog timePicker;
    private NiceSpinner eventType;
    private LinearLayout eventWrap;
    private LinearLayout eventDatePanel;
    private TextInputEditText eventDateText;
    private MaterialButton eventDateSelect;
    private TextInputEditText eventHourText;
    private MaterialButton eventHourSelect;
    private LinearLayout eventReiterativePanel;
    private NiceSpinner eventReiterativeType;
    private GridLayout eventReiterativeTypeMultiple;
    private RadioGroup eventReiterativeTypeSingle;
    private TextView eventReiterativeTypeInfo;

    private AppCompatCheckBox mondayCheck;
    private AppCompatCheckBox tuesdayCheck;
    private AppCompatCheckBox wednesdayCheck;
    private AppCompatCheckBox thursdayCheck;
    private AppCompatCheckBox fridayCheck;
    private AppCompatCheckBox saturdayCheck;
    private AppCompatCheckBox sundayCheck;
    private TextInputEditText eventDescription;
    private MaterialButton btnEventCancel;
    private MaterialButton btnEventSave;

    private Event event;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //aqui mirar como recibimo el evento mientras tanto new
        Event eventEdit = new Event();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);
        initComponents();
        loadEventToEdit();
    }

    private void loadEventToEdit() {
        Intent intent = getIntent();
        if (intent != null) {
            Event event = (Event) intent.getSerializableExtra("eventObject");
            if (event != null) {
                this.event = event;
                eventType.setSelectedIndex(event.getEventType().equals(EventType.SPECIFIC_DATE) ? EventTypeConst.SPECIFIC_DATE : EventTypeConst.REITERATIVE);
                handleEventTypeChange(eventType.getSelectedIndex());
                eventDateText.setText(DateUtil.getStringDate(event.getDate().get(Calendar.YEAR), event.getDate().get(Calendar.MONTH), event.getDate().get(Calendar.DAY_OF_MONTH), DateStringFormat.DD_MM_YYYY));
                eventDescription.setText(event.getDescription());
                if (event.getEventType().equals(EventType.SPECIFIC_DATE))
                    buildEditSpecificDate(event);
                else
                    buildEditReiterative(event);
            }
        }
    }

    private void buildEditSpecificDate(Event event) {
        eventHourText.setText(DateUtil.getStringHour24H(event.getDate().get(Calendar.HOUR_OF_DAY), event.getDate().get(Calendar.MINUTE)));
    }

    private int getIndexByReiterativeType(EventReiterativeType reiterativeType) {
        switch (reiterativeType) {
            case HOURLY:
                return EventReiterativeTypeConst.HOURLY;
            case EVERY_DAY:
                return EventReiterativeTypeConst.EVERY_DAY;
            case SPECIFIC_DAYS:
                return EventReiterativeTypeConst.SPECIFIC_DAYS;
            case WEEKLY:
                return EventReiterativeTypeConst.WEEKLY;
            case MONTHLY:
                return EventReiterativeTypeConst.MONTHLY;
            case ANNUALY:
                return EventReiterativeTypeConst.ANNUALY;
            default:
                return 0;
        }
    }

    private boolean daySelected(List<Integer> dayList, int day) {
        if (dayList == null || dayList.isEmpty())
            return false;
        return dayList.stream().anyMatch(d -> d.equals(day));
    }


    private void buildRememberDays(Event event) {
        if (event.getRememberDays() == null || event.getRememberDays().isEmpty())
            return;
        mondayCheck.setChecked(daySelected(event.getRememberDays(), Calendar.MONDAY));
        tuesdayCheck.setChecked(daySelected(event.getRememberDays(), Calendar.TUESDAY));
        wednesdayCheck.setChecked(daySelected(event.getRememberDays(), Calendar.WEDNESDAY));
        thursdayCheck.setChecked(daySelected(event.getRememberDays(), Calendar.THURSDAY));
        fridayCheck.setChecked(daySelected(event.getRememberDays(), Calendar.FRIDAY));
        saturdayCheck.setChecked(daySelected(event.getRememberDays(), Calendar.SATURDAY));
        sundayCheck.setChecked(daySelected(event.getRememberDays(), Calendar.SUNDAY));
    }


    private void selectSingleReiterativeOption(Event event) {
        if (event == null || event.getRememberDays() == null || event.getRememberDays().isEmpty())
            return;
        int day = event.getRememberDays().get(0);
        switch (day) {
            case Calendar.MONDAY:
                eventReiterativeTypeSingle.check(R.id.event_radio_monday);
                break;
            case Calendar.TUESDAY:
                eventReiterativeTypeSingle.check(R.id.event_radio_tuesday);
                break;
            case Calendar.WEDNESDAY:
                eventReiterativeTypeSingle.check(R.id.event_radio_wednesday);
                break;
            case Calendar.THURSDAY:
                eventReiterativeTypeSingle.check(R.id.event_radio_thursday);
                break;
            case Calendar.FRIDAY:
                eventReiterativeTypeSingle.check(R.id.event_radio_friday);
                break;
            case Calendar.SATURDAY:
                eventReiterativeTypeSingle.check(R.id.event_radio_saturday);
                break;
            case Calendar.SUNDAY:
                eventReiterativeTypeSingle.check(R.id.event_radio_sunday);
                break;
        }
    }

    private void buildEditReiterative(Event event) {
        eventReiterativeType.setSelectedIndex(getIndexByReiterativeType(event.getEventReiterativeType()));
        handleEventReiterativeChange(eventReiterativeType.getSelectedIndex());
        buildEditSpecificDate(event);
        if (eventReiterativeType.getSelectedIndex() == EventReiterativeTypeConst.SPECIFIC_DAYS)
            buildRememberDays(event);
        else if (eventReiterativeType.getSelectedIndex() == EventReiterativeTypeConst.WEEKLY)
            selectSingleReiterativeOption(event);

    }

    private void initComponents() {
        eventType = findViewById(R.id.event_type);
        eventWrap = findViewById(R.id.event_wrap);
        eventDatePanel = findViewById(R.id.event_date_panel);
        eventDateText = findViewById(R.id.event_date_text);
        eventDateSelect = findViewById(R.id.event_date_select);
        eventHourText = findViewById(R.id.event_hour_text);
        eventHourSelect = findViewById(R.id.event_hour_select);
        eventReiterativePanel = findViewById(R.id.event_reiterative_panel);
        eventReiterativeType = findViewById(R.id.event_reiterative_type);
        eventReiterativeTypeMultiple = findViewById(R.id.event_reiterative_type_multiple);
        eventReiterativeTypeSingle = findViewById(R.id.event_reiterative_type_single);
        eventReiterativeTypeInfo = findViewById(R.id.event_reiterative_type_info);
        mondayCheck = findViewById(R.id.monday_check);
        tuesdayCheck = findViewById(R.id.tuesday_check);
        wednesdayCheck = findViewById(R.id.wednesday_check);
        thursdayCheck = findViewById(R.id.thursday_check);
        fridayCheck = findViewById(R.id.friday_check);
        saturdayCheck = findViewById(R.id.saturday_check);
        sundayCheck = findViewById(R.id.sunday_check);
        eventDescription = findViewById(R.id.event_description);
        btnEventCancel = findViewById(R.id.btn_event_cancel);
        btnEventSave = findViewById(R.id.btn_event_save);

        Toolbar toolbar = findViewById(R.id.event_toolbar);
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        buildViewDataAndListener();
    }

    private void buildViewDataAndListener() {
        ArrayAdapter<CharSequence> eventTypeAdapter = ArrayAdapter.createFromResource(this, R.array.event_type_array, android.R.layout.simple_spinner_item);
        eventTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        eventType.setAdapter(eventTypeAdapter);
        eventType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                handleEventTypeChange(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        ArrayAdapter<CharSequence> eventReiterativeTypeAdapter = ArrayAdapter.createFromResource(this, R.array.event_reiterative_type_array, android.R.layout.simple_spinner_item);
        eventReiterativeTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        eventReiterativeType.setAdapter(eventReiterativeTypeAdapter);
        eventReiterativeType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                handleEventReiterativeChange(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        eventDateText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePicker();
            }
        });
        eventDateSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePicker();
            }
        });
        eventHourText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showTimePicker();
            }
        });
        eventHourSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showTimePicker();
            }
        });

        btnEventCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavigateTo.eventListActivity();
            }
        });
        btnEventSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveEvent();
            }
        });

    }

    private boolean validEventSpecificType() {
        if (eventDateText == null || eventDateText.getText().toString().trim().isEmpty()) {
            ToastMessage.addWarn(R.string.event_warn_event_date);
            return false;
        }
        if (eventHourText == null || eventHourText.getText().toString().trim().isEmpty()) {
            ToastMessage.addWarn(R.string.event_warn_event_hour);
            return false;
        }
        if (eventDescription == null || eventDescription.getText().toString().trim().isEmpty()) {
            ToastMessage.addWarn(R.string.event_warn_event_description);
            return false;
        }
        return true;
    }

    private boolean validEventReiterativeType() {
        if (eventReiterativeType == null || eventReiterativeType.getSelectedIndex() <= 0) {
            ToastMessage.addWarn(R.string.event_warn_event_type);
            return false;
        }
        if (eventReiterativeType.getSelectedIndex() == EventReiterativeTypeConst.SPECIFIC_DAYS)
            return validEventSpecificDays(eventReiterativeType.getSelectedIndex());
        else if (eventReiterativeType.getSelectedIndex() == EventReiterativeTypeConst.WEEKLY)
            return validEventWeekly(eventReiterativeType.getSelectedIndex());
        return validEventDateAndDescription(eventReiterativeType.getSelectedIndex());
    }

    private boolean validEventWeekly(int eventReiterativeType) {
        boolean basicValid = validEventDateAndDescription(eventReiterativeType);
        if (!basicValid) {
            return basicValid;
        }
        int radioButtonId = eventReiterativeTypeSingle.getCheckedRadioButtonId();
        if (radioButtonId < 0) {
            ToastMessage.addWarn(R.string.event_warn_event_day__single_check);
            return false;
        }
        return true;
    }

    private boolean validEventSpecificDays(int eventReiterativeType) {
        boolean basicValid = validEventDateAndDescription(eventReiterativeType);
        if (!basicValid) {
            return basicValid;
        }
        if (!mondayCheck.isChecked() && !tuesdayCheck.isChecked() &&
                !wednesdayCheck.isChecked() && !thursdayCheck.isChecked() &&
                !fridayCheck.isChecked() && !saturdayCheck.isChecked() &&
                !sundayCheck.isChecked()) {
            ToastMessage.addWarn(R.string.event_warn_event_day__multiple_check);
            return false;
        }
        return true;
    }

    private boolean validEventDateAndDescription(int ert) {
        boolean emptyDate = eventDateText.getText() == null || eventDateText.getText().toString() == null || eventDateText.getText().toString().trim().isEmpty();
        boolean emptyHour = eventHourText.getText() == null || eventHourText.getText().toString() == null || eventHourText.getText().toString().trim().isEmpty();
        switch (ert) {
            case EventReiterativeTypeConst.EVERY_DAY:
            case EventReiterativeTypeConst.HOURLY:
            case EventReiterativeTypeConst.SPECIFIC_DAYS:
            case EventReiterativeTypeConst.WEEKLY:
                if (emptyHour) {
                    ToastMessage.addWarn(R.string.event_warn_event_hour);
                    return false;
                }
                break;
            case EventReiterativeTypeConst.ANNUALY:
            case EventReiterativeTypeConst.MONTHLY:
                if (emptyDate) {
                    ToastMessage.addWarn(R.string.event_warn_event_date);
                    return false;
                }
                if (emptyHour) {
                    ToastMessage.addWarn(R.string.event_warn_event_hour);
                    return false;
                }
                break;
            default:
                return false;

        }
        if (eventDescription.getText() == null || eventDescription.getText().toString() == null || eventDescription.getText().toString().trim().isEmpty()) {
            ToastMessage.addWarn(R.string.event_warn_event_description);
            return false;
        }
        return true;
    }

    private boolean validEvent() {
        if (eventType == null || eventType.getSelectedIndex() <= 0) {
            ToastMessage.addWarn(R.string.event_warn_event_type);
            return false;
        }
        return eventType.getSelectedIndex() == EventTypeConst.SPECIFIC_DATE ? validEventSpecificType() : validEventReiterativeType();
    }

    private Calendar getDateEvent(Calendar hour) {
        if (hour == null)
            return DateUtil.getCurrentCalendar();
        Calendar date = eventDateText.getText().toString() == null || eventDateText.getText().toString().trim().isEmpty() ? DateUtil.getCurrentCalendar() : DateUtil.getCalendarFromString(eventDateText.getText().toString(), DateStringFormat.DD_MM_YYYY);
        date.set(Calendar.HOUR_OF_DAY, hour.get(Calendar.HOUR_OF_DAY));
        date.set(Calendar.MINUTE, hour.get(Calendar.MINUTE));
        date.set(Calendar.SECOND, 0);
        return date;
    }

    private List<Integer> getRememberForSpecificDays() {
        List<Integer> dayList = new ArrayList<>(1);
        if (mondayCheck.isChecked())
            dayList.add(Calendar.MONDAY);
        if (tuesdayCheck.isChecked())
            dayList.add(Calendar.TUESDAY);
        if (wednesdayCheck.isChecked())
            dayList.add(Calendar.WEDNESDAY);
        if (thursdayCheck.isChecked())
            dayList.add(Calendar.THURSDAY);
        if (fridayCheck.isChecked())
            dayList.add(Calendar.FRIDAY);
        if (saturdayCheck.isChecked())
            dayList.add(Calendar.SATURDAY);
        if (sundayCheck.isChecked())
            dayList.add(Calendar.SUNDAY);
        return dayList;
    }

    private int getDayForRadioText(String radioText) {
        if (radioText == null || radioText.trim().isEmpty())
            return -1;
        if (radioText.equalsIgnoreCase(getString(R.string.core_short_monday)))
            return Calendar.MONDAY;
        else if (radioText.equalsIgnoreCase(getString(R.string.core_short_tuesday)))
            return Calendar.TUESDAY;
        else if (radioText.equalsIgnoreCase(getString(R.string.core_short_wednesday)))
            return Calendar.WEDNESDAY;
        else if (radioText.equalsIgnoreCase(getString(R.string.core_short_thursday)))
            return Calendar.THURSDAY;
        else if (radioText.equalsIgnoreCase(getString(R.string.core_short_friday)))
            return Calendar.FRIDAY;
        else if (radioText.equalsIgnoreCase(getString(R.string.core_short_saturday)))
            return Calendar.SATURDAY;
        else if (radioText.equalsIgnoreCase(getString(R.string.core_short_sunday)))
            return Calendar.SUNDAY;
        return -1;
    }

    private List<Integer> getRememberForWeeklyDays() {
        List<Integer> dayList = new ArrayList<>(1);
        AppCompatRadioButton radio = findViewById(eventReiterativeTypeSingle.getCheckedRadioButtonId());
        dayList.add(getDayForRadioText(radio.getText().toString()));
        return dayList;
    }

    private Event getEventSave() {
        Event event = this.event == null ? new Event() : this.event;
        event.setEventType(eventType.getSelectedIndex() == EventTypeConst.SPECIFIC_DATE ? EventType.SPECIFIC_DATE : EventType.REITERATIVE);
        event.setDescription(eventDescription.getText().toString());
        Calendar hour = DateUtil.getCalendarFromString(eventHourText.getText().toString(), DateStringFormat.HH24_MM);
        event.setDate(getDateEvent(hour));

        if (event.getEventType().equals(EventType.REITERATIVE)) {
            switch (eventReiterativeType.getSelectedIndex()) {
                case EventReiterativeTypeConst.EVERY_DAY:
                    event.setEventReiterativeType(EventReiterativeType.EVERY_DAY);
                    event.setDate(getDateEvent(hour));
                    break;
                case EventReiterativeTypeConst.HOURLY:
                    event.setEventReiterativeType(EventReiterativeType.HOURLY);
                    event.setDate(getDateEvent(hour));
                    break;
                case EventReiterativeTypeConst.MONTHLY:
                case EventReiterativeTypeConst.ANNUALY:
                    event.setEventReiterativeType(eventReiterativeType.getSelectedIndex() == EventReiterativeTypeConst.MONTHLY ? EventReiterativeType.MONTHLY : EventReiterativeType.ANNUALY);
                    event.setDate(getDateEvent(hour));
                    break;
                case EventReiterativeTypeConst.SPECIFIC_DAYS:
                    event.setEventReiterativeType(EventReiterativeType.SPECIFIC_DAYS);
                    event.setRememberDays(getRememberForSpecificDays());
                    break;
                case EventReiterativeTypeConst.WEEKLY:
                    event.setEventReiterativeType(EventReiterativeType.WEEKLY);
                    event.setRememberDays(getRememberForWeeklyDays());
                    break;
            }
        }
        event.setUserId(AppPreferences.getString(PreferenceKey.APP_USER_ID));
        return event;
    }

    private void saveEvent() {
        if (!validEvent())
            return;
        getEventService().saveEvent(getEventSave());
    }

    private void showTimePicker() {
        String textHour = eventHourText.getText().toString();
        int hour = 0;
        int minutes = 0;
        if (textHour == null || textHour.trim().isEmpty()) {
            Calendar calendar = DateUtil.getCurrentCalendar();
            hour = calendar.get(Calendar.HOUR_OF_DAY);
            minutes = calendar.get(Calendar.MINUTE);
        } else {
            hour = DateUtil.getHourFromString(textHour, DateStringFormat.HH24_MM);
            minutes = DateUtil.getMinuteFromString(textHour, DateStringFormat.HH24_MM);
        }
        timePicker = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int hourOfDay, int minute) {
                eventHourText.setText(DateUtil.getStringHour24H(hourOfDay, minute));
            }
        }, hour, minutes, true);
        timePicker.show();

    }

    private void showDatePicker() {
        String textDate = eventDateText.getText().toString();
        int year = 0;
        int month = 0;
        int day = 0;
        if (textDate == null || textDate.trim().isEmpty()) {
            Calendar calendar = DateUtil.getCurrentCalendar();
            year = calendar.get(Calendar.YEAR);
            month = calendar.get(Calendar.MONTH);
            day = calendar.get(Calendar.DAY_OF_MONTH);
        } else {
            day = DateUtil.getDayOfMonthFromString(textDate, DateStringFormat.DD_MM_YYYY);
            month = DateUtil.getMonthFromString(textDate, DateStringFormat.DD_MM_YYYY);
            year = DateUtil.getYearFromString(textDate, DateStringFormat.DD_MM_YYYY);
        }
        datePicker = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {
                eventDateText.setText(DateUtil.getStringDate(year, monthOfYear, dayOfMonth, DateStringFormat.DD_MM_YYYY));
            }
        }, year, month, day);
        datePicker.show();
    }

    private void clearAllViews() {
        eventWrap.setVisibility(View.GONE);
        eventDatePanel.setVisibility(View.GONE);
        eventReiterativePanel.setVisibility(View.GONE);
        clearReiterativeViews();
    }

    private void clearReiterativeViews() {
        eventReiterativeTypeMultiple.setVisibility(View.GONE);
        eventReiterativeTypeSingle.setVisibility(View.GONE);
        eventReiterativeTypeInfo.setVisibility(View.GONE);
        mondayCheck.setChecked(false);
        tuesdayCheck.setChecked(false);
        wednesdayCheck.setChecked(false);
        thursdayCheck.setChecked(false);
        fridayCheck.setChecked(false);
        saturdayCheck.setChecked(false);
        sundayCheck.setChecked(false);
        eventReiterativeTypeSingle.clearCheck();
    }

    private void handleEventReiterativeDateAndTimePanels(int index) {
        boolean dateVisible = index >= 5;
        if (dateVisible)
            eventDatePanel.setVisibility(View.VISIBLE);
        else
            eventDatePanel.setVisibility(View.GONE);
    }

    private void handleEventReiterativeChange(int index) {
        clearReiterativeViews();
        handleEventReiterativeDateAndTimePanels(index);
        if (index <= 0 || (index != 3 && index != 4))
            return;
        if (index == 3) {
            eventReiterativeTypeSingle.setVisibility(View.GONE);
            eventReiterativeTypeMultiple.setVisibility(View.VISIBLE);
            eventReiterativeTypeInfo.setText("¿Qué días quieres que te recordemos?");
        } else {
            eventReiterativeTypeSingle.setVisibility(View.VISIBLE);
            eventReiterativeTypeMultiple.setVisibility(View.GONE);
            eventReiterativeTypeInfo.setText("¿Qué día de la semana te recordamos?");
        }
        eventReiterativeTypeInfo.setVisibility(View.VISIBLE);
    }

    private void handleEventTypeChange(int index) {
        clearAllViews();
        if (index <= 0)
            return;
        eventWrap.setVisibility(View.VISIBLE);
        if (index == 1) {
            eventDatePanel.setVisibility(View.VISIBLE);
            eventReiterativePanel.setVisibility(View.GONE);
        } else {
            eventDatePanel.setVisibility(View.GONE);
            eventReiterativePanel.setVisibility(View.VISIBLE);
        }
    }

    private EventService getEventService() {
        if (this.eventService == null)
            this.eventService = new EventService();
        return this.eventService;
    }

}
