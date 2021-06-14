package trading.message;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OhlcData implements Message {

    private final String pair;
    private final long time;
    private final long etime;
    private final double open;
    private final double high;
    private final double low;
    private final double close;
    private final double vwap;
    private final double volume;
    private final long count;

    @Override
    public Type getType() {
        return Type.OHLC_DATA;
    }
}
