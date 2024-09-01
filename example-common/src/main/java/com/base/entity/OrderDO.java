package com.base.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.base.config.ShardingConfig;
import com.base.config.ShardingConfigEnums;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @title: OrderDO
 * @description: 订单
 * @author: arron
 * @date: 2024/7/14 18:00
 */
@Data
@ShardingConfig(
        shardingColumn = "id",
        tbAlgorithmsType = ShardingConfigEnums.ShardingAlgorithmType.CLASS_BASED,
        tbStrategy = ShardingConfigEnums.ShardingStrategy.COMPLEX,
        tbAlgorithmClassName = "com.base.strategy.ComplexKeysDBShardingAlgorithm",
        tbExpression = "id%4+8",
        dbAlgorithmsType = ShardingConfigEnums.ShardingAlgorithmType.CLASS_BASED,
        dbStrategy = ShardingConfigEnums.ShardingStrategy.STANDARD,
        dbAlgorithmClassName = "com.base.strategy.StandardDbPreciseStandardShardingAlgorithm",
        dbExpression = "id%4"
)
@TableName("trade_order")
public class OrderDO implements Serializable {
    private static final long serialVersionUID = -3892390655321729414L;

    @TableId(value = "id", type = IdType.NONE)
    private Long id;

    /**
     * 订单号
     */
    private String orderNo;

    /**
     * 支付方式
     *
     * @com.base.mall.enums.MallEnum.OrderPayType
     */
    private Integer type;

    /**
     * 订单总金额
     */
    private BigDecimal amount;

    /**
     * 购买客户
     */
    private String customerId;

    /**
     * 收货人姓名
     */
    private String buyerName;

    /**
     * 收货人手机号
     */
    private String buyerMobile;

    /**
     * 收货人邮箱
     */
    private String buyerEmail;

    /**
     * 收货人邮政编码
     */
    private String buyerPostCode;

    /**
     * 收货人详细地址
     */
    private String buyerAddress;

    /**
     * 订单状态: -1：已删除，0：待支付，1：已支付，2：已取消，3：已退款，4：退款失败，5：退款成功
     *
     * @com.base.mall.enums.MallEnum.OrderStatus
     */
    private Integer status;

    /**
     * 支付时间
     */
    private Date payTime;

    /**
     * 是否自提,0:否，1：是
     *
     * @com.base.mall.enums.MallEnum.IsOrNot
     */
    private Integer isSelfDeliver;

    /**
     * 快递单号
     */
    private String expressNo;

    /**
     * 运输公司名称
     */
    private String expressName;

    /**
     * 订单备注
     */
    private String remark;

    /**
     * 创建人id
     */
    private String createUserId;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新人id
     */
    private String updateUserId;

    /**
     * 更新时间
     */
    private Date updateTime;

}
