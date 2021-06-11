package trading.exchangegate.gate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import trading.exchangegate.consumer.OhlcConsumer;
import trading.exchangegate.message.ConfigOhlcMessage;
import trading.exchangegate.message.Event;
import trading.exchangegate.message.EventMessage;
import trading.exchangegate.message.OhlcMessage;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;

public class KrakenExchangeGate implements ExchangeGate {

    private static final Logger log = LoggerFactory.getLogger(KrakenExchangeGate.class);
    private static final ObjectMapper jsonMapper = new ObjectMapper();
    private static final String URL = "wss://ws.kraken.com";

    private final Object lock = new Object();
    private final Map<String, OhlcConsumer> consumers = new ConcurrentHashMap<>();

    private volatile Consumer<EventMessage> consumer = eventMessage -> {};
    private volatile WebSocketSession session;

    @Override
    public void init() throws ExecutionException, InterruptedException {
        StandardWebSocketClient client = new StandardWebSocketClient();
        session = client.doHandshake(
                new TextWebSocketHandler() {
                    @Override
                    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
                        super.handleTextMessage(session, message);
                        String payload = message.getPayload();
                        OhlcMessage ohlcMessage = OhlcMessage.tryParse(payload);
                        if (ohlcMessage != null) {
                            Consumer<OhlcMessage> consumer = KrakenExchangeGate.this.consumers.get(ohlcMessage.getPair());
                            try {
                                consumer.accept(ohlcMessage);
                            } catch (Throwable t) {
                                log.error("Error at accepting ohlc message " + ohlcMessage, t);
                            } finally {
                                return;
                            }
                        }
                        EventMessage eventMessage = EventMessage.tryParse(payload);
                        if(eventMessage != null) {
                            try {
                                consumer.accept(eventMessage);
                            } catch (Throwable t) {
                                log.error("Error at accepting event message " + ohlcMessage, t);
                            } finally {
                                return;
                            }
                        }
                        log.warn("Cannot parse message {}", payload);
                    }
                },
                new WebSocketHttpHeaders(),
                URI.create(URL)
        ).get();
    }

    private TextMessage buildTextMessage(Object message) {
        String json;
        try {
            json = jsonMapper.writeValueAsString(message);
        }
        catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return new TextMessage(json);
    }

    @Override
    public void subscribeOHLC(String pair, OhlcConsumer consumer) {
        synchronized (lock) {
            if(consumers.containsKey(pair)) {
                consumers.put(pair, consumer);
                return;
            }
            ConfigOhlcMessage message = ConfigOhlcMessage.ohlc(pair, Event.SUBSCRIBE);
            TextMessage textMessage = buildTextMessage(message);
            String payload = textMessage.getPayload();
            try {
                consumers.put(pair, consumer);
                log.info("Sending message {}", payload);
                session.sendMessage(textMessage);
                log.info("Sent message {}", payload);
            } catch (IOException e) {
                log.error("Error at sending message + " + payload, e);
                consumers.remove(pair);
            }
        }
    }

    @Override
    public void unsubscribeOHLC(String pair) {
        synchronized (lock) {
            if(!consumers.containsKey(pair)) {
                return;
            }
            ConfigOhlcMessage message = ConfigOhlcMessage.ohlc(pair, Event.SUBSCRIBE);
            TextMessage textMessage = buildTextMessage(message);
            String payload = textMessage.getPayload();
            try {
                log.info("Sending message {}", payload);
                session.sendMessage(textMessage);
                log.info("Sent message {}", payload);
                consumers.remove(pair);
            } catch (IOException e) {
                log.error("Error at sending message + " + payload, e);
            }
        }
    }

    @Override
    public List<String> listeningPairs() {
        synchronized (lock) {
            return new ArrayList<>(consumers.keySet());
        }
    }

    @Override
    public void setEventHandler(Consumer<EventMessage> consumer) {
        if(consumer == null) {
            consumer = eventMessage -> {};
        }
        this.consumer = consumer;
    }
}
