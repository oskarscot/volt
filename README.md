# Volt

![Maven Central Version](https://img.shields.io/maven-central/v/io.github.oskarscot/volt-core) 

A lightweight Java ORM.

> ⚠️ **Warning:** This project is heavily in development. APIs may change without notice.

## Installation

### Gradle (Kotlin DSL)
```kotlin
dependencies {
    implementation("io.github.oskarscot:volt-core:0.0.3")
}
```

### Gradle (Groovy)
```groovy
dependencies {
    implementation 'io.github.oskarscot:volt-core:0.0.3'
}
```

### Maven
```xml
<dependency>
    <groupId>io.github.oskarscot</groupId>
    <artifactId>volt-core</artifactId>
    <version>0.0.3</version>
</dependency>
```

## Quick Start

### Define an Entity
```java
@Entity("users")
public class User {

    @Identifier(type = PrimaryKeyType.NUMBER, generated = true)
    private Long id;

    @NamedField(name = "email_address")
    private String email;

    private String name;
    private boolean active;

    public User() {}

    // getters and setters
}
```

### Initialize Volt
```java
HikariConfig config = new HikariConfig();
config.setJdbcUrl("jdbc:postgresql://localhost:5432/mydb");
config.setUsername("user");
config.setPassword("password");

Volt volt = VoltFactory.createVolt(config);
volt.registerEntity(User.class);
```

### Basic Operations
```java
// Create
User user = new User();
user.setName("John");
user.setEmail("john@example.com");
user.setActive(true);

Result<User, VoltError> result = volt.save(user);

if (result.isSuccess()) {
    System.out.println("Saved with ID: " + result.getValue().getId());
}

// Find by ID
Result<User, VoltError> found = volt.findById(User.class, 1L);
```

### Transactions
```java
try (Transaction tx = volt.beginTransaction()) {
    User user1 = new User("Alice", "alice@example.com");
    User user2 = new User("Bob", "bob@example.com");

    tx.save(user1);
    tx.save(user2);

    tx.commit();
}
```

### Query Builder
```java
try (Transaction tx = volt.beginTransaction()) {
    Query query = Query.where("active").eq(true)
                       .and("name").like("J%");

    Result<List<User>, VoltError> result = tx.findAllBy(User.class, query);

    if (result.isSuccess()) {
        result.getValue().forEach(u -> System.out.println(u.getName()));
    }

    tx.commit();
}
```

## Features

- [x] Entity mapping with annotations
- [x] CRUD operations
- [x] Transaction support
- [x] Fluent query builder
- [x] Type converters
- [x] Connection pooling (HikariCP)
- [ ] Relationships (OneToMany, ManyToOne)
- [ ] Migrations
- [ ] Caching
- [ ] More database support

## Requirements

- Java 21+
- PostgreSQL (other databases not yet supported)

## Contributing

All contributions are greatly appreciated! This project is in early development and there's plenty of work to do.

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'feat: add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.
