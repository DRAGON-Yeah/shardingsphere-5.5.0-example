package com.base.strategy;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.shardingsphere.sharding.api.sharding.complex.ComplexKeysShardingAlgorithm;

import java.util.Collection;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

/**
 * @title: AbsComplexKeysShardingAlgorithm
 * @description: 组合键分库分表
 * @author: arron
 * @date: 2024/9/1 10:34
 */
@Slf4j
public abstract class AbsComplexKeysShardingAlgorithm implements ComplexKeysShardingAlgorithm<Long> {
    protected Properties properties;

    @Override
    public void init(Properties properties) {
        this.properties = properties;
    }

    /**
     * 计算分表
     *
     * @param columnNameAndShardingValuesMap
     * @return -1 表示无表达式
     */
    protected int getShardingValue(Map<String, Collection<Long>> columnNameAndShardingValuesMap) {
        String expression = properties.getProperty("expression");
        if (StringUtils.isBlank(expression)) {
            log.error("expression_is_null");
            return -1;
        }
        Set<String> columnNameKeySet = columnNameAndShardingValuesMap.keySet();
        for (String columnName : columnNameKeySet) {
            Object preciseShardingValue = columnNameAndShardingValuesMap.get(columnName).iterator().next();
            expression = expression.replace(columnName, String.valueOf(preciseShardingValue));
        }
        // 执行计算
        int value = ExpressionEvaluator.evaluateExpression(expression);
        return value;
    }

}
