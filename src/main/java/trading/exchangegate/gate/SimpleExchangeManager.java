package trading.exchangegate.gate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import trading.exchangegate.message.OhlcMessage;

import java.util.Queue;

@Component
public class SimpleExchangeManager implements ExchangeManager {

    private static final Logger log = LoggerFactory.getLogger(SimpleExchangeManager.class);

    private final SubscriptionRepository repository = new SubscriptionRepository();
    private final Object lock = new Object();

    private final ExchangeClient exchangeClient;

    public SimpleExchangeManager(ExchangeClient exchangeClient) {
        this.exchangeClient = exchangeClient;
        exchangeClient.setOhlcConsumer(ohlcMessage -> {
            String pair = ohlcMessage.getPair();
            repository.findSubscriptions(pair).forEach(c -> {
                try {
                    c.getQueue().add(ohlcMessage);
                } catch (Throwable e) {
                    log.error("Error at sending message to consumer", e);
                }
            });
        });
    }

    @Override
    public long subscribe(String pair, Queue<OhlcMessage> queue) {
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
