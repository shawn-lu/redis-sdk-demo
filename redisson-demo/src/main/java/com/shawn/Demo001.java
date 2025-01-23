package com.shawn;

import org.redisson.Redisson;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.redisson.client.codec.StringCodec;
import org.redisson.config.Config;
import org.redisson.config.ReadMode;

import java.sql.Time;
import java.util.concurrent.TimeUnit;


public class Demo001 {

    public static void main(String[] args) {
        String[] nodes = new String[]{
                "redis://10.228.198.194:6379"
        };
        String pwd = "LgJBQoPI2aqzNk09RpwXtA684mWiv31c";

        Config config = new Config();

        config
                .setCodec(new StringCodec())
                .useClusterServers()
//                .setReadMode(ReadMode.MASTER_SLAVE)
                .setReadMode(ReadMode.SLAVE)
                .addNodeAddress(nodes)
                .setConnectTimeout(20000)
                .setPassword(pwd);


        RedissonClient redissonClient = Redisson.create(config);
        while(true){
            try {
                RBucket rBucket = redissonClient.getBucket("key001");
//            rBucket.set("abc",30, TimeUnit.MINUTES);
                System.out.println(rBucket.get());
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
//        System.out.println(redissonClient.getLock("abcd").isLocked());



    }
}
