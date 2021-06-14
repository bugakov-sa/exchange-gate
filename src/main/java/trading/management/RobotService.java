package trading.management;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import trading.exchange.ExchangeManager;
import trading.robot.Robot;
import trading.robot.strategy.LogLastMinuteOhlcStrategy;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.ArrayList;
import java.util.List;

@Component
public class RobotService {

    @Autowired
    private ExchangeManager exchangeManager;

    private List<Robot> robots = new ArrayList<>();

    @PostConstruct
    public void startRobots() {
        Robot robot = new Robot(new LogLastMinuteOhlcStrategy("XBT/USD"), exchangeManager);
        robots.add(robot);
        robots.forEach(Robot::start);
    }

    @PreDestroy
    public  void stopRobots() {
        robots.forEach(Robot::stop);
    }
}