package trading.exchangegate.message;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;

import java.util.List;

@Data
public class OhlcMessage {

    private static final ObjectMapper jsonParser = new ObjectMapper();

    private String pair;
    private long time, etime;
    private double open, high, low, close, vwap, volume;
    private long count;

    private OhlcMessage() {
    }

    public static OhlcMessage tryParse(String raw) {
        if (!raw.startsWith("[") || !raw.contains("ohlc")) {
            return null;
        }
        Object[] objects = new Object[0];
        try {
            objects = jsonParser.readValue(raw, Object[].class);
        } catch (JsonProcessingException e) {
            return null;
        }
        OhlcMessage message = new OhlcMessage();
        message.setPair(objects[3].toString());
        List numbers = (List) (objects[1]);
        message.setTime(Math.round(parse(numbers.get(0))));
        message.setEtime(Math.round(parse(numbers.get(1))));
        message.setOpen(parse(numbers.get(2)));
        message.setHigh(parse(numbers.get(3)));
        message.setLow(parse(numbers.get(4)));
        message.setClose(parse(numbers.get(5)));
        message.setVwap(parse(numbers.get(6)));
        message.setVolume(parse(numbers.get(7)));
        message.setCount(Long.parseLong(numbers.get(8).toString()));
        return message;
    }

    private static double parse(Object o) {
        return Double.parseDouble(o.toString());
    }
}
