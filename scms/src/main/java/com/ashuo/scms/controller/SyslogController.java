package com.ashuo.scms.controller;


import com.ashuo.scms.common.lang.ServerResponse;
import com.ashuo.scms.entity.QueryInfo;
import com.ashuo.scms.entity.Syslog;
import com.ashuo.scms.entity.User;
import com.ashuo.scms.service.SyslogService;
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

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author AShuo
 * @since 2021-04-12
 */
@Api(tags = "系统日志接口")
@RestController
@Slf4j
@RequestMapping("/syslog")
public class SyslogController {

    public  static  boolean systemStatus=true;

    @Autowired
    SyslogService syslogService;

    @ApiOperation("查询系统操作日志")
    @GetMapping("/querySyslog")
    @RequiresRoles(value = {"1"})
    public Object querySyslog(QueryInfo queryInfo, Syslog syslog) {

        if (StringUtils.isBlank(queryInfo.getQuery())) {
            queryInfo.setQuery(null);
        }else {
            User user = syslog.getExecutionUser();
            if (user == null) {
                user = new User();
            }
            user.setNickname(queryInfo.getQuery());
            syslog.setExecutionUser(user);
        }
        //分页查询
        Page<Syslog> page = new Page<Syslog>(queryInfo.getCurrentPage(), queryInfo.getPageSize());
        IPage<Syslog> syslogList = syslogService.getAllSyslog(page,syslog);
        return ServerResponse.createBySuccess(syslogList);
    }


    @ApiOperation("获取报名系统状态")
    @PostMapping("/getSystemStatus")
    @RequiresAuthentication
    public  ServerResponse getSystemStatus() {
        return ServerResponse.createBySuccess(systemStatus);
    }

    @ApiOperation("修改报名系统状态")
    @PostMapping("/switchSystemStatus")
    @RequiresAuthentication
    public  ServerResponse switchSystemStatus() {
        systemStatus=!systemStatus;
        return ServerResponse.createBySuccess(systemStatus);
    }

    @ApiOperation("清空项目排名分数运动员数据")
    @DeleteMapping("/d")
    @RequiresRoles(value = {"1"})
    public Object resetRangkingScoreAthlete(Integer itemId) {
        int effNum = 0;
        try {
            effNum = syslogService.removeAllRankingScoreAthlete();
        } catch (Exception e) {
            return ServerResponse.createByErrorCodeMessage(400, "清空失败");
        }
        if (effNum == 0) {
            return ServerResponse.createByErrorCodeMessage(400, "清空失败");
        }
        return ServerResponse.createBySuccessMessage("清空成功");
    }

    @ApiOperation("清空系统数据")
    @DeleteMapping("/resetAllData")
    @RequiresRoles(value = {"1"})
    public Object resetAllData(Integer itemId) {
        int effNum = 0;
        try {
            effNum = syslogService.removeAllData();
        } catch (Exception e) {
            return ServerResponse.createByErrorCodeMessage(400, "清空失败");
        }
        if (effNum == 0) {
            return ServerResponse.createByErrorCodeMessage(400, "清空失败");
        }
        return ServerResponse.createBySuccessMessage("清空成功");
    }

}
