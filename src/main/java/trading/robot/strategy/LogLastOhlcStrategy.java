package trading.robot.strategy;

import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import trading.message.Message;
import trading.message.OhlcData;
import trading.robot.ConfigItem;
import trading.robot.RobotStrategy;
import trading.robot.State;

import java.util.Arrays;
import java.util.List;
import java.util.Queue;

@AllArgsConstructor
public class LogLastOhlcStrategy implements RobotStrategy {

    private static final Logger log = LoggerFactory.getLogger(LogLastOhlcStrategy.class);

    private final String pair;

    @Override
    public List<ConfigItem> getConfig() {
        return Arrays.asList(
                ConfigItem.lastValue(pair)
        );
    }

    @Override
    public boolean loop(State state, Queue<Message> messages) {
        OhlcData lastValue = state.getLastValue(pair);
        log.info("Ohlc: {}", lastValue);
        return false;
    }
}
