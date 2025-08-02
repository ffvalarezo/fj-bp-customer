package com.pichincha.movement.domain;

import java.util.Map;

public record LoginResponse(
  String token,
  String refreshToken,
  long expiresIn,
  Map<String, Object> user
) {
}
