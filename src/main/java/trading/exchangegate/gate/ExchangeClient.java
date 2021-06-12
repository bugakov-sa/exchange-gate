package trading.exchangegate.gate;

import trading.exchangegate.message.EventMessage;
import trading.exchangegate.message.OhlcMessage;

import java.util.function.Consumer;

/**
 * Managing websocket connection.
 * Encoding and decoding messages.
 */
public interface ExchangeClient {

    void setOhlcConsumer(Consumer<OhlcMessage> consumer);

    void subscribe(String... pairs);

    void unsubscribe(String... pairs);

    void setEventConsumer(Consumer<EventMessage> consumer);
}
