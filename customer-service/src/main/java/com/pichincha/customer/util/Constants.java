package com.pichincha.customer.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Constants {
    public static final String CLIENT_NOT_FOUND = "Cliente no encontrado con ID: %s";
    public static final String CLIENT_ALREADY_EXISTS = "Cliente con la identificacion: %s, ya es existente.";

    public static final String ERROR_CREATE_CUSTOMER = "Error al crear el cliente";
    public static final String ERROR_COMMUNICATION = "Error de comunicacion";
    public static final String ERROR_TIMEOUT = "Error de tiempo de espera";
    public static final String ERROR_CIRCUIT_BREAKER = "Error de circuito abierto";

    public static final String ERROR_DELETE_CUSTOMER = "Error al eliminar el cliente";
    public static final String ERROR_UPDATE_CUSTOMER = "Error al actualizar el cliente";
    public static final String ERROR_DETECTED = "Error detectado en el cliente";

    public static final String ERROR_GET_CUSTOMER_BY_IDENTIFICATION = "Error al obtener el cliente por identificacion";
    public static final String ERROR_GET_ALL_CUSTOMERS = "Error al obtener todos los clientes";

    public static final String FALLBACK_IDENTIFICATION = "UNKNOWN";
    public static final String FALLBACK_FULL_NAME = "Unknown Customer";
    public static final String FALLBACK_EMAIL = "unknown@email.com";
    public static final String FALLBACK_CELULAR = "0000000000";
    public static final String FALLBACK_ADDRESS = "Unknown Address";
    public static final boolean FALLBACK_ACTIVE = false;

}