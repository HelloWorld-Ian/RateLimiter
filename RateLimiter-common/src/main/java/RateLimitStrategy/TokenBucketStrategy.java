package RateLimitStrategy;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TokenBucketStrategy {
    long tokens;
    long interval;
    long lastTick;
    long capacity;
    long quantum;
}
