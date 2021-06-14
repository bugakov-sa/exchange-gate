package trading.robot;

import lombok.Getter;

@Getter
public class ConfigItem {

    public enum Type {
        MINUTE_VALUES,
        LAST_VALUE
    }

    private final String pair;
    private final Type type;
    private final int bufferSize;

    private ConfigItem(String pair, Type type, int bufferSize) {
        this.pair = pair;
        this.type = type;
        this.bufferSize = bufferSize;
    }

    public static ConfigItem minutesValues(String pair, int bufferSize) {
        return new ConfigItem(pair, Type.MINUTE_VALUES, bufferSize);
    }

    public static ConfigItem lastValue(String pair) {
        return new ConfigItem(pair, Type.LAST_VALUE, 1);
    }
}
