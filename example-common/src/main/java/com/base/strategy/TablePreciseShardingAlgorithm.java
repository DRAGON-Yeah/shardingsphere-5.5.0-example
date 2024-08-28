package com.base.strategy;

import org.apache.shardingsphere.sharding.api.sharding.standard.PreciseShardingValue;
import org.apache.shardingsphere.sharding.api.sharding.standard.RangeShardingValue;
import org.apache.shardingsphere.sharding.api.sharding.standard.StandardShardingAlgorithm;
import org.springframework.util.StringUtils;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * @title: 分表策略
 * @description: 精确分表算法
 * @author: arron
 * @date: 2024/8/25 11:57
 */
public class TablePreciseShardingAlgorithm implements StandardShardingAlgorithm<Long> {

    /**
     * 分4个表
     */
    private static final int SPLIT_TABLE_SIZE = 4;

    @Override
    public String doSharding(Collection<String> tables, PreciseShardingValue<Long> preciseShardingValue) {
        long value = preciseShardingValue.getValue().longValue() % SPLIT_TABLE_SIZE;
        String tableName = null;
        for (String table : tables) {
            if (table.endsWith(String.valueOf(value))) {
                tableName = table;
                break;
            }
        }
        if (!StringUtils.hasLength(tableName)) {
            throw new IllegalArgumentException("根据 sharding_key取模 分表策略获取表名错误");
        }
        return tableName;
    }

    @Override
    public Collection<String> doSharding(Collection<String> tables, RangeShardingValue<Long> rangeShardingValue) {
        Set<String> result = new LinkedHashSet<>();
        // between and 的起始值
        long lower = rangeShardingValue.getValueRange().lowerEndpoint();
        long upper = rangeShardingValue.getValueRange().upperEndpoint();
        // 循环范围计算分库逻辑
        for (long i = lower; i <= upper; i++) {
            for (String table : tables) {
                if (table.endsWith(String.valueOf(i % SPLIT_TABLE_SIZE))) {
                    result.add(table);
                }
            }
        }
        return result;
    }
}
