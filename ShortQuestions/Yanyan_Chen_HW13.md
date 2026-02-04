
## 2. What is Aspect-Oriented Programming (AOP)? What does “aspect” mean? Explain its use cases.

Aspect-Oriented Programming (AOP) is a programming paradigm that improves modularity by separating cross-cutting concerns from core business logic. Cross-cutting concerns are functionalities that affect multiple parts of an application, such as logging, security, transaction management, and performance monitoring.

An aspect represents a modular unit of a cross-cutting concern. Instead of duplicating the same logic across different classes, AOP allows developers to define the logic once and apply it declaratively to multiple execution points.

Common use cases of AOP include logging, authentication and authorization, transaction management, exception handling, auditing, and performance monitoring.

---

## 3. What are the advantages and disadvantages of Spring AOP?

Spring AOP improves code maintainability by cleanly separating cross-cutting concerns from business logic. It reduces code duplication, increases readability, and integrates well with the Spring ecosystem. Configuration can be done easily using annotations, making it lightweight and developer-friendly.

However, Spring AOP has limitations because it is proxy-based. It only supports method-level join points and cannot intercept field access or object construction. Internal method calls within the same class are not intercepted, which may lead to unexpected behavior. Debugging can also become more complex due to implicit runtime behavior.

---

## 4. Compare Spring AOP vs Java Reflection vs Spring Interceptor.

Spring AOP is designed to handle cross-cutting concerns in a declarative and modular way. It works through proxies and is mainly used for logging, security, and transaction management at the method level.

Java Reflection allows runtime inspection and manipulation of classes, methods, and fields. While powerful, it is low-level, verbose, and harder to maintain. It is not suitable for managing cross-cutting concerns in a clean and structured manner.

Spring Interceptors operate at the web layer and intercept HTTP requests and responses. They are useful for request-based concerns such as authentication or logging but cannot be applied to service or repository layers.

---

## 5. Explain the following concepts in your own words.

### Aspect

An aspect is a class that encapsulates cross-cutting concerns. It contains advice methods and pointcut definitions that determine where and when the logic is applied.

### PointCut

A pointcut defines a set of join points where advice should be executed. It uses expressions to match method executions, annotations, or package structures.

### JoinPoint

A join point represents a specific point during program execution, such as a method execution. In Spring AOP, join points are always method executions.

### Advice

Advice is the action taken by an aspect at a join point. It defines when the aspect logic runs, such as before, after, or around a method execution.

---

## 6. How do we declare a pointcut? Can we declare it without annotating an empty method? Name some expressions.

A pointcut can be declared using the `@Pointcut` annotation with an empty method for reuse.

It can also be declared directly inside advice annotations without defining an empty method.

Common pointcut expressions include `execution(...)`, `within(...)`, `args(...)`, `@annotation(...)`, and `bean(...)`.

---

## 7. Compare different types of advices in Spring AOP.

Before advice executes before the target method and is commonly used for logging or validation.

After advice executes after the method regardless of its outcome and is useful for cleanup operations.

After Returning advice runs only when the method completes successfully and can access the return value.

After Throwing advice executes when the method throws an exception and is often used for error logging.

Around advice surrounds the method execution and provides full control over the execution flow. It is commonly used for performance monitoring and transaction management.

---

## 8 – Spring AOP Customized Logger

### 8.1 Implement a customized logger using Spring AOP (internal & external code)

**Explanation**

I implemented a customized logger using Spring AOP.  
The aspect intercepts:
- Internal application code (Controller and Service layers)
- External library calls (e.g. `RestTemplate.getForObject()`)

This allows the logger to record both my own code execution and external HTTP calls.

**Screenshot**

- Run `GET /hello`
- the **console output** showing:
  - `[API][REQ]`
  - `[SRV][BEFORE]`
  - `[EXT][HTTP][REQ]` and `[EXT][HTTP][RES]`

<img width="2872" height="1506" alt="image" src="https://github.com/user-attachments/assets/f19b2b5a-19cb-4821-90a8-afddad9a867c" />

<img width="2872" height="540" alt="image" src="https://github.com/user-attachments/assets/ffba70b9-3ec0-491c-b6b7-0c57e89551fd" />

---

### 8.2 Log method execution time, REST request details, and response details

**Explanation**

The AOP logger records execution time using `@Around` advice.  
It logs:
- REST request details (HTTP method and path)
- Response completion
- Execution time (`costMs`)
- Exception details when errors occur

**Screenshot**

- Run `GET /hello`
- the console logs showing:
  - `costMs` for service or API methods  

<img width="2872" height="540" alt="image" src="https://github.com/user-attachments/assets/3886ff60-d6bb-4cb7-8a99-edb2380dd6c8" />

---

### 8.3 Log all possible join points

**Explanation**

The logger covers all major AOP join points, including:
- Before method execution
- After method execution
- After returning
- After throwing exceptions
- Around (for timing)

This ensures full lifecycle logging for both normal and exceptional flows.

**Screenshot**

- Run `GET /boom`
- console logs showing:
  - `[BEFORE]`
  - `[THROW]`
  - `[AFTER]`
  - Timing-related logs
 
<img width="2872" height="1506" alt="image" src="https://github.com/user-attachments/assets/a36c899a-3209-4f4c-9714-d8b6010ffb28" />

<img width="2842" height="1192" alt="image" src="https://github.com/user-attachments/assets/daaa34b8-198a-4048-8827-db2c668c7d8c" />


---

### 8.4 Bind join points directly in AOP code

**Explanation**

Join points are bound directly inside advice annotations using explicit pointcut expressions.  
No empty or separate `@Pointcut` methods are used.

**Screenshot**

- the `AopLoggerAspect` source code
- Show advice annotations such as:
  - `@Before(...)`
  - `@Around(...)`
  - `@AfterThrowing(...)`

<img width="2046" height="816" alt="image" src="https://github.com/user-attachments/assets/b8904093-bf75-42a2-9a68-b0f91007c099" />

