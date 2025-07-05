package com.jay.domain.strategy.model.vo.tree;

import com.jay.domain.strategy.model.vo.RuleLogicCheckTypeVO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Jay
 * @date 2025/7/5 15:13
 * @description 规则树节点指向线对象，用于衔接 from -> to 节点链路关系
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RuleTreeNodeLineVO {
    /** 规则树ID */
    private Integer treeId;
    /** 规则key节点 from */
    private String ruleNodeFrom;
    /** 规则key节点 to */
    private String ruleNodeTo;
    /** 限定类型 1:=;2:>;3:<;4:>=;5:<=;6:enum[枚举范围] */
    private RuleLimitTypeVO ruleLimitType;
    /** 限定值（到下个节点）*/
    private RuleLogicCheckTypeVO ruleLimitValue;
}
