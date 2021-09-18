package com.ashuo.scms.service;

import com.ashuo.scms.entity.Syslog;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author AShuo
 * @since 2021-04-11
 */
public interface SyslogService {
    IPage<Syslog> getAllSyslog(Page<Syslog> page, Syslog syslog);

    int addSyslog(Syslog syslog);

    boolean removeAllData();
}
