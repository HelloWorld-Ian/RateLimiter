package RateLimitStrategy;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RedisFunnelStrategy {
    private long qps;

    String strategyKey;
    String redisAddress;
}
