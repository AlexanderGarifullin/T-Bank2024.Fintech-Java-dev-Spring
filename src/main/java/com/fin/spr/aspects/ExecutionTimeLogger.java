package com.fin.spr.aspects;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;


/**
 * The {@code ExecutionTimeLogger} class is an aspect that logs the execution time
 * of methods annotated with {@link com.fin.spr.annotations.LogExecutionTime}.
 * This class uses Aspect-Oriented Programming (AOP) to intercept method calls
 * and record their execution duration.
 *
 * <p>
 * The {@code logExecutionTime} method is invoked around the execution of the
 * target method. It calculates the time taken to execute the method and logs
 * this information along with the method's name and its declaring class.
 * </p>
 *
 * <p>
 * If any exception occurs during the execution of the method, it is logged as an error.
 * </p>
 *
 * <p>
 * This class is marked with the {@code @Component} annotation, allowing Spring to
 * detect and manage it as a bean.
 * </p>
 *
 * @see com.fin.spr.annotations.LogExecutionTime
 * @see org.aspectj.lang.annotation.Aspect
 *
 * @author Alexander Garifullin
 * @version 1.0
 */
@Aspect
@Component
public class ExecutionTimeLogger {

    private static final Logger logger = LoggerFactory.getLogger(ExecutionTimeLogger.class);

    @Around("@annotation(com.fin.spr.annotations.LogExecutionTime) || @within(com.fin.spr.annotations.LogExecutionTime)")
    public Object logExecutionTime(ProceedingJoinPoint joinPoint) {
        long start = System.currentTimeMillis();
        Object proceed = null;


        String methodName = joinPoint.getSignature().getName();
        String className = joinPoint.getSignature().getDeclaringTypeName();

        logger.info("Method {} of class {} started", methodName, className);

        try {
            proceed = joinPoint.proceed();
        } catch (Throwable throwable) {
            logger.error("Error during method execution: {}", throwable.getMessage());
        } finally {
            long executionTime = System.currentTimeMillis() - start;
            logger.info("Method {} of class {} finished. Execution time: {} ms", methodName, className, executionTime);
        }
        return proceed;
    }
}
