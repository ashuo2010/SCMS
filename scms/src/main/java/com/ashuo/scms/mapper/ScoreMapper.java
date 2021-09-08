package com.ashuo.scms.mapper;

import com.ashuo.scms.dto.AthleteScoreDto;
import com.ashuo.scms.entity.Score;
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
public interface ScoreMapper {
    //增加单个Score
    int insertScore(Score score);

    //修改Score
    int updateScore(Score score);

    //删除Score
    int deleteScore(int scoreId);

    //按条件查询Score
    IPage<Score> queryScoreByScoreCondition(Page<Score> page, @Param("score") Score score);

    List<Score> queryScoreByItemIdLimit(@Param("itemId") Integer itemId,@Param("condition") String condition);

    Score queryOneScoreByScoreId(@Param("scoreId") Integer scoreId);

    IPage<Score> queryScoreByTeamId(Page<Score> page, @Param("teamId") int teamId);

    IPage<AthleteScoreDto> queryAthleteScoreDto(Page<AthleteScoreDto> page , @Param("score") Score score);

}
