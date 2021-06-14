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
public class LogLastMinuteOhlcStrategy implements RobotStrategy {

    private static final Logger log = LoggerFactory.getLogger(LogLastMinuteOhlcStrategy.class);

    private final String pair;

    @Override
    public List<ConfigItem> getConfig() {
        return Arrays.asList(
                ConfigItem.minutesValues(pair, 1)
        );
    }

    @Override
    public List<Order> loop(State state) {
        List<OhlcData> minutesGrid = state.getMinuteValues(pair);
        if (minutesGrid.isEmpty()) {
            return Collections.emptyList();
        }
        log.info("Ohlc: {}", minutesGrid.get(0));
        return Collections.emptyList();
    }
}
