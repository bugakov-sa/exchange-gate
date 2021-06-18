package trading.entity;

import lombok.Data;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Data
public class StrategyDescriptor {
    private final TradeRobot.Strategy strategy;
    private final List<ParamDescriptor> params;

    public StrategyDescriptor(TradeRobot.Strategy strategy, ParamDescriptor ... params) {
        this.strategy = strategy;
        this.params = Collections.unmodifiableList(Arrays.asList(params));
    }
}
