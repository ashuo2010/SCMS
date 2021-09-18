package com.ashuo.scms.controller;


import com.ashuo.scms.common.consant.Consant;
import com.ashuo.scms.common.lang.ServerResponse;
import com.ashuo.scms.entity.Athlete;
import com.ashuo.scms.entity.Item;
import com.ashuo.scms.entity.QueryInfo;
import com.ashuo.scms.entity.User;
import com.ashuo.scms.service.AthleteService;
import com.ashuo.scms.service.ItemService;
import com.ashuo.scms.service.UserService;
import com.ashuo.scms.util.ObjectUtils;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

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

    @Autowired
    UserService userService;

    @ApiOperation("查询运动员")
    @GetMapping("/queryAthlete")
    @RequiresAuthentication
    public ServerResponse queryAthlete(QueryInfo queryInfo, Athlete athlete) {

        User user = ObjectUtils.isNull(athlete.getUser()) ? new User() : athlete.getUser();
        user.setNickname(queryInfo.getQuery());
        athlete.setUser(user);

        if (athlete.getUser() != null && athlete.getUser().getUserId() != null) {
            //加上查询多人项目
            athlete.setUserIds(athlete.getUser().getUserId().toString());
        }
        //分页查询
        Page<Athlete> page = new Page<>(queryInfo.getCurrentPage(), queryInfo.getPageSize());
        IPage<Athlete> athleteList = athleteService.getAthleteByCondition(page, athlete);
        for (Athlete a : athleteList.getRecords()) {
            //如果是多人项目，拼接名字多个运动员名字
            if (a.getUserIds() != null) {
                String[] userIds = a.getUserIds().split(",");
                List<User> userList = new ArrayList<>();
                for (String uId : userIds) {
                    User tempUser = new User();
                    tempUser.setUserId(Integer.valueOf(uId));
                    userList.addAll(userService.getUserByCondition(new Page<>(Consant.MINCURRENTPAGE, Consant.MAXPAGESIZE), tempUser).getRecords());
                }
                String userNickname = "";
                for (User u : userList) {
                    userNickname += u.getNickname() + ",";
                }
                //删除最后一个逗号
                userNickname = userNickname.substring(0, userNickname.length() - 1);
                a.getUser().setNickname(userNickname);
            }
        }


        return ServerResponse.createBySuccess(athleteList);
    }


    @ApiOperation("运动员报名")
    @PostMapping("/addAthlete")
    @RequiresAuthentication
    @Transactional(rollbackFor = Exception.class)
    public ServerResponse addAthlete(@RequestBody Athlete athlete) throws Exception {

        if (athlete == null) {
            return ServerResponse.createByErrorCodeMessage(400, "添加失败，报名信息为空");
        }

        Item item = itemService.getOneItemByCondition(athlete.getItem());
        if (item != null && item.getSeason() != null && item.getSeason().getSeasonStatus() == 0) {
            return ServerResponse.createByErrorCodeMessage(400, "报名失败，该届运动会已结束");
        }

        if (!SyslogController.systemStatus) {
            return ServerResponse.createByErrorCodeMessage(400, "报名失败，系统已关闭");
        }

        //通过Athlete,uId和iId判断是否已经报过名了
        Page<Athlete> page = new Page<>(Consant.MINCURRENTPAGE, Consant.MAXPAGESIZE);
        if (athleteService.getAthleteByCondition(page, athlete).getRecords().size() != 0) {
            return ServerResponse.createByErrorCodeMessage(400, "报名失败，你已经报名过该项目了");
        }

        //如果是单人参赛项目，判断用户是否已经报过超过3个项目
        if (item.getItemAmount() == 1) {
            Athlete tempAthlete = new Athlete();
            tempAthlete.setUser(athlete.getUser());
            if (athleteService.getAthleteByCondition(new Page<>(Consant.MINCURRENTPAGE, Consant.MAXPAGESIZE), tempAthlete).getRecords().size() >= 3) {
                return ServerResponse.createByErrorCodeMessage(400, "报名失败，你已经报名超过3门项目了");
            }
        } else if (item.getItemAmount() > 1) {
            //如果是多人参赛项目,查询该项目的ids,判断报名的运动员中是否已经加入了其他队伍
            Athlete tempAthlete = new Athlete();
            tempAthlete.setItem(item);
            List<Athlete> athleteList = athleteService.getAthleteByCondition(new Page<>(Consant.MINCURRENTPAGE, Consant.MAXPAGESIZE), tempAthlete).getRecords();
            String[] userIds = athlete.getUserIds().split(",");
            if (userIds.length < item.getItemAmount()) {
                return ServerResponse.createByErrorCodeMessage(400, "报名失败，报名该项目的人数不够");
            } else if (userIds.length > item.getItemAmount()) {
                return ServerResponse.createByErrorCodeMessage(400, "报名失败，报名该项目的人数超出限制");
            }
            for (String uId : userIds) {
                //lambda表达式，如果有匹配的，collectList就不会为空
                List<Athlete> collectList = athleteList.stream().filter(a -> Arrays.stream(a.getUserIds().split(",")).anyMatch(athleteUserId -> athleteUserId.equals(uId))).collect(Collectors.toList());

                //如果列表不为空，表示至少有一个运动员已经报名过该项目
                if (collectList != null && collectList.size() > 0) {
                    Class<User> userClass = User.class;
                    User user = userClass.getDeclaredConstructor().newInstance();
                    user.setUserId(Integer.valueOf(uId));
                    user = userService.getUserByCondition(new Page<>(Consant.MINCURRENTPAGE, Consant.MINPAGESIZE), user).getRecords().get(0);
                    return ServerResponse.createByErrorCodeMessage(400, "报名失败，" + user.getNickname() + "已经报名过该项目了");
                }
            }
        } else {
            return ServerResponse.createByErrorCodeMessage(400, "项目参赛人数设置为0，无法报名");
        }

        //设置报名时间
        athlete.setSignTime(LocalDateTime.now());
        //设置记分状态，为0表示尚未记分\
        athlete.setScoreStatus(0);
        int effNum = athleteService.addAthlete(athlete);
        if (effNum == 0) {
            return ServerResponse.createByErrorCodeMessage(400, "报名失败");
        }
        return ServerResponse.createBySuccessMessage("报名成功");
    }

    @ApiOperation("取消报名")
    @DeleteMapping("/deleteAthlete")
    @RequiresAuthentication
    @Transactional(rollbackFor = Exception.class)
    public ServerResponse deleteAthlete(Integer athleteId) {
        Page<Athlete> page = new Page(1, 1);
        Athlete athlete = new Athlete();
        athlete.setAthleteId(athleteId);
        Item item = athleteService.getAthleteByCondition(page, athlete).getRecords().get(0).getItem();
        if (item != null && item.getSeason() != null && item.getSeason().getSeasonStatus() == 0) {
            return ServerResponse.createByErrorCodeMessage(400, "报名失败，该届运动会已结束");
        }
        if (!SyslogController.systemStatus) {
            return ServerResponse.createByErrorCodeMessage(400, "取消报名失败，系统已关闭");
        }

        int effNum = athleteService.removeAthlete(athleteId);
        if (effNum == 0) {
            return ServerResponse.createByErrorCodeMessage(400, "取消报名失败");
        }
        return ServerResponse.createBySuccessMessage("取消报名成功");
    }


}
