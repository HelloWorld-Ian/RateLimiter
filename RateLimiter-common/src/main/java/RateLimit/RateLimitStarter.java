package RateLimit;

import RateLimitStrategy.RateLimitStrategy;
import log.RateLimitLogger;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@SuppressWarnings("all")
public class RateLimitStarter {

    RateLimitStrategy rateLimitStrategy;

    RateLimiter rateLimiter;

    protected RateLimitStarter(){}

    /**
     * the external method to do rate limit
     */
    public boolean doRateLimit() throws Exception {
        long waitTime= rateLimitStrategy.getTimeout();
        long timeout=waitTime+System.currentTimeMillis();
        boolean limitRetry= rateLimitStrategy.isLimitRetry();
        long retryPeriod=rateLimitStrategy.getRetryPeriod();
        while (!rateLimiter.limit()){
            if(!limitRetry){
                RateLimitLogger.error("retry policy is forbidden, request is rejected",rateLimitStrategy.isLogOn());
                return false;
            }
            Thread.sleep(retryPeriod);
            long curTime=System.currentTimeMillis();
            if(curTime>timeout){
                RateLimitLogger.error("time out, request is rejected",rateLimitStrategy.isLogOn());
                return false;
            }
        }
        return true;
    }
}
