package com.pichincha.customer.helper;

import com.pichincha.customer.domain.Customer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Context class for customer validation using strategy pattern
 * Manages different validation strategies based on requirements
 */
@Slf4j
@Component
public class CustomerValidationContext {

  private final List<CustomerValidationStrategy> validationStrategies;
  private CustomerValidationStrategy currentStrategy;

  @Autowired
  public CustomerValidationContext(List<CustomerValidationStrategy> validationStrategies) {
    this.validationStrategies = validationStrategies;
    this.currentStrategy = getStrategyByName("BasicValidation");
  }

  public void setValidationStrategy(String strategyName) {
    CustomerValidationStrategy strategy = getStrategyByName(strategyName);
    if (strategy != null) {
      this.currentStrategy = strategy;
      log.info("Validation strategy changed to: {}", strategyName);
    } else {
      log.warn("Strategy {} not found, keeping current strategy: {}", 
               strategyName, currentStrategy.getStrategyName());
    }
  }

  public boolean validateCustomer(Customer customer) {
    log.debug("Validating customer {} using strategy: {}", 
              customer.getCustomerId(), currentStrategy.getStrategyName());
    return currentStrategy.validate(customer);
  }

  public String getCurrentStrategyName() {
    return currentStrategy.getStrategyName();
  }

  public List<String> getAvailableStrategies() {
    return validationStrategies.stream()
      .map(CustomerValidationStrategy::getStrategyName)
      .toList();
  }

  private CustomerValidationStrategy getStrategyByName(String strategyName) {
    return validationStrategies.stream()
      .filter(strategy -> strategy.getStrategyName().equals(strategyName))
      .findFirst()
      .orElse(null);
  }
}
