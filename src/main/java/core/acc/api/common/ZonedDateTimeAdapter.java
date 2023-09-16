package core.acc.api.common;

import com.google.gson.*;

import java.lang.reflect.Type;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class ZonedDateTimeAdapter implements JsonSerializer<ZonedDateTime>, JsonDeserializer<ZonedDateTime> {

    private static final DateTimeFormatter formatter = DateTimeFormatter.ISO_ZONED_DATE_TIME;

    @Override
    public JsonElement serialize(ZonedDateTime zonedDateTime, Type type, JsonSerializationContext jsonSerializationContext) {
        return jsonSerializationContext.serialize(formatter.format(zonedDateTime));
    }

    @Override
    public ZonedDateTime deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        String dateString = jsonElement.getAsString();
        return ZonedDateTime.parse(dateString, formatter);
    }
}
