package trading.exchangegate.message;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class OhlcMessage {

    private static final ObjectMapper jsonParser = new ObjectMapper();

    private final String pair;
    private final long time, etime;
    private final double open, high, low, close, vwap, volume;
    private final long count;

    public static OhlcMessage tryParse(String raw) {
        if (!raw.startsWith("[") || !raw.contains("ohlc")) {
            return null;
        }
        Object[] objects;
        try {
            objects = jsonParser.readValue(raw, Object[].class);
        } catch (JsonProcessingException e) {
            return null;
        }
        OhlcMessageBuilder builder = new OhlcMessageBuilder();
        builder.pair = objects[3].toString();
        List numbers = (List) (objects[1]);
        builder.time = Math.round(parse(numbers.get(0)));
        builder.etime = Math.round(parse(numbers.get(1)));
        builder.open = parse(numbers.get(2));
        builder.high = parse(numbers.get(3));
        builder.low = parse(numbers.get(4));
        builder.close = parse(numbers.get(5));
        builder.vwap = parse(numbers.get(6));
        builder.volume = parse(numbers.get(7));
        builder.count = Long.parseLong(numbers.get(8).toString());
        return builder.build();
    }

    private static double parse(Object o) {
        return Double.parseDouble(o.toString());
    }
}
