package client;

import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;

public class Command {
    public Command(RedisClient c){
        s = c.connect();
    }
    StatefulRedisConnection<String, String> s;

    public RedisCommands<String, String> sync(){
        return s.sync();
    }
}
