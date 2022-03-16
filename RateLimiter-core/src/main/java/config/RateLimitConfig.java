package config;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RateLimitConfig {
    private long timeout;
    private boolean limitRetry;
    private long retryPeriod;
    boolean logOn = true;
}
