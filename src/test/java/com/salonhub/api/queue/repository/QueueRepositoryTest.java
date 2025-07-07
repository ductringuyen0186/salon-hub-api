package com.salonhub.api.queue.repository;

import com.salonhub.api.queue.model.Queue;
import com.salonhub.api.queue.model.QueueStatus;
import com.salonhub.api.testfixtures.QueueTestDataBuilder;
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

    @BeforeEach
    void setUp() {
        queue1 = QueueTestDataBuilder.aQueueEntry()
                .withId(null)
                .withCustomerId(1L)
                .withStatus(QueueStatus.WAITING)
                .withCreatedAt(LocalDateTime.now().minusMinutes(10))
                .build();

        queue2 = QueueTestDataBuilder.aQueueEntry()
                .withId(null)
                .withCustomerId(2L)
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
        assertThat(result.get(0).getCustomerId()).isEqualTo(1L);
        assertThat(result.get(0).getStatus()).isEqualTo(QueueStatus.WAITING);
    }

    @Test
    void findCurrentQueue_shouldReturnWaitingEntries() {
        // When
        List<Queue> result = queueRepository.findCurrentQueue();

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getCustomerId()).isEqualTo(1L);
        assertThat(result.get(0).getStatus()).isEqualTo(QueueStatus.WAITING);
    }

    @Test
    void findByCustomerIdAndStatus_shouldReturnMatchingEntries() {
        // When
        List<Queue> result = queueRepository.findByCustomerIdAndStatus(1L, QueueStatus.WAITING);

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getCustomerId()).isEqualTo(1L);
        assertThat(result.get(0).getStatus()).isEqualTo(QueueStatus.WAITING);
    }

    @Test
    void findMaxQueueNumber_shouldReturnMaxNumber() {
        // Given
        Queue queue3 = QueueTestDataBuilder.aQueueEntry()
                .withId(null)
                .withCustomerId(3L)
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
        Queue queue3 = QueueTestDataBuilder.aQueueEntry()
                .withId(null)
                .withCustomerId(3L)
                .withEmployeeId(1L)
                .build();
        entityManager.persistAndFlush(queue3);

        // When
        List<Queue> result = queueRepository.findByEmployeeId(1L);

        // Then
        assertThat(result).hasSize(2); // queue1 and queue3 both have employeeId 1L
        assertThat(result.stream().allMatch(q -> q.getEmployeeId().equals(1L))).isTrue();
    }

    @Test
    void countWaitingAhead_shouldReturnCorrectCount() {
        // Given
        LocalDateTime referenceTime = LocalDateTime.now().minusMinutes(3);

        // When
        Long count = queueRepository.countWaitingAhead(referenceTime);

        // Then
        assertThat(count).isEqualTo(1L); // queue1 was created 10 minutes ago, before reference time
    }
}
