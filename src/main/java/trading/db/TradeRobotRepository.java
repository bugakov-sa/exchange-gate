package trading.db;

import org.springframework.data.repository.CrudRepository;
import trading.entity.TradeRobot;

import java.util.UUID;

public interface TradeRobotRepository extends CrudRepository<TradeRobot, UUID> {
}
