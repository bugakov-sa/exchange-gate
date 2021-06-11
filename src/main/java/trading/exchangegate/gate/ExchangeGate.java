package trading.exchangegate.gate;

import trading.exchangegate.consumer.OhlcConsumer;
import trading.exchangegate.message.EventMessage;
import trading.exchangegate.message.OhlcMessage;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;

public interface ExchangeGate {

    void init() throws InterruptedException, ExecutionException;

    void subscribeOHLC(String pair, OhlcConsumer consumer);

    void unsubscribeOHLC(String pair);

    List<String> listeningPairs();

    void setEventHandler(Consumer<EventMessage> consumer);
}
