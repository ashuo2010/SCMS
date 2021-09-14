package com.ashuo.scms.mapper;

import com.ashuo.scms.dto.AthleteScoreDto;
import com.ashuo.scms.entity.Athlete;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import java.util.List;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author AShuo
 * @since 2021-04-05
 */
public interface AthleteMapper {
    //增加Athlete
    int insertAthlete(Athlete athlete);

    //按条件查询Athlete列表
    IPage<Athlete> queryAthleteByAthleteCondition(Page<Athlete> page, Athlete athlete);

    int updateAthlete(Athlete athlete);

    int deleteAthlete(int athleteId);

}