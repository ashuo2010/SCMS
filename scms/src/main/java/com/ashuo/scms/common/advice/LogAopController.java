package com.ashuo.scms.common.advice;

import com.ashuo.scms.entity.Syslog;
import com.ashuo.scms.entity.User;
import com.ashuo.scms.service.SyslogService;
import com.ashuo.scms.shiro.JwtToken;
import com.ashuo.scms.util.JwtUtil;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.time.LocalDateTime;

@Component
@Aspect
public class LogAopController {

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private SyslogService syslogService;

    @Transactional
    @Before("!execution(* com.ashuo.scms.service.SyslogService.*(..)) && execution(* com.ashuo.scms.service.*.add*(..))|| execution(* com.ashuo.scms.service.*.modify*(..)) || execution(* com.ashuo.scms.service.*.remove*(..)) ")
    public void doBefore(JoinPoint joinPoint) {

        Syslog sysLog = new Syslog();
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        String methodName = signature.getName();

        // 请求的方法参数值
        Object[] args = joinPoint.getArgs();
        // 请求的方法参数名称
        LocalVariableTableParameterNameDiscoverer ld = new LocalVariableTableParameterNameDiscoverer();
        String[] paramNames = ld.getParameterNames(method);
        if (args != null && paramNames != null) {
            String params = "";
            for (int i = 0; i < args.length; i++) {

                params += "  " + paramNames[i] + ": " + args[i];
            }
            sysLog.setParameter(params);
        }

        sysLog.setExecutionTime(LocalDateTime.now());
        User currentUser = new User();
        int userId = JwtUtil.getUserId(JwtToken.token);
        currentUser.setUserId(userId);
        sysLog.setExecutionUser(currentUser);

        StringBuilder tempMethodName = new StringBuilder();
        if (methodName.contains("Season")) {
            tempMethodName.append("届时");
        } else if (methodName.contains("Team")) {
            tempMethodName.append("团体");
        } else if (methodName.contains("User")) {
            tempMethodName.append("用户");
        } else if (methodName.contains("Item")) {
            tempMethodName.append("项目");
        } else if (methodName.contains("Athlete")) {
            tempMethodName.append("运动员");
        } else if (methodName.contains("Score")) {
            tempMethodName.append("分数");
        }
//        else if  (methodName.contains("Ranking")) {
//            tempMethodName.append("排名");
//        }
//        else if(methodName.contains("Record")) {
//            tempMethodName.append("记录");

        if (methodName.contains("add")) {
            tempMethodName.append("增加");
        } else if (methodName.contains("modify")) {
            tempMethodName.append("修改");
        } else {
            tempMethodName.append("删除");
        }
        sysLog.setMethod(tempMethodName.toString());
        //调用Service完成操作
        syslogService.addSyslog(sysLog);
    }

}
