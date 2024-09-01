package com.base.config;

import java.lang.annotation.*;

/**
 * @title: CustomerShardingConfig
 * @description: 自定义DO配置
 * @author: arron
 * @date: 2024/9/1 09:50
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ShardingConfig {

    /**
     * 分库分表建
     * 默认id ,组合键使用,隔开
     *
     * @return
     */
    String shardingColumn() default "id";

    /**
     * 类型 ：CLASS_BASED，INLINE，MODE，HASHMOD，只实现：CLASS_BASED
     *
     * @return
     * @com.base.config.ShardingConfigEnums.ShardingAlgorithmType
     */
    ShardingConfigEnums.ShardingAlgorithmType dbAlgorithmsType() default ShardingConfigEnums.ShardingAlgorithmType.CLASS_BASED;

    /**
     * 表达式，如：SHARDING_KEY%4，这里只实现一个key，多个KEY不实现
     * 默认为空，则分库策略：使用数据库总数取模的方式，分表策略：使用数据表总数取模的方式
     *
     * @return
     */
    String dbExpression() default "";


    /**
     * 分库策略STANDARD,COMPLEX
     *
     * @return
     * @com.base.config.ShardingConfigEnums.ShardingStrategy
     */
    ShardingConfigEnums.ShardingStrategy dbStrategy() default ShardingConfigEnums.ShardingStrategy.STANDARD;


    /**
     * 类型 ：CLASS_BASED，INLINE，MODE，HASHMOD，只实现：CLASS_BASED
     *
     * @return
     * @com.base.config.ShardingConfigEnums.ShardingAlgorithmType
     */
    ShardingConfigEnums.ShardingAlgorithmType tbAlgorithmsType() default ShardingConfigEnums.ShardingAlgorithmType.CLASS_BASED;

    /**
     * 分库算法
     *
     * @return
     */
    String dbAlgorithmClassName() default "com.base.strategy.StandardDbPreciseStandardShardingAlgorithm";


    /**
     * 分表策略STANDARD,COMPLEX
     *
     * @return
     * @com.base.config.ShardingConfigEnums.ShardingStrategy
     */
    ShardingConfigEnums.ShardingStrategy tbStrategy() default ShardingConfigEnums.ShardingStrategy.STANDARD;

    /**
     * 表达式，如：SHARDING_KEY%4，这里只实现一个key，多个KEY不实现
     * 默认为空，则分库策略：使用数据库总数取模的方式，分表策略：使用数据表总数取模的方式
     *
     * @return
     */
    String tbExpression() default "";

    /**
     * 分表算法
     *
     * @return
     */
    String tbAlgorithmClassName() default "com.base.strategy.StandardTablePreciseStandardShardingAlgorithm";

    /**
     * 数据表分片数量
     * @return
     */
    int tbShardingCount() default 4;

}
