EJERCICIO PARA CUBRIR GAPS

Tomar como referencia el caso pr�ctico enviado en el proceso de selecci�n y realizar las siguientes modificaciones y mejoras:

Frontend
- Debe ser creado por medio de Optimus y tener una arquitectura microfrontend.
- Agregar autenticaci�n por medio de tokens.
- Proteger rutas para accesos no autorizados por medio de Guards.
- Manejo de errores centralizado y personalizados.
- Manejos de observables.
- Las interfaces deben ser creadas utilizando los componentes del Design System.
- Manejo de estado centralizado (sesi�n storage, local storage).

Backend
- Agregar autenticaci�n a los microservicios basados en servicios de autenticaci�n y autorizaci�n.
- Implementaci�n correcta de manejo de errores.
- Implementar conceptos de herencia, composici�n, polimorfismo dentro de los servicios.
- Los servicios deben estar construidos con arquitectura Hexagonal y respetando el uso de las diferentes capas.
- Implementar una arquitectura orientada a eventos para su comunicaci�n, utilizar Brokers como Kafka, Rabbit, etc.
- Implementar el patr�n CQRS en su totalidad.
- Implementar el patr�n CircuitBreaker en su totalidad.
- Implementar trazabilidad de Logs mediante: Splunk, ELK, Azure Monitor.
- Realizar un correcto uso de los niveles de logs teniendo en cuenta los diferentes ambientes.
- Debe contar con optimizaci�n de las APIs/EndPoints, implementando: paginaci�n, uso de cach�, compresi�n de respuestas.
- Aseguramiento de las APIs/EndPoints.
- Todos los APIs/EndPoints deben tener creadas las pruebas automatizadas de acuerdo con el est�ndar/arquetipo definido por Banco.


General
- Deben crearse pruebas unitarias para microservicios y micrositios.
- La implementaci�n del ejercicio debe estar alienada a los est�ndares de desarrollo definidos por Banco.
- Dentro del ejercicio se deben crear pipelines los cuales permitan la integraci�n y el despliegue de los microservicios y micrositios.
- Todo el ejercicio debe ser desplegado en Contenedores mediante: minikube, oracle cloud infrasestructure (tiene un plan gratuito para pocs).
- Todo el c�digo debe aplicar las pr�cticas de OWASP.

Defensa
- Al finalizar se deber� realizar la defensa el ejercicio antes ChapterLeaders en un espacio de una hora.
- Se deber� mostrar el ecosistema funcionando.
