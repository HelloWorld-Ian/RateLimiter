package RateLimit;

import RateLimitStrategy.RateLimitStrategy;
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
    public boolean doRateLimit() throws InterruptedException {
        long waitTime= rateLimitStrategy.getTimeout();
        long timeout=waitTime+System.currentTimeMillis();
        boolean limitRetry= rateLimitStrategy.isLimitRetry();
        long retryPeriod=rateLimitStrategy.getRetryPeriod();
        while (!rateLimiter.limit()){
            if(!limitRetry){
                return false;
            }
            Thread.sleep(retryPeriod);
            long curTime=System.currentTimeMillis();
            if(curTime>timeout){
                return false;
            }
        }
        return true;
    }
}
