package com.ashuo.scms.service.impl;

import com.ashuo.scms.entity.Ranking;
import com.ashuo.scms.mapper.RankingMapper;
import com.ashuo.scms.service.RankingService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author AShuo
 * @since 2021-04-05
 */
@Service
public class RankingServiceImpl implements RankingService {
    @Autowired
    RankingMapper rankingMapper;

    //团体总分排名
    @Override
    public IPage<Ranking> getTeamTotalRanking(Page<Ranking> page, Ranking ranking) {
        IPage<Ranking> rankingList = rankingMapper.queryTeamTotalRanking(page, ranking);
        return rankingList;
    }

    //个人总分排名
    @Override
    public IPage<Ranking> getUserTotalRanking(Page<Ranking> page, Ranking ranking) {
        IPage<Ranking> rankingList = rankingMapper.queryUserTotalRanking(page, ranking);
        return rankingList;
    }


    @Override
    public int addRanking(List<Ranking> rankingList) {
        if (rankingList == null) {
            return 0;
        } else {
            int effNum = rankingMapper.insertRanking(rankingList);
            if (effNum != 1) {
                return 0;
            } else {
                return effNum;
            }
        }
    }


    @Override
    public int removeRanking(int itemId) {
        if (itemId == 0) {
            return 0;
        } else {
            int effNum = rankingMapper.deleteRankingByItemId(itemId);
            if (effNum != 1) {
                return 0;
            } else {
                return effNum;
            }
        }
    }

}
