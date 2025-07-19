# Customer Microservice - Domain Entities

Este proyecto implementa las entidades de dominio **Person** y **Customer** siguiendo los principios de Clean Architecture y las especificaciones del ejercicio fullstack.

## 📋 Entidades Implementadas

### 🧑 Person (Entidad Base)
La entidad `Person` es una clase abstracta que sirve como base para otras entidades que representen personas.

**Atributos:**
- `personId`: ID único de la persona (Long, auto-generado)
- `fullName`: Nombre completo (String, máximo 100 caracteres)
- `gender`: Género (String, máximo 10 caracteres)
- `age`: Edad (Integer, mínimo 18 años)
- `identification`: Cédula de identidad (String, 1-10 dígitos)
- `address`: Dirección (String, máximo 200 caracteres)
- `celular`: Número de celular (String, exactamente 10 dígitos)
- `email`: Correo electrónico (String, formato válido, máximo 100 caracteres)

**Métodos de Negocio:**
- `isValidAge()`: Valida que la edad sea mayor o igual a 18
- `getFormattedIdentification()`: Retorna la identificación formateada
- `hasValidEmail()`: Valida el formato del email

### 👤 Customer (Entidad de Cliente)
La entidad `Customer` extiende `Person` y agrega funcionalidades específicas para clientes bancarios.

**Atributos Adicionales:**
- `customerId`: ID único del cliente (Integer)
- `password`: Contraseña (String, 4-50 caracteres)
- `active`: Estado activo/inactivo (Boolean)
- `createdAt`: Fecha de creación (LocalDateTime)
- `updatedAt`: Fecha de última actualización (LocalDateTime)

**Métodos de Negocio:**
- `activateCustomer()`: Activa el cliente
- `deactivateCustomer()`: Desactiva el cliente
- `isActiveCustomer()`: Verifica si el cliente está activo
- `updatePassword(String)`: Actualiza la contraseña
- `updateCustomerInfo(...)`: Actualiza información del cliente
- `canPerformTransactions()`: Verifica si puede realizar transacciones
- `getDisplayName()`: Obtiene nombre para mostrar

## 🏗️ Arquitectura Implementada

### Clean Architecture
```
┌─────────────────────────────────────────────────────────────┐
│                    Infrastructure Layer                     │
│  ┌─────────────────────────────────────────────────────┐   │
│  │              Application Layer                      │   │
│  │  ┌─────────────────────────────────────────────┐   │   │
│  │  │              Domain Layer                   │   │   │
│  │  │  • Person (abstract)                       │   │   │
│  │  │  • Customer (entity)                       │   │   │
│  │  │  • CustomerRepository (interface)          │   │   │
│  │  │  • CustomerService (interface)             │   │   │
│  │  │  • Enums (GenderEnum, CustomerStatusEnum) │   │   │
│  │  └─────────────────────────────────────────────┘   │   │
│  │  • CustomerServiceImpl                             │   │
│  │  • CustomerMapper                                  │   │
│  └─────────────────────────────────────────────────────┘   │
│  • CustomerController                                      │
│  • CustomerRepository (JPA implementation)                 │
└─────────────────────────────────────────────────────────────┘
```

### 📁 Estructura de Packages
```
com.pichincha.customer/
├── domain/
│   ├── Customer.java
│   ├── Person.java
│   └── enums/
│       ├── GenderEnum.java
│       └── CustomerStatusEnum.java
├── repository/
│   └── CustomerRepository.java
├── service/
│   ├── CustomerService.java
│   ├── impl/
│   │   └── CustomerServiceImpl.java
│   └── mapper/
│       └── CustomerMapper.java
└── infrastructure/
    └── input/
        └── adapter/
            └── rest/
                └── CustomerController.java
```

## 🛠️ Tecnologías Utilizadas

- **Spring Boot 3.5.3**: Framework principal
- **Spring WebFlux**: Programación reactiva
- **Spring Data JPA**: Persistencia de datos
- **H2 Database**: Base de datos en memoria
- **Lombok**: Reducción de código boilerplate
- **MapStruct**: Mapeo entre objetos
- **Jakarta Validation**: Validaciones
- **OpenAPI Generator**: Generación de código desde contrato

## 🗄️ Base de Datos

### Tabla: customers
```sql
CREATE TABLE customers (
    person_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    customer_id INTEGER UNIQUE NOT NULL,
    full_name VARCHAR(100) NOT NULL,
    gender VARCHAR(10) NOT NULL,
    age INTEGER NOT NULL CHECK (age >= 18),
    identification VARCHAR(10) UNIQUE NOT NULL,
    address VARCHAR(200) NOT NULL,
    celular VARCHAR(10) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(50) NOT NULL,
    active BOOLEAN NOT NULL,
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);
```

### Datos de Prueba
El sistema incluye 4 clientes de prueba pre-cargados:
1. Jose Lema (ID: 1, Activo)
2. Maria Garcia (ID: 2, Activo)
3. Carlos Rodriguez (ID: 3, Activo)
4. Ana Martinez (ID: 4, Inactivo)

## 🔧 Validaciones Implementadas

### Person
- **fullName**: Requerido, máximo 100 caracteres
- **gender**: Requerido, máximo 10 caracteres
- **age**: Mínimo 18 años
- **identification**: Requerido, 1-10 dígitos numéricos, único
- **address**: Requerido, máximo 200 caracteres
- **celular**: Requerido, exactamente 10 dígitos
- **email**: Requerido, formato válido, único, máximo 100 caracteres

### Customer
- **customerId**: Único, no nulo
- **password**: Requerido, 4-50 caracteres
- **active**: Requerido, booleano

## 🚀 Endpoints Disponibles

- `GET /api/v1/customers` - Obtener todos los clientes
- `GET /api/v1/customers/{id}` - Obtener cliente por ID
- `POST /api/v1/customers` - Crear nuevo cliente
- `PUT /api/v1/customers/{id}` - Actualizar cliente
- `DELETE /api/v1/customers/{id}` - Eliminar cliente

## 📝 Logs Transaccionales

El proyecto incluye la integración con la librería de logs transaccionales de Banco Pichincha que captura automáticamente:
- Todas las peticiones HTTP entrantes
- Headers de trazabilidad (x-guid, x-channel, x-medium, x-app, x-session)
- Tiempos de respuesta
- Datos de entrada y salida
- Errores y excepciones

## ✅ Checklist de Implementación

- [x] Entidad Person (clase abstracta base)
- [x] Entidad Customer (hereda de Person)
- [x] Enumeraciones de dominio (GenderEnum, CustomerStatusEnum)
- [x] Repository interface con JPA
- [x] Service interface y implementación
- [x] Mapper para conversión entre modelos
- [x] Controller con implementación reactiva
- [x] Validaciones de negocio
- [x] Configuración de base de datos H2
- [x] Datos de prueba iniciales
- [x] Logs transaccionales integrados
- [x] Documentación completa

## 🏃‍♂️ Ejecución del Proyecto

```bash
# Compilar el proyecto
./gradlew clean build

# Ejecutar la aplicación
./gradlew bootRun

# Acceder a la consola H2
http://localhost:8080/h2-console

# Acceder a la documentación OpenAPI
http://localhost:8080/swagger-ui.html
```

Las entidades Person y Customer han sido implementadas siguiendo las mejores prácticas de Clean Architecture y cumpliendo con todos los requerimientos del ejercicio fullstack.
