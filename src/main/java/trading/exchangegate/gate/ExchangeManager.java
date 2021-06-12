package trading.exchangegate.gate;

import trading.exchangegate.message.OhlcMessage;

import java.util.Queue;

public interface ExchangeManager {

    long subscribe(String pair, Queue<OhlcMessage> queue);

    void unsubscribe(long id);
}
