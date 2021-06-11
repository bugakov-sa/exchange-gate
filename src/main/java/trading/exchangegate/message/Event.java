package trading.exchangegate.message;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum Event {
    @JsonProperty("ping")
    PING,
    @JsonProperty("pong")
    PONG,
    @JsonProperty("heartbeat")
    HEARTBEAT,
    @JsonProperty("systemStatus")
    SYSTEM_STATUS,
    @JsonProperty("subscribe")
    SUBSCRIBE,
    @JsonProperty("unsubscribe")
    UNSUBSCRIBE,
    @JsonProperty("subscriptionStatus")
    SUBSCRIPTION_STATUS;
}
