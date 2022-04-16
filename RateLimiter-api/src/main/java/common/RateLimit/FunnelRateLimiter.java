package common.RateLimit;

import common.RateLimitStrategy.FunnelStrategy;
import interfaces.RateLimiter;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class FunnelRateLimiter implements RateLimiter {
    private final Funnel funnel = new Funnel();
    Lock lock = new ReentrantLock();

    public FunnelRateLimiter(FunnelStrategy strategy) {
        // qps有效值 < 1000
        funnel.interval = 1000 / strategy.getQps();
        funnel.lastTick = System.currentTimeMillis();
    }

    @Override
    public boolean limit() {
        try{
            lock.lock();
            long cur = System.currentTimeMillis();
            if (cur - funnel.lastTick < funnel.interval) {
                return false;
            } else {
                funnel.lastTick = cur;
                return true;
            }
        } finally {
            lock.unlock();
        }
    }

    public static class Funnel {
        private long lastTick;
        private long interval;

        private Funnel() {};
    }
}
