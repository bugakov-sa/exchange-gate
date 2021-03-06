package trading.entity;

import lombok.Data;

import javax.persistence.*;
import java.util.List;
import java.util.UUID;

@Entity(name = "robot")
@Data
public class TradeRobot {

    @Id
    @GeneratedValue
    private UUID id;
    @Enumerated(EnumType.STRING)
    private Strategy strategy;
    @Column
    private String name;
    @Column
    private boolean active;
    @OneToMany(mappedBy = "robot",
            fetch = FetchType.EAGER,
            cascade = {CascadeType.ALL},
            orphanRemoval = true
    )
    private List<Param> params;

    public enum Strategy {
        LOG_LAST_OHLC,
        LOG_LAST_MINUTE_OHLC
    }
}
