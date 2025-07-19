package com.pichincha.customer.domain.enums;

/**
 * Customer status enumeration
 */
public enum CustomerStatusEnum {
  ACTIVE("Active"),
  INACTIVE("Inactive"),
  SUSPENDED("Suspended"),
  BLOCKED("Blocked");

  private final String displayName;

  CustomerStatusEnum(String displayName) {
    this.displayName = displayName;
  }

  public String getDisplayName() {
    return displayName;
  }

  public static CustomerStatusEnum fromDisplayName(String displayName) {
    for (CustomerStatusEnum status : values()) {
      if (status.displayName.equalsIgnoreCase(displayName)) {
        return status;
      }
    }
    throw new IllegalArgumentException("Unknown customer status: " + displayName);
  }

  public boolean isActive() {
    return this == ACTIVE;
  }
}
