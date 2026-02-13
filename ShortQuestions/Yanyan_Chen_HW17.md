# Testing Related

## 1. Unit Testing

**Definition:**  
Unit testing verifies the smallest testable unit of code (usually a method or class) in isolation.

**Scope:**  
Single method or class.

**Tools:**  
JUnit, Mockito.

**Example:**  
Testing `CommentServiceImpl.createComment()` by mocking `CommentRepository` to ensure the logic works without connecting to a real database.

**Key Characteristics:**
- Fast
- Isolated
- Developer-level testing

---

## 2. Functional Testing

**Definition:**  
Functional testing verifies that the system behaves according to business requirements.

**Scope:**  
Feature-level testing.

**Example:**  
Testing whether a user can create a comment through a REST API and receive the correct response.

**Difference from Unit Testing:**
- Unit testing checks internal logic.
- Functional testing checks system behavior against requirements.

---

## 3. Integration Testing

**Definition:**  
Integration testing verifies interactions between different modules or services.

**Scope:**  
Multiple components working together.

**Example:**  
Testing CommentService + Repository + Database together to ensure the comment is correctly saved.

**Difference from Unit Testing:**
- Unit testing mocks dependencies.
- Integration testing uses real dependencies.

---

## 4. Regression Testing

**Definition:**  
Regression testing ensures that new changes do not break existing functionality.

**Example:**  
After modifying the Comment entity, re-run all tests to ensure comment creation still works.

**Purpose:**  
Prevent side effects after code changes.

---

## 5. Smoke Testing

**Definition:**  
Smoke testing verifies whether the main functions of the system work.

**Example:**  
Check if the application starts successfully and main API endpoints return 200 status.

**Scope:**  
Basic system validation before deeper testing.

---

## 6. Performance Testing

**Definition:**  
Performance testing evaluates system speed, response time, and throughput under expected load.

**Example:**  
Testing how many comment requests per second the API can handle under normal traffic.

---

## 7. Stress Testing

**Definition:**  
Stress testing evaluates system behavior under extreme conditions.

**Example:**  
Sending 10,000 concurrent comment requests to determine when the system crashes.

**Difference from Performance Testing:**

| Performance Testing | Stress Testing |
|--------------------|----------------|
| Normal expected load | Extreme load |
| Measure efficiency | Find breaking point |

---

## 8. A/B Testing

**Definition:**  
A/B testing compares two versions of a feature to determine which performs better.

**Example:**  
Version A sorts comments by time.  
Version B sorts comments by likes.  
Compare user engagement and retention.

---

## 9. End-to-End Testing (E2E)

**Definition:**  
End-to-End testing validates the complete user workflow from start to finish.

**Example:**  
User logs in → creates comment → comment stored in database → comment displayed on UI.

**Scope:**  
Entire system.

---

## 10. User Acceptance Testing (UAT)

**Definition:**  
Testing performed by real users or business stakeholders to verify the system meets business needs.

**Example:**  
Product owner verifies that the comment module satisfies business requirements before release.

---

# Environment Related

## 1. Development Environment

- Used by developers
- Frequent code changes
- Local machine or development server
- Debugging enabled

---

## 2. QA (Quality Assurance) Environment

- Used by QA engineers
- Stable build version
- Used for feature testing and bug verification

---

## 3. Pre-production / Staging Environment

- Almost identical to production
- Used for final validation
- Often contains production-like data

---

## 4. Production Environment

- Live environment for real users
- Real data
- High availability and stability required
