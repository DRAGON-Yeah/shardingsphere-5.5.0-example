package com.base.config.spi;

import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.annotation.TableName;
import com.base.config.PackageScanner;
import com.base.config.ShardingConfig;
import com.base.config.ShardingConfigEnums;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.shardingsphere.infra.url.spi.ShardingSphereURLLoader;
import org.yaml.snakeyaml.Yaml;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;
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

    private boolean useConfig = true;

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

                if (useConfig) {
                    Yaml yaml = new Yaml();
                    Map<String, JSONObject> parsedData = yaml.load(config);
                    Map<String, JSONObject> dataSources = (LinkedHashMap) parsedData.get("dataSources");
                    if (dataSources == null) {
                        log.error("no dataSources config");
                        throw new RuntimeException("no dataSources config");
                    }
                    config = config + generateConfig(dataSources);
                }
                log.warn("自动生成分表配置：\n{}", config);
                return config;
            }
        }
    }

    private String generateConfig(Map<String, JSONObject> dataSources) {
        Set<Class<?>> annotatedClassesInPackage = PackageScanner.getAnnotatedClassesInPackage("com.base.domain", TableName.class);
        log.info("扫描到分表规则类：{}", JSONObject.toJSONString(annotatedClassesInPackage));
        String space2 = "  ";

        StringBuffer sb = new StringBuffer();
        sb.append("\n");
        sb.append("rules:").append("\n");
        sb.append(space2).append("- !SHARDING").append("\n");
        sb.append(space2).append(space2).append("tables:").append("\n");

        for (Class<?> clazz : annotatedClassesInPackage) {
            TableName tableName = clazz.getAnnotation(TableName.class);
            if (tableName == null) {
                continue;
            }
            // 表名
            String tb_name = tableName.value();
            sb.append(space2).append(space2).append(space2).append(tb_name).append(":\n");

            ShardingConfig shardingConfig = clazz.getAnnotation(ShardingConfig.class);
            if (shardingConfig == null) {
                continue;
            }

            int tbShardingCount = shardingConfig.tbShardingCount();
            String actualDataNodes = getActualDataNodes(tb_name, dataSources, tbShardingCount);
            sb.append(space2).append(space2).append(space2).append(space2).append(actualDataNodes).append("\n");

            // 分库策略
            sb.append(space2).append(space2).append(space2).append(space2).append("databaseStrategy").append(":\n");

            // 分片字段
            String shardingColumn = shardingConfig.shardingColumn();

            // 数据库分片算法
            String dbStrategy = shardingConfig.dbStrategy().name();

            // 表分片算法
            String tbStrategy = shardingConfig.tbStrategy().name();

            sb.append(space2).append(space2).append(space2).append(space2).append(space2).append(dbStrategy.toLowerCase()).append(":\n");
            //TODO 判断是否组合建
            if (ShardingConfigEnums.ShardingStrategy.STANDARD.equals(shardingConfig.dbStrategy())) {
                sb.append(space2).append(space2).append(space2).append(space2).append(space2).append(space2).append("shardingColumn: ").append(shardingColumn).append("\n");
            } else {
                sb.append(space2).append(space2).append(space2).append(space2).append(space2).append(space2).append("shardingColumns: ").append(shardingColumn).append("\n");
            }
            String default_db_strategy = "default_db_" + tb_name + "_strategy";
            sb.append(space2).append(space2).append(space2).append(space2).append(space2).append(space2).append("shardingAlgorithmName: ").append(default_db_strategy).append("\n");

            // 分表策略
            sb.append(space2).append(space2).append(space2).append(space2).append("tableStrategy").append(":\n");
            sb.append(space2).append(space2).append(space2).append(space2).append(space2).append(tbStrategy.toLowerCase()).append(":\n");

            //TODO 判断是否组合建
            if (ShardingConfigEnums.ShardingStrategy.STANDARD.equals(shardingConfig.tbStrategy())) {
                sb.append(space2).append(space2).append(space2).append(space2).append(space2).append(space2).append("shardingColumn: ").append(shardingColumn).append("\n");
            } else {
                sb.append(space2).append(space2).append(space2).append(space2).append(space2).append(space2).append("shardingColumns: ").append(shardingColumn).append("\n");
            }
            String default_tb_strategy = "default_tb_" + tb_name + "_strategy";
            sb.append(space2).append(space2).append(space2).append(space2).append(space2).append(space2).append("shardingAlgorithmName: ").append(default_tb_strategy).append("\n");
        }

        String shardingAlgorithms = "shardingAlgorithms:";
        sb.append(space2).append(space2).append(shardingAlgorithms).append("\n");

        //TODO 定义分片算法
        for (Class<?> clazz : annotatedClassesInPackage) {
            TableName tableName = clazz.getAnnotation(TableName.class);
            if (tableName == null) {
                continue;
            }
            // 表名
            String tb_name = tableName.value();

            ShardingConfig shardingConfig = clazz.getAnnotation(ShardingConfig.class);
            if (shardingConfig == null) {
                continue;
            }
            // 数据库分片算法
            String dbAlgorithmType = shardingConfig.dbAlgorithmsType().name();
            String dbAlgorithmClassName = shardingConfig.dbAlgorithmClassName();
            String dbStrategy = shardingConfig.dbStrategy().name();
            String dbExpression = shardingConfig.dbExpression();

            // 表分片算法
            String tbAlgorithmType = shardingConfig.tbAlgorithmsType().name();
            String tbAlgorithmClassName = shardingConfig.tbAlgorithmClassName();
            String tbExpression = shardingConfig.tbExpression();
            String tbStrategy = shardingConfig.tbStrategy().name();

            String default_db_strategy = "default_db_" + tb_name + "_strategy";
            //分库
            sb.append(space2).append(space2).append(space2).append(default_db_strategy).append(":\n");
            sb.append(space2).append(space2).append(space2).append(space2).append("type: ").append(dbAlgorithmType).append("\n");
            sb.append(space2).append(space2).append(space2).append(space2).append("props:").append("\n");
            sb.append(space2).append(space2).append(space2).append(space2).append(space2).append("strategy: ").append(dbStrategy).append("\n");
            sb.append(space2).append(space2).append(space2).append(space2).append(space2).append("algorithmClassName: ").append(dbAlgorithmClassName).append("\n");
            sb.append(space2).append(space2).append(space2).append(space2).append(space2).append("expression: ").append(dbExpression).append("\n");

            String default_tb_strategy = "default_tb_" + tb_name + "_strategy";
            //分表
            sb.append(space2).append(space2).append(space2).append(default_tb_strategy).append(":\n");
            sb.append(space2).append(space2).append(space2).append(space2).append("type: ").append(tbAlgorithmType).append("\n");
            sb.append(space2).append(space2).append(space2).append(space2).append("props:").append("\n");
            sb.append(space2).append(space2).append(space2).append(space2).append(space2).append("strategy: ").append(tbStrategy).append("\n");
            sb.append(space2).append(space2).append(space2).append(space2).append(space2).append("algorithmClassName: ").append(tbAlgorithmClassName).append("\n");
            sb.append(space2).append(space2).append(space2).append(space2).append(space2).append("expression: ").append(tbExpression).append("\n");
        }

        String tableConfig = sb.toString();
//        log.warn("自动生成分表配置：\n{}", tableConfig);
        return tableConfig;
    }

    private String getActualDataNodes(String tbName, Map<String, JSONObject> dataSources, int tbShardingCount) {
        // 数据库数量
        Set<String> dbKeySet = dataSources.keySet();
        int dbSize = dbKeySet.size();
        String actualDataNodes = "actualDataNodes:";

        if (dbSize == 1) {
            String dbName = dbKeySet.iterator().next();
            actualDataNodes = actualDataNodes + " " + dbName + "." + tbName;
            return actualDataNodes;
        }

        int i = 0;
        for (String key : dbKeySet) {
            String dbName = key;
            actualDataNodes = actualDataNodes + " " + dbName + "." + tbName + "_${0.." + (tbShardingCount - 1) + "}";
            if (i < dbSize - 1) {
                actualDataNodes = actualDataNodes + ",";
            }
            i++;
        }
        return actualDataNodes;
    }


    @Override
    public Object getType() {
        return CUSTOMER_CLASSPATH_TYPE;
    }
}
