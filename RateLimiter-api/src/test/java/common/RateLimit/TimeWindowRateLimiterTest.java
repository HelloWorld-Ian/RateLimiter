package common.RateLimit;

import RateLimit.RateLimitStarter;
import RateLimit.RateLimitStarterBootstrap;
import common.RateLimitStrategy.TimeWindowStrategy;
import config.RateLimitConfig;
import interfaces.RateLimiter;
import org.junit.Before;
import org.junit.Test;

public class TimeWindowRateLimiterTest {

    public static RateLimitStarter starter;

    @Before
    public void beforeTest(){
        RateLimitConfig strategy = new RateLimitConfig();
        strategy.setLimitRetry(false);
        strategy.setTimeout(2000);
        strategy.setRetryPeriod(500);
        strategy.setLogOn(true);

        TimeWindowStrategy timeWindowStrategy = new TimeWindowStrategy();
        timeWindowStrategy.setTime(1000);
        timeWindowStrategy.setMax(20);
        RateLimiter rateLimiter = new TimeWindowRateLimiter(timeWindowStrategy);

        starter = new RateLimitStarterBootstrap()
                .setRateLimitStrategy(strategy)
                .setRateLimiter(rateLimiter)
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