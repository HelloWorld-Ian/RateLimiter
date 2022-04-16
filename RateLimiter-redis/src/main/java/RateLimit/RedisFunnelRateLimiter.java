package RateLimit;

import RateLimitStrategy.RedisFunnelStrategy;
import client.Command;
import client.Redis;
import interfaces.RateLimiter;
import io.lettuce.core.api.sync.RedisCommands;

public class RedisFunnelRateLimiter implements RateLimiter {
    private final Funnel funnel = new Funnel();
    private final Redis redis;

    public RedisFunnelRateLimiter(RedisFunnelStrategy strategy) {
        redis = new Redis(strategy.getRedisAddress());
        funnel.interval = 1000/strategy.getQps();
        funnel.strategyKey = strategy.getStrategyKey();
    }

    @Override
    public boolean limit() {
        Command command = redis.redisConnect();
        RedisCommands<String,String> r = command.sync();
        r.watch(funnel.strategyKey);
        boolean cas = false;
        boolean pass = false;

        while (!cas) {
            String lastTickStr = r.get(funnel.strategyKey);
            r.multi();
            long cur = System.currentTimeMillis();
            if (lastTickStr == null) {
                r.setex(funnel.strategyKey, funnel.interval,Long.toString(cur));
                pass = true;
            } else {
                long lastTick = Long.parseLong(lastTickStr);
                if (cur - lastTick >= funnel.interval ) {
                    r.setex(funnel.strategyKey, funnel.interval,Long.toString(cur));
                    pass = true;
                }
            }
            if (r.exec() != null) {
                cas = true;
            } else {
                pass = false;
            }
        }
        return pass;
    }

    private static class Funnel {
        private long interval;
        String strategyKey;
        private Funnel(){};
    }
}
