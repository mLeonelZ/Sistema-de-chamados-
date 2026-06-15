package com.helpdesk.integration;

import com.helpdesk.model.User;
import com.helpdesk.repository.CategoryRepository;
import com.helpdesk.repository.DepartmentRepository;
import com.helpdesk.repository.NotificationRepository;
import com.helpdesk.repository.SlaPolicyRepository;
import com.helpdesk.repository.TicketMessageRepository;
import com.helpdesk.repository.TicketRepository;
import com.helpdesk.repository.UserRepository;
import com.helpdesk.service.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.UUID;

@SpringBootTest(properties = {
        "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration,org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration,org.springframework.boot.autoconfigure.flyway.FlywayAutoConfiguration"
})
@AutoConfigureMockMvc
public abstract class AbstractIntegrationTest {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    private JwtService jwtService;

    @MockBean
    protected UserRepository userRepository;

    @MockBean
    protected DepartmentRepository departmentRepository;

    @MockBean
    protected SlaPolicyRepository slaPolicyRepository;

    @MockBean
    protected CategoryRepository categoryRepository;

    @MockBean
    protected TicketRepository ticketRepository;

    @MockBean
    protected TicketMessageRepository ticketMessageRepository;

    @MockBean
    protected NotificationRepository notificationRepository;

    protected String generateValidToken() {
        User user = new User();
        user.setId(UUID.randomUUID());
        user.setEmail("test@example.com");
        user.setName("Test User");
        user.setPassword("hashed_password");
        user.setStatus(com.helpdesk.model.enums.UserStatus.ACTIVE);
        user.setRole(com.helpdesk.model.enums.Role.ADMIN);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());

        return jwtService.generateToken(user);
    }
}
