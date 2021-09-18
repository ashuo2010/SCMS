package com.ashuo.scms.controller;


import cn.hutool.crypto.SecureUtil;
import com.ashuo.scms.common.consant.Consant;
import com.ashuo.scms.common.lang.ServerResponse;
import com.ashuo.scms.dto.UserDto;
import com.ashuo.scms.entity.QueryInfo;
import com.ashuo.scms.entity.User;
import com.ashuo.scms.service.UserService;
import com.ashuo.scms.util.JwtUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author AShuo
 * @since 2021-04-05
 */

@Api(tags = "用户接口")
@RestController
@Slf4j
@RequestMapping("/user")
public class UserController {
    @Autowired
    UserService userService;

    @ApiOperation("用户登录")
    @PostMapping("/login")
    public ServerResponse login(@Validated @RequestBody UserDto userDto, HttpServletResponse response) {
        if (userDto.getUsername() == null) {
            return ServerResponse.createByErrorMessage("用户名不能为空");
        }
        if (userDto.getPassword() == null) {
            return ServerResponse.createByErrorMessage("密码不能为空");
        }
        User tempUser = new User();
        tempUser.setUsername(userDto.getUsername());

        //用户名唯一，所以肯定只返回一个
        Page<User> page = new Page<>(Consant.MINCURRENTPAGE, Consant.MINPAGESIZE);
        IPage<User> userList = userService.getUserByCondition(page, tempUser);
        if (userList.getRecords() == null || userList.getRecords().size() == 0) {
            return ServerResponse.createByErrorCodeMessage(400, "用户不存在");
        }
        User user = userList.getRecords().get(0);
        if (!user.getPassword().equals(SecureUtil.md5(userDto.getPassword()))) {
            return ServerResponse.createByErrorCodeMessage(400, "用户名或密码错误");
        }
        String jwt = JwtUtil.sign(user.getUsername(), user.getUserId());
        response.setHeader("Authorization", jwt);
        response.setHeader("Access-Control-Expose-Headers", "Authorization");
        return ServerResponse.createBySuccess(user);
    }


    @ApiOperation("用户注销登录")
    @GetMapping("/logout")
    public ServerResponse logout() {
        SecurityUtils.getSubject().logout();
        return ServerResponse.createBySuccess("注销登录成功");
    }

    @ApiOperation("修改密码")
    @PutMapping("/changePwd")
    @RequiresAuthentication
    public ServerResponse changePwd(@RequestBody UserDto userDto) {
        if (StringUtils.isEmpty(userDto.getUsername()) || StringUtils.isEmpty(userDto.getPassword()) || StringUtils.isEmpty(userDto.getNewPassword())) {
            return ServerResponse.createByErrorCodeMessage(400, "修改失败，用户信息为空");
        }
        User user = new User();
        user.setUsername(userDto.getUsername());
        //密码MD5加密
        user.setPassword(SecureUtil.md5(userDto.getPassword()));

        //用户名唯一，所以肯定只返回一个
        Page<User> page = new Page<>(Consant.MINCURRENTPAGE, Consant.MINPAGESIZE);
        IPage<User> userList = userService.getUserByCondition(page, user);
        if (userList.getRecords() == null || userList.getRecords().size() == 0) {
            return ServerResponse.createByErrorMessage("密码错误");
        }
        //新密码MD5加密
        user.setUserId(userList.getRecords().get(0).getUserId());
        user.setPassword(SecureUtil.md5(userDto.getNewPassword()));
        user.setEditTime(LocalDateTime.now());
        int effNum = 0;
        try {
            effNum = userService.modifyUser(user);
        } catch (Exception e) {
            return ServerResponse.createByErrorCodeMessage(400, "修改密码失败");
        }
        if (effNum == 0) {
            return ServerResponse.createByErrorCodeMessage(400, "修改密码失败");
        }
        return ServerResponse.createBySuccessMessage("修改密码成功");
    }


    @ApiOperation("查询用户")
    @GetMapping("/queryUser")
    @RequiresAuthentication
    public ServerResponse queryUser(QueryInfo queryInfo, User user) {
        user.setNickname(queryInfo.getQuery());
        Page<User> page = new Page(queryInfo.getCurrentPage(), queryInfo.getPageSize());
        IPage<User> userList = userService.getUserByCondition(page, user);
        return ServerResponse.createBySuccess(userList);
    }


    @ApiOperation("添加用户")
    @PostMapping("/addUser")
    @RequiresRoles(value = {"1"})
    public ServerResponse addUser(@RequestBody User user) {

        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(400, "添加失败，用户信息为空");
        }

        Page<User> page = new Page(1, 999999);

        //根据学号判断是否已经添加过了
        User tempUser = new User();
        tempUser.setUserNo(user.getUserNo());
        IPage<User> userList = userService.getUserByCondition(page, tempUser);

        if (userList.getRecords() != null && (userList.getRecords().size() != 0)) {
            return ServerResponse.createByErrorCodeMessage(400, "添加失败，用户已存在");
        }
        //密码md5加密
        user.setPassword(SecureUtil.md5(user.getPassword()));
        //设置创建时间
        user.setCreateTime(LocalDateTime.now());
        user.setEditTime(LocalDateTime.now());
        int effNum = 0;
        try {
            effNum = userService.addUser(user);
        } catch (Exception e) {
            return ServerResponse.createByErrorCodeMessage(400, "添加失败");
        }
        if (effNum == 0) {
            return ServerResponse.createByErrorCodeMessage(400, "添加失败");
        }
        return ServerResponse.createBySuccessMessage("添加成功");
    }


    @ApiOperation("删除用户")
    @DeleteMapping("/deleteUser")
    @RequiresRoles(value = {"1"})
    public ServerResponse deleteUser(Integer userId) {
        int effNum = userService.removeUser(userId);
        if (effNum <= 0) {
            return ServerResponse.createByErrorCodeMessage(400, "删除失败");
        }
        return ServerResponse.createBySuccessMessage("删除成功");
    }


    @ApiOperation("修改用户")
    @PutMapping("/editUser")
    @RequiresRoles(value = {"1"})
    public ServerResponse editUser(@RequestBody User user) {
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(400, "修改失败，用户信息为空");
        }
        //如果密码不为空,密码MD5加密
        if (!StringUtils.isBlank(user.getPassword())) {
            user.setPassword(SecureUtil.md5(user.getPassword()));
        }

        user.setEditTime(LocalDateTime.now());
        int effNum = 0;
        try {
            effNum = userService.modifyUser(user);
        } catch (Exception e) {
            return ServerResponse.createByErrorCodeMessage(400, "修改失败");
        }
        if (effNum == 0) {
            return ServerResponse.createByErrorCodeMessage(400, "修改失败");
        }
        return ServerResponse.createBySuccessMessage("修改成功");
    }

}
