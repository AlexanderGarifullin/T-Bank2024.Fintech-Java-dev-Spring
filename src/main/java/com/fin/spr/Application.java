package com.fin.spr;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

/**
 * The {@code Application} class serves as the entry point for the Spring Boot application.
 * It is annotated with {@link SpringBootApplication}, which enables auto-configuration,
 * component scanning, and allows for the configuration of Spring beans.
 *
 * <p>
 * This class also enables AspectJ auto-proxying with the {@link EnableAspectJAutoProxy}
 * annotation, allowing the use of aspects for cross-cutting concerns such as logging and
 * transaction management.
 * </p>
 *
 * <p>
 * The main method is the starting point of the application, which launches the Spring
 * application context.
 * </p>
 *
 * @see SpringBootApplication
 * @see EnableAspectJAutoProxy
 *
 * @author Alexander Garifullin
 * @version 1.0
 */
@SpringBootApplication
@EnableAspectJAutoProxy
public class Application {

	/**
	 * The main method, which serves as the entry point for the Spring Boot application.
	 *
	 * @param args Command-line arguments passed to the application
	 */
	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}
}
