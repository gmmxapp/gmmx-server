package com.gmmx.gmmxbackend;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.TestPropertySource;
import com.gmmx.mvp.GmmxBackendApplication;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = GmmxBackendApplication.class)
@AutoConfigureMockMvc
@TestPropertySource(properties = {
    "spring.profiles.active=test",
    "spring.datasource.url=jdbc:h2:mem:testdb;MODE=PostgreSQL;DB_CLOSE_DELAY=-1",
    "spring.datasource.username=sa",
    "spring.datasource.password=",
    "spring.mail.host=localhost",
    "spring.mail.port=25",
    "spring.mail.username=test",
    "spring.mail.password=test",
    "app.jwt.secret=mysecretkey1234567890123456789012345678901234567890",
    "app.jwt.access-expiration=3600000",
    "app.jwt.refresh-expiration=604800000",
    "spring.flyway.enabled=false",
    "spring.jpa.hibernate.ddl-auto=create-drop",
    "spring.datasource.driver-class-name=org.h2.Driver"
})
class GmmxBackendApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void contextLoads() {
    }

    @Test
    void debugEndpointShouldNotBeExposedOutsideDevProfile() throws Exception {
        mockMvc.perform(get("/api/debug/check-user").param("identifier", "member@example.com"))
                .andExpect(status().isForbidden());
    }

}
