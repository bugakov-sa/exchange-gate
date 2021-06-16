package trading.engine;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import trading.db.TradeRobotRepository;
import trading.entity.Param;
import trading.entity.TradeRobot;
import trading.exchange.ExchangeManager;
import trading.thread.Worker;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Component
public class TradeEngineManager extends Worker {

    private final Map<UUID, TradeEngine> activeEngines = new HashMap<>();

    @Autowired
    private TradeRobotRepository tradeRobotRepository;
    @Autowired
    private ExchangeManager exchangeManager;
    @Value("${robot.loop.sleep.millis:5000}")
    private long loopSleepMillis;

    public TradeEngineManager() {
        super("trade-engine-manager");
    }

    @PostConstruct
    public void init() {
        createRobotExamples();
        start();
    }

    @PreDestroy
    public void close() {
        stop();
    }

    @Override
    protected long executeLoop() {
        Iterable<TradeRobot> robots = tradeRobotRepository.findAll();
        for(TradeRobot robot : robots) {
            UUID robotId = robot.getId();
            boolean engineStarted = activeEngines.containsKey(robotId);
            if(robot.isActive() && !engineStarted) {
                TradeStrategy strategy = TradeStrategyFactory.create(robot);
                TradeEngine engine = new TradeEngine(
                        robot.getName(),
                        strategy,
                        exchangeManager,
                        loopSleepMillis
                );
                engine.start();
                activeEngines.put(robotId, engine);
            }
            if(!robot.isActive() && engineStarted) {
                activeEngines.get(robotId).stop();
                activeEngines.remove(robotId);
            }
        }
        return loopSleepMillis;
    }

    @Override
    protected void beforeFinish() {
        activeEngines.values().forEach(Worker::stop);
        activeEngines.clear();
    }

    private void createRobotExamples() {
        boolean anyRobotExists = tradeRobotRepository.findAll().iterator().hasNext();
        if(!anyRobotExists) {
            TradeRobot robot = new TradeRobot();
            robot.setName("XBT/USD-1min");
            robot.setActive(true);
            robot.setStrategy(TradeRobot.Strategy.LOG_LAST_MINUTE_OHLC);
            robot.setParams(new ArrayList<>());
            Param param = new Param();
            param.setName("pair");
            param.setType(Param.Type.STRING);
            param.setValue("XBT/USD");
            param.setRobot(robot);
            robot.getParams().add(param);
            tradeRobotRepository.save(robot);
        }
    }
}
