package com.base.strategy;

import lombok.extern.slf4j.Slf4j;
import org.apache.shardingsphere.sharding.api.sharding.complex.ComplexKeysShardingValue;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Map;

/**
 * @title: ComplexKeysTableShardingAlgorithm
 * @description: 复合字段分表策略
 * @author: arron
 * @date: 2024/9/1 10:41
 */
@Slf4j
public class ComplexKeysTableShardingAlgorithm extends AbsComplexKeysShardingAlgorithm {

    @Override
    public Collection<String> doSharding(Collection<String> tables, ComplexKeysShardingValue<Long> complexKeysShardingValue) {
        if (CollectionUtils.isEmpty(tables)) {
            log.error("ComplexKeysTableShardingAlgorithm_tables_is_null");
            throw new IllegalArgumentException("ComplexKeysTableShardingAlgorithm_tables_is_null");
        }
        Collection<String> result = new LinkedHashSet<>();
        String tableName = null;

        if (tables.size() == 1) {
            log.error("ComplexKeysTableShardingAlgorithm_tables_size_is_one");
            tableName = tables.iterator().next();
            result.add(tableName);
            return result;
        }

        Map<String, Collection<Long>> columnNameAndShardingValuesMap = complexKeysShardingValue.getColumnNameAndShardingValuesMap();
        int value = getShardingValue(columnNameAndShardingValuesMap);

        for (String table : tables) {
            if (table.endsWith(String.valueOf(value))) {
                tableName = table;
                result.add(tableName);
                break;
            }
        }

        if (result.isEmpty()) {
            log.error("ComplexKeysTableShardingAlgorithm_tableName_error 根据 sharding_key 取模 分表策略获取表名错误");
            throw new IllegalArgumentException("ComplexKeysTableShardingAlgorithm_tableName_error 根据 sharding_key取模 分表策略获取表名错误");
        }
        return result;
    }
}
