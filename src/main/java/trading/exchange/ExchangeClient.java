package trading.exchange;

import trading.message.Message;

import java.util.function.Consumer;

public interface ExchangeClient {

    void setConsumer(Consumer<Message> consumer);

    void subscribe(String... pairs);

    void unsubscribe(String... pairs);
}
