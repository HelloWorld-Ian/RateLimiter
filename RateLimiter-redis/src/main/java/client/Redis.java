package client;

import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;

public class Redis {
    private final RedisClient client;

    public Redis(String address){
        client = RedisClient.create(address);
    }

    public Command redisConnect(){
        return new Command(client);
    }
}
