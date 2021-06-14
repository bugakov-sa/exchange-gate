package trading.robot.strategy;

import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import trading.message.OhlcData;
import trading.message.Order;
import trading.robot.ConfigItem;
import trading.robot.RobotStrategy;
import trading.robot.State;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

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
    public List<Order> loop(State state) {
        OhlcData lastValue = state.getLastValue(pair);
        log.info("Ohlc: {}", lastValue);
        return Collections.emptyList();
    }
}
