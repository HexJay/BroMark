package com.jay.domain.strategy.model.vo.tree;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * @author Jay
 * @date 2025/7/5 15:08
 * @description 规则树对象 【不具有唯一ID，不需要改变数据库结果的对象，可定义为值对象】
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RuleTreeVO {
    /** 规则树ID */
    private Integer treeId;
    /** 规则树名称 */
    private String treeName;
    /** 规则树描述 */
    private String treeDesc;
    /** 规则树根节点 */
    private String treeRootNode;
    /** 规则树节点 */
    private Map<String, RuleTreeNodeVO> treeNodeMap;
}
