package com.ashuo.scms.mapper;

import com.ashuo.scms.entity.Team;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author ashuo
 * @since 2021-03-31
 */
public interface TeamMapper {
    //增加Team
    int insertTeam(Team team);

    //修改Team
    int updateTeam(Team team);

    //删除Team
    int deleteTeam(int teamId);

    //按条件查询Team
    IPage<Team> queryTeamByTeamName(Page<Team> page, @Param("team") Team team);


    Team queryTeamByTeamCondition(@Param("team") Team team);

}
