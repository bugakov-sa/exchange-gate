package trading.engine;

import lombok.Getter;

@Getter
public class StateConfig {

    public enum Type {
        MINUTE_VALUES,
        LAST_VALUE
    }

    private final String pair;
    private final Type type;
    private final int bufferSize;

    private StateConfig(String pair, Type type, int bufferSize) {
        this.pair = pair;
        this.type = type;
        this.bufferSize = bufferSize;
    }

    public static StateConfig minutesValues(String pair, int bufferSize) {
        return new StateConfig(pair, Type.MINUTE_VALUES, bufferSize);
    }

    public static StateConfig lastValue(String pair) {
        return new StateConfig(pair, Type.LAST_VALUE, 1);
    }
}
