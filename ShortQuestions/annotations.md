# Annotations Learned

---

## Core Spring / Spring Boot

- **`@SpringBootApplication`**  
  Entry point of a Spring Boot application.  
  Combines `@Configuration`, `@EnableAutoConfiguration`, and `@ComponentScan`.

- **`@Component`**  
  Marks a generic Spring-managed bean.  
  Automatically detected during component scanning.

- **`@Service`**  
  Specialized `@Component` for service-layer classes.  
  Typically contains business logic.

- **`@Repository`**  
  Specialized `@Component` for DAO / persistence layer.  
  Enables automatic translation of persistence exceptions into Spring’s `DataAccessException`.

- **`@Autowired`**  
  Enables dependency injection by type.  
  Can be applied to constructors, fields, or setter methods  
  (constructor injection is recommended).

---

## Web / REST

- **`@RestController`**  
  Combines `@Controller` and `@ResponseBody`.  
  Used to build RESTful APIs that return JSON/XML instead of views.

- **`@RequestMapping`**  
  Defines a base URL path (and optional HTTP methods) for a controller or handler method.

- **`@GetMapping`**  
  Handles HTTP GET requests.

- **`@PostMapping`**  
  Handles HTTP POST requests.

- **`@PutMapping`**  
  Handles HTTP PUT requests.

- **`@DeleteMapping`**  
  Handles HTTP DELETE requests.

- **`@PathVariable`**  
  Binds a URI template variable to a method parameter.

- **`@RequestParam`**  
  Binds a query parameter from the request URL.

- **`@RequestBody`**  
  Maps the HTTP request body (usually JSON) to a Java object.

---

## JPA / Hibernate

- **`@Entity`**  
  Marks a class as a JPA entity mapped to a database table.

- **`@Table`**  
  Specifies the table name and optional constraints.

- **`@Id`**  
  Defines the primary key of the entity.

- **`@GeneratedValue`**  
  Specifies how the primary key value is generated.

- **`@Column`**  
  Customizes the mapping between a field and a database column.

- **`@OneToMany`**  
  Defines a one-to-many relationship between entities.

- **`@ManyToOne`**  
  Defines a many-to-one relationship.

- **`@ManyToMany`**  
  Defines a many-to-many relationship.

- **`@JoinColumn`**  
  Specifies the foreign key column in an association.

- **`@JoinTable`**  
  Defines the join table for a many-to-many relationship.

- **`@MappedSuperclass`**  
  Base class whose fields are inherited by entities but not mapped to a table itself.

- **`@Enumerated`**  
  Maps a Java enum to a database column.

- **`@Temporal`**  
  Specifies how date/time fields are stored in the database.

- **`@Transactional`**  
  Manages transaction boundaries (commit / rollback).

- **`@CreationTimestamp`**  
  Automatically sets the entity creation timestamp.

- **`@UpdateTimestamp`**  
  Automatically updates the entity modification timestamp.

---

## Repository / Spring Data JPA

- **`JpaRepository`**  
  Provides CRUD operations, pagination, and sorting.

- **`PagingAndSortingRepository`**  
  Provides pagination and sorting capabilities  
  (base interface of `JpaRepository`).

---

## Custom Queries

- **`@NamedQuery`**  
  Defines a static JPQL query associated with an entity.

- **`@NamedQueries`**  
  Container for multiple `@NamedQuery` definitions.

- **`@Query`**  
  Defines a custom JPQL or native SQL query directly on a repository method.

- **`@Modifying`**  
  Indicates that a query method performs update or delete operations.

---

## Exception Handling

- **`@ExceptionHandler`**  
  Handles specific exceptions and returns custom responses.  
  Applied at the method level.

- **`@ControllerAdvice`**  
  Global exception handler applied across multiple controllers.  
  Declares the class as a Spring-managed bean.

- **`@ResponseStatus`**  
  Maps an exception to a specific HTTP status code.

---

## Validation

- **`@Valid`**  
  Triggers validation on request body or method parameters.  
  Commonly used in controller methods.

- **Common validation annotations**  
  `@NotEmpty`, `@NotBlank`, `@Size`, `@Pattern`, `@Email`

---

## Bean Configuration / IoC

- **`@Configuration`**  
  Marks a class as a Java-based Spring configuration class.

- **`@Bean`**  
  Registers a Spring bean using a method.  
  Commonly used for third-party libraries (e.g. `ModelMapper`).

- **`@Primary`**  
  Specifies the default bean when multiple candidates exist.

- **`@Qualifier`**  
  Explicitly selects which bean to inject when multiple implementations are available.

---

## Dependency Injection

- **`@Autowired`**  
  Performs automatic dependency injection by type.  
  Works with the Spring IoC container to manage object creation and wiring.
