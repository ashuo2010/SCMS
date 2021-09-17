package com.ashuo.scms.service;

import com.ashuo.scms.entity.Team;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;


/**
 * <p>
 * 服务类
 * </p>
 *
 * @author ashuo
 * @since 2021-03-31
 */

public interface TeamService {
    IPage<Team> getAllTeam(Page<Team> page, Team team);

    Team getTeamByCondition(Team team);

    int addTeam(Team team);

    int modifyTeam(Team team);

    int removeTeam(int teamId);
}

