package RateLimitStrategy;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RateLimitStrategy {
    private long timeout;
    private boolean limitRetry;
    private long retryPeriod;
    boolean logOn = true;
}
