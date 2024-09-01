package com.base.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.base.config.ShardingConfig;
import com.base.config.ShardingConfigEnums;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * sequence模型
 *
 * @author arron
 */
@Data
@ShardingConfig(
        shardingColumn = "sharding_key",
        tbAlgorithmsType = ShardingConfigEnums.ShardingAlgorithmType.CLASS_BASED,
        tbStrategy = ShardingConfigEnums.ShardingStrategy.STANDARD,
        tbAlgorithmClassName = "com.base.strategy.StandardTablePreciseStandardShardingAlgorithm",
        tbExpression = "sharding_key%4",
        dbAlgorithmsType = ShardingConfigEnums.ShardingAlgorithmType.CLASS_BASED,
        dbStrategy = ShardingConfigEnums.ShardingStrategy.STANDARD,
        dbAlgorithmClassName = "com.base.strategy.StandardDbPreciseStandardShardingAlgorithm",
        dbExpression = "sharding_key%4",
        tbShardingCount = 4
)
@TableName("sequence")
public class SequenceDO implements Serializable {

    private static final long serialVersionUID = 5375836779851705267L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("sharding_key")
    private Long shardingKey;

    @TableField("name")
    private String name;

    @TableField("value")
    private Long value;

    @TableField("gmt_modified")
    private Date gmtModified;

}
