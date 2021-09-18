package com.ashuo.scms.service;

import com.ashuo.scms.dto.AthleteScoreDto;
import com.ashuo.scms.entity.Score;
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
public interface ScoreService {

    IPage<Score> getScoreByScoreCondition(Page<Score> page, Score score);


    IPage<AthleteScoreDto> getAthleteScoreDto(Page<AthleteScoreDto> page, Score score);


    List<Score> getScoreByItemIdLimit(int itemId, String condition);

    Score getOneScoreByScoreId(int scoreId);

    int addScore(Score score);

    int modifyScore(Score score);


}
