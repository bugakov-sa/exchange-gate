package trading.exchangegate.message;

import lombok.Data;

@Data
public class ConfigOhlcMessage extends EventMessage {

    private String[] pair;
    private Subscription subscription;

    private ConfigOhlcMessage() {}

    public static ConfigOhlcMessage ohlc(String pair, Event event) {
        ConfigOhlcMessage message = new ConfigOhlcMessage();
        message.setEvent(event);
        message.setSubscription(new Subscription("ohlc"));
        message.setPair(new String[]{pair});
        return message;
    }
}
