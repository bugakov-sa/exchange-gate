package trading.exchangegate.robot;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import trading.exchangegate.gate.ExchangeManager;

import javax.annotation.PostConstruct;

@Component
public class RobotService {

    @Autowired
    private ExchangeManager exchangeManager;

    @PostConstruct
    public void startRobots() {
        LoggingPriceRobot robot = new LoggingPriceRobot(exchangeManager, "XBT/USD");
        robot.start();
    }
}
