package com.helpdesk;

import com.helpdesk.repository.CategoryRepository;
import com.helpdesk.repository.DepartmentRepository;
import com.helpdesk.repository.NotificationRepository;
import com.helpdesk.repository.SlaPolicyRepository;
import com.helpdesk.repository.TicketMessageRepository;
import com.helpdesk.repository.TicketRepository;
import com.helpdesk.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@SpringBootTest(properties = {
	"spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration,org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration,org.springframework.boot.autoconfigure.flyway.FlywayAutoConfiguration"
})
class ChamadosApplicationTests {

	@MockBean
	private UserRepository userRepository;

	@MockBean
	private DepartmentRepository departmentRepository;

	@MockBean
	private SlaPolicyRepository slaPolicyRepository;

	@MockBean
	private CategoryRepository categoryRepository;

	@MockBean
	private TicketRepository ticketRepository;

	@MockBean
	private TicketMessageRepository ticketMessageRepository;

	@MockBean
	private NotificationRepository notificationRepository;

	@Test
	void contextLoads() {
	}
}
