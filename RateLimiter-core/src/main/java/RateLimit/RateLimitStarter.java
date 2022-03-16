package RateLimit;

import config.RateLimitConfig;
import interfaces.RateLimiter;
import log.RateLimitLogger;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@SuppressWarnings("all")
public class RateLimitStarter {

    RateLimitConfig rateLimitConfig;

    RateLimiter rateLimiter;

    protected RateLimitStarter(){}

    /**
     * the external method to do rate limit
     */
    public boolean doRateLimit() throws Exception {
        long waitTime= rateLimitConfig.getTimeout();
        long timeout=waitTime+System.currentTimeMillis();
        boolean limitRetry= rateLimitConfig.isLimitRetry();
        long retryPeriod= rateLimitConfig.getRetryPeriod();
        while (!rateLimiter.limit()){
            if(!limitRetry){
                RateLimitLogger.error("retry policy is forbidden, request is rejected", rateLimitConfig.isLogOn());
                return false;
            }
            Thread.sleep(retryPeriod);
            long curTime=System.currentTimeMillis();
            if(curTime>timeout){
                RateLimitLogger.error("time out, request is rejected", rateLimitConfig.isLogOn());
                return false;
            }
        }
        return true;
    }
}
