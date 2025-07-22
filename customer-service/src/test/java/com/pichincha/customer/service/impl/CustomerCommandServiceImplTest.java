package com.pichincha.customer.service.impl;

import com.pichincha.customer.repository.CustomerRepository;
import com.pichincha.customer.service.mapper.CustomerMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
class CustomerCommandServiceImplTest {

  @Mock
  private CustomerRepository customerRepository;

  @Mock
  private CustomerMapper customerMapper;

  @InjectMocks
  private CustomerCommandServiceImpl customerCommandService;

  @BeforeEach
  void setUp() {
    // Configuración inicial para cada test
  }

  @Test
  void serviceIsCreated() {
    assertNotNull(customerCommandService);
  }

  @Test
  void repositoryIsInjected() {
    assertNotNull(customerRepository);
  }

  @Test
  void mapperIsInjected() {
    assertNotNull(customerMapper);
  }
}
