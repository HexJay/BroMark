package com.jay.infrastructure.dao;

import com.jay.infrastructure.dao.po.Award;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author Jay
 * @date 2025/6/24 20:45
 * @description 奖品表DAO
 */
@Mapper
public interface IAwardDao {
    List<Award> queryAwardList();

    String queryAwardConfigByAwardId(Integer awardId);

    String queryAwardKey(Integer awardId);
}
