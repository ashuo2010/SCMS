package com.ashuo.scms.controller;


import com.ashuo.scms.common.lang.ServerResponse;
import com.ashuo.scms.entity.QueryInfo;
import com.ashuo.scms.entity.Syslog;
import com.ashuo.scms.entity.User;
import com.ashuo.scms.service.SyslogService;
import com.ashuo.scms.util.ObjectUtils;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;
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

    public static boolean systemStatus = true;
    @Autowired
    SyslogService syslogService;
    @Value("${meet.you.year}")
    private String meetYouYear;

    @ApiOperation("查询系统操作日志")
    @GetMapping("/querySyslog")
    @RequiresRoles(value = {"1"})
    public ServerResponse querySyslog(QueryInfo queryInfo, Syslog syslog) {
        User user = ObjectUtils.isNull(syslog.getExecutionUser()) ? new User() : syslog.getExecutionUser();
        user.setNickname(queryInfo.getQuery());
        syslog.setExecutionUser(user);

        //分页查询
        Page<Syslog> page = new Page<>(queryInfo.getCurrentPage(), queryInfo.getPageSize());
        IPage<Syslog> syslogList = syslogService.getAllSyslog(page, syslog);
        return ServerResponse.createBySuccess(syslogList);
    }


    @ApiOperation("获取报名系统状态")
    @PostMapping("/getSystemStatus")
    @RequiresAuthentication
    public ServerResponse getSystemStatus() {
        return ServerResponse.createBySuccess(systemStatus);
    }

    @ApiOperation("修改报名系统状态")
    @PostMapping("/switchSystemStatus")
    @RequiresAuthentication
    public ServerResponse switchSystemStatus() {
        systemStatus = !systemStatus;
        return ServerResponse.createBySuccess(systemStatus);
    }


    @ApiOperation("清空系统数据")
    @DeleteMapping("/resetAllData")
    @RequiresRoles(value = {"1"})
    @Transactional
    public ServerResponse resetAllData(String comfirmPassword) {
        if (!comfirmPassword.equals(meetYouYear)) {
            return ServerResponse.createByErrorCodeMessage(400, "密码输入错误");
        }
        boolean status = syslogService.removeAllData();
        if (!status) {
            return ServerResponse.createByErrorCodeMessage(400, "清空失败");
        }
        return ServerResponse.createBySuccessMessage("清空成功");
    }

}
