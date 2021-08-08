package com.ashuo.scms.service.impl;

import com.ashuo.scms.entity.User;
import com.ashuo.scms.mapper.UserMapper;
import com.ashuo.scms.service.UserService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author ashuo
 * @since 2021-03-29
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
    @Autowired
    UserMapper userMapper;


    @Override
    public IPage<User> getUserByCondition(Page<User> page, User user) {
        if (user == null) {
            return null;
        }
        IPage<User> userList = userMapper.queryUserByUserCondition(page, user);
        return userList;
    }

    @Override
    public int addUser(User user) {
        if (user == null) {
            return 0;
        } else {
            int effNum = userMapper.insertUser(user);
            if (effNum != 1) {
                return 0;
            } else {
                return effNum;
            }
        }
    }

    @Override
    public int removeUser(int userId) {
        if (userId == 0) {
            return 0;
        } else {
            int effNum = userMapper.deleteUser(userId);
            if (effNum != 1) {
                return 0;
            } else {
                return effNum;
            }
        }
    }

    @Override
    public int modifyUser(User user) {
        if (user == null) {
            return 0;
        } else {
            int effNum = userMapper.updateUser(user);
            if (effNum != 1) {
                return 0;
            } else {
                return effNum;
            }
        }
    }

}
