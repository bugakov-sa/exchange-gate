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

    void subscribe(String pair);

    void unsubscribe(String pair);

    void setEventConsumer(Consumer<EventMessage> consumer);
}
