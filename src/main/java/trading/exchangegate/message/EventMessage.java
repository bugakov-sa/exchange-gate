package trading.exchangegate.message;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class EventMessage {

    private static final ObjectMapper jsonParser = new ObjectMapper();

    private Event event;
    @JsonIgnore
    private String rawData;

    public static EventMessage tryParse(String raw) {
        try {
            EventMessage eventMessage = jsonParser.readValue(raw, EventMessage.class);
            eventMessage.setRawData(raw);
            return eventMessage;
        } catch (JsonProcessingException e) {
            return null;
        }
    }
}
