package com.ashuo.scms.service;

import com.ashuo.scms.entity.Ranking;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import java.util.List;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author AShuo
 * @since 2021-04-05
 */
public interface RankingService {
    IPage<Ranking> getTeamTotalRanking(Page<Ranking> page, Ranking ranking);

    IPage<Ranking> getUserTotalRanking(Page<Ranking> page, Ranking ranking);

    int addRanking(List<Ranking> rankingList);

    int removeRanking(int itemId);


}
