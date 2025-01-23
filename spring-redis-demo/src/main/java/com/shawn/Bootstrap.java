package com.shawn;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.data.redis.core.RedisTemplate;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;


@SpringBootApplication
public class Bootstrap implements CommandLineRunner {
    private static final Logger log = LoggerFactory.getLogger(Bootstrap.class);
    @Resource
    private RedisTemplate redisTemplate;

    @Autowired
    private ApplicationContext ctx;

    public static void main(String[] args) {
        SpringApplication.run(Bootstrap.class, args);
        log.info("start success");
    }

    @Override
    public void run(String... args) throws Exception {

        while(true){
            redisTemplate.opsForValue().get("abc");
            TimeUnit.MILLISECONDS.sleep(500);
        }
    }
}