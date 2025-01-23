//package com.shawn.configuration;
//
//import io.lettuce.core.ReadFrom;
//import io.lettuce.core.TimeoutOptions;
//import io.lettuce.core.cluster.ClusterClientOptions;
//import io.lettuce.core.cluster.ClusterTopologyRefreshOptions;
//import org.springframework.boot.autoconfigure.data.redis.LettuceClientConfigurationBuilderCustomizer;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//import java.time.Duration;
//
///**
// * @Author: linshanting
// * @Date: 2024/11/26
// * @Description: Redis连接客户端Lettuce配置类
// */
//@Configuration
//public class RedisLettuceConfig {
//
//
//    @Bean
//    public LettuceClientConfigurationBuilderCustomizer lettuceClientConfigurationBuilderCustomizer() {
//
//        return clientConfigurationBuilder -> {
//            // 配置用于开启自适应刷新和定时刷新。如自适应刷新不开启，Redis集群变更时将会导致连接异常
//            ClusterTopologyRefreshOptions clusterTopologyRefreshOptions = ClusterTopologyRefreshOptions.builder()
//                    //  开启自适应刷新
//                    .enableAllAdaptiveRefreshTriggers()
//                    .refreshPeriod(Duration.ofMinutes(1))
//                    .enablePeriodicRefresh()
//                    .build();
//
//            ClusterClientOptions clusterClientOptions = ClusterClientOptions.builder()
//                    //redis 命令超时时间 根据自己需要设置这里设置为2s
//                    .timeoutOptions(TimeoutOptions.enabled(Duration.ofSeconds(2)))
//                    //拓扑刷新
//                    .topologyRefreshOptions(clusterTopologyRefreshOptions)
//                    .autoReconnect(true)
//
//
//                    .build();
//            clientConfigurationBuilder
//                    .clientOptions(clusterClientOptions)
//                    .readFrom(ReadFrom.MASTER);
//        };
//    }
//
//}
