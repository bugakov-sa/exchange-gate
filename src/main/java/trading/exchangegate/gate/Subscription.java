package trading.exchangegate.gate;

import lombok.AllArgsConstructor;
import lombok.Getter;
import trading.exchangegate.message.OhlcMessage;

import java.util.Queue;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;

@AllArgsConstructor
@Getter
class Subscription {
    private static final AtomicLong GATE_SUBSCRIPTION_ID_GENERATOR = new AtomicLong(1);

    private final long id = GATE_SUBSCRIPTION_ID_GENERATOR.incrementAndGet();
    private final String pair;
    private final Queue<OhlcMessage> queue;
}
