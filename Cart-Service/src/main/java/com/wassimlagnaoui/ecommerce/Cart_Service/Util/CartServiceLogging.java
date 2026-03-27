package com.wassimlagnaoui.ecommerce.Cart_Service.Util;


import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;

@Slf4j
@Aspect
public class CartServiceLogging {
    // this class is used for logging

        /*
            * This method will log the execution of any method in the CartService class.
            * It uses the @Before advice to log a message before the method is executed.
        */

    @Before("execution(* com.wassimlagnaoui.ecommerce.Cart_Service.Service.CartService.*(..))")
    public void logBeforeCartServiceMethods() {
        log.info("Executing CartService method...");
        log.info("Mehod"+Thread.currentThread().getStackTrace()[2].getMethodName());
    }


    /*
        * This method will log any exceptions thrown by methods in the CartService class.
        * It uses the @AfterThrowing advice to catch exceptions and log them with an error level.
     */
    @AfterThrowing(pointcut = "execution(* com.wassimlagnaoui.ecommerce.Cart_Service.Service.CartService.*(..))", throwing = "ex")
    public void logCartServiceExceptions(Exception ex) {
        log.error("Exception in CartService method: " + ex.getMessage(), ex);
        log.info("Method"+Thread.currentThread().getStackTrace()[2].getMethodName());

    }


}

