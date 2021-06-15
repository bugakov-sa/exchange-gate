package trading.engine;

import trading.engine.strategy.LogLastMinuteOhlcStrategy;
import trading.engine.strategy.LogLastOhlcStrategy;
import trading.entity.Param;
import trading.entity.TradeRobot;

public class TradeStrategyFactory {

    private static class Params {
        private final TradeRobot robot;

        Params(TradeRobot robot) {
            this.robot = robot;
        }

        private Param getParam(String name) {
            return robot.getParams()
                    .stream()
                    .filter(p -> p.getName().equals(name))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException(
                            "Not found param " + name + " for " + robot)
                    );
        }

        private void checkParamType(String name, Param.Type expectedType) {
            Param.Type actualType = getParam(name).getType();
            if (actualType != expectedType) {
                String message = String.format(
                        "Unexpected param type. Actual type %s. Expected type %s. Robot %s",
                        actualType, expectedType, robot
                );
                throw new RuntimeException(message);
            }
        }

        String getString(String name) {
            checkParamType(name, Param.Type.STRING);
            return getParam(name).getValue();
        }
    }

    public static TradeStrategy create(TradeRobot robot) {
        Params params = new Params(robot);
        switch (robot.getStrategy()) {
            case LOG_LAST_OHLC:
                return new LogLastOhlcStrategy(
                        params.getString("pair")
                );
            case LOG_LAST_MINUTE_OHLC:
                return new LogLastMinuteOhlcStrategy(
                        params.getString("pair")
                );
            default:
                throw new IllegalArgumentException("Unknown strategy " + robot.getStrategy());
        }
    }
}
