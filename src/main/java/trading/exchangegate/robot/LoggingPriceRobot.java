package trading.exchangegate.robot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import trading.exchangegate.gate.ExchangeManager;
import trading.exchangegate.message.OhlcMessage;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class LoggingPriceRobot implements Robot {

    private static final Logger log = LoggerFactory.getLogger(LoggingPriceRobot.class);
    private static final int SLEEP_MILLIS = 5000;
    private static final int SHUTDOWN_MILLIS = 5000;

    private final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
    private final Queue<OhlcMessage> rawMarketData = new ConcurrentLinkedQueue<>();

    private final ExchangeManager exchangeManager;
    private final String pair;
    private volatile long subscriptionId = 0;

    public LoggingPriceRobot(ExchangeManager exchangeManager, String pair) {
        this.exchangeManager = exchangeManager;
        this.pair = pair;
    }

    @Override
    public void start() {
        subscriptionId = exchangeManager.subscribe(pair, message -> rawMarketData.add(message));
        executorService.scheduleWithFixedDelay(() -> loop(), 0, SLEEP_MILLIS, TimeUnit.MILLISECONDS);
        log.info("Started");
    }

    @Override
    public void loop() {
        int newDataSize = rawMarketData.size();
        for (int i = 0; i < newDataSize; i++) {
            log.info("OHLC: {}", rawMarketData.poll());
        }
    }

    @Override
    public void stop() {
        exchangeManager.unsubscribe(subscriptionId);
        try {
            executorService.awaitTermination(SHUTDOWN_MILLIS, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            log.error("Error during waiting shutdown robot", e);
        }
    }
}
