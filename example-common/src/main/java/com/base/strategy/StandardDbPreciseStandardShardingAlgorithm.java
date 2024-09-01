package com.base.strategy;

import lombok.extern.slf4j.Slf4j;
import org.apache.shardingsphere.sharding.api.sharding.standard.PreciseShardingValue;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.Collection;

/**
 * @title: 分库策略
 * @description: 精准查询，根据sharding_key 进行分库 如：sharding_key%4
 * @author: arron
 * @date: 2024/8/22 10:35
 */
@Slf4j
public class StandardDbPreciseStandardShardingAlgorithm extends AbsStandardShardingAlgorithm {
    @Override
    public String doSharding(Collection<String> databaseNames, PreciseShardingValue<Long> preciseShardingValue) {
        if (CollectionUtils.isEmpty(databaseNames)) {
            log.error("DbPreciseShardingAlgorithm_databaseNames_is_null");
            throw new IllegalArgumentException("DbPreciseShardingAlgorithm_databaseNames_is_null");
        }
        String destDBName = null;

        if (databaseNames.size() == 1) {
            log.error("DbPreciseShardingAlgorithm_databaseNames_size_is_one");
            destDBName = databaseNames.iterator().next();
            return destDBName;
        }

        long value = getShardingValue(preciseShardingValue);
        if (-1 == value) {
            log.error("DbPreciseShardingAlgorithm_use mod databaseNames.size()");
            value = preciseShardingValue.getValue() % databaseNames.size();
        }

        for (String databaseName : databaseNames) {
            if (databaseName.endsWith(String.valueOf(value))) {
                destDBName = databaseName;
                break;
            }
        }
        if (!StringUtils.hasLength(destDBName)) {
            log.error("DbPreciseShardingAlgorithm_destDBName_error sharding_key取模 分库策略获取库名错误");
            throw new IllegalArgumentException("DbPreciseShardingAlgorithm_根据 sharding_key取模 分库策略获取库名错误");
        }
        return destDBName;
    }

}
