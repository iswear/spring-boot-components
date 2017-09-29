package me.iswear.springbootcontainer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;

@SpringBootApplication
@ConditionalOnBean
public class SpringBootContainerApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringBootContainerApplication.class, args);
	}
}
