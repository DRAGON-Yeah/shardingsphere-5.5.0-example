package com.base.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.base.domain.SequenceDO;
import com.base.mapper.SequenceMapper;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @title: IndexController
 * @description: <TODO description for purpose>
 * @author: arron
 * @date: 2024/8/21 17:04
 */
@RestController
@RequestMapping("/")
public class IndexController {

    @Resource
    private SequenceMapper sequenceMapper;


    @GetMapping("index")
    public String index() {
        return "index";
    }

    @GetMapping("list")
    public List<SequenceDO> list(long shardingKey) {
        LambdaQueryWrapper<SequenceDO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SequenceDO::getShardingKey, shardingKey);
        queryWrapper.eq(SequenceDO::getValue, 19000);
        List<SequenceDO> list = sequenceMapper.selectList(queryWrapper);
        return list;
    }



}
