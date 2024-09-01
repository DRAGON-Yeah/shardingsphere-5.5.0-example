package com.base.config;

import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.yaml.snakeyaml.Yaml;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * @title: ShardingRuleScanAndGenerate
 * @description: 启动时候扫描并且生成规则
 * @author: arron
 * @date: 2024/9/1 11:50
 */
@Slf4j
public class ShardingRuleScanAndGenerate {


    public static String generateConfig(String config) {
        Yaml yaml = new Yaml();
        Map<String, JSONObject> parsedData = yaml.load(config);
        Map<String, JSONObject> dataSources = (LinkedHashMap) parsedData.get("dataSources");
        if (dataSources == null) {
            log.error("no dataSources config");
            throw new RuntimeException("no dataSources config");
        }
        config = config + generateConfig(dataSources);
        return config;
    }


    public static String generateConfig(Map<String, JSONObject> dataSources) {
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

    public static String getActualDataNodes(String tbName, Map<String, JSONObject> dataSources, int tbShardingCount) {
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



}
