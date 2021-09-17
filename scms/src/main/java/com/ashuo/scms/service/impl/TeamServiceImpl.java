package com.ashuo.scms.service.impl;

import com.ashuo.scms.entity.Team;
import com.ashuo.scms.mapper.TeamMapper;
import com.ashuo.scms.service.TeamService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author ashuo
 * @since 2021-03-31
 */
@Service
public class TeamServiceImpl implements TeamService {
    @Autowired
    TeamMapper teamMapper;

    @Override
    public IPage<Team> getAllTeam(Page<Team> page, Team team) {
        IPage<Team> teamList = teamMapper.queryTeamByTeamName(page, team);
        return teamList;
    }


    @Override
    public Team getTeamByCondition(Team teamCondition) {
        Team team = teamMapper.queryTeamByTeamCondition(teamCondition);
        return team;
    }


    @Override
    public int addTeam(Team team) {
        if (team == null) {
            return 0;
        } else {
            team.setCreateTime(LocalDateTime.now());
            team.setEditTime(LocalDateTime.now());
            int effNum = teamMapper.insertTeam(team);
            if (effNum != 1) {
                return 0;
            } else {
                return effNum;
            }
        }
    }

    @Override
    public int modifyTeam(Team team) {
        if (team == null) {
            return 0;
        } else {
            int effNum = teamMapper.updateTeam(team);
            if (effNum != 1) {
                return 0;
            } else {
                return effNum;
            }
        }
    }

    @Override
    public int removeTeam(int teamId) {
        if (teamId == 0) {
            return 0;
        } else {
            int effNum = teamMapper.deleteTeam(teamId);
            if (effNum != 1) {
                return 0;
            } else {
                return effNum;
            }
        }
    }
}
