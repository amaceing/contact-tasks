package com.anthonymace.contacttasks;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(
		basePackages = {
				"com.anthonymace.contacttasks.api.v1.resource",
				"com.anthonymace.contacttasks.services"
		}
)
public class ContactTasksApplication {

	public static void main(String[] args) {
		SpringApplication.run(ContactTasksApplication.class, args);
	}

}
