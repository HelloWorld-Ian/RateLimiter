package RateLimit;

import RateLimitStrategy.RedisTimeWindowStrategy;
import client.Command;
import client.Redis;
import interfaces.RateLimiter;
import io.lettuce.core.api.sync.RedisCommands;

public class RedisTimeWindowRateLimiter implements RateLimiter {
    private final Redis redis;
    private final TimeWindow timeWindow = new TimeWindow();

    public RedisTimeWindowRateLimiter(RedisTimeWindowStrategy strategy) {
        redis = new Redis(strategy.getRedisAddress());
        timeWindow.max = strategy.getMax();
        timeWindow.time = strategy.getTime();
        timeWindow.strategyKey = strategy.getStrategyKey();
    }

    @Override
    public boolean limit() {
        Command command = redis.redisConnect();
        RedisCommands<String, String> r = command.sync();
        long cur = r.incr(timeWindow.strategyKey);
        if (cur == 1) {
            r.expire(timeWindow.strategyKey, timeWindow.time);
        }
        return cur <= timeWindow.max;
    }

    /**
     * time window
     */
    public static class TimeWindow{
        private long time;
        private long max;
        private String strategyKey;
        private TimeWindow(){}
    }
}
