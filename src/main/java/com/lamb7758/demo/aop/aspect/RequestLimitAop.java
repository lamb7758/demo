package com.lamb7758.demo.aop.aspect;

import com.lamb7758.demo.aop.annotation.RequestLimit;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;


@Component
@Aspect
@Order(-5)
public class RequestLimitAop {

    private static Log log = LogFactory.getLog(RequestLimitAop.class);
//    @Pointcut("execution(* com.lamb7758.demo.controller.*.*(..))")
//    @Pointcut("@annotation(com.lamb7758.demo.aop.annotation.RequestLimit)")
////    public void controllerAspect(){
////        System.out.println("********************************************");
////    }


        @Before("within(@org.springframework.web.bind.annotation.RestController *) && @annotation(limit)")
//    @Before("within(@com.lamb7758.demo.controller.* *) && @annotation(limit)")
    public void requestLimit(JoinPoint joinPoint, RequestLimit limit) throws Exception {
        try {
            Object[] args = joinPoint.getArgs();
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();

//            HttpServletResponse response = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getResponse();
//            response.setContentType("text/html;charset=UTF-8");
//            response.setCharacterEncoding("UTF-8");
//            PrintWriter out;
//            try {
//                out = response.getWriter();
//                out.print("请求过于频繁,超出限制!");
//                out.flush();
//                out.close();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
            String ip = getIpAddress(request);
            String url = request.getRequestURL().toString();
            String key = "req_limit_".concat(url).concat("_").concat(ip);
            boolean checkResult = checkWithRedis(limit, key);
            if (!checkResult) {
                log.debug("requestLimited," + "[用户ip:{}],[访问地址:{}]超过了限定的次数[{}]次");
                throw new Exception("请求过于频繁,超出限制!");
            }
        }  catch (Exception e) {
            log.error("RequestLimitAop.requestLimit has some exceptions: ", e);
            throw e;
        }
    }

    /**
     * 以redis实现请求记录
     *
     * @param limit
     * @param key
     * @return
     */
    private boolean checkWithRedis(RequestLimit limit, String key) {
//        long count = stringRedisTemplate.opsForValue().increment(key, 1);
//        if (count == 1) {
//            stringRedisTemplate.expire(key, limit.time(), TimeUnit.MILLISECONDS);
//        }
//        if (count > limit.count()) {
//            return false;
//        }
        return true;
    }



    /**
     * 获取用户真实IP地址，不使用request.getRemoteAddr()的原因是有可能用户使用了代理软件方式避免真实IP地址,
     * 可是，如果通过了多级反向代理的话，X-Forwarded-For的值并不止一个，而是一串IP值
     *
     * @return ip
     */
    private String getIpAddress(HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for");
        log.info("x-forwarded-for ip: " + ip);
        if (ip != null && ip.length() != 0 && !"unknown".equalsIgnoreCase(ip)) {
            // 多次反向代理后会有多个ip值，第一个ip才是真实ip
            if( ip.indexOf(",")!=-1 ){
                ip = ip.split(",")[0];
            }
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
            log.info("Proxy-Client-IP ip: " + ip);
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
            log.info("WL-Proxy-Client-IP ip: " + ip);
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
            log.info("HTTP_CLIENT_IP ip: " + ip);
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
            log.info("HTTP_X_FORWARDED_FOR ip: " + ip);
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
            log.info("X-Real-IP ip: " + ip);
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
            log.info("getRemoteAddr ip: " + ip);
        }
        log.info("获取客户端ip: " + ip);
        return ip;
    }

}
