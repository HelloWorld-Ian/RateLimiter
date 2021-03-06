package common.RateLimit;

import interfaces.RateLimiter;
import common.RateLimitStrategy.TokenBucketStrategy;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * use token bucket rate limit strategy
 */
public class TokenBucketRateLimiter implements RateLimiter {
    private final Bucket bucket=new Bucket();
    private final Lock lock = new ReentrantLock();

    public TokenBucketRateLimiter(TokenBucketStrategy strategy){
        bucket.interval = strategy.getInterval();
        bucket.capacity = strategy.getCapacity();
        bucket.quantum = strategy.getQuantum();

        bucket.lastTick = System.currentTimeMillis();
        bucket.tokens = bucket.capacity;
    }

    @Override
    public boolean limit(){
        try {
            lock.lock();
            long increment=increment(bucket);
            long curToken= bucket.tokens;

            if (curToken>0||increment>0){
                if(increment>0){
                    bucket.tokens=Math.min(bucket.capacity, curToken+increment)-1;
                    bucket.lastTick=System.currentTimeMillis();
                }else{
                    bucket.tokens-=1;
                }
                return true;
            }
            return false;
        } finally {
            lock.unlock();
        }
    }

    /**
     * the total tokens add to bucket during the last tick to current
     */
    private long increment(Bucket bucket){
        long cur = System.currentTimeMillis();
        long round = (cur - bucket.lastTick)/ bucket.interval;
        return round* bucket.quantum;
    }

    public static class Bucket{
        private long tokens;
        private long interval;
        private long lastTick;
        private long capacity;
        private long quantum;
        private Bucket(){}
    }
}
