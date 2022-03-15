package RateLimitStrategy;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TokenBucketStrategy {
    long interval;
    long capacity;
    long quantum;
}
