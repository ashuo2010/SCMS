package com.ashuo.scms.controller;


import com.ashuo.scms.common.lang.ServerResponse;
import com.ashuo.scms.entity.Athlete;
import com.ashuo.scms.entity.QueryInfo;
import com.ashuo.scms.entity.User;
import com.ashuo.scms.service.AthleteService;
import com.ashuo.scms.service.ItemService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author AShuo
 * @since 2021-04-05
 */

@Api(tags = "项目报名接口")
@RestController
@Slf4j
@RequestMapping("/athlete")
public class AthleteController {
    @Autowired
    AthleteService athleteService;

    @Autowired
    ItemService itemService;
    @ApiOperation("查询运动员")
    @GetMapping("/queryAthlete")
    @RequiresAuthentication
    public Object queryAthlete(QueryInfo queryInfo, Athlete athlete) {
        //athlete.status传0查询所有(默认为0)，传1查询已记分，传2查询未记分
        if (StringUtils.isBlank(queryInfo.getQuery())) {
            queryInfo.setQuery(null);
        } else {
            User user = athlete.getUser();
            if (user == null) {
                user = new User();
            }
            user.setNickname(queryInfo.getQuery());
            athlete.setUser(user);
        }

        //分页查询
        Page<Athlete> page = new Page<>(queryInfo.getCurrentPage(), queryInfo.getPageSize());
        IPage<Athlete> athleteList = athleteService.getAthleteByCondition(page, athlete);
        return ServerResponse.createBySuccess(athleteList);
    }

    //报名
    @Transactional
    @ApiOperation("运动员报名")
    @PostMapping("/addAthlete")
    @RequiresAuthentication
    public Object addAthlete(@RequestBody Athlete athlete) {

        if (!SyslogController.systemStatus) {
            return ServerResponse.createByErrorCodeMessage(400, "报名失败，系统已关闭");
        }

        if (athlete == null) {
            return ServerResponse.createByErrorCodeMessage(400, "添加失败，Athlete信息为空");
        }

        //通过Athlete,uId和iId判断是否已经报过名了
        Page<Athlete> page = new Page<>(1, 999999999);
        if (athleteService.getAthleteByCondition(page, athlete).getRecords().size() != 0) {
            return ServerResponse.createByErrorCodeMessage(400, "报名失败，你已经报名过该项目了");
        }

        //判断用户是否已经报过超过3个项目
        Athlete tempAthlete = new Athlete();
        tempAthlete.setUser(athlete.getUser());
        if (athleteService.getAthleteByCondition(page, tempAthlete).getRecords().size() >= 3) {
            return ServerResponse.createByErrorCodeMessage(400, "报名失败，你已经报名超过3门项目了");
        }

        //设置报名时间
        athlete.setSignTime(LocalDateTime.now());
        //设置记分状态，为0表示尚未记分
        athlete.setStatus(0);
        int effNum = 0;
        effNum = athleteService.addAthlete(athlete);
        if (effNum == 0) {
            return ServerResponse.createByErrorCodeMessage(400, "添加失败");
        }
        return ServerResponse.createBySuccessMessage("添加成功");
    }

    @ApiOperation("取消报名")
    @DeleteMapping("/deleteAthlete")
    @RequiresAuthentication
    @Transactional
    public Object deleteAthlete(Integer athleteId) {

        if (!SyslogController.systemStatus) {
            return ServerResponse.createByErrorCodeMessage(400, "取消报名失败，系统已关闭");
        }

        int effNum = 0;
        try {
            effNum = athleteService.removeAthlete(athleteId);
        } catch (Exception e) {
            return ServerResponse.createByErrorCodeMessage(400, "删除失败");
        }
        if (effNum == 0) {
            return ServerResponse.createByErrorCodeMessage(400, "删除失败");
        }
        return ServerResponse.createBySuccessMessage("删除成功");
    }


}
