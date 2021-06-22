package trading.exchange;

import trading.message.ExchangeCommand;

public interface ExchangeClient {

    void send(ExchangeCommand command);
}
