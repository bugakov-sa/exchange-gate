package trading.management;

import org.springframework.data.repository.CrudRepository;
import trading.management.entity.TradeRobot;

import java.util.UUID;

public interface TradeRobotRepository extends CrudRepository<TradeRobot, UUID> {
}
