package me.iswear.springtestcontainer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SpringTestContainerApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringTestContainerApplication.class, args);
		while (true) {
			try {
				Thread.sleep(1000000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

}
