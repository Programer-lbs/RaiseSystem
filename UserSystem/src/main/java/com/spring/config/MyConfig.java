package com.spring.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import redis.clients.jedis.Jedis;

@Configuration
public class MyConfig {

    @Bean
    public Jedis jedis(){
        return new Jedis("120.26.175.78",6379);
    }

    //加入加密方式
    @Bean
    public BCryptPasswordEncoder encoder(){
        return new BCryptPasswordEncoder();
    }
}
