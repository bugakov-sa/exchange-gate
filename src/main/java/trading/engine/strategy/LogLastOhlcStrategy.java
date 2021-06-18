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
public class LogLastOhlcStrategy implements TradeStrategy {

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
        messages.add(new DebugMessage(lastValue.toString()));
        return false;
    }
}
