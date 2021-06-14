package trading.exchange.manager;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Collections.emptyList;
import static java.util.Collections.unmodifiableList;

class SubscriptionRepository {

    private final Object lock = new Object();
    private volatile List<Subscription> subscriptions = emptyList();

    boolean existSubscriptions(String pair) {
        return subscriptions.stream()
                .filter(gs -> gs.getPair().equals(pair))
                .findFirst()
                .isPresent();
    }

    List<Subscription> findSubscriptions(String pair) {
        return subscriptions.stream()
                .filter(gs -> gs.getPair().equals(pair))
                .collect(Collectors.toList());
    }

    Subscription findSubscription(long id) {
        return subscriptions.stream()
                .filter(gs -> gs.getId() == id)
                .findFirst()
                .orElse(null);
    }

    void addSubscription(Subscription subscription) {
        synchronized (lock) {
            List<Subscription> list = new ArrayList<>(subscriptions);
            list.add(subscription);
            subscriptions = unmodifiableList(list);
        }
    }

    void removeSubscription(Subscription subscription) {
        synchronized (lock) {
            List<Subscription> list = new ArrayList<>(subscriptions);
            list.remove(subscription);
            subscriptions = unmodifiableList(list);
        }
    }
}
