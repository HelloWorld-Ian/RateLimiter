package log;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RateLimitLogger {

    public static void info(String msg,boolean on){
        if(on){
            log.info(msg);
        }
    }

    public static void error(String msg,boolean on){
        if(on){
            log.error(msg);
        }
    }

}
