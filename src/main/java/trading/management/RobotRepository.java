package trading.management;

import org.springframework.data.repository.CrudRepository;
import trading.management.entity.Robot;

import java.util.UUID;

public interface RobotRepository extends CrudRepository<Robot, UUID> {
}
