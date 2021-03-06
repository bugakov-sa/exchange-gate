package trading.exchange.client.message;

import lombok.Data;

import java.util.List;

import static java.util.Collections.unmodifiableList;
import static trading.exchange.client.message.Event.SUBSCRIBE;
import static trading.exchange.client.message.Event.UNSUBSCRIBE;

@Data
public class ConfigSubscriptionMessage {

    @Data
    public static class Subscription {
        private final String name;
    }

    private static final Subscription OHLC = new Subscription("ohlc");

    private final Event event;
    private final List<String> pair;
    private final Subscription subscription;

    private ConfigSubscriptionMessage(Event event, Subscription subscription, List<String> pairs) {
        this.event = event;
        this.pair = unmodifiableList(pairs);
        this.subscription = subscription;
    }

    public static ConfigSubscriptionMessage subscribeOhlc(List<String> pairs) {
        return new ConfigSubscriptionMessage(SUBSCRIBE, OHLC, pairs);
    }

    public static ConfigSubscriptionMessage unsubscribeOhlc(List<String> pairs) {
        return new ConfigSubscriptionMessage(UNSUBSCRIBE, OHLC, pairs);
    }
}
