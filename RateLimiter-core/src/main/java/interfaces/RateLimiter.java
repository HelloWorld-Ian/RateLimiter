package interfaces;

public interface RateLimiter {
    /**
     * do rate limit accord to different limit strategy
     */
    boolean limit();
}
