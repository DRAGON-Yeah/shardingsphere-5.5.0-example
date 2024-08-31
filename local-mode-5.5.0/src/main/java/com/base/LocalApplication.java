package com.base;

import org.mybatis.spring.annotation.MapperScan;
import org.mybatis.spring.annotation.MapperScans;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @title: LocalApplication
 * @description: 单机模式： 启动程序
 * @author: arron
 * @date: 2024/8/21 16:15
 */
@MapperScans({
        @MapperScan("com.base.mapper")
})
@SpringBootApplication
public class LocalApplication {

    public static void main(String[] args) {
        SpringApplication.run(LocalApplication.class, args);
    }


}
