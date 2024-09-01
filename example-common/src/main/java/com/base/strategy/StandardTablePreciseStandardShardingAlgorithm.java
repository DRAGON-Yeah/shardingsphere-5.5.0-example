package com.base.strategy;

import lombok.extern.slf4j.Slf4j;
import org.apache.shardingsphere.sharding.api.sharding.standard.PreciseShardingValue;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.Collection;

/**
 * @title: 分表策略
 * @description: 精确分表算法
 * @author: arron
 * @date: 2024/8/25 11:57
 */
@Slf4j
public class StandardTablePreciseStandardShardingAlgorithm extends AbsStandardShardingAlgorithm {


    @Override
    public String doSharding(Collection<String> tables, PreciseShardingValue<Long> preciseShardingValue) {
        if (CollectionUtils.isEmpty(tables)) {
            log.error("TablePreciseShardingAlgorithm_tables_is_null");
            throw new IllegalArgumentException("TablePreciseShardingAlgorithm_tables_is_null");
        }
        String tableName = null;

        if (tables.size() == 1) {
            log.error("TablePreciseShardingAlgorithm_tables_size_is_one");
            tableName = tables.iterator().next();
            return tableName;
        }

        long value = getShardingValue(preciseShardingValue);
        if (-1 == value) {
            log.error("TablePreciseShardingAlgorithm_use mod databaseNames.size()");
            value = preciseShardingValue.getValue().longValue() % tables.size();
        }
        for (String table : tables) {
            if (table.endsWith(String.valueOf(value))) {
                tableName = table;
                break;
            }
        }

        if (!StringUtils.hasLength(tableName)) {
            log.error("TablePreciseShardingAlgorithm_tableName_error 根据 sharding_key取模 分表策略获取表名错误");
            throw new IllegalArgumentException("TablePreciseShardingAlgorithm_tableName_error 根据 sharding_key取模 分表策略获取表名错误");
        }
        return tableName;
    }

}
