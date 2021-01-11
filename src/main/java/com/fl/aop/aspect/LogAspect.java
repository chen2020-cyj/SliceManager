package com.fl.aop.aspect;

import com.fl.aop.annotation.Log;
import com.fl.entity.OperationLog;
import com.fl.service.OperationLogService;
import com.fl.utils.GsonUtils;
import com.fl.utils.HttpContextUtils;
import com.fl.utils.JwtUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;

@Aspect
@Component
public class LogAspect {

    @Autowired
    private OperationLogService operationLogService;

    @Pointcut("@annotation(com.fl.aop.annotation.Log)")
    public void pointcut() {
    }

    @Around("pointcut()")
    public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable {

        Object result = joinPoint.proceed();

        handle(joinPoint);


        return result;
    }

    /**
     * 处理切面
     * @param point
     * @throws Throwable
     */
    public void handle(ProceedingJoinPoint point) throws Throwable {
        MethodSignature signature = (MethodSignature) point.getSignature();
        Method method = signature.getMethod();
        Log log = method.getAnnotation(Log.class);
        String value = "";
        if (log != null) {
            //注解上的描述
            value = log.value();
        }
        // 请求的方法名
        String className = point.getTarget().getClass().getName();
        String methodName = signature.getName();

        // 请求的方法参数值
        Object[] args = point.getArgs();
        // 请求的方法参数名称
        LocalVariableTableParameterNameDiscoverer u = new LocalVariableTableParameterNameDiscoverer();
        String[] paramNames = u.getParameterNames(method);
        String params = "";
        if (args != null && paramNames != null) {

            for (int i = 0; i < args.length; i++) {
                params += "  " + paramNames[i] + ": " + args[i];
            }
//            System.out.println("方法参数"+params);
        }

        String[] split = value.split(":");

        HttpServletRequest request = HttpContextUtils.getHttpServletRequest();
        String token = request.getHeader("token");
        String userId = JwtUtils.tokenInfo(token, "userId");

        updateLogInfo(split[1],Integer.valueOf(userId),params);
    }
    /**
     * 更新操作日志信息
     */
    public void updateLogInfo(String logInfo,Integer userId,String param){

        switch (logInfo){
            case "updateFilmInfo":
                insert(userId,"修改了电影信息"+ GsonUtils.toJson(param));
                break;
            case "addNewLanguage":
                insert(userId,"添加了新的语言"+ GsonUtils.toJson(param));
                break;
            case "register":
                insert(userId,"注册了一个账号"+ GsonUtils.toJson(param));
                break;
            case "updateMinio":
                insert(userId,"切换了一个新的桶"+ GsonUtils.toJson(param));
                break;
            case "addFilmSource":
                insert(userId,"添加一个任务"+ GsonUtils.toJson(param));
                break;
            case "delTask":
                insert(userId,"删除一个任务"+ GsonUtils.toJson(param));
                break;
            case "updateTask":

                insert(userId,"更新一个任务"+ GsonUtils.toJson(param));
                break;
            case "updateSystem":
                insert(userId,"更新系统值"+ GsonUtils.toJson(param));
                break;

        }
    }

    public void insert(Integer userId,String msg){
        OperationLog operationLog = new OperationLog();

        operationLog.setCreateTime(String.valueOf(System.currentTimeMillis()/1000));
        operationLog.setUserId(userId);
        operationLog.setMsg(msg);
        operationLogService.logInsert(operationLog);

    }

}
