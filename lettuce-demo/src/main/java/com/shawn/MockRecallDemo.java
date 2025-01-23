package com.shawn;

import com.google.common.collect.Lists;
import io.lettuce.core.ClientOptions;
import io.lettuce.core.RedisFuture;
import io.lettuce.core.RedisURI;
import io.lettuce.core.SocketOptions;
import io.lettuce.core.cluster.ClusterClientOptions;
import io.lettuce.core.cluster.ClusterTopologyRefreshOptions;
import io.lettuce.core.cluster.RedisClusterClient;
import io.lettuce.core.cluster.api.StatefulRedisClusterConnection;
import io.lettuce.core.cluster.api.async.RedisAdvancedClusterAsyncCommands;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;


public class MockRecallDemo {
    private static Logger log = LoggerFactory.getLogger(MockRecallDemo.class);

    private StatefulRedisClusterConnection statefulRedisClusterConnection;

    private Command command;


    MockRecallDemo(StatefulRedisClusterConnection statefulRedisClusterConnection,
                   Command command){
        this.statefulRedisClusterConnection = statefulRedisClusterConnection;
        this.command = command;
    }



    public static void main(String[] args) throws Exception{
        try (StatefulRedisClusterConnection<String,String> statefulRedisClusterConnection = connection()) {

            MockRecallDemo demo = new MockRecallDemo(connection(),
                    (key, commands) -> commands.get(key)
                    );
            while(true){
                TimeUnit.MILLISECONDS.sleep(2000);

                List<Pair<Boolean, Object>> pairs = demo.load(Lists.newArrayList("abc", "ddd"), 1000);
                System.out.println(pairs);
            }

        }


    }


    public List<Pair<Boolean, Object>> load(List<String> keys, long milliseconds) {
        long start = System.currentTimeMillis();
        if (null == keys || keys.size() == 0) {
            return Collections.emptyList();
        }
        List<RedisFuture> futures = refreshCommands(keys); // 刷新命令
        long refreshCommands = System.currentTimeMillis();
        List<Pair<Boolean, Object>> list = blockGet(futures, milliseconds);// 阻塞等待获取数据
        long get = System.currentTimeMillis();
        if (get - start > 300) {
            log.warn("redis pipeline load timeout, refresh cost {}, load cost {}", refreshCommands - start, get - refreshCommands);
        }
        return list;
    }

    protected List<RedisFuture> refreshCommands(List<String> keys) {
        RedisAdvancedClusterAsyncCommands commands = statefulRedisClusterConnection.async();
        List<RedisFuture> futures = Lists.newArrayListWithCapacity(keys.size() + 1);
        for (String key : keys) {
            futures.add(buildCommand(key, commands));
        }
        if (futures.size() > 0) commands.flushCommands();
        return futures;
    }

    protected RedisFuture buildCommand(String key, RedisAdvancedClusterAsyncCommands commands) {
        return command.apply(key, commands);
    }



    private List<Pair<Boolean, Object>> blockGet(List<RedisFuture> futures, long milliseconds) {
        List<Pair<Boolean, Object>> result = Lists.newArrayListWithCapacity(futures.size());
        try {

            long deadline = System.currentTimeMillis() + milliseconds;

            boolean is_timeout = false;
            long countdown = 0;
            for (int i = 0; i < futures.size(); i++) {
                try {
                    Future f = futures.get(i);
                    if (countdown > 0 || !is_timeout) {
                        countdown = Math.max(deadline - System.currentTimeMillis(), 0);
                    }
                    if (countdown == 0) {
                        if (f.isDone()) {
                            result.add(Pair.of(Boolean.TRUE, f.get()));
                        } else {
                            result.add(Pair.of(Boolean.FALSE, null));
                        }
                    } else {

                        result.add(Pair.of(Boolean.TRUE, f.get(countdown, TimeUnit.MILLISECONDS)));
                    }
                } catch (Exception e) {
                    if (!is_timeout) {
                        log.error("pipeline get timeout，timeout is {},countdown is {}", milliseconds, countdown, e);
                        is_timeout = true;
                    }
                    result.add(Pair.of(Boolean.FALSE, null));
                }
            }

        } catch (Exception e) {
            log.error("pipeline get timeout", e);
        }

        return result;
    }




    @FunctionalInterface
    public interface Command {
        RedisFuture<String> apply(String key, RedisAdvancedClusterAsyncCommands<String,String> commands);
    }


    private static StatefulRedisClusterConnection<String,String> connection(){
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


//                .protocolVersion(ProtocolVersion.RESP2)
                        .topologyRefreshOptions(ClusterTopologyRefreshOptions.builder()
                                .enablePeriodicRefresh()
                                .enableAllAdaptiveRefreshTriggers()
                                .build())//拓扑刷新
                        .disconnectedBehavior(ClientOptions.DisconnectedBehavior.REJECT_COMMANDS)
                        .autoReconnect(true)
                        .socketOptions(SocketOptions.builder().keepAlive(true).build()).validateClusterNodeMembership(false)// 取消校验集群节点的成员关系
                        .build()
        );

        StatefulRedisClusterConnection<String, String> connection = client.connect();
        return connection;
    }
}
