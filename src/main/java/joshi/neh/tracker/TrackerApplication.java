package joshi.neh.tracker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class TrackerApplication {

	public static void main(String[] args) {

		SpringApplication.run(TrackerApplication.class, args);
	}

}
