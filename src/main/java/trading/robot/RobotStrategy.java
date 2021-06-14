package trading.robot;

import trading.message.Order;

import java.util.List;

public interface RobotStrategy {

    List<ConfigItem> getConfig();

    List<Order> loop(State state);
}
