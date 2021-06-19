package trading.message.handler;

import org.springframework.stereotype.Component;
import trading.message.Message;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Queue;
import java.util.stream.Collectors;

@Component
public class MessageHandler {

    private final Object lock = new Object();
    private volatile List<MessageRouter> routers = Collections.EMPTY_LIST;

    public void register(MessageRouter router) {
        synchronized (lock) {
            if(!routers.contains(router)) {
                List<MessageRouter> newRouters = new ArrayList<>(routers);
                newRouters.add(router);
                routers = Collections.unmodifiableList(newRouters);
            }
        }
    }

    public void unregister(MessageRouter router) {
        synchronized (lock) {
            List<MessageRouter> newRouters = new ArrayList<>(routers);
            newRouters.remove(router);
            routers = Collections.unmodifiableList(newRouters);
        }
    }

    public boolean handle(Message message) {
        List<Queue<Message>> queues = routers.stream()
                .flatMap(router -> router.apply(message).stream())
                .collect(Collectors.toList());
        if(queues.isEmpty()) {
            return false;
        }
        queues.forEach(q -> q.add(message));
        return true;
    }
}
