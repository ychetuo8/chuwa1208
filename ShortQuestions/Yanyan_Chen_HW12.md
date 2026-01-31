
## 2. Explain TLS, PKI, Certificate, Public Key, Private Key, and Signature

**TLS (Transport Layer Security)**  
TLS is a cryptographic protocol that ensures secure communication over a network by providing encryption, integrity, and authentication.

**PKI (Public Key Infrastructure)**  
PKI is a system that manages digital certificates and public/private keys to establish trust between parties.

**Certificate**  
A digital certificate binds a public key to an identity. It is issued and signed by a Certificate Authority (CA).

**Public Key**  
Used to encrypt data or verify a digital signature. It is shared publicly.

**Private Key**  
Kept secret by the owner. Used to decrypt data or create digital signatures.

**Digital Signature**  
A signature created using a private key to prove data integrity and authenticity. Verified using the corresponding public key.

---
## 3. HTTPS API with Self-Signed Certificate


### 3.1 Pack self-signed certificate as JKS file in the application

### Description

A Spring Boot application was created using **Spring Web** and **Spring Security**.  
A simple GET API `/api/ping` was implemented, which returns an empty response (`204 No Content`) and is used only to verify HTTPS connectivity.

To enable HTTPS, a **self-signed certificate** was generated using Java `keytool` and packaged as a **JKS (Java KeyStore)** file.  
The keystore is included **inside the application resources**, making it part of the application itself.

The certificate has the following properties:

- Keystore type: `JKS`
- Alias: `local-https`
- Common Name (CN): `localhost`
- Key algorithm: `RSA`
- Key size: `2048`
- Validity: 365 days

The keystore file is stored at: src/main/resources/keystore/local-https.jks

Spring Boot is configured to use this keystore to serve HTTPS traffic on port **8443**.

### Evidence (Screenshots Required)

**Screenshot 3.1.1**  
- Project structure showing `src/main/resources/keystore/local-https.jks`
<img width="668" height="456" alt="image" src="https://github.com/user-attachments/assets/a398caa3-a133-440c-a66e-fc720d1554f7" />

**Screenshot 3.1.2**  
- Terminal output of the `keytool -genkeypair` command showing successful keystore generation
<img width="1152" height="654" alt="image" src="https://github.com/user-attachments/assets/43d343ee-c594-4693-a0fd-9aafa227cf32" />

**Screenshot 3.1.3**  
- `application.properties`:

<img width="2074" height="930" alt="image" src="https://github.com/user-attachments/assets/1daee75d-429b-4d04-bd8a-cb0c44a840ec" />

---

## 3.2 Verify HTTPS API without importing the self-signed certificate

### Description

After starting the application, the HTTPS API was tested **without importing the self-signed certificate into the local trust store**.

When sending a request using `curl`:

```bash
curl -i https://localhost:8443/api/ping
```

The request failed with the following error(**Screenshot 3.2.1**):
<img width="1140" height="738" alt="image" src="https://github.com/user-attachments/assets/5ee64ade-92f1-4bea-8541-2d6a4bca83c1" />

## 3.3 Make HTTPS call work without bypassing TLS/SSL verification
### Description

To make the HTTPS call work without bypassing TLS/SSL verification, the server certificate was explicitly trusted by the client.

### Step 1: Export the server certificate

The self-signed certificate was exported from the JKS keystore:

```bash
keytool -exportcert \
  -alias local-https \
  -keystore src/main/resources/keystore/local-https.jks \
  -storepass changeit \
  -rfc \
  -file local-https.crt
```

This produced a PEM-format certificate file: local-https.crt.

### Step 2: Verify HTTPS using curl (with certificate)

The HTTPS API was successfully verified using:

```bash
curl -i --cacert local-https.crt https://localhost:8443/api/ping
```

The request succeeded with:

```bash
HTTP/1.1 204 No Content
```

This confirms that TLS verification succeeded because the certificate was trusted, not because verification was disabled.

### Step 3: Configure Postman to trust the certificate

In Postman:

- The global “Disable SSL verification” option was NOT used
- The exported certificate (local-https.crt) was added under:

  <img width="1410" height="994" alt="image" src="https://github.com/user-attachments/assets/05ca048d-1d3e-4d7c-9b3f-e882f0be93b8" />

After importing the certificate, Postman was able to successfully call:
```bash
GET https://localhost:8443/api/ping
```
without any TLS errors.

<img width="2516" height="1514" alt="image" src="https://github.com/user-attachments/assets/fcacc9f8-cab1-4203-b8d7-ca67c9a625a6" />


### Explanation

TLS verification works because:

- The client explicitly trusts the self-signed certificate

- The certificate chain can now be validated

- SSL verification remains enabled at all times

This approach is secure and correctly demonstrates how HTTPS works with self-signed certificates.

### Evidence (Screenshots Required)

**Screenshot 3.3.1**

- Terminal output showing keytool -exportcert success

<img width="1140" height="442" alt="image" src="https://github.com/user-attachments/assets/bfe9ae8b-51a0-42b8-a7e2-48d260261686" />

**Screenshot 3.3.2**

- Terminal output showing successful curl --cacert HTTPS call

<img width="1140" height="324" alt="image" src="https://github.com/user-attachments/assets/cc23641a-726b-46db-a2f2-f4303bf65a16" />


**Screenshot 3.3.3**

- Postman Certificates settings page showing local-https.crt added under CA Certificates

