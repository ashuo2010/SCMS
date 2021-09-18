package com.ashuo.scms.mapper;

import com.ashuo.scms.entity.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author ashuo
 * @since 2021-03-29
 */
public interface UserMapper extends BaseMapper<User> {
    IPage<User> queryUserByUserCondition(Page<User> page, @Param("user") User user);

    int insertUser(User user);

    int deleteUser(int userId);

    int updateUser(@Param("user") User user);
}
