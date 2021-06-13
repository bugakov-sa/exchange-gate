package trading.exchangegate.robot.entity;

import lombok.Data;

import javax.persistence.*;
import java.util.List;
import java.util.UUID;

@Entity(name = "robot")
@Data
public class Robot {

    @Id
    @GeneratedValue
    private UUID id;
    @Enumerated(EnumType.STRING)
    private Type type;
    @Column
    private String name;
    @Enumerated(EnumType.STRING)
    private Status status;
    @OneToMany(mappedBy = "robot",
            fetch = FetchType.EAGER,
            cascade = {CascadeType.ALL},
            orphanRemoval = true
    )
    private List<Param> params;

    public enum Type {
        LOG_LAST_OHLC,
        LOG_LAST_MINUTE_OHLC
    }

    public enum Status {
        OFF,
        STARTING,
        WORKING,
        STOPPING
    }
}
