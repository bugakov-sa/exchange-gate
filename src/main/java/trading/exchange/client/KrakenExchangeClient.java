package trading.exchange.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import trading.exchange.ExchangeClient;
import trading.message.ExchangeCommand;
import trading.message.Message;
import trading.message.StartTransferOhlc;
import trading.message.handler.MessageHandler;
import trading.thread.Worker;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.IOException;
import java.net.URI;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutionException;

import static trading.exchange.client.message.ConfigSubscriptionMessage.subscribeOhlc;

@Component
public class KrakenExchangeClient extends Worker implements ExchangeClient {

    private static final Logger log = LoggerFactory.getLogger(KrakenExchangeClient.class);
    private static final ObjectMapper jsonMapper = new ObjectMapper();

    private final Queue<ExchangeCommand> commands = new ConcurrentLinkedQueue<>();
    private final MessageHandler messageHandler;
    private final String url;

    public KrakenExchangeClient(
            MessageHandler messageHandler,
            @Value("${exchange.kraken.url}") String url
    ) {
        super("kraken-exchange-client");
        this.messageHandler = messageHandler;
        this.url = url;
    }

    private final TextWebSocketHandler handler = new TextWebSocketHandler() {
        @Override
        protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
            super.handleTextMessage(session, message);
            Message m = DecodingHelper.tryParse(message.getPayload());
            if (m != null) {
                try {
                    messageHandler.handle(m);
                } catch (Throwable t) {
                    log.error("Error at accepting message " + m, t);
                }
            } else {
                log.warn("Cannot parse message {}", message.getPayload());
            }
        }
    };

    private volatile WebSocketSession session;

    @Override
    protected void beforeStart() throws ExecutionException, InterruptedException {
        log.info("Initialization of gate...");
        StandardWebSocketClient client = new StandardWebSocketClient();
        session = client.doHandshake(
                handler,
                new WebSocketHttpHeaders(),
                URI.create(url)
        ).get();
        log.info("Initialization of gate completed.");
    }

    @Override
    protected long executeLoop() {
        while (!commands.isEmpty()) {
            ExchangeCommand command = commands.poll();
            switch (command.getType()) {
                case START_TRANSFER_OHLC:
                    send(subscribeOhlc(((StartTransferOhlc) command).getPairs()));
            }
        }
        return 1000;
    }

    @Override
    protected void beforeFinish() throws IOException {
        session.close();
    }

    @PostConstruct
    public void init() {
        super.start();
    }

    @PreDestroy
    public void close() throws IOException {
        super.stop();
    }

    @Override
    public void send(ExchangeCommand command) {
        switch (command.getType()) {
            case START_TRANSFER_OHLC:
                commands.add(command);
                return;
            default:
                throw new RuntimeException("Unsupported command type " + command.getType());
        }
    }

    private TextMessage buildTextMessage(Object message) {
        String json;
        try {
            json = jsonMapper.writeValueAsString(message);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return new TextMessage(json);
    }

    private void send(Object message) {
        TextMessage textMessage = buildTextMessage(message);
        String payload = textMessage.getPayload();
        try {
            session.sendMessage(textMessage);
            log.info("Sent message {}", payload);
        } catch (IOException e) {
            log.error("Error at sending message + " + payload, e);
        }
    }
}
