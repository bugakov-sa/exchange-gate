package trading.message;

public class Heartbeat implements Message {
    @Override
    public Type getType() {
        return Type.HEARTBEAT;
    }
}
