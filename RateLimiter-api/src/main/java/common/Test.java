package common;


import RateLimit.RateLimitStarter;
import RateLimit.RateLimitStarterBootstrap;
import interfaces.RateLimiter;
import config.RateLimitConfig;
import common.RateLimit.TokenBucketRateLimiter;
import common.RateLimitStrategy.TokenBucketStrategy;
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
        strategy.setLogOn(false);

        TokenBucketStrategy tokenBucketStrategy = new TokenBucketStrategy();
        tokenBucketStrategy.setCapacity(40);
        tokenBucketStrategy.setInterval(1000);
        tokenBucketStrategy.setQuantum(20);
        RateLimiter rateLimiter = new TokenBucketRateLimiter(tokenBucketStrategy);

        starter = new RateLimitStarterBootstrap()
                .setRateLimitStrategy(strategy)
                .setRateLimiter(rateLimiter)
                .build();

        Runnable runnable = () -> {
            try {
                while (true){
                    if(starter.doRateLimit()){
                        RateLimitLogger.info("pass success",false);
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

        for(int i=0;i<30;i++){
            new Thread(runnable).start();
        }

        new Thread(() -> {
            while (true){
                try {
                    Thread.sleep(1000);
                    synchronized (lock){
                        System.out.println("QPS========================"+(double)(pass)*1000/(System.currentTimeMillis()-start));
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
