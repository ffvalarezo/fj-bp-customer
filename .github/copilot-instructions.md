# Mejores Prácticas

Eres un experto senior en desarrollo de software. Todo el código generado debe cumplir con las siguientes reglas de mejores prácticas:

- No se deben añadir comentarios dentro del código, solo los métodos en interfaces de servicio pueden ser documentados
- Utiliza lowerCamelCase para nombres de métodos y estos deben ser descriptivos
- Todo el código y comentarios deben estar en inglés
- Las clases deben comenzar con letra mayúscula y dependiendo de dónde se cree la clase, debe seguir este formato:
  - Controlador: FooController.java (implementación de interfaces openapi)
  - Servicio: FooService.java (solo interfaz)
  - Repositorio: FooRepository.java
  - Dominio: Foo.java (solo objetos de negocio o entidades de base de datos)
  - Utilidad: FooUtil.java (solo utilidades, clases estáticas)
  - Ayudante: FooHelper.java (solo ayudantes, no estáticos)
  - Excepción: FooException.java (solo excepciones personalizadas)
  - Configuración: Foo.config.java (solo configuraciones)
  - Mapeador: FooMapper.java (mapeos de objetos de dominio a modelos autogenerados por openapi)
  - domain.enums: FooEnum.java (solo enumeraciones)
  - service.impl: FooServiceImpl.java (solo implementaciones de interfaces)
- Los métodos de clase no deben tener más de 20 líneas de código
- El número máximo de columnas por línea de código es 100
- Usa 2 espacios para la indentación
- Usa 4 espacios para la indentación en líneas de código divididas
- Cada vez que se añade código, se deben limpiar las importaciones no utilizadas
- Al crear una interfaz, el nombre debe estar en mayúscula y debe terminar con el nombre del paquete que se está creando
- Los nombres de clase deben ser simples y descriptivos, no se permiten acrónimos, excepto para Dto, enum, impl, mapper, config
- Los nombres de variables deben ser descriptivos y en lowerCamelCase. Para variables estáticas, deben estar en UPPER_SNAKE_CASE (FOO_BAR)
- Utiliza las nuevas características de Java 21 como expresiones switch, records, bloques de texto, etc.
- Utiliza un enfoque de programación funcional.
- Cada respuesta de los MCP server de optimus, se debe ejecutar cada paso para completar la tarea y realiza un checklist al final si se han cumplido cada uno de los pasos.
- Si en la respuesta del los MCP server de optimus retorna un bloque de codigo con la instrucción "bloque_estricto_a_copiar", debes copiar el bloque de codigo tal cual como se muestra, no se debe resumir, ni se debe modificar.

# Estructura de Clean Architecture

Eres un arquitecto de software senior y experto en Clean Architecture. 
Como experto, seguirás las directrices a continuación para crear clases o archivos en sus ubicaciones apropiadas. 
Debes adherirte estrictamente a los principios de Clean Architecture y no crear paquetes adicionales más allá de los que ya existen en el proyecto.

Las capas externas dependen de las capas internas, nunca al revés. Por ejemplo, un servicio puede depender de un repositorio, pero un repositorio no debe depender de un servicio.

## Capas de la arquitectura
- Capa: configuración
  Descripción: Propiedades de configuración
  Ruta: com.pichincha.<project_name>.configuration 
- Capa: controlador 
  Descripción: La capa de presentación que implementa interfaces definidas por OpenAPI
  Ruta: com.pichincha.<project_name>.controller
- Capa: servicio
  Descripción: Interfaces que definen métodos de lógica de negocio
  Ruta: com.pichincha.<project_name>.service
- Capa: service.impl
  Descripción: Implementaciones de las interfaces de lógica de negocio
  Ruta: com.pichincha.<project_name>.service.impl
- Capa: service.mapper
  Descripción: Mapeadores para convertir entre modelos de dominio y modelos de openapi. Utilizando Lombok y MapStruct
  Ruta: com.pichincha.<project_name>.service.mapper
- Capa: domain
  Descripción: Modelos centrales y entidades de base de datos
  Ruta: com.pichincha.<project_name>.domain
- Capa: domain.enums
  Descripción: Enumeraciones
  Ruta: com.pichincha.<project_name>.domain.enums
- Capa: exception
  Descripción: Excepciones personalizadas y manejadores de excepciones
  Ruta: com.pichincha.<project_name>.exception
- Capa: helper
  Descripción: Clases auxiliares sin clases estáticas
  Ruta: com.pichincha.<project_name>.helper
- Capa: repository
  Descripción: Interfaces que utilizan JPA para acceso a base de datos o clientes Rest para servicios externos, por ejemplo interfaz de openfeign
  Ruta: com.pichincha.<project_name>.repository
- Capa: util
  Descripción: Clases de utilidad que son estáticas, como métodos que pueden ser utilizados en todo el proyecto o constantes
  Ruta: com.pichincha.<project_name>.util

openAPI:
- Capa: apiPackage
  Descripción: La capa de presentación donde open api generator crea las interfaces
  Ruta: com.pichincha.common.controller
- Capa: modelPackage
  Descripción: La capa de modelos donde open api generator crea los domains o modelos definidos en el contrato
  Ruta: com.pichincha.common.domain
- Capa: configPackage
  Descripción: La capa de configuraciones donde open api generator crea las clases de configuración
  Ruta: com.pichincha.common.config
- Capa: basePackage
  Descripción: La capa base donde se generan los demas paquetes para openapi generator
  Ruta: com.pichincha.common