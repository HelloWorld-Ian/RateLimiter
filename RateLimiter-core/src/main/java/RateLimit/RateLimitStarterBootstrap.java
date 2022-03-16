package RateLimit;

import config.RateLimitConfig;
import interfaces.RateLimiter;

public class RateLimitStarterBootstrap {
    private final RateLimitStarter starter=new RateLimitStarter();

    public RateLimitStarterBootstrap setRateLimitStrategy(RateLimitConfig strategy){
        starter.setRateLimitConfig(strategy);
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
