package trading.exchange;

import trading.message.Message;

import java.util.Queue;

public interface ExchangeManager {

    long subscribe(String pair, Queue<Message> queue);

    void unsubscribe(long id);
}
