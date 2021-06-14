package trading.robot;

import trading.message.Message;

import java.util.List;
import java.util.Queue;

public interface RobotStrategy {

    List<ConfigItem> getConfig();

    boolean loop(State state, Queue<Message> messages);
}
