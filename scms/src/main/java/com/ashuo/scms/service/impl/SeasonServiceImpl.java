package com.ashuo.scms.service.impl;

import com.ashuo.scms.entity.Season;
import com.ashuo.scms.mapper.SeasonMapper;
import com.ashuo.scms.service.SeasonService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author AShuo
 * @since 2021-09-07
 */
@Service
public class SeasonServiceImpl extends ServiceImpl<SeasonMapper, Season> implements SeasonService {
    @Autowired
    SeasonMapper seasonMapper;


    @Override
    public IPage<Season> getSeasonByCondition(Page<Season> page, Season season) {
        if (season == null) {
            return null;
        }
        IPage<Season> seasonList = seasonMapper.querySeasonBySeasonCondition(page, season);
        if (seasonList.getRecords().size() == 0) {
            return null;
        }
        return seasonList;
    }

    @Override
    public Season getSeasonById(Season season) {
        if (season == null || season.getSeasonId() == null || season.getSeasonId() == 0) {
            return null;
        }
        return seasonMapper.querySeasonById(season);
    }

    @Override

    public int addSeason(Season season) {
        if (season == null) {
            return 0;
        } else {
            int effNum = seasonMapper.insertSeason(season);
            if (effNum != 1) {
                return 0;
            } else {
                return effNum;
            }
        }
    }

    @Override
    public int removeSeason(int seasonId) {
        if (seasonId == 0) {
            return 0;
        } else {
            int effNum = seasonMapper.deleteSeason(seasonId);
            if (effNum != 1) {
                return 0;
            } else {
                return effNum;
            }
        }
    }

    @Override
    public int modifySeason(Season season) {
        if (season == null) {
            return 0;
        } else {
            int effNum = seasonMapper.updateSeason(season);
            if (effNum != 1) {
                return 0;
            } else {
                return effNum;
            }
        }
    }
}
