package com.base.strategy;

import org.apache.commons.lang3.StringUtils;
import org.apache.shardingsphere.sharding.api.sharding.standard.PreciseShardingValue;
import org.apache.shardingsphere.sharding.api.sharding.standard.RangeShardingValue;
import org.apache.shardingsphere.sharding.api.sharding.standard.StandardShardingAlgorithm;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Properties;
import java.util.Set;

/**
 * @title: 分表策略
 * @description: 精确分表算法
 * @author: arron
 * @date: 2024/8/25 11:57
 */
public class TablePreciseShardingAlgorithm implements StandardShardingAlgorithm<Long> {

    /**
     * 默认常量
     */
    private static String SHARDING_KEY = "SHARDING_KEY";

    /**
     * 自定义参数
     */
    private Properties properties;

    @Override
    public void init(Properties properties) {
        this.properties = properties;
    }

    /**
     * 计算分表
     *
     * @param preciseShardingValue
     * @return
     */
    private int getShardingValue(long preciseShardingValue) {
        String formula = properties.getProperty("formula");
        String expression = formula.replace(SHARDING_KEY, String.valueOf(preciseShardingValue));
        // 执行计算
        int value = ExpressionEvaluator.evaluateExpression(expression);
        return value;
    }

    @Override
    public String doSharding(Collection<String> tables, PreciseShardingValue<Long> preciseShardingValue) {
        int value = getShardingValue(preciseShardingValue.getValue().longValue());

        String tableName = null;
        for (String table : tables) {
            if (table.endsWith(String.valueOf(value))) {
                tableName = table;
                break;
            }
        }
        if (StringUtils.isBlank(tableName)) {
            throw new IllegalArgumentException("根据 sharding_key取模 分表策略获取表名错误");
        }
        return tableName;
    }

    @Override
    public Collection<String> doSharding(Collection<String> tables, RangeShardingValue<Long> rangeShardingValue) {
        int size = tables.size();
        Set<String> result = new LinkedHashSet<>();
        // between and 的起始值
        long lower = rangeShardingValue.getValueRange().lowerEndpoint();
        long upper = rangeShardingValue.getValueRange().upperEndpoint();
        // 循环范围计算分库逻辑
        for (long i = lower; i <= upper; i++) {
            for (String table : tables) {
                if (table.endsWith(String.valueOf(i % size))) {
                    result.add(table);
                }
            }
        }
        return result;
    }
}
