package com.pichincha.customer.domain.enums;

/**
 * Gender enumeration for person entities
 */
public enum GenderEnum {
  MALE("Male"),
  FEMALE("Female"),
  OTHER("Other");

  private final String displayName;

  GenderEnum(String displayName) {
    this.displayName = displayName;
  }

  public String getDisplayName() {
    return displayName;
  }

  public static GenderEnum fromDisplayName(String displayName) {
    for (GenderEnum gender : values()) {
      if (gender.displayName.equalsIgnoreCase(displayName)) {
        return gender;
      }
    }
    throw new IllegalArgumentException("Unknown gender: " + displayName);
  }
}
