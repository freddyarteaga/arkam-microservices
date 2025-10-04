package com.arkam.user;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Disabled("Disabled due to MongoDB configuration issues in test environment")
class UserApplicationTests {

	@Test
	void contextLoads() {
	}

}
