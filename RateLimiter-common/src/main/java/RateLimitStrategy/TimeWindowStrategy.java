package RateLimitStrategy;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TimeWindowStrategy {
    private long active;
    private long lastTick;
    private long time;
    private long max;
}
