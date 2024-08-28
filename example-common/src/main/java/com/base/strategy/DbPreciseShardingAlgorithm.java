package com.base.strategy;

import org.apache.shardingsphere.sharding.api.sharding.standard.PreciseShardingValue;
import org.apache.shardingsphere.sharding.api.sharding.standard.RangeShardingValue;
import org.apache.shardingsphere.sharding.api.sharding.standard.StandardShardingAlgorithm;
import org.springframework.util.StringUtils;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * @title: 分库策略
 * @description: 精准查询，根据sharding_key 进行分库 如：sharding_key%4
 * @author: arron
 * @date: 2024/8/22 10:35
 */
public class DbPreciseShardingAlgorithm implements StandardShardingAlgorithm<Long> {
    @Override
    public String doSharding(Collection<String> databaseNames, PreciseShardingValue<Long> preciseShardingValue) {
        long value = preciseShardingValue.getValue() % databaseNames.size();
        String destDBName = null;
        for (String databaseName : databaseNames) {
            if (databaseName.endsWith(String.valueOf(value))) {
                destDBName = databaseName;
                break;
            }
        }
        if (!StringUtils.hasLength(destDBName)) {
            throw new IllegalArgumentException("根据 sharding_key取模 分库策略获取库名错误");
        }
        return destDBName;
    }

    @Override
    public Collection<String> doSharding(Collection<String> databaseNames, RangeShardingValue<Long> rangeShardingValue) {
        Set<String> result = new LinkedHashSet<>();
        // between and 的起始值
        long lower = rangeShardingValue.getValueRange().lowerEndpoint();
        long upper = rangeShardingValue.getValueRange().upperEndpoint();
        // 循环范围计算分库逻辑
        for (long i = lower; i <= upper; i++) {
            for (String databaseName : databaseNames) {
                if (databaseName.endsWith(String.valueOf(i % databaseNames.size()))) {
                    result.add(databaseName);
                }
            }
        }
        return result;
    }
}
