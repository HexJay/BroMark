package com.jay.domain.strategy.model.vo.tree;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author Jay
 * @date 2025/7/5 15:11
 * @description 规则树节点对象
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RuleTreeNodeVO {

    /** 规则树ID */
    private Integer treeId;
    /** 规则key，是树节点的key，比如幸运奖为rule_luck_award，由数据库配置*/
    private String ruleKey;
    /** 规则描述 */
    private String ruleDesc;
    /** 规则比值 */
    private String ruleValue;
    /** 规则连线 */
    private List<RuleTreeNodeLineVO> nodeLineVOList;
}
