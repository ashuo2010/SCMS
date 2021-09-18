package com.ashuo.scms.service.impl;

import com.ashuo.scms.entity.Syslog;
import com.ashuo.scms.entity.Team;
import com.ashuo.scms.entity.User;
import com.ashuo.scms.mapper.SyslogMapper;
import com.ashuo.scms.mapper.TeamMapper;
import com.ashuo.scms.mapper.UserMapper;
import com.ashuo.scms.service.SyslogService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author AShuo
 * @since 2021-04-11
 */
@Service
public class SyslogServiceImpl implements SyslogService {
    @Autowired
    SyslogMapper syslogMapper;

    @Autowired
    TeamMapper teamMapper;

    @Autowired
    UserMapper userMapper;

    @Override
    public Page<Syslog> getAllSyslog(Page<Syslog> page, Syslog syslog) {
        Page<Syslog> syslogPage = syslogMapper.querySyslogBySyslogCondition(page, syslog);
        List<Syslog> syslogList = syslogPage.getRecords();
        for (Syslog s : syslogList) {
            s.setParameter(removeNullStr(s.getParameter()));
        }
        syslogPage.setRecords(syslogList);
        return syslogPage;
    }


    @Override
    public int addSyslog(Syslog syslog) {
        if (syslog == null) {
            return 0;
        } else {
            int effNum = syslogMapper.insertSyslog(syslog);
            if (effNum != 1) {
                return 0;
            } else {
                return effNum;
            }
        }
    }

    @Override
    @Transactional
    public boolean removeAllData() {
        //删除所有记录
        syslogMapper.deleteAllRecord();
        //删除所有排名
        syslogMapper.deleteAllRanking();
        //删除所有分数
        syslogMapper.deleteAllScore();
        //删除所有参赛运动员
        syslogMapper.deleteAllAthlete();
        //删除所有参赛项目
        syslogMapper.deleteAllItem();
        //删除所有系统日志
        syslogMapper.deleteAllSyslog();
        //删除所有用户
        syslogMapper.deleteAllUser();
        //删除所有团队
        syslogMapper.deleteAllTeam();
        //删除所有运动会
        syslogMapper.deleteAllSeason();

        //添加root用户,清空数据后用于登录系统
        Team rootTeam = new Team();
        rootTeam.setTeamId(1);
        rootTeam.setTeamName("超级管理员");
        rootTeam.setCreateTime(LocalDateTime.now());
        rootTeam.setEditTime(LocalDateTime.now());
        teamMapper.insertTeam(rootTeam);

        User rootUser = new User();
        rootUser.setUserId(1);
        rootUser.setUserNo("17140809011");
        rootUser.setNickname("AShuo");
        rootUser.setUsername("ashuo");
        rootUser.setPassword("e10adc3949ba59abbe56e057f20f883e");
        rootUser.setUserSex("男");
        rootUser.setUserType(1);
        rootUser.setTeam(rootTeam);
        rootUser.setPhone("1772726XXXX");
        rootUser.setCreateTime(LocalDateTime.now());
        rootUser.setEditTime(LocalDateTime.now());
        userMapper.insertUser(rootUser);
        //删除所有系统日志
        syslogMapper.deleteAllSyslog();
        return true;
    }

    //字符串去除null
    public String removeNullStr(String args) {
        String[] strings = args.split(",");
        String str = "";
        for (String s : strings) {
            if (!s.contains("null")) {
                str = str + s + ",";
            } else {
                if (s.contains(")")) {
                    str += ")";
                }
            }
        }
        return str;
    }
}
