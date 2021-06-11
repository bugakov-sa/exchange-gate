package trading.exchangegate.consumer;

import trading.exchangegate.message.OhlcMessage;

import java.util.function.Consumer;

public interface OhlcConsumer extends Consumer<OhlcMessage> {
}
