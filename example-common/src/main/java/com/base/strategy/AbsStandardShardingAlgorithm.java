package com.base.strategy;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.shardingsphere.sharding.api.sharding.standard.PreciseShardingValue;
import org.apache.shardingsphere.sharding.api.sharding.standard.RangeShardingValue;
import org.apache.shardingsphere.sharding.api.sharding.standard.StandardShardingAlgorithm;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Properties;
import java.util.Set;

/**
 * @title: AbsShardingAlgorithm
 * @description: 分库分表算法抽象类
 * @author: arron
 * @date: 2024/8/31 15:18
 */
@Slf4j
public abstract class AbsStandardShardingAlgorithm implements StandardShardingAlgorithm<Long> {

    /**
     * 自定义参数
     */
    protected Properties properties;

    @Override
    public void init(Properties properties) {
        this.properties = properties;
    }

    /**
     * 计算分表
     *
     * @param preciseShardingValue
     * @return -1 表示无表达式
     */
    protected int getShardingValue(PreciseShardingValue<Long> preciseShardingValue) {
        String expressionStr = properties.getProperty("expression");
        if (StringUtils.isBlank(expressionStr)) {
            log.error("expression_is_null");
            return -1;
        }
        String columnName = properties.getProperty("columnName");
        long shardingValue = preciseShardingValue.getValue().longValue();
        String expression = expressionStr.replace(columnName, String.valueOf(shardingValue));
        // 执行计算
        int value = ExpressionEvaluator.evaluateExpression(expression);
        return value;
    }

    /**
     * 分库分表算法 针对范围查询
     *
     * @param databaseNames
     * @param rangeShardingValue
     * @return
     */
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
