package trading.exchangegate.robot;

import lombok.ToString;
import trading.exchangegate.message.OhlcMessage;

import java.util.*;

import static java.util.Collections.*;

@ToString
public class State {

    private final Map<String, OhlcMessage> lastValues;
    private final Map<String, List<OhlcMessage>> minuteValues;

    private State(Map<String, OhlcMessage> lastValues, Map<String, List<OhlcMessage>> minuteValues) {
        this.lastValues = lastValues;
        this.minuteValues = minuteValues;
    }

    public State() {
        this(emptyMap(), emptyMap());
    }

    public List<OhlcMessage> getMinuteValues(String pair) {
        return minuteValues.getOrDefault(pair, new ArrayList<>());
    }

    public OhlcMessage getLastValue(String pair) {
        return lastValues.get(pair);
    }

    public static class Builder {
        Map<String, OhlcMessage> lastValues = new HashMap<>();
        Map<String, List<OhlcMessage>> minutesValues = new HashMap<>();

        public Builder setLastValue(String pair, OhlcMessage value) {
            lastValues.put(pair, value);
            return this;
        }

        public Builder setMinutesValues(String pair, List<OhlcMessage> data) {
            minutesValues.put(pair, unmodifiableList(data));
            return this;
        }

        public State build() {
            return new State(unmodifiableMap(lastValues), unmodifiableMap(minutesValues));
        }
    }
}
