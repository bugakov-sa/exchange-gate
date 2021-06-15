package trading.engine;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import trading.exchange.ExchangeManager;
import trading.message.Message;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static java.util.Collections.unmodifiableList;

public class TradeEngine {

    private static final Logger log = LoggerFactory.getLogger(TradeEngine.class);

    private final Queue<Message> exchangeQueue = new ConcurrentLinkedQueue<>();
    private final Queue<Message> robotQueue = new LinkedList<>();

    private final TradeStrategy strategy;
    private final ExchangeManager exchange;
    private final List<StateConfig> config;
    private final StateHolder state;
    private final long loopSleepMillis;

    private volatile boolean stopping = false;

    public TradeEngine(TradeStrategy strategy, ExchangeManager exchange, long loopSleepMillis) {
        this.strategy = strategy;
        this.exchange = exchange;
        this.config = unmodifiableList(strategy.getConfig());
        this.loopSleepMillis = loopSleepMillis;
        this.state = new StateHolder(new State(), config);
    }

    public void start() {
        log.info("Starting robot...");
        List<Long> ids = config.stream()
                .map(StateConfig::getPair)
                .map(pair -> exchange.subscribe(pair, exchangeQueue))
                .collect(Collectors.toList());
        new Thread(() -> {
            log.info("Started robot.");
            do {
                try {
                    List<Message> newMessages = pollNewMessages();
                    state.update(newMessages);
                    if (state.isStateChanged()) {
                        stopping = strategy.loop(state.getState(), robotQueue);
                        processRobotMessages();
                    }
                    if (!stopping) {
                        Thread.sleep(loopSleepMillis);
                    }
                } catch (Throwable error) {
                    log.error("Error during robot loop", error);
                }
            }
            while (!stopping);
            ids.forEach(exchange::unsubscribe);
            log.info("Stopping robot...");
        }).start();
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

    public void stop() {
        stopping = true;
    }
}
