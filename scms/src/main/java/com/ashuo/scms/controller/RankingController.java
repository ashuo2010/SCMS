package com.ashuo.scms.controller;


import com.ashuo.scms.common.lang.ServerResponse;
import com.ashuo.scms.entity.*;
import com.ashuo.scms.service.RankingService;
import com.ashuo.scms.util.ObjectUtils;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;



/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author AShuo
 * @since 2021-04-05
 */
@Api(tags = "团体总分排名接口")
@RestController
@Slf4j
@RequestMapping("/ranking")
public class RankingController {

    @Autowired
    RankingService rankingService;

    @ApiOperation("团体总排名")
    @GetMapping("/queryTeamRanking")
    @RequiresAuthentication
    public ServerResponse queryTeamRanking(QueryInfo queryInfo,Ranking ranking) {
        Athlete athlete= ObjectUtils.isNull(ranking.getAthlete())? new Athlete(): ranking.getAthlete();
        User user =ObjectUtils.isNull(athlete.getUser())? new User():athlete.getUser();
        Team team =ObjectUtils.isNull(user.getTeam())? new Team():user.getTeam();

        team.setTeamName(queryInfo.getQuery());
        user.setTeam(team);
        athlete.setUser(user);
        ranking.setAthlete(athlete);

        //分页查询
        Page<Ranking> page = new Page<>(queryInfo.getCurrentPage(), queryInfo.getPageSize());
        IPage<Ranking> rankingList = rankingService.getTeamTotalRanking(page, ranking);
        return ServerResponse.createBySuccess(rankingList);
    }


    @ApiOperation("个人总排名")
    @GetMapping("/queryUserRanking")
    @RequiresAuthentication
    public ServerResponse queryUserRanking(QueryInfo queryInfo,Ranking ranking) {
        Athlete athlete= ObjectUtils.isNull(ranking.getAthlete())? new Athlete(): ranking.getAthlete();
        User user =ObjectUtils.isNull(athlete.getUser())? new User():athlete.getUser();

        user.setNickname(queryInfo.getQuery());
        athlete.setUser(user);
        ranking.setAthlete(athlete);

        //分页查询
        Page<Ranking> page = new Page<>(queryInfo.getCurrentPage(), queryInfo.getPageSize());
        IPage<Ranking> rankingList = rankingService.getUserTotalRanking(page, ranking);
        return ServerResponse.createBySuccess(rankingList);
    }
}
