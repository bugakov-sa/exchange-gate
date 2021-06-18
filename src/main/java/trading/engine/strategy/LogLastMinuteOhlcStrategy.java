package trading.engine.strategy;

import lombok.AllArgsConstructor;
import trading.engine.State;
import trading.engine.StateConfig;
import trading.engine.TradeStrategy;
import trading.message.DebugMessage;
import trading.message.Message;
import trading.message.OhlcData;

import java.util.Arrays;
import java.util.List;
import java.util.Queue;

@AllArgsConstructor
public class LogLastMinuteOhlcStrategy implements TradeStrategy {

    private final String pair;

    @Override
    public List<StateConfig> getConfig() {
        return Arrays.asList(
                StateConfig.minutesValues(pair, 1)
        );
    }

    @Override
    public boolean loop(State state, Queue<Message> messages) {
        List<OhlcData> minutesGrid = state.getMinuteValues(pair);
        if (!minutesGrid.isEmpty()) {
            messages.add(new DebugMessage(minutesGrid.get(0).toString()));
        }
        return false;
    }
}
