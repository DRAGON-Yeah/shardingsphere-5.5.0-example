package com.base.config.spi;

import com.base.config.ShardingRuleScanAndGenerate;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.shardingsphere.infra.url.spi.ShardingSphereURLLoader;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.Properties;
import java.util.stream.Collectors;

/**
 * @title: CustomClassPathURLLoader
 * @description: 实现SPI，读取配置，自动生成配置
 * @author: arron
 * @date: 2024/8/28 22:09
 */
@Slf4j
public class CustomClassPathURLLoader implements ShardingSphereURLLoader {

    /**
     * 定义jdbc:shardingsphere:后的类型为nacos:
     */
    private static final String CUSTOMER_CLASSPATH_TYPE = "custmer-classpath:";


    /**
     * 接收custmer:classpath:
     *
     * @param configurationSubject configuration dataId
     * @param queryProps           url参数，已经解析成为Properties
     * @return
     */
    @Override
    @SneakyThrows
    public String load(String configurationSubject, Properties queryProps) {
        try (InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(configurationSubject)) {
            Objects.requireNonNull(inputStream);
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
                String config = reader.lines().collect(Collectors.joining(System.lineSeparator()));

                String useAutoConfig = queryProps.getProperty("useAutoConfig");
                if (!StringUtils.isBlank(useAutoConfig) && Boolean.parseBoolean(useAutoConfig)) {
                    config = ShardingRuleScanAndGenerate.generateConfig(config);
                }
                log.warn("自动生成分表配置：\n{}", config);
                return config;
            }
        }
    }


    @Override
    public Object getType() {
        return CUSTOMER_CLASSPATH_TYPE;
    }
}
