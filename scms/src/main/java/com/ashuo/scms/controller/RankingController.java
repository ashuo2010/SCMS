package com.ashuo.scms.controller;


import com.ashuo.scms.common.lang.ServerResponse;
import com.ashuo.scms.entity.QueryInfo;
import com.ashuo.scms.entity.Ranking;
import com.ashuo.scms.entity.User;
import com.ashuo.scms.service.RankingService;
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
    public Object queryTeamRanking(QueryInfo queryInfo) {

        if (StringUtils.isBlank(queryInfo.getQuery())) {
            queryInfo.setQuery(null);
        }
        //分页查询
        Page<Ranking> page = new Page<Ranking>(queryInfo.getCurrentPage(), queryInfo.getPageSize());
        IPage<Ranking> rankingList = rankingService.getTeamTotalRanking(page, queryInfo.getQuery());
        return ServerResponse.createBySuccess(rankingList);
    }


    @ApiOperation("个人总排名")
    @GetMapping("/queryUserRanking")
    @RequiresAuthentication
    public Object queryUserRanking(QueryInfo queryInfo,Ranking ranking) {

        if (StringUtils.isBlank(queryInfo.getQuery())) {
            queryInfo.setQuery(null);
        }else {
            User user=ranking.getUser();
            if(user==null){
                user=new User();
            }
            user.setNickname(queryInfo.getQuery());
            ranking.setUser(user);
        }

        //分页查询
        Page<Ranking> page = new Page<Ranking>(queryInfo.getCurrentPage(), queryInfo.getPageSize());
        IPage<Ranking> rankingList = rankingService.getUserTotalRanking(page, ranking);
        return ServerResponse.createBySuccess(rankingList);
    }
}