<img width="1410" height="994" alt="image" src="https://github.com/user-attachments/assets/9bb035c1-7cc6-422c-b98a-8f81c87388c7" />

**Screenshot 3.3.4**

- Postman request to https://localhost:8443/api/ping returning 204 No Content

<img width="2516" height="1514" alt="image" src="https://github.com/user-attachments/assets/1471d508-8d57-4b7c-b661-6e9a1c62862f" />


This task demonstrates:

- How to enable HTTPS in a Spring Security application

- How to package a self-signed certificate as a JKS file

- Why HTTPS verification fails without trusting the certificate

- How to correctly make HTTPS calls work without bypassing TLS/SSL verification

All TLS verification was performed correctly and securely.

---

## 4. HTTP Status Codes Related to Authentication and Authorization

- `401 Unauthorized` – Authentication required or failed
- `403 Forbidden` – Authenticated but not authorized
- `400 Bad Request` – Invalid authentication data
- `302 Found` – Redirect to login page (session-based auth)

---

## 5. Compare Authentication and Authorization

**Authentication** answers: *Who are you?*  
**Authorization** answers: *What are you allowed to do?*

### Spring Security Components
- `UserDetailsService` – Loads user data
- `AuthenticationProvider` – Performs authentication logic
- `AuthenticationManager` – Coordinates authentication
- `SecurityContext` – Stores authenticated user info
- `AccessDecisionManager` – Handles authorization decisions

---

## 6. Explain HTTP Session

An HTTP session is a server-side mechanism used to store user state across multiple requests. Each session is identified by a session ID.

---

## 7. Explain Cookie

A cookie is a small piece of data stored on the client side by the browser and sent with each HTTP request to the server.

---

## 8. Compare Session and Cookie

| Aspect | Session | Cookie |
|------|--------|--------|
| Storage | Server-side | Client-side |
| Security | More secure | Less secure |
| Size | Larger | Limited |
| Lifetime | Server controlled | Client controlled |

---

## 9. Google Single Sign-On (SSO)

This question asks for at least two websites that support login using a Google account
and to identify SSO-related REST calls using Chrome Developer Tools.

### Website 1: Medium

Medium allows users to log in using their Google account.

When I clicked **“Continue with Google”** on Medium, the browser was redirected to Google
for authentication.

In Chrome Developer Tools (Network tab), I observed an SSO-related request with the following characteristics:

- **Request URL:** `https://accounts.google.com/o/oauth2/auth`
- **Request Method:** GET
- **Status Code:** 302 (Redirect)
- **Type:** document
- **State Parameter:** `state=google`

This indicates that Medium uses Google Single Sign-On based on OAuth 2.0.
After authentication, Google redirects the user back to Medium via a callback endpoint.

<img width="1458" height="765" alt="image" src="https://github.com/user-attachments/assets/57bd0ef3-a684-44d6-9cee-43bfaaa593d7" />

---

### Website 2: Stack Overflow

Stack Overflow also supports login using a Google account.

After clicking **“Log in with Google”**, Chrome Developer Tools shows a redirect
to Google’s OAuth authorization endpoint.

The observed SSO-related REST call includes:

- **Request URL:** `https://accounts.google.com/o/oauth2/auth`
- **Request Method:** GET
- **Status Code:** 302 (Redirect)
- **Type:** document
- **Redirect URI:** Stack Overflow callback via `stackauth.com`

This confirms that Stack Overflow uses Google SSO implemented with OAuth 2.0.

<img width="1458" height="765" alt="image" src="https://github.com/user-attachments/assets/d42615c4-2954-4a10-b07e-33848eb3be37" />


Both Medium and Stack Overflow allow users to log in using their Google account.
Chrome Developer Tools clearly show SSO-related REST calls involving
redirection to `accounts.google.com`, which confirms the use of Google Single Sign-On.


---

## 10. Using Session and Cookie to Maintain User State

- Cookie stores session ID
- Server uses session ID to retrieve user data
- Enables user state persistence across requests

---

## 11. Spring Security Filter

Spring Security uses a **filter chain** to intercept HTTP requests and apply security logic such as authentication and authorization before reaching controllers.

---

## 12. Bearer Token and JWT

**Bearer Token**  
A token sent in the `Authorization` header to access protected resources.

**JWT (JSON Web Token)**  
A stateless token containing user claims, signed to prevent tampering.

---

## 13. Secure Storage of Sensitive Information

- Passwords: hashed using BCrypt or Argon2
- Credit cards: tokenization or encryption
- Never store plaintext sensitive data

---

## 14. Compare Security Components

- `UserDetailsService` – Loads user info
- `AuthenticationProvider` – Validates credentials
- `AuthenticationManager` – Manages providers
- `AuthenticationFilter` – Intercepts login requests

---

## 15. Disadvantages of Session and Solutions

**Disadvantages**
- Server memory usage
- Not scalable in distributed systems

**Solutions**
- Use JWT
- Centralized session store (Redis)

---

## 16. Get Values from application.properties

Use `@Value` or `@ConfigurationProperties` to inject values from configuration files.

---

## 17. Role of configure Methods

- `configure(HttpSecurity http)` – Defines authorization rules
- `configure(AuthenticationManagerBuilder auth)` – Configures authentication sources

---

## 19. Best Practices to Store Secrets

- Use environment variables
- Use secret managers (AWS Secrets Manager, Vault)
- Avoid hardcoding secrets
- Restrict access permissions
