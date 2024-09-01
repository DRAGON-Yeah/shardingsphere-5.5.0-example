package com.base.config;

/**
 * @title: ShardingConfigEnums
 * @description: 枚举定义
 * @author: arron
 * @date: 2024/9/1 11:33
 */
public class ShardingConfigEnums {


    /**
     * 枚举
     * 类型 ：CLASS_BASED，INLINE，MODE，HASHMOD，只实现：CLASS_BASED
     *
     * @return
     */
    public enum ShardingAlgorithmType {
        CLASS_BASED("CLASS_BASED"),
        INLINE("INLINE"),
        MODE("MODE"),
        HASHMOD("HASHMOD");

        private String type;

        ShardingAlgorithmType(String type) {
            this.type = type;
        }

        public String getType() {
            return type;
        }
    }

    /**
     * 枚举
     * 分库分表策略STANDARD,COMPLEX
     *
     * @return
     */
    public enum ShardingStrategy {
        STANDARD("STANDARD"),
        COMPLEX("COMPLEX");

        private String strategy;

        ShardingStrategy(String strategy) {
            this.strategy = strategy;
        }

        public String getStrategy() {
            return strategy;
        }
    }

}
