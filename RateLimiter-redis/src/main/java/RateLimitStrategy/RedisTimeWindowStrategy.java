package RateLimitStrategy;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RedisTimeWindowStrategy {
    long time;
    long max;

    String strategyKey;
    String redisAddress;
}
