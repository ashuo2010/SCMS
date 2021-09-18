package com.ashuo.scms.mapper;

import com.ashuo.scms.entity.Ranking;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author AShuo
 * @since 2021-04-05
 */
public interface RankingMapper {
    //增加单个Ranking
    int insertRanking(List<Ranking> rankingList);

    int deleteRankingByItemId(@Param("itemId") Integer itemId);

    //返回团体排名和总积分
    IPage<Ranking> queryTeamTotalRanking(Page<Ranking> page, @Param("ranking") Ranking ranking);

    //返回个人排名和总积分
    IPage<Ranking> queryUserTotalRanking(Page<Ranking> page, @Param("ranking") Ranking ranking);

}
