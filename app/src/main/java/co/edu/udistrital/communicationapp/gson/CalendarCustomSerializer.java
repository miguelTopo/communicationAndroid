package co.edu.udistrital.communicationapp.gson;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;
import java.util.Calendar;

public class CalendarCustomSerializer implements JsonSerializer<Calendar>, JsonDeserializer<Calendar> {

    @Override
    public JsonElement serialize(Calendar calendar, Type type, JsonSerializationContext context) {
        return new JsonPrimitive(calendar.getTimeInMillis());
    }

    @Override
    public Calendar deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        if (json == null)
            return null;
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(json.getAsLong());
        return cal;
    }
}
