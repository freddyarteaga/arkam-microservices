package com.arkam.product;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Disabled("Disabled due to MongoDB configuration issues in test environment")
class ProductApplicationTests {

	@Test
	void contextLoads() {
	}

}
