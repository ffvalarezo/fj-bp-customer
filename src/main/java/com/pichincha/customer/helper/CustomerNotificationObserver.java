package com.pichincha.customer.helper;

import com.pichincha.customer.domain.Customer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Notification observer for customer events
 * Sends notifications for important customer events
 */
@Slf4j
@Component
public class CustomerNotificationObserver implements CustomerEventObserver {

  @Override
  public void onCustomerCreated(Customer customer) {
    log.info("NOTIFICATION: Sending welcome email to customer: {}", customer.getEmail());
    sendWelcomeNotification(customer);
  }

  @Override
  public void onCustomerUpdated(Customer customer, Customer previousData) {
    log.info("NOTIFICATION: Customer profile updated - ID: {}", customer.getCustomerId());
    sendUpdateNotification(customer);
  }

  @Override
  public void onCustomerActivated(Customer customer) {
    log.info("NOTIFICATION: Sending activation confirmation to customer: {}", customer.getEmail());
    sendActivationNotification(customer);
  }

  @Override
  public void onCustomerDeactivated(Customer customer) {
    log.info("NOTIFICATION: Sending deactivation notice to customer: {}", customer.getEmail());
    sendDeactivationNotification(customer);
  }

  @Override
  public void onCustomerDeleted(String customerId) {
    log.info("NOTIFICATION: Customer account deletion processed - ID: {}", customerId);
  }

  @Override
  public String getObserverName() {
    return "CustomerNotificationObserver";
  }

  private void sendWelcomeNotification(Customer customer) {
    log.debug("Sending welcome email to: {}", customer.getEmail());
  }

  private void sendUpdateNotification(Customer customer) {
    log.debug("Sending profile update confirmation to: {}", customer.getEmail());
  }

  private void sendActivationNotification(Customer customer) {
    log.debug("Sending account activation confirmation to: {}", customer.getEmail());
  }

  private void sendDeactivationNotification(Customer customer) {
    log.debug("Sending account deactivation notice to: {}", customer.getEmail());
  }
}
