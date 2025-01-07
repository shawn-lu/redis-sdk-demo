package com.shawn;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisCluster;

import java.util.concurrent.TimeUnit;

public class JedisTest001 {

    public static void main(String[] args) throws Exception{
        HostAndPort hostAndPort = new HostAndPort("10.228.225.146", 6379);
        String pwd = "Nd3gBgTHTtHY2NVvDSglv7TBblfAY5nj";
        JedisCluster jedisCluster = new JedisCluster(hostAndPort,3000,3000,5,pwd,new GenericObjectPoolConfig());
        while(true){
            try{
                System.out.println(jedisCluster.get("abc"));
//                System.out.println(jedisCluster.hget("hset-key","a"));

//                System.out.println(jedisCluster.setnx("lock-key","1"));

                TimeUnit.SECONDS.sleep(2);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
}
//watch redis.clients.jedis.JedisSlotBasedConnectionHandler getConnectionFromSlot returnObj.client.socket -n 3
//watch redis.clients.jedis.JedisClusterInfoCache

//10.228.83.175:6379@16379 10.228.17.255:6379@16379 10.228.127.161:6379@16379