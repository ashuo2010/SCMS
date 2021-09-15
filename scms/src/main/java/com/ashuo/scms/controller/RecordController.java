package com.ashuo.scms.controller;


import com.ashuo.scms.common.lang.ServerResponse;
import com.ashuo.scms.entity.*;
import com.ashuo.scms.service.RecordService;
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
 * @since 2021-09-10
 */
@Api(tags = "项目分数接口")
@Slf4j
@RestController
@RequestMapping("/record")
public class RecordController {
    @Autowired
    RecordService recordService;
    
    @ApiOperation("查询运动员项目记录")
    @GetMapping("/queryRecord")
    @RequiresAuthentication
    public Object queryRecord(QueryInfo queryInfo, Record record) {
        if (StringUtils.isBlank(queryInfo.getQuery())) {
            queryInfo.setQuery(null);
        }else {
            Athlete athlete=new Athlete();
            User user=new User();
            user.setNickname(queryInfo.getQuery());
            if (record==null){
                record=new Record();
            }
            athlete.setUser(user);
            record.setAthlete(athlete);
        }

        //分页查询
        Page<Record> page = new Page<Record>(queryInfo.getCurrentPage(), queryInfo.getPageSize());

        IPage<Record> recordList = recordService.getRecordByRecordCondition(page, record);
        return ServerResponse.createBySuccess(recordList);
    }


}
