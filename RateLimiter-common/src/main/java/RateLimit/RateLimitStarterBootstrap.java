package RateLimit;

import RateLimitStrategy.RateLimitStrategy;

public class RateLimitStarterBootstrap {
    private final RateLimitStarter starter=new RateLimitStarter();

    public RateLimitStarterBootstrap setRateLimitStrategy(RateLimitStrategy strategy){
        starter.setRateLimitStrategy(strategy);
        return this;
    }

    public RateLimitStarterBootstrap setRateLimiter(RateLimiter rateLimiter){
        starter.setRateLimiter(rateLimiter);
        return this;
    }

    public RateLimitStarter build(){
        return starter;
    }
}
