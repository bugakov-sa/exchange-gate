package trading.exchange.manager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import trading.exchange.ExchangeClient;
import trading.exchange.ExchangeManager;
import trading.message.Message;
import trading.message.OhlcData;

import javax.annotation.PostConstruct;
import java.util.Collections;
import java.util.List;
import java.util.Queue;
import java.util.stream.Collectors;

@Component
public class KrakenExchangeManager implements ExchangeManager {

    private static final Logger log = LoggerFactory.getLogger(KrakenExchangeManager.class);

    private final SubscriptionRepository repository = new SubscriptionRepository();
    private final Object lock = new Object();

    private final ExchangeClient exchangeClient;

    public KrakenExchangeManager(ExchangeClient exchangeClient) {
        this.exchangeClient = exchangeClient;
    }

    @PostConstruct
    public void init() {
        exchangeClient.setConsumer(message -> getQueues(message).forEach(queue -> {
            try {
                queue.add(message);
            } catch (Throwable throwable) {
                log.error("Error at sending message to queue", throwable);
            }
        }));
    }

    private List<Queue> getQueues(Message message) {
        switch (message.getType()) {
            case OHLC_DATA:
                String pair = ((OhlcData) message).getPair();
                return repository.findSubscriptions(pair)
                        .stream()
                        .map(Subscription::getQueue)
                        .distinct()
                        .collect(Collectors.toList());
            case HEARTBEAT:
                return Collections.emptyList();
            case UNPARSED_EVENT:
                log.info("Unparsed event {}", message);
                return Collections.emptyList();
            default:
                //TODO: handle orders state
                return Collections.emptyList();
        }
    }

    @Override
    public long subscribe(String pair, Queue<Message> queue) {
        Subscription newSubscription = new Subscription(pair, queue);
        synchronized (lock) {
            if (!repository.existSubscriptions(pair)) {
                exchangeClient.subscribe(pair);
            }
            repository.addSubscription(newSubscription);
        }
        return newSubscription.getId();
    }

    @Override
    public void unsubscribe(long id) {
        Subscription subscription = repository.findSubscription(id);
        if (subscription == null) {
            return;
        }
        synchronized (lock) {
            repository.removeSubscription(subscription);
            if (!repository.existSubscriptions(subscription.getPair())) {
                exchangeClient.unsubscribe(subscription.getPair());
            }
        }
    }
}
