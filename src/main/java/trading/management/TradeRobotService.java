package trading.management;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import trading.engine.TradeEngine;
import trading.engine.strategy.LogLastMinuteOhlcStrategy;
import trading.exchange.ExchangeManager;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.ArrayList;
import java.util.List;

@Component
public class TradeRobotService {

    @Autowired
    private ExchangeManager exchangeManager;
    @Value("${robot.loop.sleep.millis:5000}")
    private long loopSleepMillis;

    private List<TradeEngine> tradeEngines = new ArrayList<>();

    @PostConstruct
    public void startRobots() {
        TradeEngine tradeEngine = new TradeEngine(
                new LogLastMinuteOhlcStrategy("XBT/USD"),
                exchangeManager,
                loopSleepMillis
        );
        tradeEngines.add(tradeEngine);
        tradeEngines.forEach(TradeEngine::start);
    }

    @PreDestroy
    public  void stopRobots() {
        tradeEngines.forEach(TradeEngine::stop);
    }
}
