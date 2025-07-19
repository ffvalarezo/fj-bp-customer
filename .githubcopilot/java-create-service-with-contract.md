# Guide to Creating a Spring Boot Service

This document describes the flow for creating a complete Spring Boot service, including: domain, DTO, mapper, repository, deployment, controller, utilities, helpers, and unit tests.


## Content Index

- [Environment Configuration](#environment-configuration)
- [Project Base Structure](#project-base-structure)
  - [Structure for Spring Web](#structure-for-spring-web)
  - [Structure for Spring WebFlux](#structure-for-spring-webflux)
- [Conventions and Best Practices](#conventions-and-best-practices)
  - [Naming Conventions](#-naming-conventions)
  - [Code Documentation](#-code-documentation)
  - [Code Style](#-code-style)
  - [Project Naming Conventions](#-project-naming-conventions)
  - [Package Structure Convention](#-package-structure-convention)
  - [Security](#-security)
  - [Code Quality](#-code-quality)
- [Importance of Code Mutation in Tests](#importance-of-code-mutation-in-tests)
- [Step-by-Step Development Flow](#step-by-step-development-flow)
  - [Entity](#entity)
  - [DTO (Data Transfer Object)](#dto-data-transfer-object)
  - [Mapper](#mapper)
  - [Repository](#repository)
  - [Service](#service)
  - [Service Implementation](#service-implementation)
  - [Controller](#controller)
  - [Utilities](#utilities)
  - [Exceptions](#exceptions)
  - [Helpers](#helpers)
- [Unit Tests](#unit-tests)
  - [DAMP Approach](#damp-approach-descriptive-and-meaningful-phrases)
  - [3A Pattern](#3a-pattern-arrange-act-assert)
  - [Unit Tests for Domain](#unit-tests-for-domain)
  - [Unit Tests for Repositories](#unit-tests-for-repositories)
  - [Unit Tests for Services](#unit-tests-for-services)
  - [Unit Tests for Controller](#unit-tests-for-controller)
  - [Unit Tests for Utilities](#unit-tests-for-utilities)
  - [Unit Tests for Exceptions](#unit-tests-for-exceptions)
  - [Unit Tests for Helpers](#unit-tests-for-helpers)
- [Advanced Examples and Best Practices](#advanced-examples-and-best-practices)
  - [Using Fixtures](#using-fixtures-beforeall-beforeeach)
  - [Basic Integration Tests](#basic-integration-tests)
  - [Code Coverage and Tools](#code-coverage-and-tools)
  - [Additional Best Practices](#additional-best-practices)

## Environment Configuration

Make sure your development environment is correctly configured:
### General Considerations
- **Do Not Modify**. Do not modify the following files:
    - Files inside the `.github` folder.
    - Do not modify files inside the `build.gradle` and `target` folders.
- **Dependency Manager**. 
    - Verify if `Gradle` or `Maven` is being used as the dependency manager.
    - If using Gradle, check the `build.gradle` file.
    - In the `build.gradle` file, located at the root of the project, in the java section, update the Java version to 21, as shown below::

    ```groovy
    java {
        sourceCompatibility = JavaVersion.VERSION_21
        toolchain {
        languageVersion = JavaLanguageVersion.of(21)
        }
      }
    ext {
      mapstructVersion = "1.4.2.Final"
      lombokVersion = "1.18.30"  // Updated to a version compatible with Java 21
      }
 
     dependencies {
      // Lombok must be before other dependencies
      compileOnly "org.projectlombok:lombok:${lombokVersion}"
      annotationProcessor "org.projectlombok:lombok:${lombokVersion}"

      // MapStruct
      implementation "org.mapstruct:mapstruct:${mapstructVersion}"
      annotationProcessor "org.mapstruct:mapstruct-processor:${mapstructVersion}"

     //dependency for JPA
     implementation 'org.springframework.boot:spring-boot-starter-data-jpa'
     runtimeOnly 'com.h2database:h2'
    }
    ```
    - If using Maven, check the `pom.xml` file.
- Similarly to Gradle, make sure the Java version is correct:
  ```xml
  <properties>
      <java.version>21</java.version>
      <mapstruct.version>1.4.2.Final</mapstruct.version>
      <lombok.version>1.18.30</lombok.version> <!-- Updated to a version compatible with Java 21 -->
      <maven.compiler.source>21</maven.compiler.source>
      <maven.compiler.target>21</maven.compiler.target>
  </properties>
  ```
- When creating new code, implement libraries that are compatible with the Java version you're referencing in the `build.gradle` or `pom.xml` file.
    - For example, use `java.time.LocalDate` instead of `java.util.Date`.
    - Use `jakarta.*` instead of `javax.*`.

- Modify the `application.yml` file located in `src/main/resources`, to include the H2 database configuration:
```yaml
 spring:
    application:
       name: arquetipo-msa-diseno-deta3
    header:
       channel: digital
       medium: web
    datasource:
       url: jdbc:h2:mem:testdb
       driver-class-name: org.h2.Driver
       username: sa
       password: password
    h2:
       console:
          enabled: true
          path: /h2-console
    jpa:
       database-platform: org.hibernate.dialect.H2Dialect
       hibernate:
          ddl-auto: update
       show-sql: true   
```

## Project Base Structure
Verify that the project structure follows the standard Spring Boot conventions. Below are typical examples for **Spring Web** and **Spring WebFlux**.

### Structure for Spring Web
```
src/
├── main/
│   ├── java/
│       └── com.pichincha.<celula>.<nombre-proyecto>/
│   │       ├── configuration/         # Configuraciones generales
│   │       ├── controller/            # Controladores REST
│   │       ├── service/               # Interfaces de servicios
│   │       │   └── impl/              # Implementaciones de servicios
│   │       ├── repository/            # Interfaces de acceso a datos
│   │       │   └── impl/              # Implementaciones de repositorios
│   │       ├── exception/             # Manejo de excepciones
│   │       ├── domain/                # Entidades del dominio
│   │       │   ├── dto/               # Objetos de transferencia de datos
│   │       │   └── enums/             # Enumeraciones
│   │       ├── helper/                # Clases instanciables de ayuda
│   └── resources/
│       ├── application.yml           # Configuración principal
│       └── db.migration/             # Scripts de migración de base de datos
├── test/
│   └── java/
│       └── com.pichincha.test.<celula>.<nombre-proyecto>/
│           ├── configuration/
│           ├── controller/
│           ├── service/
│           ├── repository/
│           ├── util/
│           └── helper/
├── azure-pipelines.yml               # Configuración de CI/CD
├── Dockerfile                        # Imagen de contenedor
├── README.md                         # Documentación del proyecto
├── settings.xml                      # Configuración de Maven
└── <nombre-proyecto>.yml             # Documentación de API (Swagger/OpenAPI)
```

#### Example code for Spring Web (Controller)
```java
// UserController.java
@RestController
@RequestMapping("/users")
public class UserController { @GetMapping("/{id}")
 public ResponseEntity<UserDto> getUser(@PathVariable Long id) {
 // logic to return user
 return ResponseEntity.ok(new UserDto());
 }
}
```

### Structure for Spring WebFlux

```
src/
├── main/
│   ├── java/
│   │   └── com.pichincha.<celula>.<nombre-proyecto>/
│   │       ├── configuration/         # Configuraciones generales
│   │       ├── repository/            # Interfaces de acceso a datos
│   │       │   └── impl/              # Implementaciones de repositorios
│   │       ├── handler/               # Manejadores reactivos
│   │       │   └── impl/              # Implementaciones de manejadores
│   │       ├── service/               # Interfaces de servicios
│   │       │   └── impl/              # Implementaciones de servicios
│   │       ├── exception/             # Manejo de excepciones
│   │       ├── domain/                # Entidades del dominio
│   │       │   ├── dto/               # Objetos de transferencia de datos
│   │       │   └── enums/             # Enumeraciones
│   │       ├── util/                  # Clases utilitarias
│   │       ├── helper/                # Clases instanciables de ayuda
│   └── resources/
│       ├── application.yml           # Configuración principal
│       └── db.migration/             # Scripts de migración de base de datos
├── test/
│   └── java/
│       └── com.pichincha.test.<celula>.<nombre-proyecto>/
│           ├── configuration/
│           ├── repository/
│           ├── service/
│           ├── util/
│           ├── handler/
│           └── helper/
├── azure-pipelines.yml               # Configuración de CI/CD
├── Dockerfile                        # Imagen de contenedor
├── README.md                         # Documentación del proyecto
├── settings.xml                      # Configuración de Maven
└── <nombre-proyecto>.yml     ��       # Documentación de API (Swagger/OpenAPI)
```

## Conventions and Best Practices

### 🧠 Naming Conventions

#### Classes
- **Format**: `UpperCamelCase`
- **Example**: `UserService`, `OrderController`, `OrderHandler`

#### Methods
- Methods should be verbs in infinitive form with lowerCamelCase nomenclature.
- Methods should have descriptive names that are EASY TO READ AND UNDERSTAND.
- Methods should start with a verb that explains or implies what function the method serves.
- Regarding code length, methods should be small, preferably not exceeding 20-30 lines.
- A method should do ONE THING ONLY and be named according to that function. Single
  Responsibility.
- **Format**: `lowerCamelCase`
- **Example**: `getUserById()`, `userList`
- For variables, use descriptive names and avoid unnecessary abbreviations.

#### Variables
- Variable names should be relevant and meaningful to their use: Example int
  elapsedTimeInDays.
- Don't use generic names like accountList, but rather something more specific like
  customerBankAccountList.
- Avoid using variables like i, j, k, l in loops. When you have nested loops, this
  becomes very difficult to understand.
- Use variable names that can be searched quickly and easily. Each variable should have 
  a meaningful name in each class. For example, if a variable is called account, and is searched 
  through the IDE, that variable may appear in many more classes besides the desired one.
- A variable should only be created when it's going to be used. (Be careful with variables in return statements)

#### Constants
- **Format**: `UPPER_SNAKE_CASE`
- **Example**: `MAX_RETRY_COUNT`

#### Packages
- **Format**: lowercase, without hyphens or uppercase
- **Example**: `com.company.project.service`

#### Favor constructor injection over @Autowired

- When using `@Autowired` on fields, circular dependencies between beans can be created, which may cause compilation failures.
- Constructor injection facilitates the creation of instances for unit tests and makes dependencies explicit.
- There are two recommended ways to implement constructor injection:

1. **Standard form:**
```java
@Service
public class MyService {
    private final MyDependency dependency;
    
    public MyService(MyDependency dependency) {
        this.dependency = dependency;
    }
}
```

2. **Using Lombok with `@RequiredArgsConstructor`:**
```java
@Service
@RequiredArgsConstructor
public class MyService {
    private final MyDependency dependency; // Lombok will generate the constructor automatically
}
```

### 📝 Code Documentation

#### Documentation Comments (JavaDoc)
- It is mandatory to provide documentation comments for each class/interface.
- Comments should be written in the third person and special characters must be encoded.
- Documentation should be clear, concise, and provide the necessary information to understand the purpose and behavior of the code.
- Document any exceptions that may be thrown by the methods.
- Include usage examples when it's helpful for understanding the code.

Example for classes:
```java
/**
 * @class_name MessageController.java
 * @class_description This class has the ability to handle message-related HTTP requests.
 * @author John Doe
 * @create_date 2025-06-17
 * Copyright by Banco Pichincha.
 */
@RestController
@RequestMapping("/messages")
public class MessageController {
    // Controller implementation
}
```

Example for methods:

```java
/**
 * @class_name MessageDTO.java
 * @class_description DTO for transferring message data.
 * @author Banco Pichincha
 * @create_date 2025-06-18
 * Copyright by Banco Pichincha.
 */
package com.pichincha.rrhh.customer.service.dto;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MessageDTO implements Serializable {

  @NotNull(message = "Code message cannot be null")
  @Positive(message = "Code message must be positive")
  private int codeMessage;

  @NotBlank(message = "Description cannot be blank")
  @Size(max = 255, message = "Description cannot exceed 255 characters")
  private String descriptionMessage;
}
```

### 🧹 Code Style

- It's recommended to use the **Google Java Style**. You can access the official repository at the following link: [Google Style Guide](https://github.com/google/styleguide).
- Configure the Java style in the editor or IDE you use to ensure consistency in code formatting.

---

### 📛 Project Naming Conventions

- The project name should follow this format: `<name_1>-<name_2>-<name_3>`.
    - **`name_1`**: Initials of your celula (for example, `crd` for Credit).
    - **`name_2`**: Architecture used (for example, `msa` for Microservice Architecture).
    - **`name_3`**: Project name.
- **Example**: `crd-msa-customer`.

---

### 📦 Package Structure Convention


- For package structure management, it's recommended to follow the **Domain-Driven Design (DDD)** approach.
- Organize the packages so that they reflect the domain contexts and responsibilities of each module.
- **Example**:
    - `com.pichincha.<celula>.<project-name>.domain` for domain entities.
    - `com.pichincha.<celula>.<project-name>.service` for business logic.

---

### 🔐 Security

Verify the following security points:
- **Input Validation**: Validate and sanitize all inputs using annotations such as `@Valid`, `@NotNull`, `@Size`, etc.
- **Authentication and Authorization**: Implement appropriate mechanisms with `Spring Security`.
- **Secure Logging**: Avoid exposing sensitive information in logs or responses.
- **Security Configuration**: Use security filters and configurations.

---

### 🧪 Code Quality

#### General Principles
- **Avoid hardcoded values**: Use constants or configuration files.
- **Separation of concerns**: Apply SOLID principles.
- **Dependency injection**: Prefer constructor injection over field injection with `@Autowired`. This improves testability and makes class dependencies explicit.
- **Avoid logic in controllers or handlers**: Delegate to services.
- **Avoid code duplication**: Reuse components and services.

## Importance of Code Mutation in Tests

**Code mutation** is an advanced technique for evaluating the quality of your unit tests. It involves modifying (mutating) small parts of the source code to verify if existing tests detect the changes (fail when they should).
- If a test does not detect the mutation, it may indicate lack of coverage or that the test is not robust enough.
- Recommended tools: [PIT Mutation Testing](https://pitest.org/).

**Recommendation:** Integrate mutation testing into your CI/CD flow to ensure your tests truly validate the code behavior.

---

## Step-by-Step Development Flow

### Entity
Example of an Entity.

```java

/**
 * @class_name Message.java
 * @class_description This class represents a message entity in the system.
 * @author John Doe
 * @create_date 2025-06-17
 * Copyright by Banco Pichincha.
 */
package com.pichincha.rrhh.customer.domain;

import jakarta.persistence.*;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "MESSAGES")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Message implements Serializable {

    @Id
    @Column(name = "CODE_MESSAGE")
    private int codeMessage;

    @Column(name = "DESCRIPTION_MESSAGE")
    private String descriptionMessage;
}
```

### DTO (Data Transfer Object)
Correct example:
```java
/**
 * @class_name MessageDTO.java
 * @class_description DTO for transferring message data.
 * @author Banco Pichincha
 * @create_date 2025-06-18
 * Copyright by Banco Pichincha.
 */
package com.pichincha.rrhh.customer.service.dto;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MessageDTO implements Serializable {

    @NotNull(message = "Code message cannot be null")
    @Positive(message = "Code message must be positive")
    private int codeMessage;
    
    @NotBlank(message = "Description cannot be blank")
    @Size(max = 255, message = "Description cannot exceed 255 characters")
    private String descriptionMessage;
}
```

### Mapper
Correct example:
```java
/**
 * @class_name MessageMapper.java
 * @class_description Mapper for converting between Message entity and MessageDTO.
 * @author Banco Pichincha
 * @create_date 2025-06-18
 * Copyright by Banco Pichincha.
 */
package com.pichincha.rrhh.customer.service.mapper;

import com.pichincha.rrhh.customer.domain.Message;
import com.pichincha.rrhh.customer.service.dto.MessageDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface MessageMapper {

    MessageDTO messageToMessageDTO(Message message);

    Message messageDTOToMessage(MessageDTO messageDTO);
}

```

### Repository
```java
/**
 * @class_name MessageRepository.java
 * @class_description Repository for accessing Message entities.
 * @author Banco Pichincha
 * @create_date 2025-06-18
 * Copyright by Banco Pichincha.
 */
package com.pichincha.rrhh.customer.repository;

import com.pichincha.rrhh.customer.domain.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MessageRepository extends JpaRepository<Message, Integer> {
    // Custom methods can be added here
}

```

### Service
```java
/**
 * @class_name MessageService.java
 * @class_description Interface for message services.
 * @author Banco Pichincha
 * @create_date 2025-06-18
 * Copyright by Banco Pichincha.
 */
package com.pichincha.rrhh.customer.service;

import com.pichincha.rrhh.customer.service.dto.MessageDTO;
import java.util.List;

public interface MessageService {

    /**
     * Get all messages
     * @return list of all messages
     */
    List<MessageDTO> getAllMessages();

    /**
     * Get a message by its ID
     * @param id the ID of the message
     * @return the message DTO
     */
    MessageDTO getMessageById(int id);

    /**
     * Create a new message
     * @param messageDTO the message data
     * @return the created message DTO
     */
    MessageDTO createMessage(MessageDTO messageDTO);

    /**
     * Update an existing message
     * @param id the ID of the message to update
     * @param messageDTO the new message data
     * @return the updated message DTO
     */
    MessageDTO updateMessage(int id, MessageDTO messageDTO);

    /**
     * Delete a message
     * @param id the ID of the message to delete
     */
    void deleteMessage(int id);
}

```

### Service Implementation
```java
/**
 * @class_name MessageServiceImpl.java
 * @class_description Implementation of the message service operations.
 * @author Banco Pichincha
 * @create_date 2025-06-18
 * Copyright by Banco Pichincha.
 */
package com.pichincha.rrhh.customer.service.impl;

import com.pichincha.rrhh.customer.domain.Message;
import com.pichincha.rrhh.customer.exception.MessageNotFoundException;
import com.pichincha.rrhh.customer.repository.MessageRepository;
import com.pichincha.rrhh.customer.service.MessageService;
import com.pichincha.rrhh.customer.service.dto.MessageDTO;
import com.pichincha.rrhh.customer.service.mapper.MessageMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class MessageServiceImpl implements MessageService {

    private static final Logger logger = LoggerFactory.getLogger(MessageServiceImpl.class);

    private final MessageRepository messageRepository;
    private final MessageMapper messageMapper;

    // Constructor para inyección de dependencias
    public MessageServiceImpl(MessageRepository messageRepository, MessageMapper messageMapper) {
        this.messageRepository = messageRepository;
        this.messageMapper = messageMapper;
    }

    @Override
    public List<MessageDTO> getAllMessages() {
        try {
            return messageRepository.findAll().stream()
                    .map(messageMapper::messageToMessageDTO)
                    .collect(Collectors.toList());
        } catch (Exception ex) {
            logger.error("Error al obtener todos los mensajes", ex);
            throw ex;
        }
    }

    @Override
    public MessageDTO getMessageById(int id) {
        try {
            return messageRepository.findById(id)
                    .map(messageMapper::messageToMessageDTO)
                    .orElseThrow(() -> new MessageNotFoundException("Message not found with id: " + id));
        } catch (Exception ex) {
            logger.error("Error al obtener el mensaje con id {}", id, ex);
            throw ex;
        }
    }

    @Override
    public MessageDTO createMessage(MessageDTO messageDTO) {
        try {
            Message message = messageMapper.messageDTOToMessage(messageDTO);
            Message savedMessage = messageRepository.save(message);
            return messageMapper.messageToMessageDTO(savedMessage);
        } catch (Exception ex) {
            logger.error("Error al crear el mensaje", ex);
            throw ex;
        }
    }

    @Override
    public MessageDTO updateMessage(int id, MessageDTO messageDTO) {
        try {
            return messageRepository.findById(id)
                    .map(existingMessage -> {
                        existingMessage.setDescriptionMessage(messageDTO.getDescriptionMessage());
                        Message updatedMessage = messageRepository.save(existingMessage);
                        return messageMapper.messageToMessageDTO(updatedMessage);
                    })
                    .orElseThrow(() -> new MessageNotFoundException("Message not found with id: " + id));
        } catch (Exception ex) {
            logger.error("Error al actualizar el mensaje con id {}", id, ex);
            throw ex;
        }
    }

    @Override
    public void deleteMessage(int id) {
        try {
            if (!messageRepository.existsById(id)) {
                throw new MessageNotFoundException("Message not found with id: " + id);
            }
            messageRepository.deleteById(id);
        } catch (Exception ex) {
            logger.error("Error al eliminar el mensaje con id {}", id, ex);
            throw ex;
        }
    }
}

```

### Controller

```java
/**
 * @class_name MessageController.java
 * @class_description REST controller for managing messages.
 * @author Banco Pichincha
 * @create_date 2025-06-18
 * Copyright by Banco Pichincha.
 */
package com.pichincha.rrhh.customer.controller;

import com.pichincha.rrhh.customer.service.MessageService;
import com.pichincha.rrhh.customer.service.dto.MessageDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/messages")
public class MessageController {

    private final MessageService messageService;

    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    /**
     * GET /api/messages : Get all messages
     *
     * @return the ResponseEntity with status 200 (OK) and the list of messages
     */
    @GetMapping
    public ResponseEntity<List<MessageDTO>> getAllMessages() {
        List<MessageDTO> messages = messageService.getAllMessages();
        return ResponseEntity.ok(messages);
    }

    /**
     * GET /api/messages/:id : Get a message by id
     *
     * @param id the id of the message to retrieve
     * @return the ResponseEntity with status 200 (OK) and the message, or with status 404 (Not Found)
     */
    @GetMapping("/{id}")
    public ResponseEntity<MessageDTO> getMessageById(@PathVariable("id") int id) {
        MessageDTO message = messageService.getMessageById(id);
        return ResponseEntity.ok(message);
    }

    /**
     * POST /api/messages : Create a new message
     *
     * @param messageDTO the messageDTO to create
     * @return the ResponseEntity with status 201 (Created) and the new message
     */
    @PostMapping
    public ResponseEntity<MessageDTO> createMessage(@RequestBody MessageDTO messageDTO) {
        MessageDTO result = messageService.createMessage(messageDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    /**
     * PUT /api/messages/:id : Update an existing message
     *
     * @param id the id of the message to update
     * @param messageDTO the messageDTO to update
     * @return the ResponseEntity with status 200 (OK) and the updated message
     */
    @PutMapping("/{id}")
    public ResponseEntity<MessageDTO> updateMessage(@PathVariable("id") int id, @RequestBody MessageDTO messageDTO) {
        MessageDTO result = messageService.updateMessage(id, messageDTO);
        return ResponseEntity.ok(result);
    }

    /**
     * DELETE /api/messages/:id : Delete a message
     *
     * @param id the id of the message to delete
     * @return the ResponseEntity with status 204 (NO_CONTENT)
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMessage(@PathVariable("id") int id) {
        messageService.deleteMessage(id);
        return ResponseEntity.noContent().build();
    }
}

```

### Utilities 

```java
/**
 * @class_name MessageUtil.java
 * @class_description Utility class for message operations.
 * @author Banco Pichincha
 * @create_date 2025-06-18
 * Copyright by Banco Pichincha.
 */
package com.pichincha.rrhh.customer.util;

public class MessageUtil {    private MessageUtil() {
        // Private constructor to prevent instantiation
    }

    /**
     * Validates if a message description is valid
     * @param description the description to validate
     * @return true if valid, false otherwise
     */
    public static boolean isValidMessageDescription(String description) {
        return description != null && !description.trim().isEmpty();
    }
}

```

### Exceptions

```java
/**
 * @class_name MessageNotFoundException.java
 * @class_description Exception thrown when a message is not found.
 * @author Banco Pichincha
 * @create_date 2025-06-18
 * Copyright by Banco Pichincha.
 */
package com.pichincha.rrhh.customer.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class MessageNotFoundException extends RuntimeException {

    public MessageNotFoundException(String message) {
        super(message);
    }
}

```
```java
package com.pichincha.sp.employee.vacation.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestControllerAdvice
public class GlobalExceptionHandler {

  private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

  @ExceptionHandler(Exception.class)
  public ResponseEntity<Object> handleAllExceptions(Exception ex, WebRequest request) {
    logger.error("Error interno del servidor: {}", ex.getMessage(), ex);
    Map<String, Object> body = new HashMap<>();
    body.put("timestamp", LocalDateTime.now());
    body.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
    body.put("error", "Error interno del servidor");
    body.put("message", ex.getMessage());
    body.put("path", request.getDescription(false));
    return new ResponseEntity<>(body, HttpStatus.INTERNAL_SERVER_ERROR);
  }

  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<Object> handleIllegalArgumentException(IllegalArgumentException ex, WebRequest request) {
    logger.warn("Solicitud incorrecta: {}", ex.getMessage());
    Map<String, Object> body = new HashMap<>();
    body.put("timestamp", LocalDateTime.now());
    body.put("status", HttpStatus.BAD_REQUEST.value());
    body.put("error", "Solicitud incorrecta");
    body.put("message", ex.getMessage());
    body.put("path", request.getDescription(false));
    return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
  }
}
```

### Helpers

```java
/**
 * @class_name MessageHelper.java
 * @class_description Helper class for message operations.
 * @author Banco Pichincha
 * @create_date 2025-06-18
 * Copyright by Banco Pichincha.
 */
package com.pichincha.rrhh.customer.helper;

import com.pichincha.rrhh.customer.service.dto.MessageDTO;
import org.springframework.stereotype.Component;

@Component
public class MessageHelper {

    private static final String INVALID_DATA = "Invalid message data.";

    /**
     * Formats a message for display
     * @param messageDTO the message to format
     * @return formatted message string
     */
    public String formatMessageForDisplay(MessageDTO messageDTO) {
        if (messageDTO == null) {
            return INVALID_DATA;
        }

        return String.format("Message Code: %d, Description: %s",
                messageDTO.getCodeMessage(),
                messageDTO.getDescriptionMessage());
    }
}

```

### Service Implementation with Constructor Dependency Injection

When creating services in Spring Boot, it is recommended to use constructor dependency injection instead of `@Autowired` on fields. This improves testability and makes the dependencies of the class explicit.

#### Example of Service with Constructor Injection

```java
package com.pichincha.rrhh.customer.service.impl;

import com.pichincha.rrhh.customer.domain.Message;
import com.pichincha.rrhh.customer.exception.MessageNotFoundException;
import com.pichincha.rrhh.customer.repository.MessageRepository;
import com.pichincha.rrhh.customer.service.MessageService;
import com.pichincha.rrhh.customer.service.dto.MessageDTO;
import com.pichincha.rrhh.customer.service.mapper.MessageMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @class_name MessageServiceImpl.java
 * @class_description This class implements the message service operations.
 * @author John Doe
 * @create_date 2025-06-17
 * Copyright by Banco Pichincha.
 */
@Service
public class MessageServiceImpl implements MessageService {

    private static final Logger logger = LoggerFactory.getLogger(MessageServiceImpl.class);

    private final MessageRepository messageRepository;
    private final MessageMapper messageMapper;

    // Constructor para inyección de dependencias
    public MessageServiceImpl(MessageRepository messageRepository, MessageMapper messageMapper) {
        this.messageRepository = messageRepository;
        this.messageMapper = messageMapper;
    }

    @Override
    public List<MessageDTO> getAllMessages() {
        try {
            return messageRepository.findAll().stream()
                    .map(messageMapper::messageToMessageDTO)
                    .collect(Collectors.toList());
        } catch (Exception ex) {
            logger.error("Error al obtener todos los mensajes", ex);
            throw ex;
        }
    }

    @Override
    public MessageDTO getMessageById(int id) {
        try {
            return messageRepository.findById(id)
                    .map(messageMapper::messageToMessageDTO)
                    .orElseThrow(() -> new MessageNotFoundException("Message not found with id: " + id));
        } catch (Exception ex) {
            logger.error("Error al obtener el mensaje con id {}", id, ex);
            throw ex;
        }
    }

    @Override
    public MessageDTO createMessage(MessageDTO messageDTO) {
        try {
            Message message = messageMapper.messageDTOToMessage(messageDTO);
            Message savedMessage = messageRepository.save(message);
            return messageMapper.messageToMessageDTO(savedMessage);
        } catch (Exception ex) {
            logger.error("Error al crear el mensaje", ex);
            throw ex;
        }
    }

    @Override
    public MessageDTO updateMessage(int id, MessageDTO messageDTO) {
        try {
            return messageRepository.findById(id)
                    .map(existingMessage -> {
                        existingMessage.setDescriptionMessage(messageDTO.getDescriptionMessage());
                        Message updatedMessage = messageRepository.save(existingMessage);
                        return messageMapper.messageToMessageDTO(updatedMessage);
                    })
                    .orElseThrow(() -> new MessageNotFoundException("Message not found with id: " + id));
        } catch (Exception ex) {
            logger.error("Error al actualizar el mensaje con id {}", id, ex);
            throw ex;
        }
    }

    @Override
    public void deleteMessage(int id) {
        try {
            if (!messageRepository.existsById(id)) {
                throw new MessageNotFoundException("Message not found with id: " + id);
            }
            messageRepository.deleteById(id);
        } catch (Exception ex) {
            logger.error("Error al eliminar el mensaje con id {}", id, ex);
            throw ex;
        }
    }
}
```

#### Example of Controller with Constructor Injection

```java
package com.pichincha.rrhh.customer.controller;

import com.pichincha.rrhh.customer.service.MessageService;
import com.pichincha.rrhh.customer.service.dto.MessageDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @class_name MessageController.java
 * @class_description This class handles HTTP requests related to messages.
 * @author John Doe
 * @create_date 2025-06-17
 * Copyright by Banco Pichincha.
 */
@RestController
@RequestMapping("/messages")
public class MessageController {

    private final MessageService messageService;

    // Constructor para inyección de dependencias
    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    /**
     * Retrieves all messages
     * 
     * @return A list of all messages
     */
    @GetMapping
    public ResponseEntity<List<MessageDTO>> getAllMessages() {
        return ResponseEntity.ok(messageService.getAllMessages());
    }

    /**
     * Retrieves a message by its ID
     * 
     * @param id The ID of the message to retrieve
     * @return The message if found
     */
    @GetMapping("/{id}")
    public ResponseEntity<MessageDTO> getMessageById(@PathVariable int id) {
        return ResponseEntity.ok(messageService.getMessageById(id));
    }

    /**
     * Creates a new message
     * 
     * @param messageDTO The message data to create
     * @return The created message
     */
    @PostMapping
    public ResponseEntity<MessageDTO> createMessage(@RequestBody MessageDTO messageDTO) {
        return new ResponseEntity<>(messageService.createMessage(messageDTO), HttpStatus.CREATED);
    }

    /**
     * Updates an existing message
     * 
     * @param id The ID of the message to update
     * @param messageDTO The new message data
     * @return The updated message
     */
    @PutMapping("/{id}")
    public ResponseEntity<MessageDTO> updateMessage(@PathVariable int id, @RequestBody MessageDTO messageDTO) {
        return ResponseEntity.ok(messageService.updateMessage(id, messageDTO));
    }

    /**
     * Deletes a message
     * 
     * @param id The ID of the message to delete
     * @return No content
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMessage(@PathVariable int id) {
        messageService.deleteMessage(id);
        return ResponseEntity.noContent().build();
    }
}

```

## Unit Tests

Once the code is improved, you can proceed to write unit tests. Use the following JUnit and Mockito annotations and tools:

### DAMP Approach (Descriptive And Meaningful Phrases)
Tests should follow the DAMP approach, which promotes:
- Descriptive tests that document the expected behavior
- Test names that clearly describe the scenario and expected result
- Readable and maintainable test code, even if it involves some duplication

### 3A Pattern (Arrange, Act, Assert)
Tests should be structured following the 3A pattern:
1. **Arrange**: Preparation of the data and objects needed for the test
2. **Act**: Execution of the action to be tested
3. **Assert**: Verification of the expected results

Ejemplo:
```java
@Test
void GivenExistingIdWhenGetMessageByIdThenReturnMessage() {
    // Arrange
    int messageId = 1;
    Message message = new Message(messageId, "Test Message");
    when(messageRepository.findById(messageId)).thenReturn(Optional.of(message));
    when(messageMapper.messageToMessageDTO(message)).thenReturn(new MessageDTO(messageId, "Test Message"));
    
    // Act
    MessageDTO result = messageService.getMessageById(messageId);
    
    // Assert
    assertNotNull(result);
    assertEquals(messageId, result.getCodeMessage());
    assertEquals("Test Message", result.getDescriptionMessage());
}
```
- Create unit tests for the created objects:
    - For entities.
    - For DTOs.
    - For mappers.
    - For repositories.    - For controllers.
    - For services.
    - For utilities.
    - For exceptions.
    - For helpers.

### Test Naming in Gherkin Format

It is recommended to name test methods following the **Gherkin** format:  
**Given[InitialCondition]When[Action]Then[ExpectedResult]**

- Example:  
  `GivenUserExistsWhenGetUserByIdThenReturnUser()`
- This facilitates understanding of the tested scenario and the expected result.

---

### Unit Tests for Domain
Implementation of unit test for ENUM. Example code is shown below.
```java
package com.pichincha.rrhh.customer.domain.enums;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class MessageErrorEnumTest {

    @Test
    void GivenMessageNotFoundEnumWhenGetCodeThenReturnCorrectCode() {
        assertEquals("MSG_001", MessageErrorEnum.MESSAGE_NOT_FOUND.getCode());
    }

    @Test
    void GivenMessageNotFoundEnumWhenGetMessageThenReturnCorrectMessage() {
        assertEquals("Message not found with id: 100", MessageErrorEnum.MESSAGE_NOT_FOUND.getMessage(100));
    }

    @Test
    void GivenGeneralErrorEnumWhenGetCodeThenReturnCorrectCode() {
        assertEquals("MSG_GEN_001", MessageErrorEnum.GENERAL_ERROR.getCode());
    }

    @Test
    void GivenGeneralErrorEnumWhenGetMessageThenReturnCorrectMessage() {
        assertEquals("An unexpected error occurred.", MessageErrorEnum.GENERAL_ERROR.getMessage());
    }

    @Test
    void GivenMessageNotFoundEnumWhenGetMessageWithNoArgsThenThrowsException() {
        // Verifica el comportamiento cuando se llama a getMessage sin los argumentos esperados
        // Esto dependerá de cómo esté implementado String.format y si quieres probar ese caso específico.
        // Por lo general, String.format lanzará una MissingFormatArgumentException si faltan argumentos.
        assertThrows(java.util.MissingFormatArgumentException.class, () -> {
            MessageErrorEnum.MESSAGE_NOT_FOUND.getMessage(); // No se proporcionan argumentos para %s
        });
    }

    @Test
    void GivenGeneralErrorEnumWhenGetMessageWithArgsThenMessageIsUnchanged() {
        // Para enums que no esperan argumentos, pasar argumentos no debería cambiar el mensaje.
        assertEquals("An unexpected error occurred.", MessageErrorEnum.GENERAL_ERROR.getMessage("arg1", "arg2"));
    }
}


```

### Unit Tests for Repositories

Example code for repository unit test.
```java
package com.pichincha.rrhh.customer.repository;

import com.pichincha.rrhh.customer.domain.Message;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class MessageRepositoryTest {    private final TestEntityManager entityManager;
    private final MessageRepository messageRepository;
    
    // Constructor for dependency injection
    public MessageRepositoryTest(TestEntityManager entityManager, MessageRepository messageRepository) {
        this.entityManager = entityManager;
        this.messageRepository = messageRepository;
    }

    private static final int TEST_CODE_MESSAGE = 1;
    private static final String TEST_DESCRIPTION_MESSAGE = "Test Description";

    @Test
    void GivenNewMessageWhenSaveThenMessageIsPersisted() {
        Message newMessage = new Message(TEST_CODE_MESSAGE, TEST_DESCRIPTION_MESSAGE);
        Message savedMessage = messageRepository.save(newMessage);

        assertNotNull(savedMessage);
        assertEquals(TEST_CODE_MESSAGE, savedMessage.getCodeMessage());
        assertEquals(TEST_DESCRIPTION_MESSAGE, savedMessage.getDescriptionMessage()); // Verificar descripción en el objeto guardado

        entityManager.flush(); // Forzar el flush a la base de datos

        Message foundMessage = entityManager.find(Message.class, savedMessage.getCodeMessage());
        assertNotNull(foundMessage, "La entidad debería encontrarse después de save y flush");
        assertEquals(TEST_DESCRIPTION_MESSAGE, foundMessage.getDescriptionMessage());
    }

    @Test
    void GivenExistingMessageIdWhenFindByIdThenReturnMessage() {
        Message message = new Message(TEST_CODE_MESSAGE, TEST_DESCRIPTION_MESSAGE);
        entityManager.persistAndFlush(message);

        Optional<Message> foundMessageOpt = messageRepository.findById(TEST_CODE_MESSAGE);

        assertTrue(foundMessageOpt.isPresent());
        assertEquals(TEST_DESCRIPTION_MESSAGE, foundMessageOpt.get().getDescriptionMessage());
    }    @Test
    void GivenNonExistingMessageIdWhenFindByIdThenReturnEmptyOptional() {
        Optional<Message> foundMessageOpt = messageRepository.findById(999); // ID that doesn't exist
        assertFalse(foundMessageOpt.isPresent());
    }
}

```

### Unit Tests for Services

Implementation of unit test for DTOs. Example code is shown below.
```java
package com.pichincha.rrhh.customer.service.dto;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class MessageDTOTest {

    private static final int TEST_CODE_MESSAGE = 1;
    private static final String TEST_DESCRIPTION_MESSAGE = "Test Description";

    @Test
    void GivenValidParametersWhenMessageDTOCreatedThenGettersReturnCorrectValues() {
        MessageDTO messageDTO = MessageDTO.builder()
                .codeMessage(TEST_CODE_MESSAGE)
                .descriptionMessage(TEST_DESCRIPTION_MESSAGE)
                .build();

        assertEquals(TEST_CODE_MESSAGE, messageDTO.getCodeMessage());
        assertEquals(TEST_DESCRIPTION_MESSAGE, messageDTO.getDescriptionMessage());
    }

    @Test
    void GivenNoArgsConstructorWhenMessageDTOCreatedThenFieldsAreDefault() {
        MessageDTO messageDTO = new MessageDTO();
        assertEquals(0, messageDTO.getCodeMessage());
        assertNull(messageDTO.getDescriptionMessage());
    }

    @Test
    void GivenAllArgsConstructorWhenMessageDTOCreatedThenFieldsAreSet() {
        MessageDTO messageDTO = new MessageDTO(TEST_CODE_MESSAGE, TEST_DESCRIPTION_MESSAGE);
        assertEquals(TEST_CODE_MESSAGE, messageDTO.getCodeMessage());
        assertEquals(TEST_DESCRIPTION_MESSAGE, messageDTO.getDescriptionMessage());
    }

    @Test
    void GivenSetterUsedWhenMessageDTOModifiedThenGettersReturnUpdatedValues() {
        MessageDTO messageDTO = new MessageDTO();
        messageDTO.setCodeMessage(TEST_CODE_MESSAGE);
        messageDTO.setDescriptionMessage(TEST_DESCRIPTION_MESSAGE);

        assertEquals(TEST_CODE_MESSAGE, messageDTO.getCodeMessage());
        assertEquals(TEST_DESCRIPTION_MESSAGE, messageDTO.getDescriptionMessage());
    }
}
```

Example code for unit test of Mappers.

```java
package com.pichincha.rrhh.customer.service.mapper;

import com.pichincha.rrhh.customer.domain.Message;
import com.pichincha.rrhh.customer.service.dto.MessageDTO;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.junit.jupiter.api.Assertions.*;

class MessageMapperTest {

    private final MessageMapper messageMapper = Mappers.getMapper(MessageMapper.class);

    private static final int TEST_CODE_MESSAGE = 1;
    private static final String TEST_DESCRIPTION_MESSAGE = "Test Description";

    @Test
    void GivenNonNullMessageWhenMessageToMessageDTOThenCorrectDTOIsReturned() {
        Message message = new Message(TEST_CODE_MESSAGE, TEST_DESCRIPTION_MESSAGE);

        MessageDTO messageDTO = messageMapper.messageToMessageDTO(message);

        assertNotNull(messageDTO);
        assertEquals(TEST_CODE_MESSAGE, messageDTO.getCodeMessage());
        assertEquals(TEST_DESCRIPTION_MESSAGE, messageDTO.getDescriptionMessage());
    }

    @Test
    void GivenNullMessageWhenMessageToMessageDTOThenNullIsReturned() {
        MessageDTO messageDTO = messageMapper.messageToMessageDTO(null);
        assertNull(messageDTO);
    }

    @Test
    void GivenNonNullMessageDTOWhenMessageDTOToMessageThenCorrectEntityIsReturned() {
        MessageDTO messageDTO = new MessageDTO(TEST_CODE_MESSAGE, TEST_DESCRIPTION_MESSAGE);

        Message message = messageMapper.messageDTOToMessage(messageDTO);

        assertNotNull(message);
        assertEquals(TEST_CODE_MESSAGE, message.getCodeMessage());
        assertEquals(TEST_DESCRIPTION_MESSAGE, message.getDescriptionMessage());
    }

    @Test
    void GivenNullMessageDTOWhenMessageDTOToMessageThenNullIsReturned() {
        Message message = messageMapper.messageDTOToMessage(null);
        assertNull(message);
    }
}

```

Implementation of unit test for services. Example code is shown below.

```java
package com.pichincha.rrhh.customer.service.impl;

import com.pichincha.rrhh.customer.domain.Message;
import com.pichincha.rrhh.customer.exception.MessageNotFoundException;
import com.pichincha.rrhh.customer.repository.MessageRepository;
import com.pichincha.rrhh.customer.service.dto.MessageDTO;
import com.pichincha.rrhh.customer.service.mapper.MessageMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MessageServiceImplTest {

    @Mock
    private MessageRepository messageRepository;

    @Mock
    private MessageMapper messageMapper;
    
    private MessageServiceImpl messageService;
    
    private Message message;
    private MessageDTO messageDTO;

    private static final int TEST_ID = 1;
    private static final String TEST_DESCRIPTION = "Test Message";
    private static final String UPDATED_DESCRIPTION = "Updated Message";    
    
    @BeforeEach
    void setUp() {
        messageService = new MessageServiceImpl(messageRepository, messageMapper);
        message = new Message(TEST_ID, TEST_DESCRIPTION);
        messageDTO = new MessageDTO(TEST_ID, TEST_DESCRIPTION);
    }

    @Test
    void GivenMessagesExistWhenGetAllMessagesThenReturnListOfMessageDTOs() {
        when(messageRepository.findAll()).thenReturn(Arrays.asList(message));
        when(messageMapper.messageToMessageDTO(any(Message.class))).thenReturn(messageDTO);

        List<MessageDTO> result = messageService.getAllMessages();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(messageDTO, result.get(0));
        verify(messageRepository).findAll();
        verify(messageMapper).messageToMessageDTO(message);
    }

    @Test
    void GivenExistingIdWhenGetMessageByIdThenReturnMessageDTO() {
        when(messageRepository.findById(TEST_ID)).thenReturn(Optional.of(message));
        when(messageMapper.messageToMessageDTO(message)).thenReturn(messageDTO);

        MessageDTO result = messageService.getMessageById(TEST_ID);

        assertNotNull(result);
        assertEquals(messageDTO, result);
        verify(messageRepository).findById(TEST_ID);
        verify(messageMapper).messageToMessageDTO(message);
    }

    @Test
    void GivenNonExistingIdWhenGetMessageByIdThenThrowMessageNotFoundException() {
        when(messageRepository.findById(TEST_ID)).thenReturn(Optional.empty());

        assertThrows(MessageNotFoundException.class, () -> messageService.getMessageById(TEST_ID));
        verify(messageRepository).findById(TEST_ID);
        verify(messageMapper, never()).messageToMessageDTO(any());
    }

    @Test
    void GivenValidMessageDTOWhenCreateMessageThenReturnCreatedMessageDTO() {
        when(messageMapper.messageDTOToMessage(any(MessageDTO.class))).thenReturn(message);
        when(messageRepository.save(any(Message.class))).thenReturn(message);
        when(messageMapper.messageToMessageDTO(any(Message.class))).thenReturn(messageDTO);

        MessageDTO result = messageService.createMessage(messageDTO);

        assertNotNull(result);
        assertEquals(messageDTO, result);
        verify(messageMapper).messageDTOToMessage(messageDTO);
        verify(messageRepository).save(message);
        verify(messageMapper).messageToMessageDTO(message);
    }

    @Test
    void GivenExistingIdAndMessageDTOWhenUpdateMessageThenReturnUpdatedMessageDTO() {
        MessageDTO updatedMessageDTO = new MessageDTO(TEST_ID, UPDATED_DESCRIPTION);

        // Usamos doReturn().when() con any(Message.class) para evitar la comparación exacta de objetos
        when(messageRepository.findById(TEST_ID)).thenReturn(Optional.of(message));
        when(messageRepository.save(any(Message.class))).thenReturn(message); // La entidad guardada podría ser cualquier instancia
        doReturn(updatedMessageDTO).when(messageMapper).messageToMessageDTO(any(Message.class));

        MessageDTO result = messageService.updateMessage(TEST_ID, updatedMessageDTO);

        assertNotNull(result);
        assertEquals(updatedMessageDTO.getDescriptionMessage(), result.getDescriptionMessage());
        verify(messageRepository).findById(TEST_ID);
        verify(messageRepository).save(any(Message.class));
        verify(messageMapper).messageToMessageDTO(any(Message.class)); // Verificamos que se llamó con cualquier Message
    }

    @Test
    void GivenNonExistingIdWhenUpdateMessageThenThrowMessageNotFoundException() {
        MessageDTO updatedMessageDTO = new MessageDTO(TEST_ID, UPDATED_DESCRIPTION);
        when(messageRepository.findById(TEST_ID)).thenReturn(Optional.empty());

        assertThrows(MessageNotFoundException.class, () -> messageService.updateMessage(TEST_ID, updatedMessageDTO));
        verify(messageRepository).findById(TEST_ID);
        verify(messageRepository, never()).save(any());
        verify(messageMapper, never()).messageToMessageDTO(any());
    }

    @Test
    void GivenExistingIdWhenDeleteMessageThenCallRepositoryDeleteById() {
        when(messageRepository.existsById(TEST_ID)).thenReturn(true);
        doNothing().when(messageRepository).deleteById(TEST_ID);

        messageService.deleteMessage(TEST_ID);

        verify(messageRepository).existsById(TEST_ID);
        verify(messageRepository).deleteById(TEST_ID);
    }

    @Test
    void GivenNonExistingIdWhenDeleteMessageThenThrowMessageNotFoundException() {
        when(messageRepository.existsById(TEST_ID)).thenReturn(false);

        assertThrows(MessageNotFoundException.class, () -> messageService.deleteMessage(TEST_ID));
        verify(messageRepository).existsById(TEST_ID);
        verify(messageRepository, never()).deleteById(anyInt());
    }
}


```

### Unit Tests for Controller
Implementation of unit test for Controller. Example code is shown below.
```java
package com.pichincha.rrhh.customer.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pichincha.rrhh.customer.service.MessageService;
import com.pichincha.rrhh.customer.service.dto.MessageDTO;
import com.pichincha.rrhh.customer.exception.MessageNotFoundException; // O MessageNotFoundException
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MessageController.class)
@ExtendWith(MockitoExtension.class)
class MessageControllerTest {

    private static final int TEST_CODE_MESSAGE = 1;
    private static final String TEST_DESCRIPTION_MESSAGE = "Test Message";
    private static final String UPDATED_MESSAGE_DESCRIPTION = "Updated Message";

    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;
      @MockBean
    private MessageService messageService;
      // Constructor for dependency injection
    public MessageControllerTest(MockMvc mockMvc, ObjectMapper objectMapper) {
        this.mockMvc = mockMvc;
        this.objectMapper = objectMapper;
    }

    @Test
    void GivenMessagesExistWhenGetAllMessagesThenReturnListOfMessages() throws Exception {
        MessageDTO messageDTO = new MessageDTO(TEST_CODE_MESSAGE, TEST_DESCRIPTION_MESSAGE);
        List<MessageDTO> messageList = Arrays.asList(messageDTO);

        when(messageService.getAllMessages()).thenReturn(messageList);

        mockMvc.perform(get("/messages")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].codeMessage", is(TEST_CODE_MESSAGE)))
                .andExpect(jsonPath("$[0].descriptionMessage", is(TEST_DESCRIPTION_MESSAGE)));
    }

    @Test
    void GivenMessageExistsWhenGetMessageByIdThenReturnMessage() throws Exception {
        MessageDTO messageDTO = new MessageDTO(TEST_CODE_MESSAGE, TEST_DESCRIPTION_MESSAGE);
        when(messageService.getMessageById(TEST_CODE_MESSAGE)).thenReturn(messageDTO);

        mockMvc.perform(get("/messages/{id}", TEST_CODE_MESSAGE)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.codeMessage", is(TEST_CODE_MESSAGE)))
                .andExpect(jsonPath("$.descriptionMessage", is(TEST_DESCRIPTION_MESSAGE)));
    }

    @Test
    void GivenNonExistingMessageIdWhenGetMessageByIdThenReturnNotFound() throws Exception {
        when(messageService.getMessageById(TEST_CODE_MESSAGE)).thenThrow(new MessageNotFoundException("Message not found"));

        mockMvc.perform(get("/messages/{id}", TEST_CODE_MESSAGE)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void GivenValidMessageDTOWhenCreateMessageThenReturnCreatedMessage() throws Exception {
        MessageDTO messageDTO = new MessageDTO(TEST_CODE_MESSAGE, TEST_DESCRIPTION_MESSAGE);
        MessageDTO createdMessageDTO = new MessageDTO(TEST_CODE_MESSAGE, TEST_DESCRIPTION_MESSAGE);

        when(messageService.createMessage(any(MessageDTO.class))).thenReturn(createdMessageDTO);

        mockMvc.perform(post("/messages")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(messageDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.codeMessage", is(TEST_CODE_MESSAGE)))
                .andExpect(jsonPath("$.descriptionMessage", is(TEST_DESCRIPTION_MESSAGE)));
    }

    @Test
    void GivenExistingIdAndMessageDTOWhenUpdateMessageThenReturnUpdatedMessage() throws Exception {
        MessageDTO messageDTO = new MessageDTO(TEST_CODE_MESSAGE, UPDATED_MESSAGE_DESCRIPTION);
        MessageDTO updatedMessageDTO = new MessageDTO(TEST_CODE_MESSAGE, UPDATED_MESSAGE_DESCRIPTION);

        when(messageService.updateMessage(eq(TEST_CODE_MESSAGE), any(MessageDTO.class))).thenReturn(updatedMessageDTO);

        mockMvc.perform(put("/messages/{id}", TEST_CODE_MESSAGE)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(messageDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.codeMessage", is(TEST_CODE_MESSAGE)))
                .andExpect(jsonPath("$.descriptionMessage", is(UPDATED_MESSAGE_DESCRIPTION)));
    }

    @Test
    void GivenNonExistingIdWhenUpdateMessageThenReturnNotFound() throws Exception {
        MessageDTO messageDTO = new MessageDTO(TEST_CODE_MESSAGE, UPDATED_MESSAGE_DESCRIPTION);
        when(messageService.updateMessage(eq(TEST_CODE_MESSAGE), any(MessageDTO.class))).thenThrow(new MessageNotFoundException("Message not found"));

        mockMvc.perform(put("/messages/{id}", TEST_CODE_MESSAGE)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(messageDTO)))
                .andExpect(status().isNotFound());
    }

    @Test
    void GivenExistingIdWhenDeleteMessageThenReturnNoContent() throws Exception {
        doNothing().when(messageService).deleteMessage(TEST_CODE_MESSAGE);

        mockMvc.perform(delete("/messages/{id}", TEST_CODE_MESSAGE))
                .andExpect(status().isNoContent());

        verify(messageService, times(1)).deleteMessage(TEST_CODE_MESSAGE);
    }

    @Test
    void GivenNonExistingIdWhenDeleteMessageThenReturnNotFound() throws Exception {
        doThrow(new MessageNotFoundException("Message not found")).when(messageService).deleteMessage(TEST_CODE_MESSAGE);

        mockMvc.perform(delete("/messages/{id}", TEST_CODE_MESSAGE))
                .andExpect(status().isNotFound());
    }
}


```

### Unit Tests for Utilities

Implementation of unit test for utilities. Example code is shown below.
```java
package com.pichincha.rrhh.customer.util;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class MessageUtilTest {

    @Test
    void GivenValidDescriptionWhenIsValidMessageDescriptionThenReturnTrue() {
        assertTrue(MessageUtil.isValidMessageDescription("Valid description"));
    }

    @Test
    void GivenNullDescriptionWhenIsValidMessageDescriptionThenReturnFalse() {
        assertFalse(MessageUtil.isValidMessageDescription(null));
    }

    @Test
    void GivenEmptyDescriptionWhenIsValidMessageDescriptionThenReturnFalse() {
        assertFalse(MessageUtil.isValidMessageDescription(""));
    }

    @Test
    void GivenBlankDescriptionWhenIsValidMessageDescriptionThenReturnFalse() {
        assertFalse(MessageUtil.isValidMessageDescription("   "));
    }
}


```

### Unit Tests for Exceptions

Implementation of unit test for Exceptions. Example code is shown below.
```java
package com.pichincha.rrhh.customer.exception;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class MessageNotFoundExceptionTest {

    private static final String ERROR_MESSAGE = "Test error message";

    @Test
    void GivenMessageWhenMessageNotFoundExceptionCreatedThenGetMessageReturnsCorrectMessage() {
        MessageNotFoundException exception = new MessageNotFoundException(ERROR_MESSAGE);
        assertEquals(ERROR_MESSAGE, exception.getMessage());
    }

    @Test
    void GivenNullMessageWhenMessageNotFoundExceptionCreatedThenGetMessageReturnsNull() {
        MessageNotFoundException exception = new MessageNotFoundException(null);
        assertNull(exception.getMessage());
    }
}


```

### Unit Tests for Helpers

Implementation of unit test for Helpers. Example code is shown below.
```java
package com.pichincha.rrhh.customer.helper;

import com.pichincha.rrhh.customer.service.dto.MessageDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class MessageHelperTest {

    private MessageHelper messageHelper;
    
    private static final int TEST_CODE_MESSAGE = 1;
    private static final String TEST_DESCRIPTION_MESSAGE = "Test Description";
    private static final String EXPECTED_FORMATTED_STRING = "Message Code: 1, Description: Test Description";
    private static final String EXPECTED_INVALID_DATA_STRING = "Invalid message data.";
    
    @BeforeEach
    void setUp() {
        this.messageHelper = new MessageHelper();
    }

    @Test
    void GivenValidMessageDTOWhenFormatMessageForDisplayThenReturnFormattedString() {
        MessageDTO messageDTO = new MessageDTO(TEST_CODE_MESSAGE, TEST_DESCRIPTION_MESSAGE);
        String formattedMessage = messageHelper.formatMessageForDisplay(messageDTO);
        assertEquals(EXPECTED_FORMATTED_STRING, formattedMessage);
    }

    @Test
    void GivenNullMessageDTOWhenFormatMessageForDisplayThenReturnInvalidDataString() {
        String formattedMessage = messageHelper.formatMessageForDisplay(null);
        assertEquals(EXPECTED_INVALID_DATA_STRING, formattedMessage);
    }

    @Test
    void GivenMessageDTOWithNullDescriptionWhenFormatMessageForDisplayThenReturnFormattedStringWithNullDescription() {
        MessageDTO messageDTO = new MessageDTO(TEST_CODE_MESSAGE, null);
        String expectedOutput = String.format("Message Code: %d, Description: %s", TEST_CODE_MESSAGE, null);
        String formattedMessage = messageHelper.formatMessageForDisplay(messageDTO);
        assertEquals(expectedOutput, formattedMessage);
    }

    @Test
    void GivenMessageDTOWithZeroCodeWhenFormatMessageForDisplayThenReturnFormattedStringWithZeroCode() {
        MessageDTO messageDTO = new MessageDTO(0, TEST_DESCRIPTION_MESSAGE);
        String expectedOutput = String.format("Message Code: %d, Description: %s", 0, TEST_DESCRIPTION_MESSAGE);
        String formattedMessage = messageHelper.formatMessageForDisplay(messageDTO);
        assertEquals(expectedOutput, formattedMessage);
    }
}


```

### Advanced Examples and Best Practices

#### Testing Exceptions

- Make sure to test scenarios where the code should throw exceptions.

```java
@Test
void GivenInvalidInputWhenMethodFailsThenThrowIllegalArgumentException() {
    MyClass myClass = new MyClass();
    assertThrows(IllegalArgumentException.class, () -> myClass.methodThatFails());
}
```

---

#### Parameterized Tests

- Use parameterized tests to validate multiple inputs efficiently.

```java
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

@ParameterizedTest
@ValueSource(strings = { "value1", "value2", "value3" })
void GivenVariousInputsWhenTestMethodWithMultipleValuesThenNotNull(String input) {
    assertNotNull(input);
}
```

---

#### Using Fixtures (`@BeforeAll`, `@BeforeEach`)

Take advantage of initialization methods to prepare reusable data.

```java
@BeforeAll
static void initAll() {
    // Global initialization before all tests
}

@BeforeEach
void init() {
    // Initialization before each test
}
```

---

#### Basic Integration Tests

Difference between unit tests and integration tests, and how to create a simple integration.

```java
@SpringBootTest
class MyIntegrationTest {
    private final MyService myService;
    
    // Constructor for dependency injection
    public MyIntegrationTest(MyService myService) {
        this.myService = myService;
    }

    @Test
    void GivenServiceAvailableWhenImportantMethodThenNotNull() {
        assertNotNull(myService.importantMethod());
    }
}
```

---

#### Code Coverage and Tools

Recommends the use of tools like JaCoCo to measure test coverage.

```text
To measure test coverage, integrate JaCoCo into your build.gradle or pom.xml and review the generated reports.
```

---

#### Additional Best Practices

- Use `@DisplayName` to better describe tests.
- Separate unit tests and integration tests into different folders.
- Run tests in your CI/CD pipeline.
- Mock external dependencies (APIs, DB, etc.) to keep unit tests isolated.

