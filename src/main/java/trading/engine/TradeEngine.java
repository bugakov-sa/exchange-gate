package trading.engine;

import trading.exchange.ExchangeManager;
import trading.message.Message;
import trading.thread.Worker;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static java.util.Collections.unmodifiableList;

public class TradeEngine extends Worker {

    private final Queue<Message> exchangeQueue = new ConcurrentLinkedQueue<>();
    private final Queue<Message> robotQueue = new LinkedList<>();

    private final TradeStrategy strategy;
    private final ExchangeManager exchange;
    private final List<StateConfig> config;
    private final StateHolder state;
    private final long loopSleepMillis;

    private List<Long> ids;

    public TradeEngine(String name, TradeStrategy strategy, ExchangeManager exchange, long loopSleepMillis) {
        super(name);
        this.strategy = strategy;
        this.exchange = exchange;
        this.config = unmodifiableList(strategy.getConfig());
        this.loopSleepMillis = loopSleepMillis;
        this.state = new StateHolder(new State(), config);
    }

    @Override
    protected void beforeStart() {
        ids = config.stream()
                .map(StateConfig::getPair)
                .map(pair -> exchange.subscribe(pair, exchangeQueue))
                .collect(Collectors.toList());
    }

    @Override
    protected long executeLoop() {
        List<Message> newMessages = pollNewMessages();
        state.update(newMessages);
        if (state.isStateChanged()) {
            active = !strategy.loop(state.getState(), robotQueue);
            processRobotMessages();
        }
        return loopSleepMillis;
    }

    @Override
    protected void beforeFinish() {
        ids.forEach(exchange::unsubscribe);
    }

    private List<Message> pollNewMessages() {
        return IntStream.range(0, exchangeQueue.size())
                .mapToObj(i -> exchangeQueue.poll())
                .collect(Collectors.toList());
    }

    private void processRobotMessages() {
        //TODO: send orders to exchange
        //TODO: send debug messages to management ui
        //TODO: send notifications to mail
    }
}
