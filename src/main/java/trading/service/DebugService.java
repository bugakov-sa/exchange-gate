package trading.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import trading.message.Message;
import trading.message.handler.MessageHandler;
import trading.message.handler.MessageRouter;
import trading.thread.Worker;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

@Component
public class DebugService extends Worker {

    private final static Logger log = LoggerFactory.getLogger(DebugService.class);

    private final Queue<Message> messages = new ConcurrentLinkedQueue<>();

    private final MessageHandler messageHandler;

    public DebugService(MessageHandler messageHandler) {
        super("debugger");
        this.messageHandler = messageHandler;
    }

    @PostConstruct
    public void init() {
        start();
    }

    @Override
    protected void beforeStart() {
        MessageRouter messageRouter = MessageRouter.builder()
                .whenType(Message.Type.DEBUG, Message.Type.UNPARSED_EVENT)
                .then(messages)
                .build();
        messageHandler.register(messageRouter);
    }

    @Override
    protected long executeLoop() {
        while (!messages.isEmpty()) {
            log.info(messages.poll().toString());
        }
        return 1000;
    }

    @PreDestroy
    public void close() {
        stop();
    }
}
