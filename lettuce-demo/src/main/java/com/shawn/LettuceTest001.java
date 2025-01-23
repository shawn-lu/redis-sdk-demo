package com.shawn;

import io.lettuce.core.*;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.async.RedisStringAsyncCommands;
import io.lettuce.core.api.sync.RedisStringCommands;
import io.lettuce.core.cluster.ClusterClientOptions;
import io.lettuce.core.cluster.ClusterTopologyRefreshOptions;
import io.lettuce.core.cluster.RedisClusterClient;
import io.lettuce.core.cluster.api.StatefulRedisClusterConnection;
import io.lettuce.core.cluster.api.async.RedisAdvancedClusterAsyncCommands;
import io.lettuce.core.cluster.models.partitions.RedisClusterNode;
import io.lettuce.core.internal.HostAndPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class LettuceTest001 {

    static final Logger logger = LoggerFactory.getLogger(LettuceTest001.class);

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        logger.info("dd");
        String pwd = "OQ2wjzTINKHVmqLx45YGyRWgasCd1v78";

        RedisURI redisUri = RedisURI.builder()
                .withHost("10.228.225.166")
                .withPort(6379)
                .withPassword(pwd)
                .withTimeout(Duration.of(5, ChronoUnit.SECONDS))
                .build();
//        RedisClient client = RedisClient.create(redisUri);
        RedisClusterClient client = RedisClusterClient.create(redisUri);
        client.setOptions(ClusterClientOptions.builder()
                .nodeFilter(node -> !node.getFlags().contains(RedisClusterNode.NodeFlag.FAIL))
//                .protocolVersion(ProtocolVersion.RESP2)
                .topologyRefreshOptions(ClusterTopologyRefreshOptions.builder()
                        .enablePeriodicRefresh(Duration.ofSeconds(10000))
                        .enablePeriodicRefresh(true)
                        .enableAllAdaptiveRefreshTriggers()

                        .build())//拓扑刷新
                .disconnectedBehavior(ClientOptions.DisconnectedBehavior.REJECT_COMMANDS)
                .autoReconnect(true)
                .socketOptions(SocketOptions.builder().keepAlive(true).build()).validateClusterNodeMembership(false)// 取消校验集群节点的成员关系
                .build()
        );

        StatefulRedisClusterConnection<String, String> connection = client.connect();

//        RedisStringCommands<String,String> sync = connection.sync();
//        System.out.println(sync.set("sync_key","aaa"));
//        Object value = sync.get("sync_key");
//        System.out.println(value);
//        if(true){
//            return;
//        }
        connection.setReadFrom(ReadFrom.ANY);


        int i = 0;
        Random random = new Random();
        int min = 0; // 指定范围的最小值
        int max = 100; // 指定范围的最大值
        while(true){
            TimeUnit.MILLISECONDS.sleep(5000);
            System.out.println("start");
            long start = System.nanoTime();
//        RedisStringAsyncCommands<String, String> async = connection.async();
            RedisAdvancedClusterAsyncCommands<String,String> commands = connection.async();
            RedisFuture<String> f1 = commands.get("abc" + random.nextInt(max - min + 1));
//            RedisFuture<String> f2 = commands.get("ddd" + random.nextInt(max - min + 1));


//            RedisFuture<String> f2 = commands.get("abc2");
//            RedisFuture<String> f3 = commands.get("abc3");
////            RedisFuture<String> f2 = commands.get("ddd");
            commands.flushCommands();
//        RedisFuture<String> set = async.set("aaa", "value");
//        RedisFuture<String> get = async.get("aaa");

//        LettuceFutures.awaitAll(Duration.ofSeconds(5),set, get);


            System.out.println("cost:" + ((System.nanoTime() - start)/1000) + ",value=" +f1.get());
//        System.out.println(set.get());
//            System.out.println("cost:" + (System.currentTimeMillis() - start) + ",value=" +f1.get()+ ",value=" +f2.get()+ ",value=" +f3.get());
//            System.out.println(f2.get());
        }
    }
}
//watch io.lettuce.core.cluster.ClusterDistributionChannelWriter writeCommand  -n 3

//watch io.lettuce.core.cluster.ClusterDistributionChannelWriter writeCommand "{params[2],target}" -n 3
//watch io.lettuce.core.cluster.ClusterDistributionChannelWriter getWriterToUse "{params[0].channel}"  -n 3

//watch com.yonghui.recommend.recall.cache.processor.RedisPipelineProcessor load -n 3
//watch com.yonghui.recommend.recall.cache.processor.RedisPipelineProcessor load 'fields["command"]' -n 3
//watch com.yonghui.recommend.recall.cache.processor.RedisPipelineProcessor command
//watch io.lettuce.core.cluster.RedisClusterClient getTopologyRefreshSource -n 3