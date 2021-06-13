package trading.exchangegate.robot;

import org.springframework.data.repository.CrudRepository;
import trading.exchangegate.robot.entity.Robot;

import java.util.UUID;

public interface RobotRepository extends CrudRepository<Robot, UUID> {
}
