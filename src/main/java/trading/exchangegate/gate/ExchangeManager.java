package trading.exchangegate.gate;

import trading.exchangegate.message.OhlcMessage;

import java.util.function.Consumer;

/**
 * Routing ohlc messages to consumers.
 */
public interface ExchangeManager {

    long subscribe(String pair, Consumer<OhlcMessage> consumer);

    void unsubscribe(long id);
}
