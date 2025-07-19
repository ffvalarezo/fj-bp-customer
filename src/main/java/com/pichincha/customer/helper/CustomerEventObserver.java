package com.pichincha.customer.helper;

import com.pichincha.customer.domain.Customer;

/**
 * Observer interface for customer events
 * Implements observer pattern for reactive programming
 */
public interface CustomerEventObserver {
  
  /**
   * Called when a customer is created
   * @param customer newly created customer
   */
  void onCustomerCreated(Customer customer);
  
  /**
   * Called when a customer is updated
   * @param customer updated customer
   * @param previousData previous customer state
   */
  void onCustomerUpdated(Customer customer, Customer previousData);
  
  /**
   * Called when a customer is activated
   * @param customer activated customer
   */
  void onCustomerActivated(Customer customer);
  
  /**
   * Called when a customer is deactivated
   * @param customer deactivated customer
   */
  void onCustomerDeactivated(Customer customer);
  
  /**
   * Called when a customer is deleted
   * @param customerId ID of deleted customer
   */
  void onCustomerDeleted(String customerId);
  
  /**
   * Returns observer name for identification
   * @return observer name
   */
  String getObserverName();
}
