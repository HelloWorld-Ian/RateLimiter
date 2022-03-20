package RateLimit;

import RateLimitStrategy.RedisTokenBucketStrategy;
import config.RateLimitConfig;
import org.junit.Before;
import org.junit.Test;

public class RedisTokenBucketRateLimiterTest {
    public static RateLimitStarter starter;

    @Before
    public void beforeTest(){
        RateLimitConfig strategy = new RateLimitConfig();
        strategy.setLimitRetry(false);
        strategy.setTimeout(2000);
        strategy.setRetryPeriod(500);
        strategy.setLogOn(true);

        RedisTokenBucketStrategy redisTokenBucketStrategy = new RedisTokenBucketStrategy();
        redisTokenBucketStrategy.setRedisAddress("redis://127.0.0.1:6379");
        redisTokenBucketStrategy.setStrategyKey("test");
        redisTokenBucketStrategy.setInterval(1000);
        redisTokenBucketStrategy.setQuantum(20);
        redisTokenBucketStrategy.setCapacity(40);
        RedisTokenBucketRateLimiter limiter = new RedisTokenBucketRateLimiter(redisTokenBucketStrategy);

        starter = new RateLimitStarterBootstrap()
                .setRateLimitStrategy(strategy)
                .setRateLimiter(limiter)
                .build();
    }

    @Test
    public void tokenBucketRateLimitTest() throws InterruptedException {
        Runnable runnable = () -> {
            try {
                while (true){
                    starter.doRateLimit();
                    Thread.sleep(1000);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        };

        for(int i=0;i<40;i++){
            new Thread(runnable).start();
        }

        Thread.sleep(10000);
    }
}