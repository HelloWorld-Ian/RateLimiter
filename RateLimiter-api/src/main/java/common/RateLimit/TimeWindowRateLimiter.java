package common.RateLimit;

import interfaces.RateLimiter;
import common.RateLimitStrategy.TimeWindowStrategy;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * use time window rate limit strategy
 */
public class TimeWindowRateLimiter implements RateLimiter {
    private final TimeWindow timeWindow=new TimeWindow();
    private final Lock lock = new ReentrantLock();

    public TimeWindowRateLimiter(TimeWindowStrategy strategy){
        timeWindow.active = 0;
        timeWindow.lastTick = System.currentTimeMillis();
        timeWindow.time = strategy.getTime();
        timeWindow.max = strategy.getMax();
    }

    @Override
    public boolean limit() {
        lock.lock();
        boolean pass=false;
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
        lock.unlock();
        return pass;
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
