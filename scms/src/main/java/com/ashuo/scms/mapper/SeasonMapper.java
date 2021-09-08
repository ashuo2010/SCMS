package com.ashuo.scms.mapper;

import com.ashuo.scms.entity.Season;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author AShuo
 * @since 2021-09-07
 */

public interface SeasonMapper extends BaseMapper<Season> {
    IPage<Season> querySeasonBySeasonCondition(Page<Season> page, @Param("season") Season season);

    Season querySeasonById(Season season);

    int insertSeason(Season season);

    int deleteSeason(int seasonId);

    int updateSeason(@Param("season") Season season);
}
