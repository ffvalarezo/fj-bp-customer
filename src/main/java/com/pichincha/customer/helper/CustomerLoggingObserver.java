package com.pichincha.customer.helper;

import com.pichincha.customer.domain.Customer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Logging observer for customer events
 * Logs all customer-related events for audit purposes
 */
@Slf4j
@Component
public class CustomerLoggingObserver implements CustomerEventObserver {

  @Override
  public void onCustomerCreated(Customer customer) {
    log.info("AUDIT: Customer created - ID: {}, Email: {}, Status: {}", 
             customer.getCustomerId(), customer.getEmail(), customer.getStatus());
  }

  @Override
  public void onCustomerUpdated(Customer customer, Customer previousData) {
    log.info("AUDIT: Customer updated - ID: {}, Previous Status: {}, New Status: {}", 
             customer.getCustomerId(), 
             previousData != null ? previousData.getStatus() : "UNKNOWN", 
             customer.getStatus());
  }

  @Override
  public void onCustomerActivated(Customer customer) {
    log.info("AUDIT: Customer activated - ID: {}, Email: {}", 
             customer.getCustomerId(), customer.getEmail());
  }

  @Override
  public void onCustomerDeactivated(Customer customer) {
    log.warn("AUDIT: Customer deactivated - ID: {}, Email: {}, Reason: Status changed to {}", 
             customer.getCustomerId(), customer.getEmail(), customer.getStatus());
  }

  @Override
  public void onCustomerDeleted(String customerId) {
    log.warn("AUDIT: Customer deleted - ID: {}", customerId);
  }

  @Override
  public String getObserverName() {
    return "CustomerLoggingObserver";
  }
}
