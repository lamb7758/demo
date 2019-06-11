package com.lamb7758.demo.aop.aspect;

import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Aspect
@Component
@Order(-5)
public class SysAspect {
	/***
     * 定义controller切入点拦截规则，拦截SystemControllerLog注解的方法
     */
//	 @Pointcut("@annotation(cn.lamb.aop.annotation.SystemControllerLog)")
    @Pointcut("execution(* com.lamb7758.demo.controller.*.*(..))")
    public void controllerAspect(){

    }
    
    
    @Around("controllerAspect()")
    public String recordLog(ProceedingJoinPoint joinPoint) throws Throwable {
    	System.out.println("@Around.............");
    	System.out.println(joinPoint.getSignature().getName());
    	
    	 Object proceed = joinPoint.proceed();
    	 proceed = joinPoint.proceed();
        String str = (String) proceed;

    	  HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
    	  String ip = request.getRemoteAddr();
    	  System.out.println("ip--->"+ip);

    	  return  str;
    }
    
    
    @AfterThrowing(pointcut = "controllerAspect()",throwing="e")
    public void doAfterThrowing(JoinPoint joinPoint,Throwable e) throws Throwable{
    	System.out.println(e.getClass().getName());
    	 HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
   	  String ip = request.getRemoteAddr();
   	  System.out.println("ip--->"+ip);
    	System.out.println("........"+e.getMessage());
    }
    
   @Before("controllerAspect()")
    public void brforelogging(JoinPoint joinPoint){ //注意 JoinPoint 的包       
        String methodName = joinPoint.getSignature().getName();
        List<Object> args = Arrays.asList(joinPoint.getArgs());
        System.out.println("The brforelogging methor"+ methodName+" begin with"+args);
    }
    
    @After("controllerAspect()")
    public void afterlogging(JoinPoint joinPoint){
        String methodName = joinPoint.getSignature().getName();
        System.out.println("The afterlogging method "+methodName+" end");
    }

    @AfterReturning(value="controllerAspect()",returning="result")
    public void afterreturninglogging(JoinPoint joinPoint,Object result){
        String methodName = joinPoint.getSignature().getName();
        System.out.println("The afterreturninglogging method "+methodName+" end with "+result);

    }

}
