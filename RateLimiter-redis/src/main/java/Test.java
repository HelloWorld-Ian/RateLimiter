import RateLimit.RateLimitStarter;
import RateLimit.RateLimitStarterBootstrap;
import RateLimit.RedisTokenBucketRateLimiter;
import RateLimitStrategy.RedisTokenBucketStrategy;
import config.RateLimitConfig;
import log.RateLimitLogger;

public class Test {
    public static RateLimitStarter starter;
    public static final Object lock = new Object();
    public static Integer pass = 0;
    public static long start = System.currentTimeMillis();
    public static void main(String[] args) {
        RateLimitConfig strategy = new RateLimitConfig();
        strategy.setLimitRetry(false);
        strategy.setTimeout(2000);
        strategy.setRetryPeriod(500);
        strategy.setLogOn(true);

        RedisTokenBucketStrategy tokenBucketStrategy = new RedisTokenBucketStrategy();
        tokenBucketStrategy.setCapacity(40);
        tokenBucketStrategy.setQuantum(20);
        tokenBucketStrategy.setInterval(1000);
        tokenBucketStrategy.setStrategyKey("rateLimit");
        tokenBucketStrategy.setRedisAddress("redis://127.0.0.1:6379");

        RedisTokenBucketRateLimiter limiter = new RedisTokenBucketRateLimiter(tokenBucketStrategy);

        starter = new RateLimitStarterBootstrap()
                .setRateLimitStrategy(strategy)
                .setRateLimiter(limiter)
                .build();

        Runnable runnable = () -> {
            try {
                while (true){
                    if(starter.doRateLimit()){
                        RateLimitLogger.info("pass success",true);
                        synchronized (lock){
                            pass++;
                        }
                    }
                    Thread.sleep(1000);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        };

        for(int i=0;i<40;i++){
            new Thread(runnable).start();
        }
    }
}
