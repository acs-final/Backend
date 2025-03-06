package com.common.global.config;

import io.lettuce.core.ClientOptions;
import io.lettuce.core.SocketOptions;
import io.lettuce.core.cluster.ClusterClientOptions;
import io.lettuce.core.cluster.ClusterTopologyRefreshOptions;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisClusterConfiguration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;

import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;

import org.springframework.data.redis.serializer.StringRedisSerializer;


import java.time.Duration;
import java.util.Arrays;
import java.util.List;


@Configuration
@EnableRedisRepositories
@RequiredArgsConstructor
public class RedisConfig {

//    @Value("${spring.data.redis.host}")
//    private String redisHost;
//
//    @Value("${spring.data.redis.port}")
//    private int redisPort;


    // lettuce
//    @Bean
//    public RedisConnectionFactory redisConnectionFactory() {
//        return new LettuceConnectionFactory(redisHost, redisPort);
//    }

//    @Value("${spring.data.redis.cluster.nodes}")
//    private List<String> redisHosts;
//
//    private String redisHost0 = redisHosts.get(0);
//
//    private String redisHost1 = redisHosts.get(1);
//
//    private String redisHost2 = redisHosts.get(2);
//
//    String[] host0 = redisHost0.split(":");
//    String[] host1 = redisHost1.split(":");
//    String[] host2 = redisHost2.split(":");

    // redis cluster
//    @Bean
//    public RedisConnectionFactory redisConnectionFactory() {
////        RedisClusterConfiguration clusterConfig = new RedisClusterConfiguration();
//////        clusterConfig.clusterNode(host0[0], Integer.valueOf(host0[1]));
//////        clusterConfig.clusterNode(host1[0], Integer.valueOf(host1[1]));
//////        clusterConfig.clusterNode(host2[0], Integer.valueOf(host2[1]));
////        clusterConfig.clusterNode("192.168.56.101",30080);
////        clusterConfig.clusterNode("192.168.56.101",30082);
////        clusterConfig.clusterNode("192.168.56.102",30085);
////        clusterConfig.setMaxRedirects(3);
////
////        // (2) Socket 옵션
////        SocketOptions socketOptions = SocketOptions.builder()
////                .connectTimeout(Duration.ofMillis(10000L))
////                .keepAlive(true)
////                .build();
////
////        // (3) Cluster topology refresh 옵션
////        ClusterTopologyRefreshOptions clusterTopologyRefreshOptions = ClusterTopologyRefreshOptions.builder()
////                .dynamicRefreshSources(true)
////                .enableAllAdaptiveRefreshTriggers()
////                .enablePeriodicRefresh(Duration.ofMinutes(30L))
////                .build();
////
////        // (4) Cluster Client 옵션
////        ClientOptions clientOptions = ClusterClientOptions.builder()
////                .topologyRefreshOptions(clusterTopologyRefreshOptions)
////                .socketOptions(socketOptions)
////                .build();
////
////        // (5) Lettuce Client 옵션
////        LettuceClientConfiguration clientConfiguration = LettuceClientConfiguration.builder()
////                .clientOptions(clientOptions)
////                .commandTimeout(Duration.ofMillis(30000L))
////                .build();
////
////        return new LettuceConnectionFactory(clusterConfig, clientConfiguration);
//
//        RedisClusterConfiguration clusterConfig = new RedisClusterConfiguration(
//                Arrays.asList(
//                        "redis-0.redis.svc.cluster.local:6379",
//                "redis-1.redis.svc.cluster.local:6379",
//                "redis-2.redis.svc.cluster.local:6379"
//                )
//        );
//        return new LettuceConnectionFactory(clusterConfig);
//    }
//
//    // Redis template
//    @Bean
//    public RedisTemplate<?, ?> redisTemplate() {
//        RedisTemplate<?, ?> redisTemplate = new RedisTemplate<>();
//
//        redisTemplate.setConnectionFactory(redisConnectionFactory());   //connection
//        redisTemplate.setKeySerializer(new StringRedisSerializer());    // key
//        redisTemplate.setValueSerializer(new StringRedisSerializer());  // value
//
//        return redisTemplate;
//    }

}
