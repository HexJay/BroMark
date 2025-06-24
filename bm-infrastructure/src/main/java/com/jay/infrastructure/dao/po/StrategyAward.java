package com.jay.infrastructure.dao.po;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author Jay
 * @date 2025/6/24 17:13
 * @description 抽奖策略奖品明细 - 概率、规则
 */
@Data
public class StrategyAward {

    /**
     *
     */
    private Long id;
    /**
     * 自增ID
     */
    private Long strategyId;
    /**
     * 抽奖策略ID
     */
    private Integer awardId;
    /**
     * 抽奖奖品ID - 内部流转使用
     */
    private String awardTitle;
    /**
     * 抽奖奖品标题
     */
    private String awardSubtitle;
    /**
     * 抽奖奖品副标题
     */
    private Integer awardCount;
    /**
     * 奖品库存总量
     */
    private Integer awardCountSurplus;
    /**
     * 奖品库存剩余
     */
    private BigDecimal awardRate;
    /**
     * 奖品中奖概率
     */
    private String ruleModels;
    /**
     * 规则模型，rule配置的模型同步到此表，便于使用
     */
    private Integer sort;
    /**
     * 排序
     */
    private Date createTime;
    /**
     * 创建时间
     */
    private Date updateTime;
}