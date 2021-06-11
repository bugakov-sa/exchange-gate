package trading.exchangegate.consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import trading.exchangegate.message.OhlcMessage;

public class LoggingConsumer implements OhlcConsumer {

    private static final Logger log = LoggerFactory.getLogger(LoggingConsumer.class);

    @Override
    public void accept(OhlcMessage ohlcMessage) {
        log.info("OHLC: {}", ohlcMessage);
    }
}
