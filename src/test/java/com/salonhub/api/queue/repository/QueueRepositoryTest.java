package com.salonhub.api.queue.repository;

import com.salonhub.api.queue.model.Queue;
import com.salonhub.api.queue.model.QueueStatus;
import com.salonhub.api.testfixtures.QueueTestDataBuilder;
import com.salonhub.api.customer.model.Customer;
import com.salonhub.api.employee.model.Employee;
import com.salonhub.api.employee.model.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class QueueRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private QueueRepository queueRepository;

    private Queue queue1;
    private Queue queue2;
    private Customer customer1;
    private Customer customer2;
    private Employee employee1;
    private Employee employee2;

    @BeforeEach
    void setUp() {
        // Create required Customer and Employee entities first
        customer1 = new Customer(null, "Jane Doe", "jane@example.com", "555-0101");
        customer2 = new Customer(null, "John Smith", "john@salon.com", "555-0202");
        employee1 = new Employee(null, "Alice Stylist", true, Role.TECHNICIAN);
        employee2 = new Employee(null, "Bob Manager", true, Role.TECHNICIAN);
        
        customer1 = entityManager.persistAndFlush(customer1);
        customer2 = entityManager.persistAndFlush(customer2);
        employee1 = entityManager.persistAndFlush(employee1);
        employee2 = entityManager.persistAndFlush(employee2);

        queue1 = QueueTestDataBuilder.aQueueEntry()
                .withId(null)
                .withCustomerId(customer1.getId())
                .withEmployeeId(employee1.getId())
                .withStatus(QueueStatus.WAITING)
                .withCreatedAt(LocalDateTime.now().minusMinutes(10))
                .build();

        queue2 = QueueTestDataBuilder.aQueueEntry()
                .withId(null)
                .withCustomerId(customer2.getId())
                .withEmployeeId(employee2.getId())
                .withStatus(QueueStatus.IN_PROGRESS)
                .withCreatedAt(LocalDateTime.now().minusMinutes(5))
                .build();

        entityManager.persistAndFlush(queue1);
        entityManager.persistAndFlush(queue2);
    }

    @Test
    void findByStatusOrderByCreatedAtAsc_shouldReturnEntriesInCorrectOrder() {
        // When
        List<Queue> result = queueRepository.findByStatusOrderByCreatedAtAsc(QueueStatus.WAITING);

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getCustomerId()).isEqualTo(customer1.getId());
        assertThat(result.get(0).getStatus()).isEqualTo(QueueStatus.WAITING);
    }

    @Test
    void findCurrentQueue_shouldReturnWaitingEntries() {
        // When
        List<Queue> result = queueRepository.findCurrentQueue();

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getCustomerId()).isEqualTo(customer1.getId());
        assertThat(result.get(0).getStatus()).isEqualTo(QueueStatus.WAITING);
    }

    @Test
    void findByCustomerIdAndStatus_shouldReturnMatchingEntries() {
        // When
        List<Queue> result = queueRepository.findByCustomerIdAndStatus(customer1.getId(), QueueStatus.WAITING);

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getCustomerId()).isEqualTo(customer1.getId());
        assertThat(result.get(0).getStatus()).isEqualTo(QueueStatus.WAITING);
    }

    @Test
    void findMaxQueueNumber_shouldReturnMaxNumber() {
        // Given
        Customer customer3 = new Customer(null, "Customer 3", "customer3@salon.com", "555-0303");
        customer3 = entityManager.persistAndFlush(customer3);
        
        Employee employee3 = new Employee(null, "Employee 3", true, Role.TECHNICIAN);
        employee3 = entityManager.persistAndFlush(employee3);
        
        Queue queue3 = QueueTestDataBuilder.aQueueEntry()
                .withId(null)
                .withCustomerId(customer3.getId())
                .withEmployeeId(employee3.getId())
                .withQueueNumber(5)
                .build();
        entityManager.persistAndFlush(queue3);

        // When
        Optional<Integer> result = queueRepository.findMaxQueueNumber();

        // Then
        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(5);
    }

    @Test
    void findMaxQueueNumber_shouldReturnEmpty_whenNoEntries() {
        // Given
        entityManager.clear();
        queueRepository.deleteAll();

        // When
        Optional<Integer> result = queueRepository.findMaxQueueNumber();

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    void findByEmployeeId_shouldReturnMatchingEntries() {
        // Given
        Customer customer3 = new Customer(null, "Customer 3", "customer3@salon.com", "555-0303");
        customer3 = entityManager.persistAndFlush(customer3);
        
        Queue queue3 = QueueTestDataBuilder.aQueueEntry()
                .withId(null)
                .withCustomerId(customer3.getId())
                .withEmployeeId(employee1.getId())
                .build();
        entityManager.persistAndFlush(queue3);

        // When
        List<Queue> result = queueRepository.findByEmployeeId(employee1.getId());

        // Then
        assertThat(result).hasSize(2); // queue1 and queue3 both have the same employeeId
        assertThat(result.stream().allMatch(q -> q.getEmployeeId().equals(employee1.getId()))).isTrue();
    }

    @Test
    void countWaitingAhead_shouldReturnCorrectCount() {
        // Given - Use the actual created time from queue1 (which has WAITING status)
        LocalDateTime referenceTime = queue1.getCreatedAt().plusMinutes(1);

        // When
        Long count = queueRepository.countWaitingAhead(referenceTime);

        // Then
        assertThat(count).isEqualTo(1L); // queue1 was created before reference time
    }
}
