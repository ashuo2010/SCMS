package com.ashuo.scms.service;

import com.ashuo.scms.entity.User;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author ashuo
 * @since 2021-03-29
 */
public interface UserService extends IService<User> {

    IPage<User> getUserByCondition(Page<User> page, User user);

    int addUser(User user);

    int removeUser(int userId);

    int modifyUser(User user);
}
