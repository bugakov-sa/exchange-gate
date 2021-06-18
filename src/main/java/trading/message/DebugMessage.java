package trading.message;

import lombok.Data;

@Data
public class DebugMessage implements Message {

    private final String message;

    @Override
    public Type getType() {
        return Type.DEBUG;
    }
}
