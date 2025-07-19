package com.pichincha.customer.helper;

import com.pichincha.customer.domain.Customer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Event publisher for customer events using observer pattern
 * Manages observers and publishes events to all registered observers
 */
@Slf4j
@Component
public class CustomerEventPublisher {

  private final List<CustomerEventObserver> observers = new CopyOnWriteArrayList<>();

  @Autowired
  public CustomerEventPublisher(List<CustomerEventObserver> observers) {
    this.observers.addAll(observers);
    log.info("CustomerEventPublisher initialized with {} observers", observers.size());
    observers.forEach(observer -> log.debug("Registered observer: {}", observer.getObserverName()));
  }

  public void addObserver(CustomerEventObserver observer) {
    observers.add(observer);
    log.debug("Added observer: {}", observer.getObserverName());
  }

  public void removeObserver(CustomerEventObserver observer) {
    observers.remove(observer);
    log.debug("Removed observer: {}", observer.getObserverName());
  }

  public void publishCustomerCreated(Customer customer) {
    log.debug("Publishing customer created event for: {}", customer.getCustomerId());
    observers.forEach(observer -> {
      try {
        observer.onCustomerCreated(customer);
      } catch (Exception e) {
        log.error("Error in observer {} while processing customer created event: {}", 
                  observer.getObserverName(), e.getMessage(), e);
      }
    });
  }

  public void publishCustomerUpdated(Customer customer, Customer previousData) {
    log.debug("Publishing customer updated event for: {}", customer.getCustomerId());
    observers.forEach(observer -> {
      try {
        observer.onCustomerUpdated(customer, previousData);
      } catch (Exception e) {
        log.error("Error in observer {} while processing customer updated event: {}", 
                  observer.getObserverName(), e.getMessage(), e);
      }
    });
  }

  public void publishCustomerActivated(Customer customer) {
    log.debug("Publishing customer activated event for: {}", customer.getCustomerId());
    observers.forEach(observer -> {
      try {
        observer.onCustomerActivated(customer);
      } catch (Exception e) {
        log.error("Error in observer {} while processing customer activated event: {}", 
                  observer.getObserverName(), e.getMessage(), e);
      }
    });
  }

  public void publishCustomerDeactivated(Customer customer) {
    log.debug("Publishing customer deactivated event for: {}", customer.getCustomerId());
    observers.forEach(observer -> {
      try {
        observer.onCustomerDeactivated(customer);
      } catch (Exception e) {
        log.error("Error in observer {} while processing customer deactivated event: {}", 
                  observer.getObserverName(), e.getMessage(), e);
      }
    });
  }

  public void publishCustomerDeleted(String customerId) {
    log.debug("Publishing customer deleted event for: {}", customerId);
    observers.forEach(observer -> {
      try {
        observer.onCustomerDeleted(customerId);
      } catch (Exception e) {
        log.error("Error in observer {} while processing customer deleted event: {}", 
                  observer.getObserverName(), e.getMessage(), e);
      }
    });
  }

  public List<String> getRegisteredObservers() {
    return observers.stream()
      .map(CustomerEventObserver::getObserverName)
      .toList();
  }
}
