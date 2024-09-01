package com.base.strategy;

import lombok.extern.slf4j.Slf4j;
import org.apache.shardingsphere.sharding.api.sharding.complex.ComplexKeysShardingValue;
import org.springframework.util.CollectionUtils;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Map;

/**
 * @title: ComplexKeysDBShardingAlgorithm
 * @description: 复合字段分库策略
 * @author: arron
 * @date: 2024/9/1 10:41
 */
@Slf4j
public class ComplexKeysDBShardingAlgorithm extends AbsComplexKeysShardingAlgorithm {

    @Override
    public Collection<String> doSharding(Collection<String> databaseNames, ComplexKeysShardingValue<Long> complexKeysShardingValue) {
        if (CollectionUtils.isEmpty(databaseNames)) {
            log.error("ComplexKeysDBShardingAlgorithm_tables_is_null");
            throw new IllegalArgumentException("ComplexKeysDBShardingAlgorithm_tables_is_null");
        }
        Collection<String> result = new LinkedHashSet<>();
        String destDBName = null;

        if (databaseNames.size() == 1) {
            log.error("ComplexKeysDBShardingAlgorithm_tables_size_is_one");
            destDBName = databaseNames.iterator().next();
            result.add(destDBName);
            return result;
        }

        Map<String, Collection<Long>> columnNameAndShardingValuesMap = complexKeysShardingValue.getColumnNameAndShardingValuesMap();
        int value = getShardingValue(columnNameAndShardingValuesMap);

        for (String databaseName : databaseNames) {
            if (databaseName.endsWith(String.valueOf(value))) {
                destDBName = databaseName;
                result.add(destDBName);
                break;
            }
        }

        if (result.isEmpty()) {
            log.error("ComplexKeysDBShardingAlgorithm_destDBName_error sharding_key取模 分库策略获取库名错误");
            throw new IllegalArgumentException("ComplexKeysDBShardingAlgorithm_根据 sharding_key取模 分库策略获取库名错误");
        }
        return result;
    }
}
