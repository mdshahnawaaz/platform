package com.logistic.platform;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.logistic.platform.services.EmailService;

@SpringBootTest
class PlatformApplicationTests {

	@Test
	void contextLoads() {
	}
	@Autowired
	private EmailService emailService;

	@Test
	void sendEmail()
	{
		emailService.sendEmail("shahnawaazansari038@gmail.com","check","heelo kaise hi");
	}

}
