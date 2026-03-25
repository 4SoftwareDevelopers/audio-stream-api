# AGENTS.md - Audio Stream API

## Descripción del Proyecto

- **Lenguaje**: Java 21
- **Framework**: Spring Boot 4.0.0
- **Build Tool**: Maven (con Maven wrapper `./mvnw`)
- **Base de Datos**: PostgreSQL con Flyway migrations
- **Arquitectura**: Arquitectura Hexagonal (Domain-Driven Design with ports/adapters)

## Comandos de Build, Lint y Test

### Build
```bash
./mvnw clean package          # Compilar la aplicación
./mvnw spring-boot:run         # Ejecutar la aplicación
```

### Testing
```bash
./mvnw test                   # Ejecutar todos los tests
./mvnw test -Dtest=ClassName              # Ejecutar clase de test específica
./mvnw test -Dtest=ClassName#methodName   # Ejecutar método de test específico
./mvnw test -Dtest="ClassName,ClassName2" # Ejecutar múltiples clases de test
```

### Otros Comandos
```bash
./mvnw compile                # Compilar sin ejecutar tests
./mvnw dependency:tree      # Mostrar árbol de dependencias
```

## Guías de Estilo de Código

### Capas de Arquitectura

```
src/main/java/com/forsoftwaredevelopers/audio_stream_api/
├── application/
│   ├── command/      # Command DTOs (Java records)
│   ├── service/      # Implementaciones de servicios
│   └── usecase/     # Interfaces de casos de uso
├── domain/
│   ├── model/        # Entidades de dominio
│   ├── port/         # Interfaces de puertos (repositorio, almacenamiento)
│   └── result/       # Tipo Result<T> para manejo de errores
└── infrastructure/
    ├── audio/        # Implementaciones de almacenamiento de audio
    ├── config/       # Propiedades de configuración
    ├── persistense/  # Entidades JPA, repositorios, mappers
    └── web/          # Controladores REST
```

### Convenciones de Nombres

- **Use Cases**: `XxxUseCase` (interfaz)
- **Commands**: `XxxCommand` (Java records para DTOs inmutables)
- **Services**: `XxxService` (implementación)
- **Controllers**: `XxxController` o `XxxMessageController`
- **JPA Entities**: `XxxJPAEntity`
- **Ports**: `XxxPort` (interfaz)
- **Repository Adapters**: `XxxRepositoryAdapter`
- **Mappers**: `XxxJpaMapper`

### Manejo de Errores

- Usar la interfaz sealed `Result<T>` para manejo explícito de errores
- Crear `DomainError` con código de error y tipo de error (VALIDATION, CONFLICT, NOT_FOUND)
- Siempre retornar `Result<T>` desde métodos de use case
- Usar `result.isFail()`, `result.getOrThrow()`, `result.propagate()`

Example:
```java
public Result<Void> play(PlayVoiceMessageCommand command) {
    VoiceMessage voiceMessage = voiceMessageRepository.findById(command.voiceMessageId());
    Result<Void> result = voiceMessage.markAsPlayed();
    if (result.isFail()) {
        return result.propagate();
    }
    voiceMessageRepository.save(voiceMessage);
    return Result.ok(null);
}
```

### Imports

- Organizar imports: java.*, org.springframework.*, paquetes domain, paquetes application, paquetes infrastructure
- Usar nombres completamente calificados cuando sea inequívoco para reducir clutter

### Formatting

- Usar 4 espacios para indentación (no tabs)
- Llave de apertura en la misma línea (`{`)
- Longitud máxima de línea: 120 caracteres (guía suave)
- Usar `var` para inferencia de tipo de variable local cuando el tipo sea obvio
- Usar Java records para DTOs/commands inmutables

### Types

- Usar `String` para IDs (generados via `UUID.randomUUID().toString()`)
- Usar `java.time.Instant` para timestamps
- Usar `java.util.List` para colecciones
- Usar interfaces sealed para tipos union (ej., `Result<T>`)

### Mejores Prácticas

1. **Dependency Injection**: Usar inyección por constructor (no inyección por campo)
2. **Inmutabilidad**: Usar Java records para commands y DTOs
3. **Validación**: Realizar validación en modelos de dominio usando métodos estáticos factory
4. **Database Migrations**: Usar Flyway para migraciones de schema (agregar archivos SQL en `src/main/resources/db/migration/`)
5. **Configuración de Propiedades**: Usar `@ConfigurationProperties` para configuración tipada

### Testing

Capas de testing (3 niveles):

1. **Pruebas Unitarias** (`domain/`): Testean entidades y lógica de dominio sin dependencias externas. Usar solo JUnit y Mockito.
2. **Pruebas de Integración** (`application/` y `infrastructure/`): Testean servicios y repositorios con base de datos en memoria (H2). Usar `@SpringBootTest` con perfil test.
3. **Pruebas E2E** (`integration/`): Testean la API completa simulando llamadas HTTP reales.

Las clases de test deben estar en `src/test/java/` reflejando la estructura de paquetes del main

### Uso de MapStruct

- Ubicar mappers en `infraestructure.persistense.mapper`
- Usar anotación `@Mapper(componentModel = "spring")`
- MapStruct genera implementación en tiempo de compilación

### Patrones Comunes

**Creando Domain Objects**:
```java
public static Result<VoiceMessage> create(String streamId, String username, String email) {
    var validationResult = validate(streamId, username, email);
    if (validationResult.isFail()) {
        return validationResult.propagate();
    }
    return Result.ok(new VoiceMessage(...));
}
```

**Private Constructors with Factory Methods**:
```java
private VoiceMessage(...) {
    // initialization
}

public static Result<VoiceMessage> create(...) { ... }
public static VoiceMessage restore(...) { ... }  # para reconstrucción JPA
```

### Información sobre decisiones de arquitectura
00-architecture-decisions.md