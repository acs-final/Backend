package com.common.global.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;

import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;

import org.springframework.data.redis.serializer.StringRedisSerializer;


@Configuration
@EnableRedisRepositories
@RequiredArgsConstructor
public class RedisConfig {

//    @Value("${spring.data.redis.host}")
//    private String redisHost;
//
//    @Value("${spring.data.redis.port}")
//    private int redisPort;
//
//
//    // lettuce
//    @Bean
//    public RedisConnectionFactory redisConnectionFactory() {
//        return new LettuceConnectionFactory(redisHost, redisPort);
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
