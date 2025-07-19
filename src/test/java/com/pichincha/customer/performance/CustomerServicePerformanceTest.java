package com.pichincha.customer.performance;

import com.pichincha.customer.domain.Customer;
import com.pichincha.customer.domain.enums.CustomerStatus;
import com.pichincha.customer.service.CustomerService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.util.StopWatch;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

/**
 * Performance tests for Customer Service
 * Tests response times and throughput under load
 */
@SpringBootTest
@ActiveProfiles("test")
@DisplayName("Customer Service Performance Tests")
class CustomerServicePerformanceTest {

  @SuppressWarnings("removal")
  @MockBean
  private CustomerService customerService;

  private static final int LARGE_DATASET_SIZE = 1000;
  private static final Duration MAX_EXECUTION_TIME = Duration.ofMillis(500);

  @Test
  @DisplayName("Should handle large dataset retrieval within acceptable time")
  void shouldHandleLargeDatasetRetrievalWithinAcceptableTime() {
    // Arrange
    Flux<Customer> largeCustomerFlux = generateLargeCustomerDataset(LARGE_DATASET_SIZE);
    when(customerService.getAllCustomers()).thenReturn(largeCustomerFlux);

    // Act
    StopWatch stopWatch = new StopWatch();
    stopWatch.start();

    StepVerifier.create(customerService.getAllCustomers())
        .expectNextCount(LARGE_DATASET_SIZE)
        .expectComplete()
        .verify(Duration.ofSeconds(5));

    stopWatch.stop();

    // Assert
    assertThat(stopWatch.getTotalTimeMillis())
        .isLessThan(MAX_EXECUTION_TIME.toMillis())
        .withFailMessage("Dataset retrieval took too long: %d ms", stopWatch.getTotalTimeMillis());
  }

  @Test
  @DisplayName("Should handle concurrent customer creation requests")
  void shouldHandleConcurrentCustomerCreationRequests() {
    // Arrange
    Customer mockCustomer = createTestCustomer("CUST-PERF-001", "Performance Test");
    when(customerService.createCustomer(any(Customer.class))).thenReturn(Mono.just(mockCustomer));

    int concurrentRequests = 100;
    AtomicInteger successCount = new AtomicInteger(0);

    // Act
    StopWatch stopWatch = new StopWatch();
    stopWatch.start();

    Flux<Customer> concurrentCreations = Flux.range(1, concurrentRequests)
        .flatMap(i -> {
          Customer customer = createTestCustomer("CUST-PERF-" + i, "Concurrent Customer " + i);
          return customerService.createCustomer(customer);
        })
        .doOnNext(customer -> successCount.incrementAndGet());

    StepVerifier.create(concurrentCreations)
        .expectNextCount(concurrentRequests)
        .expectComplete()
        .verify(Duration.ofSeconds(10));

    stopWatch.stop();

    // Assert
    assertThat(successCount.get()).isEqualTo(concurrentRequests);
    assertThat(stopWatch.getTotalTimeMillis()).isLessThan(5000);
  }

  @Test
  @DisplayName("Should maintain low memory usage during bulk operations")
  void shouldMaintainLowMemoryUsageDuringBulkOperations() {
    // Arrange
    Runtime runtime = Runtime.getRuntime();
    long initialMemory = runtime.totalMemory() - runtime.freeMemory();
    
    Flux<Customer> bulkCustomerFlux = generateLargeCustomerDataset(5000);
    when(customerService.getAllCustomers()).thenReturn(bulkCustomerFlux);

    // Act
    StepVerifier.create(customerService.getAllCustomers()
            .buffer(100)
            .flatMap(Flux::fromIterable))
        .expectNextCount(5000)
        .expectComplete()
        .verify(Duration.ofSeconds(10));

    // Assert
    runtime.gc();
    long finalMemory = runtime.totalMemory() - runtime.freeMemory();
    long memoryIncrease = finalMemory - initialMemory;
    
    assertThat(memoryIncrease)
        .isLessThan(50 * 1024 * 1024)
        .withFailMessage("Memory usage increased by %d bytes", memoryIncrease);
  }

  @Test
  @DisplayName("Should handle rapid sequential operations efficiently")
  void shouldHandleRapidSequentialOperationsEfficiently() {
    // Arrange
    Customer mockCustomer = createTestCustomer("CUST-SEQ-001", "Sequential Test");
    when(customerService.getCustomerById(anyString())).thenReturn(Mono.just(mockCustomer));
    when(customerService.updateCustomer(anyString(), any(Customer.class))).thenReturn(Mono.just(mockCustomer));

    int operationCount = 500;

    // Act
    StopWatch stopWatch = new StopWatch();
    stopWatch.start();

    Flux<Customer> sequentialOperations = Flux.range(1, operationCount)
        .flatMap(i -> customerService.getCustomerById("CUST-SEQ-" + i)
            .flatMap(customer -> {
              customer.setFullName("Updated Name " + i);
              return customerService.updateCustomer(customer.getCustomerId(), customer);
            }));

    StepVerifier.create(sequentialOperations)
        .expectNextCount(operationCount)
        .expectComplete()
        .verify(Duration.ofSeconds(15));

    stopWatch.stop();

    // Assert
    double operationsPerSecond = (double) operationCount / (stopWatch.getTotalTimeMillis() / 1000.0);
    assertThat(operationsPerSecond)
        .isGreaterThan(100)
        .withFailMessage("Performance too low: %.2f operations/second", operationsPerSecond);
  }

  @Test
  @DisplayName("Should handle timeout scenarios gracefully")
  void shouldHandleTimeoutScenariosGracefully() {
    // Arrange
    when(customerService.getCustomerById(anyString()))
        .thenReturn(Mono.delay(Duration.ofSeconds(2))
            .map(delay -> createTestCustomer("CUST-TIMEOUT-001", "Timeout Test")));

    // Act & Assert
    StepVerifier.create(customerService.getCustomerById("CUST-TIMEOUT-001")
            .timeout(Duration.ofSeconds(1)))
        .expectError()
        .verify();
  }

  private Flux<Customer> generateLargeCustomerDataset(int size) {
    return Flux.range(1, size)
        .map(i -> createTestCustomer("CUST-LARGE-" + i, "Large Dataset Customer " + i));
  }

  private Customer createTestCustomer(String customerId, String fullName) {
    Customer customer = new Customer();
    customer.setCustomerId(customerId);
    customer.setFullName(fullName);
    customer.setGender("M");
    customer.setAge(30);
    customer.setIdentification("1234567890");
    customer.setAddress("123 Performance Test St");
    customer.setCelular("0987654321");
    customer.setEmail("performance@example.com");
    customer.setPassword("ENCODED_password123");
    customer.setActive(true);
    customer.setStatus(CustomerStatus.ACTIVE);
    customer.setCreatedAt(LocalDateTime.now());
    customer.setUpdatedAt(LocalDateTime.now());
    return customer;
  }
}
