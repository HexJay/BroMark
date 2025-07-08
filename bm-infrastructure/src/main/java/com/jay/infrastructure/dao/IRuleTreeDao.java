package com.jay.infrastructure.dao;

import com.jay.infrastructure.dao.po.RuleTree;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author Jay
 * @date 2025/7/7 14:58
 * @description 规则树表DAO
 */
@Mapper
public interface IRuleTreeDao {
    RuleTree queryRuleTreeTreeId(String treeId);
}
