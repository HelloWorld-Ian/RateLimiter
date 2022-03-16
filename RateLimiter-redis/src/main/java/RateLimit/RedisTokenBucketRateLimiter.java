package RateLimit;

import RateLimitStrategy.RedisTokenBucketStrategy;
import client.Command;
import client.Redis;
import interfaces.RateLimiter;
import io.lettuce.core.TransactionResult;
import io.lettuce.core.api.sync.RedisCommands;

/**
 * use token bucket rate limit strategy, and use redis to store the key
 */
public class RedisTokenBucketRateLimiter implements RateLimiter {

    private final RedisTokenBucketStrategy strategy;
    private final Redis redis;
    private final Bucket bucket = new Bucket();

    public RedisTokenBucketRateLimiter(RedisTokenBucketStrategy strategy){
        this.strategy = strategy;

        bucket.capacity = strategy.getCapacity();
        bucket.interval = strategy.getInterval();
        bucket.quantum = strategy.getQuantum();

        redis = new Redis(strategy.getRedisAddress());
    }

    @Override
    public synchronized boolean limit() {
            String strategyKey = strategy.getStrategyKey();
            String watchKey = key(strategyKey,Type.WATCH_KEY);
            String tokenKey = key(strategyKey,Type.TOKENS);
            String lastTickKey = key(strategyKey,Type.LAST_TICK);

            Command c = redis.redisConnect();
            RedisCommands<String,String> r = c.sync();

            r.watch(watchKey);

            boolean cas = false;

            boolean pass = false;
            while (!cas){

                String tokenVal = r.hget(watchKey, tokenKey);
                String lastTickVal = r.hget(watchKey,lastTickKey);

                r.multi();

                long tokens;
                long lastTick;
                long addNum;

                if (tokenVal == null || lastTickVal == null){
                    tokens = bucket.capacity;
                    addNum = 0;
                    r.hset(watchKey,lastTickKey,Long.toString(System.currentTimeMillis()));
                }else{
                    tokens = Long.parseLong(tokenVal);
                    lastTick = Long.parseLong(lastTickVal);
                    addNum = increment(lastTick, bucket.quantum, bucket.interval);
                }

                if(tokens > 0 || addNum > 0){
                    if (addNum > 0){
                        r.hset(watchKey,tokenKey,Long.toString(Math.min(bucket.capacity, tokens+addNum)-1));
                        r.hset(watchKey,lastTickKey,Long.toString(System.currentTimeMillis()));
                    }else {
                        r.hset(watchKey,tokenKey,Long.toString(tokens-1));
                    }
                    pass = true;
                }

                TransactionResult ret = r.exec();
                if(ret!=null){
                    cas = true;
                }else{
                    pass = false;
                }
            }

            return pass;

    }

    /**
     * the total tokens add to bucket during the last tick to current
     */
    private long increment(long lastTick,long quantum,long interval){
        long cur = System.currentTimeMillis();
        long round = (cur - lastTick)/ interval;
        return round * quantum;
    }

    /**
     * assemble the redis key
     */
    public String key(String key,Type type){
        return String.format("rateLimit_%s_%s",key,type.key);
    }

    /**
     * the key type
     */
    public enum Type{
        WATCH_KEY("watchKey"),
        LAST_TICK("lastTick"),
        TOKENS("quantum");

        String key;

        Type(String key){
            this.key = key;
        }
    }

    /**
     * the bucket which stores the token
     */
    public static class Bucket{
        private long interval;
        private long capacity;
        private long quantum;

        private Bucket(){}
    }

}
