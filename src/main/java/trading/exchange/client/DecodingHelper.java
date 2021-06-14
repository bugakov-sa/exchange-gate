package trading.exchange.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import trading.exchange.client.message.EventMessage;
import trading.message.Heartbeat;
import trading.message.Message;
import trading.message.OhlcData;
import trading.message.UnparsedEvent;

import java.util.List;

class DecodingHelper {

    private static final ObjectMapper jsonParser = new ObjectMapper();

    private static OhlcData tryParseOhlcData(String raw) {
        if (!raw.startsWith("[") || !raw.contains("ohlc")) {
            return null;
        }
        Object[] objects;
        try {
            objects = jsonParser.readValue(raw, Object[].class);
        } catch (JsonProcessingException e) {
            return null;
        }
        OhlcData.OhlcDataBuilder builder = OhlcData.builder();
        builder.pair(objects[3].toString());
        List numbers = (List) (objects[1]);
        builder.time(Math.round(parse(numbers.get(0))));
        builder.etime(Math.round(parse(numbers.get(1))));
        builder.open(parse(numbers.get(2)));
        builder.high(parse(numbers.get(3)));
        builder.low(parse(numbers.get(4)));
        builder.close(parse(numbers.get(5)));
        builder.vwap(parse(numbers.get(6)));
        builder.volume(parse(numbers.get(7)));
        builder.count(Long.parseLong(numbers.get(8).toString()));
        return builder.build();
    }

    private static double parse(Object o) {
        return Double.parseDouble(o.toString());
    }

    private static Message tryParseEvent(String raw) {
        try {
            EventMessage eventMessage = jsonParser.readValue(raw, EventMessage.class);
            switch (eventMessage.getEvent()) {
                case HEARTBEAT:
                    return new Heartbeat();
                default:
                    return new UnparsedEvent(eventMessage.getEvent().name(), raw);
            }
        } catch (JsonProcessingException e) {
            return null;
        }
    }

    public static Message tryParse(String raw) {
        OhlcData ohlcData = DecodingHelper.tryParseOhlcData(raw);
        if (ohlcData != null) {
            return ohlcData;
        }
        return DecodingHelper.tryParseEvent(raw);
    }
}
