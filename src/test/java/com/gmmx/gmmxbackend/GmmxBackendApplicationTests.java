package com.gmmx.gmmxbackend;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import com.gmmx.mvp.GmmxBackendApplication;

@SpringBootTest(classes = GmmxBackendApplication.class)
@TestPropertySource(properties = {
    "DB_URL=jdbc:h2:mem:testdb",
    "DB_USERNAME=sa",
    "DB_PASSWORD=",
    "MAIL_HOST=localhost",
    "MAIL_PORT=25",
    "MAIL_USER=test",
    "MAIL_PASS=test",
    "JWT_SECRET=mysecretkey1234567890123456789012345678901234567890",
    "spring.flyway.enabled=false",
    "spring.jpa.hibernate.ddl-auto=create-drop",
    "spring.datasource.driver-class-name=org.h2.Driver"
})
class GmmxBackendApplicationTests {

    @Test
    void contextLoads() {
    }

}
