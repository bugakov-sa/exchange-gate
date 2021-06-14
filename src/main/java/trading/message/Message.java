package trading.message;

/**
 * Interface for all messages exchanged between the components of the trading system.
 * Example 1. Messages exchanged between exchange gate and robots.
 * Example 2. Messages exchanged between robots and management UI.
 * All implementations must be immutable.
 */
public interface Message {
    enum Type {
        OHLC_DATA,
        ORDER,
        HEARTBEAT,
        UNPARSED_EVENT
    }

    Type getType();
}

