package com.ashuo.scms.controller;


import cn.hutool.crypto.SecureUtil;
import com.ashuo.scms.common.lang.ServerResponse;
import com.ashuo.scms.entity.QueryInfo;
import com.ashuo.scms.entity.Season;
import com.ashuo.scms.entity.Season;
import com.ashuo.scms.service.SeasonService;
import com.ashuo.scms.service.UserService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author AShuo
 * @since 2021-09-07
 */
@Api(tags = "用户接口")
@RestController
@Slf4j
@RequestMapping("/season")
public class SeasonController {
    @Autowired
    SeasonService seasonService;

    @ApiOperation("查询用户")
    @GetMapping("/querySeason")
    @RequiresAuthentication
    public Object querySeason(QueryInfo queryInfo, Season season) {
        if (StringUtils.isBlank(queryInfo.getQuery())) {
            queryInfo.setQuery(null);
        }
        //如果输入框有内容,则根据姓名搜索
        season.setSeasonName(queryInfo.getQuery());
        Page<Season> page = new Page(queryInfo.getCurrentPage(), queryInfo.getPageSize());
        IPage<Season> seasonList = seasonService.getSeasonByCondition(page, season);
        return ServerResponse.createBySuccess(seasonList);
    }


    @ApiOperation("添加用户")
    @PostMapping("/addSeason")
    @RequiresRoles(value = {"1"})
    public Object addSeason(@RequestBody Season season) {

        if (season == null) {
            return ServerResponse.createByErrorCodeMessage(400, "添加失败，Season信息为空");
        }

        Page<Season> page = new Page(1, 999999);

        if (seasonService.getSeasonByCondition(page, season) != null) {
            return ServerResponse.createByErrorCodeMessage(400, "添加失败，届时名称已存在");
        }
        //设置创建时间
        season.setCreateTime(LocalDateTime.now());
        season.setEditTime(LocalDateTime.now());
        int effNum = 0;
        try {
            effNum = seasonService.addSeason(season);
        } catch (Exception e) {
            return ServerResponse.createByErrorCodeMessage(400, "添加失败");
        }
        if (effNum == 0) {
            return ServerResponse.createByErrorCodeMessage(400, "添加失败");
        }
        return ServerResponse.createBySuccessMessage("添加成功");
    }


    @ApiOperation("删除用户")
    @DeleteMapping("/deleteSeason")
    @RequiresRoles(value = {"1"})
    public Object deleteSeason(Integer seasonId) {
        int effNum = seasonService.removeSeason(seasonId);
        if (effNum <= 0) {
            return ServerResponse.createByErrorCodeMessage(400, "删除失败");
        }
        return ServerResponse.createBySuccessMessage("删除成功");
    }

    @ApiOperation("获取团体信息")
    @GetMapping("/getSeason")
    @RequiresRoles(value = {"1"})
    public Object getSeason(Season seasonCondition) {
        Season season = seasonService.getSeasonById(seasonCondition);
        if (season != null) {
            return ServerResponse.createBySuccess(season);
        }
        return ServerResponse.createByErrorMessage("查询不到该Season信息");
    }


    @ApiOperation("修改用户")
    @PutMapping("/editSeason")
    @RequiresRoles(value = {"1"})
    public Object editSeason(@RequestBody Season season) {
        if (season == null) {
            return ServerResponse.createByErrorCodeMessage(400, "修改失败，届时信息为空");
        }

        Page<Season> page = new Page(1, 999999);
        if (seasonService.getSeasonByCondition(page, season) != null) {
            return ServerResponse.createByErrorCodeMessage(400, "添加失败，届时名称已存在");
        }

        season.setEditTime(LocalDateTime.now());
        int effNum = 0;
        try {
            effNum = seasonService.modifySeason(season);
        } catch (Exception e) {
            return ServerResponse.createByErrorCodeMessage(400, "修改失败");
        }
        if (effNum == 0) {
            return ServerResponse.createByErrorCodeMessage(400, "修改失败");
        }
        return ServerResponse.createBySuccessMessage("修改成功");
    }

}
