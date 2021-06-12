package trading.exchangegate.robot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import trading.exchangegate.gate.ExchangeManager;
import trading.exchangegate.message.OhlcMessage;

import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static java.util.Collections.unmodifiableList;

public class Robot {

    private static final Logger log = LoggerFactory.getLogger(Robot.class);
    public static final int LOOP_SLEEP_MILLIS = 5000;

    private final Queue<OhlcMessage> messageQueue = new ConcurrentLinkedQueue<>();

    private final RobotStrategy robotStrategy;
    private final List<ConfigItem> configItems;
    private final ExchangeManager exchangeManager;
    private final StateHolder stateHolder;

    private volatile boolean stopping = false;

    public Robot(RobotStrategy robotStrategy, ExchangeManager exchangeManager) {
        this.robotStrategy = robotStrategy;
        this.configItems = unmodifiableList(robotStrategy.getConfig());
        this.stateHolder = new StateHolder(new State(), configItems);
        this.exchangeManager = exchangeManager;
    }

    public void start() {
        log.info("Starting robot...");
        final Set<String> pairs = configItems.stream()
                .map(ConfigItem::getPair)
                .collect(Collectors.toSet());
        final List<Long> ids = pairs.stream()
                .map(pair -> exchangeManager.subscribe(pair, messageQueue::add))
                .collect(Collectors.toList());
        Thread thread = new Thread(() -> {
            log.info("Started robot.");
            while (!stopping) {
                try {
                    State oldState = stateHolder.getState();
                    List<OhlcMessage> newMessages = pollNewMessages();
                    stateHolder.update(newMessages);
                    if (stateHolder.isStateChanged()) {
                        log.debug("New state calculated.");
                        log.debug("New messages {}", newMessages);
                        log.debug("Old state {}", oldState);
                        log.debug("New state {}", stateHolder.getState());
                        log.debug("Calling robot loop...");
                        List<Order> orders = robotStrategy.loop(stateHolder.getState());
                        executeOrders(orders);
                    }
                    Thread.sleep(LOOP_SLEEP_MILLIS);
                } catch (Throwable error) {
                    log.error("Error during robot loop", error);
                }

            }
            ids.forEach(exchangeManager::unsubscribe);
            log.info("Stopping robot...");
        });
        thread.start();
    }

    private List<OhlcMessage> pollNewMessages() {
        return IntStream.range(0, messageQueue.size())
                .mapToObj(i -> messageQueue.poll())
                .collect(Collectors.toList());
    }

    private void executeOrders(List<Order> orders) {
        //TODO
    }

    public void stop() {
        stopping = true;
    }
}
