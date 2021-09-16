package com.ashuo.scms.mapper;

import com.ashuo.scms.entity.Syslog;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author AShuo
 * @since 2021-04-11
 */
public interface SyslogMapper {
    //增加单个Score
    int insertSyslog(Syslog syslog);


    //按条件查询Score
    Page<Syslog> querySyslogBySyslogCondition(Page<Syslog> page, @Param("syslog") Syslog syslog);

    //删除所有项目记录
    int deleteAllRecord();

    //删除所有项目排名
    int deleteAllRanking();

    //删除所有项目分数
    int deleteAllScore();

    //删除所有项目运动员
    int deleteAllAthlete();

    //删除所有参赛项目
    int deleteAllItem();

    //删除所有系统日志
    int deleteAllSyslog();

    //删除所有用户
    int deleteAllUser();

    //删除所有团队
    int deleteAllTeam();

    //删除所有运动会
    int deleteAllSeason();

}
