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

public class LogLastMinuteOhlcStrategy implements RobotStrategy {

    private static final Logger log = LoggerFactory.getLogger(LogLastMinuteOhlcStrategy.class);

    private final String pair;

    public LogLastMinuteOhlcStrategy(String pair) {
        this.pair = pair;
    }

    @Override
    public List<ConfigItem> getConfig() {
        return Arrays.asList(
                ConfigItem.minutesValues(pair, 1)
        );
    }

    @Override
    public List<Order> loop(State state) {
        List<OhlcMessage> minutesGrid = state.getMinuteValues(pair);
        if(minutesGrid.isEmpty()) {
            return Collections.emptyList();
        }
        log.info("Ohlc: {}", minutesGrid.get(0));
        return Collections.emptyList();
    }
}
