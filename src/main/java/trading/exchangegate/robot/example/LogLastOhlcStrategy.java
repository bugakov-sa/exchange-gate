package trading.exchangegate.robot.example;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import trading.exchangegate.message.OhlcMessage;
import trading.exchangegate.robot.ConfigItem;
import trading.exchangegate.robot.Order;
import trading.exchangegate.robot.RobotStrategy;
import trading.exchangegate.robot.State;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class LogLastOhlcStrategy implements RobotStrategy {

    private static final Logger log = LoggerFactory.getLogger(LogLastOhlcStrategy.class);

    private final String pair;

    public LogLastOhlcStrategy(String pair) {
        this.pair = pair;
    }

    @Override
    public List<ConfigItem> getConfig() {
        return Arrays.asList(
                ConfigItem.lastValue(pair)
        );
    }

    @Override
    public List<Order> loop(State state) {
        OhlcMessage lastValue = state.getLastValue(pair);
        log.info("Ohlc: {}", lastValue);
        return Collections.emptyList();
    }
}
