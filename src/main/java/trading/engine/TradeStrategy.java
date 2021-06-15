package trading.engine;

import trading.message.Message;

import java.util.List;
import java.util.Queue;

public interface TradeStrategy {

    List<StateConfig> getConfig();

    boolean loop(State state, Queue<Message> messages);
}
