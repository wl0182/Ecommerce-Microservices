package com.wassimlagnaoui.ecommerce.user_service.util;


import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@Aspect
@Component
public class MonitorAspect {

    // Before
    @Before("execution(* com.wassimlagnaoui.ecommerce.user_service.Service.UserService.*(..))")
    public void logBeforeMethod(JoinPoint joinPoint) {
        LocalDateTime start = LocalDateTime.now();
        log.info("Method "+ joinPoint.getSignature().getName() +" execution started at {}", start);
    }
    // AfterReturning
    @AfterReturning(pointcut = "execution(* com.wassimlagnaoui.ecommerce.user_service.Service.UserService.*(..))", returning = "result")
    public void logAfterMethod(JoinPoint joinPoint, Object result) {
        LocalDateTime end = LocalDateTime.now();
        log.info("Method "+ joinPoint.getSignature().getName() +" execution ended at {}", end);
    }

    // AfterThrowing [EXCEPTION] <methodName> threw <exceptionMessage>
    @AfterThrowing(pointcut = "execution(* com.wassimlagnaoui.ecommerce.user_service.Service.UserService.*(..))", throwing = "ex")
    public void logAfterThrowing(JoinPoint joinPoint, Throwable ex) {
        log.error("Method "+ joinPoint.getSignature().getName() +" threw exception: {}", ex.getMessage());
    }

    // Around : [TIME] <methodName> took X ms to execute
    @Around("execution(* com.wassimlagnaoui.ecommerce.user_service.Service.UserService.*(..))")
    public Object logAroundMethod(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        Object result = joinPoint.proceed();
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        log.info("Method "+ joinPoint.getSignature().getName()+" in class "+joinPoint.getTarget().getClass().getName() +" took {} ms to execute", duration);
        return result;
    }

}
