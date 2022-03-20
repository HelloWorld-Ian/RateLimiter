package common.RateLimitStrategy;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TimeWindowStrategy {
    private long time;
    private long max;
}
