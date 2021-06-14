package trading.exchange.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import trading.exchange.ExchangeClient;
import trading.message.Message;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.IOException;
import java.net.URI;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;

import static trading.exchange.client.message.ConfigSubscriptionMessage.subscribeOhlc;
import static trading.exchange.client.message.ConfigSubscriptionMessage.unsubscribeOhlc;

@Component
public class KrakenExchangeClient implements ExchangeClient {

    private static final Logger log = LoggerFactory.getLogger(KrakenExchangeClient.class);
    private static final ObjectMapper jsonMapper = new ObjectMapper();
    private static final String URL = "wss://ws.kraken.com";

    private volatile Consumer<Message> consumer;

    @Override
    public void setConsumer(Consumer<Message> consumer) {
        this.consumer = consumer;
    }

    private final TextWebSocketHandler handler = new TextWebSocketHandler() {
        @Override
        protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
            super.handleTextMessage(session, message);
            Message m = DecodingHelper.tryParse(message.getPayload());
            if (m != null) {
                try {
                    consumer.accept(m);
                } catch (Throwable t) {
                    log.error("Error at accepting message " + m, t);
                }
            } else {
                log.warn("Cannot parse message {}", message.getPayload());
            }
        }
    };

    private volatile WebSocketSession session;

    @PostConstruct
    public void init() throws ExecutionException, InterruptedException {
        log.info("Initialization of gate...");
        StandardWebSocketClient client = new StandardWebSocketClient();
        session = client.doHandshake(
                handler,
                new WebSocketHttpHeaders(),
                URI.create(URL)
        ).get();
        log.info("Initialization of gate completed.");
    }

    @Override
    public void subscribe(String... pairs) {
        send(subscribeOhlc(pairs));
    }

    @Override
    public void unsubscribe(String... pairs) {
        send(unsubscribeOhlc(pairs));
    }

    @PreDestroy
    public void close() throws IOException {
        session.close();
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
