package com.ashuo.scms.shiro;


import com.ashuo.scms.common.consant.Consant;
import com.ashuo.scms.entity.User;
import com.ashuo.scms.service.UserService;
import com.ashuo.scms.util.JwtUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AccountRealm extends AuthorizingRealm {

    @Autowired
    private UserService userService;

    @Override
    public boolean supports(AuthenticationToken token) {
        return token instanceof JwtToken;
    }

    /**
     * 只有当需要检测用户权限的时候才会调用此方法
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        String username = JwtUtil.getUsername(principals.toString());
        IPage<User> userList = userService.getUserByCondition(new Page<>(Consant.MINCURRENTPAGE, Consant.MINPAGESIZE), new User().setUsername(username));
        SimpleAuthorizationInfo simpleAuthorizationInfo = new SimpleAuthorizationInfo();
        simpleAuthorizationInfo.addRole(String.valueOf(userList.getRecords().get(0).getUserType()));
        return simpleAuthorizationInfo;
    }

    /**
     * 默认使用此方法进行用户名正确与否验证。
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken auth) throws AuthenticationException {
        String token = (String) auth.getCredentials();
        // 解密获得username，用于和数据库进行对比
        String username = JwtUtil.getUsername(token);
        int userId = JwtUtil.getUserId(token);
        if (username == null) {
            throw new AuthenticationException("token invalid");
        }

        IPage<User> userList = userService.getUserByCondition(new Page<>(Consant.MINCURRENTPAGE, Consant.MINPAGESIZE), new User().setUsername(username));

        if (userList.getRecords() == null || userList.getRecords().size() == 0) {
            throw new AuthenticationException("user didn't existed!");
        }

        if (!JwtUtil.verify(token, username, userId)) {
            throw new AuthenticationException("username or password error");
        }

        return new SimpleAuthenticationInfo(token, token, "account_realm");
    }
}
