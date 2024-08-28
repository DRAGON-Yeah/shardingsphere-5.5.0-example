package com.base;

import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.mybatis.spring.annotation.MapperScans;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * @title: ClusterApplication
 * @description: 集群模式：启动程序
 * @author: arron
 * @date: 2024/8/25 22:44
 */
@MapperScans({
        @MapperScan("com.base.mapper")
})
@Slf4j
@EnableDiscoveryClient
@SpringBootApplication
public class NacosConfigWebApplication {

    public static void main(String[] args) {
        SpringApplication.run(NacosConfigWebApplication.class, args);
    }

}
