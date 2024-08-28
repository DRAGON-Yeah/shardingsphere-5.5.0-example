package com.base.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.base.domain.SequenceDO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author womeng
 */
public interface SequenceMapper extends BaseMapper<SequenceDO> {

    List<SequenceDO> list(@Param("shardingKey") Long shardingKey);
}
