package trading.entity;

import lombok.Data;

import javax.persistence.*;
import java.util.UUID;

@Data
@Entity(name = "param")
public class Param {

    @Id
    @GeneratedValue
    private UUID id;
    @Column
    private String name;
    @Enumerated(EnumType.STRING)
    private Type type;
    @Column
    private String value;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "robot_id")
    private TradeRobot robot;

    public enum Type {
        INT,
        DOUBLE,
        BOOLEAN,
        STRING
    }
}
