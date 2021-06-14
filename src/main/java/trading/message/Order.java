package trading.message;

public class Order implements Message {
    @Override
    public Type getType() {
        return Type.ORDER;
    }
}
