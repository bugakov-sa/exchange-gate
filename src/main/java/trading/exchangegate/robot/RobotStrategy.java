package trading.exchangegate.robot;

import java.util.List;

public interface RobotStrategy {

    List<ConfigItem> getConfig();

    List<Order> loop(State state);
}
