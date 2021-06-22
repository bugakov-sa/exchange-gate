package trading.engine;

import trading.exchange.ExchangeClient;
import trading.message.Message;
import trading.message.OhlcData;
import trading.message.StartTransferOhlc;
import trading.message.handler.MessageHandler;
import trading.message.handler.MessageRouter;
import trading.thread.Worker;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class TradeEngine extends Worker {

    private final Queue<Message> ohlcDataQueue = new ConcurrentLinkedQueue<>();
    private final Queue<Message> robotQueue = new LinkedList<>();

    private final TradeStrategy strategy;
    private final ExchangeClient exchangeClient;
    private final MessageHandler messageHandler;
    private final StateHolder state;
    private final long loopSleepMillis;
    private final MessageRouter messageRouter;

    public TradeEngine(String name, TradeStrategy strategy, ExchangeClient exchangeClient, MessageHandler messageHandler, long loopSleepMillis) {
        super(name);

        this.strategy = strategy;
        this.exchangeClient = exchangeClient;
        this.messageHandler = messageHandler;
        this.loopSleepMillis = loopSleepMillis;
        this.state = new StateHolder(new State(), strategy.getConfig());

        Set<String> pairs = getPairs().collect(Collectors.toSet());
        this.messageRouter = MessageRouter.builder()
                .when(message -> {
                    if(message.getType() == Message.Type.OHLC_DATA) {
                        if(pairs.contains(((OhlcData)message).getPair())) {
                            return true;
                        }
                    }
                    return false;
                })
                .then(ohlcDataQueue)
                .build();
    }

    @Override
    protected void beforeStart() {
        messageHandler.register(messageRouter);
        exchangeClient.send(new StartTransferOhlc(getPairs().collect(Collectors.toList())));
    }

    @Override
    protected long executeLoop() {
        List<Message> newMessages = IntStream.range(0, ohlcDataQueue.size())
                .mapToObj(i -> ohlcDataQueue.poll())
                .collect(Collectors.toList());
        state.update(newMessages);
        if (state.isStateChanged()) {
            active = !strategy.loop(state.getState(), robotQueue);
            while (!robotQueue.isEmpty()) {
                messageHandler.handle(robotQueue.poll());
            }
        }
        return loopSleepMillis;
    }

    @Override
    protected void beforeFinish() {
        messageHandler.unregister(messageRouter);
    }

    private Stream<String> getPairs() {
        return strategy.getConfig().stream().map(StateConfig::getPair);
    }
}
