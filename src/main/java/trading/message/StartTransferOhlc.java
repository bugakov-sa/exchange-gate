package trading.message;

import lombok.Data;

import java.util.Collections;
import java.util.List;

@Data
public class StartTransferOhlc implements ExchangeCommand {
    private final List<String> pairs;

    public StartTransferOhlc(List<String> pairs) {
        this.pairs = Collections.unmodifiableList(pairs);
    }

    @Override
    public Type getType() {
        return Type.START_TRANSFER_OHLC;
    }
}
