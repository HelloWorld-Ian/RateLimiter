package RateLimit;

import config.RateLimitConfig;
import interfaces.RateLimiter;
import log.RateLimitLogger;
import lombok.Getter;
import lombok.Setter;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

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
        long waitTime = rateLimitConfig.getTimeout();
        long timeout = waitTime+System.currentTimeMillis();
        boolean limitRetry = rateLimitConfig.isLimitRetry();
        long retryPeriod = rateLimitConfig.getRetryPeriod();
        boolean pass = false;
        while (!pass){
            if (rateLimiter.limit()) {
                pass = true;
            } else if (!limitRetry) {
                RateLimitLogger.error("retry policy is forbidden, request is rejected", rateLimitConfig.isLogOn());
                break;
            } else {
                long curTime = System.currentTimeMillis();
                if(curTime > timeout){
                    RateLimitLogger.error("time out, request is rejected", rateLimitConfig.isLogOn());
                    break;
                }
            }
        }
        if (pass){
            RateLimitLogger.info("pass success", rateLimitConfig.isLogOn());
        }
        return pass;
    }
}
