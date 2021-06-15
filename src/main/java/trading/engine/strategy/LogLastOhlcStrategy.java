package trading.engine.strategy;

import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import trading.engine.State;
import trading.engine.StateConfig;
import trading.engine.TradeStrategy;
import trading.message.Message;
import trading.message.OhlcData;

import java.util.Arrays;
import java.util.List;
import java.util.Queue;

@AllArgsConstructor
public class LogLastOhlcStrategy implements TradeStrategy {

    private static final Logger log = LoggerFactory.getLogger(LogLastOhlcStrategy.class);

    private final String pair;

    @Override
    public List<StateConfig> getConfig() {
        return Arrays.asList(
                StateConfig.lastValue(pair)
        );
    }

    @Override
    public boolean loop(State state, Queue<Message> messages) {
        OhlcData lastValue = state.getLastValue(pair);
        log.info("Ohlc: {}", lastValue);
        return false;
    }
}
