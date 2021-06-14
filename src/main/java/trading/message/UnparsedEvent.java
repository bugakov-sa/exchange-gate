package trading.message;

import lombok.Data;

@Data
public class UnparsedEvent implements Message {

    private final String event;
    private final String rawData;

    @Override
    public Type getType() {
        return Type.UNPARSED_EVENT;
    }
}
