package common.RateLimit;

import interfaces.RateLimiter;
import common.RateLimitStrategy.TimeWindowStrategy;

/**
 * use time window rate limit strategy
 */
public class TimeWindowRateLimiter implements RateLimiter {
    private final TimeWindow timeWindow=new TimeWindow();

    public TimeWindowRateLimiter(TimeWindowStrategy strategy){
        timeWindow.active=strategy.getActive();
        timeWindow.lastTick= strategy.getLastTick();
        timeWindow.time=strategy.getTime();
        timeWindow.max=strategy.getMax();
    }

    @Override
    public boolean limit() {
        boolean pass=false;
        synchronized (timeWindow){
            long cur=System.currentTimeMillis();
            if (cur - timeWindow.lastTick >= timeWindow.time){
                timeWindow.active=1;
                timeWindow.lastTick=System.currentTimeMillis();
                pass=true;
            }else{
                if (timeWindow.active<timeWindow.max){
                    timeWindow.active+=1;
                    pass=true;
                }
            }
            return pass;
        }
    }

    /**
     * time window
     */
    public static class TimeWindow{
        private long active;
        private long lastTick;
        private long time;
        private long max;
        private TimeWindow(){}
    }
}
