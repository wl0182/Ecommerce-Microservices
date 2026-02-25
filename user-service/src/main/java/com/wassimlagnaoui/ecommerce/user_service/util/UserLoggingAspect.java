package com.wassimlagnaoui.ecommerce.user_service.util;


import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Aspect
@Component
public class UserLoggingAspect {

    // before User Service methods
    @Before("execution(* com.wassimlagnaoui.ecommerce.user_service.Service.UserService.*(..))")
    public void logBeforeUserService() {
        System.out.println("UserService method starting...");
    }

    // after returning
    @AfterReturning(pointcut = "execution(* com.wassimlagnaoui.ecommerce.user_service.Service.UserService.*(..))", returning = "result")
    public void logAfterUserService(Object result) throws Throwable {
        System.out.println("Result: " + result);
        System.out.println("UserService method completed.");
    }

    // Around advice
    @Around("execution(* com.wassimlagnaoui.ecommerce.user_service.Service.UserService.*(..))")
    public Object logAroundUserService(ProceedingJoinPoint joinPoint) throws Throwable {
        LocalDateTime start = LocalDateTime.now();
        System.out.println("UserService method " + joinPoint.getSignature().getName() + " started at " + start);
        Object result = joinPoint.proceed();
        LocalDateTime end = LocalDateTime.now();
        System.out.println("UserService method " + joinPoint.getSignature().getName() + " ended at " + end);
        System.out.println("Duration: " + java.time.Duration.between(start, end).toMillis() + " ms");
        return result;

    }

}
