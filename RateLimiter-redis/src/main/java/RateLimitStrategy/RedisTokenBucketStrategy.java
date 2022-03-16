package RateLimitStrategy;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RedisTokenBucketStrategy {
    long interval;
    long capacity;
    long quantum;

    String strategyKey;
    String redisAddress;
}
