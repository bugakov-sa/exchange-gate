package trading.exchange;

public interface ExchangeClient {

    void subscribe(String... pairs);

    void unsubscribe(String... pairs);
}
