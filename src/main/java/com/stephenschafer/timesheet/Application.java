package com.stephenschafer.timesheet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootApplication
public class Application {
	@Autowired
	private BCryptPasswordEncoder passwordEncoder;

	public static void main(final String[] args) {
		SpringApplication.run(Application.class, args);
	}

	@Bean
	public CommandLineRunner init(final UserDao userDao) {
		return args -> {
			if (args.length >= 1 && args[0].equals("init")) {
				final UserEntity user1 = new UserEntity();
				user1.setFirstName("Devglan");
				user1.setLastName("Devglan");
				user1.setUsername("devglan");
				user1.setPassword(passwordEncoder.encode("devglan"));
				userDao.save(user1);
				final UserEntity user2 = new UserEntity();
				user2.setFirstName("John");
				user2.setLastName("Doe");
				user2.setUsername("john");
				user2.setPassword(passwordEncoder.encode("john"));
				userDao.save(user2);
			}
			else {
			}
		};
	}

	@Bean
	public EventDao getEventDao() {
		return new EventDaoImpl();
	}
}
