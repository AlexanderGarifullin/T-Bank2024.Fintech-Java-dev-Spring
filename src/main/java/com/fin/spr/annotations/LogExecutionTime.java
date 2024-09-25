package com.fin.spr.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * The {@code LogExecutionTime} annotation is used for logging the execution time
 * of methods and classes. This annotation can be applied to both methods and classes.
 *
 * <p>
 * When applied to a method, the execution time of that method will be captured
 * and recorded in the log. If the annotation is applied to a class, all methods
 * within that class will log their execution time.
 * </p>
 *
 * <p>
 * The annotation is retained at runtime, allowing it to be utilized in
 * aspect-oriented programming (AOP) mechanisms, such as Spring AOP.
 * </p>
 *
 * @see org.aspectj.lang.annotation.Aspect
 * @see org.aspectj.lang.ProceedingJoinPoint
 *
 * @author Alexander Garifullin
 * @version 1.0
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface LogExecutionTime {
}
