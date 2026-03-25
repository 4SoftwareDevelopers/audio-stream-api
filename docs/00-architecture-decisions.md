# Decisiones de Arquitectura

## Decisiones a Nivel de Proyecto

1. **Proveedor Cloud**: Digital Ocean - priorizando presupuesto y requisitos de votación en stream.
2. **Base de Datos**: PostgreSQL como motor de base de datos por defecto, cumple con todos los requisitos del proyecto.
3. **Patrón de Arquitectura**: Arquitectura Hexagonal (Ports and Adapters), sin Modulith.
4. **Patrón de Aplicación**: Monolito headless aplicando el patrón BFF (Backend for Frontend).
5. **Comunicación API**: REST para comunicación Backend-Frontend.
6. **CI/CD**: Pipelines de CI/CD para cada proyecto.
7. **Monitoreo**: Actuator, Prometheus (opcional).

## Decisiones de Arquitectura Hexagonal

### 1. ¿Por qué Arquitectura Hexagonal?

Elegimos Arquitectura Hexagonal (Ports and Adapters) sobre otras alternativas como layered architecture o Clean Architecture tradicional porque:

- **Testabilidad**: El dominio no tiene dependencias externas, lo que permite probar la lógica de negocio sin base de datos ni frameworks.
- **Flexibilidad de adapters**: Podemos cambiar la implementación de persistencia, API externa, o almacenamiento de audio sin tocar el dominio.
- **Claridad en dependencias**: Las dependencias siempre van hacia el dominio, nunca al revés.
- **No Modulith**: Separamos explícitamente los módulos del dominio en lugar de depender de la modularidad de Spring.

### 2. Estructura de Capas

Estructura de paquetes que refleja la arquitectura hexagonal:

| Capa | Paquete | Propósito |
|------|---------|-----------|
| Domain | `domain/model/` | Entidades de negocio puras |
| Domain | `domain/port/` | Interfaces para adapters externos |
| Domain | `domain/result/` | Tipo Result para manejo de errores |
| Application | `application/command/` | DTOs inmutables para operaciones |
| Application | `application/usecase/` | Interfaces de casos de uso |
| Application | `application/service/` | Implementaciones que orquestan el dominio |
| Infrastructure | `infrastructure/web/` | Controladores REST |
| Infrastructure | `infrastructure/persistence/` | Entidades JPA y repositorios |
| Infrastructure | `infrastructure/audio/` | Implementaciones de almacenamiento de audio |
| Infrastructure | `infrastructure/config/` | Configuración con `@ConfigurationProperties` |

### 3. Patrón Result Type

**Decisión**: Usar un sealed interface `Result<T>` para manejar éxito y fracaso explícitamente.

**Justificación**:
- Elimina excepciones runtime para errores de negocio (validación, no encontrado, conflicto).
- Fuerza al desarrollador a manejar errores en tiempo de compilación.
- Evita el patrón de "throw exception" que oculta el flujo de control.

### 4. Domain Objects: Factory Methods

**Decisión**: Entidades con constructores privados y métodos estáticos `create()` y `restore()`.

**Justificación**:
- `create()` valida el negocio y retorna `Result<Entity>`, garantizando objetos válidos.
- `restore()` reconstruye entidades desde persistencia (JPA) sin validación de negocio.
- Separa claramente la creación nueva de la reconstrucción.

### 5. Command DTOs como Records

**Decisión**: Usar Java records para todos los command DTOs.

**Justificación**:
- Inmutabilidad por defecto.
- Reduce boilerplate (equals, hashCode, toString automáticos).
- Claridad semántica: un record es un "valor", no un objeto con estado mutable.

### 6. Solo Inyección por Constructor

**Decisión**: Inyección de dependencias exclusivamente por constructor.

**Justificación**:
- Dependencias explícitas y visibles.
- Facilita testing: mock de dependencias en el constructor.
- Evita campos nulos o parcialmente inicializados.

### 7. Flyway para Migraciones de Base de Datos

**Decisión**: Usar Flyway SQL migrations en lugar de JPA auto-ddl o herramientas como Liquibase.

**Justificación**:
- Control total sobre el schema: SQL explícito, sin "magia" de JPA.
- Versionado claro y reversible.
- El equipo puede entender el schema sin dependencia de herramienta.
- Integración nativa con Spring Boot.