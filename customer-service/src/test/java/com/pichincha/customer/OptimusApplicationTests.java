package com.pichincha.customer;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class OptimusApplicationTests {

  @Test
  void applicationClassExists() {
    // Test simple que verifica que la clase principal existe
    OptimusApplication application = new OptimusApplication();
    assertNotNull(application);
  }

  @Test
  void mainMethodExists() {
    // Test que verifica que el método main existe y es accesible
    try {
      OptimusApplication.class.getMethod("main", String[].class);
    } catch (NoSuchMethodException e) {
      throw new AssertionError("El método main no existe", e);
    }
  }
}
