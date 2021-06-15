package trading.engine;

import lombok.ToString;
import trading.message.OhlcData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Collections.*;

@ToString
public class State {

    private final Map<String, OhlcData> lastValues;
    private final Map<String, List<OhlcData>> minuteValues;

    private State(Map<String, OhlcData> lastValues, Map<String, List<OhlcData>> minuteValues) {
        this.lastValues = lastValues;
        this.minuteValues = minuteValues;
    }

    public State() {
        this(emptyMap(), emptyMap());
    }

    public List<OhlcData> getMinuteValues(String pair) {
        return minuteValues.getOrDefault(pair, new ArrayList<>());
    }

    public OhlcData getLastValue(String pair) {
        return lastValues.get(pair);
    }

    public static class Builder {
        Map<String, OhlcData> lastValues = new HashMap<>();
        Map<String, List<OhlcData>> minutesValues = new HashMap<>();

        public Builder setLastValue(String pair, OhlcData value) {
            lastValues.put(pair, value);
            return this;
        }

        public Builder setMinutesValues(String pair, List<OhlcData> data) {
            minutesValues.put(pair, unmodifiableList(data));
            return this;
        }

        public State build() {
            return new State(unmodifiableMap(lastValues), unmodifiableMap(minutesValues));
        }
    }
}
