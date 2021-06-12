package trading.exchangegate.gate;

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
import trading.exchangegate.message.Event;
import trading.exchangegate.message.EventMessage;
import trading.exchangegate.message.OhlcMessage;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.IOException;
import java.net.URI;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;

import static trading.exchangegate.message.SubscribeMessage.subscribeOhlc;
import static trading.exchangegate.message.SubscribeMessage.unsubscribeOhlc;

@Component
public class KrakenExchangeClient implements ExchangeClient {

    private static final Logger log = LoggerFactory.getLogger(KrakenExchangeClient.class);
    private static final ObjectMapper jsonMapper = new ObjectMapper();
    private static final String URL = "wss://ws.kraken.com";

    private volatile Consumer<OhlcMessage> ohlcConsumer = m -> {
    };
    private volatile Consumer<EventMessage> eventConsumer = e -> {
    };

    private final TextWebSocketHandler handler = new TextWebSocketHandler() {
        @Override
        protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
            super.handleTextMessage(session, message);
            String payload = message.getPayload();
            OhlcMessage ohlcMessage = OhlcMessage.tryParse(payload);
            if (ohlcMessage != null) {
                try {
                    ohlcConsumer.accept(ohlcMessage);
                } catch (Throwable t) {
                    log.error("Error at accepting ohlc message " + ohlcMessage, t);
                } finally {
                    return;
                }
            }
            EventMessage eventMessage = EventMessage.tryParse(payload);
            if (eventMessage != null) {
                try {
                    if(eventMessage.getEvent() != Event.HEARTBEAT) {
                        log.info("Event {}", eventMessage);
                        eventConsumer.accept(eventMessage);
                    }
                } catch (Throwable t) {
                    log.error("Error at accepting event message " + ohlcMessage, t);
                } finally {
                    return;
                }
            }
            log.warn("Cannot parse message {}", payload);
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
    public void setOhlcConsumer(Consumer<OhlcMessage> consumer) {
        if (consumer == null) {
            consumer = m -> {
            };
        }
        ohlcConsumer = consumer;
    }

    @Override
    public void subscribe(String ... pairs) {
        send(subscribeOhlc(pairs));
    }

    @Override
    public void unsubscribe(String ... pairs) {
        send(unsubscribeOhlc(pairs));
    }

    @Override
    public void setEventConsumer(Consumer<EventMessage> consumer) {
        if (consumer == null) {
            consumer = m -> {
            };
        }
        eventConsumer = consumer;
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
