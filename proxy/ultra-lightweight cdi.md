# Espresso Architecture Design Specification (Ultimate Edition)

## Executive Summary: The "Espresso" Philosophy

Espresso is an Ultra-Light CDI (ULCDI) implementation designed for high-performance Java applications. It extracts the
essence of components (Beans) under "high pressure" by stripping away legacy complexity. It strictly adheres to the
Single Interface Principle (SIP) to ensure total decoupling and high-speed execution.
---

## 1. Technical Framework Comparison

| Component            | **Espresso (ULCDI)**         | **Weld (Jakarta CDI)**     | **Spring Framework**    |
|:---------------------|:-----------------------------|:---------------------------|:------------------------|
| **Philosophy**       | **"Pure Extraction"**        | **"Strict Separation"**    | **"Universal Embrace"** |
| **Type Requirement** | **Interface Mandatory**      | Flexible (Class/Interface) | Flexible (POJO)         |
| **Proxy Type**       | **JDK Dynamic Proxy**        | Client Proxy (Bytecode)    | CGLIB / JDK Proxy       |
| **Proxy Mechanism**  | **Interface Implementation** | Subclassing                | Dynamic Subclassing     |
| **Identity Key**     | **BeanIdentity (Record)**    | BeanIdentifier             | Bean Name (String)      |
| **Instance**         | **POJO**                     | POJO                       | POJO                    |

---

## 2. Core Interface Mapping (Espresso to Jakarta CDI 4.1)

| ULCDI Interface / Class | Jakarta CDI 4.1 Equivalent                                                                                       | Descriptive Role                                                                                           | Official Javadoc (Jakarta EE 10/11)                                                                            |
|:------------------------|:-----------------------------------------------------------------------------------------------------------------|:-----------------------------------------------------------------------------------------------------------|:---------------------------------------------------------------------------------------------------------------|
| **`ProxyContainer`**    | [`BeanManager`](https://jakarta.ee/specifications/cdi/4.1/apidocs/jakarta/enterprise/inject/spi/beanmanager)     | **The Command Center.** Integrates all beans and contexts. Primary entry point for instance resolution.    | [BeanManager](https://jakarta.ee/specifications/cdi/4.1/apidocs/jakarta/enterprise/inject/spi/beanmanager)     |
| **`Context`**           | [`Context`](https://jakarta.ee/specifications/cdi/4.1/apidocs/jakarta/enterprise/context/spi/context)            | **The Lifecycle Boundary.** Manages the scope and lifespan of bean instances (e.g., `@ApplicationScoped`). | [Context](https://jakarta.ee/specifications/cdi/4.1/apidocs/jakarta/enterprise/context/spi/context)            |
| **`Contextual`**        | [`Contextual`](https://jakarta.ee/specifications/cdi/4.1/apidocs/jakarta/enterprise/context/spi/contextual)      | **The Creation Recipe.** Defines the logic for creating and destroying bean instances.                     | [Contextual](https://jakarta.ee/specifications/cdi/4.1/apidocs/jakarta/enterprise/context/spi/contextual)      |
| **`ClassWrapper`**      | [`AnnotatedType`](https://jakarta.ee/specifications/cdi/4.1/apidocs/jakarta/enterprise/inject/spi/annotatedtype) | **Structural Metadata.** Reflection wrapper that analyzes annotations, constructors, and bean identities.  | [AnnotatedType](https://jakarta.ee/specifications/cdi/4.1/apidocs/jakarta/enterprise/inject/spi/annotatedtype) |
| **`ClassScanner`**      | `Deployment Phase / Discovery`                                                                                   | **The Search Engine.** Scans the classpath during discovery to find candidate classes for registration.    | [Spec: Discovery](https://jakarta.ee/specifications/cdi/4.1/jakarta-cdi-spec-4.1#bean_discovery)               |
| **`ProxyFactory`**      | `Client Proxy Generator`                                                                                         | **The Proxy Smith.** Forges dynamic proxies (`$Proxy`) to enable circular dependency support.              | [Spec: Client Proxies](https://jakarta.ee/specifications/cdi/4.1/jakarta-cdi-spec-4.1#client_proxies)          |
| **`ContextHandler`**    | `Client Proxy (InvocationHandler)`                                                                               | **The Execution Bridge.** Intercepts method calls on proxies and delegates them to contextual instances.   | [Spec: Invocation](https://jakarta.ee/specifications/cdi/4.1/jakarta-cdi-spec-4.1#client_proxy_invocation)     |

---

## 3. Bean Identity Normalization Cases (The "Military Rules")

| Case                         | CDI Specification Interpretation                                    | **Espresso (ULCDI) Key Management**                  |
|:-----------------------------|:--------------------------------------------------------------------|:-----------------------------------------------------|
| **Case A: No `@Named`**      | Treated as a "Default Bean."                                        | `new BeanIdentity(Service.class, "____UNNAMED____")` |
| **Case B: `@Named("foo")`**  | Explicitly identified by name.                                      | `new BeanIdentity(Service.class, "foo")`             |
| **Case C: `@Named` (Empty)** | **The Default Name Rule:** Simple class name with decapitalization. | `new BeanIdentity(Service.class, "serviceImpl")`     |

---

## The Strict Laws of Engineering

The following rules apply to the Espresso development environment to maintain "Crystalline Integrity."

### 1. The Shortest Path of Forging (make.sh)

[cite_start]The build process must be completed via `m -> a -> [TAB]`[cite: 22].

- [cite_start]`build.sh` is replaced by the more fundamental `make.sh`[cite: 22].
- [cite_start]`mvn clean install -DskipTests` ensures the fastest possible binary forging[cite: 22].

### 2. Rigorous Validation Flow (review.sh)

Physical blocking of failed code from contaminating the record of evolution (Git).

- [cite_start]Standardized use of `set -e`, `set -u`, and `set -o pipefail`[cite: 22].
- Immediate termination upon test failure. [cite_start]Only verified evolutions are recorded in
  `diff_staged.log`[cite: 22, 26].

### 3. Immutable Identity (Identity Strategy)

The next phase involves a total migration from **Type + Name** to **Qualifiers (Type-Safe)** to eliminate String-based
identification.
---

## The Etymology: Why "Espresso"?

1. **Java:** The language and the coffee.
2. **Beans:** The minimal unit of a component.
3. **Espresso:** A high-pressure extraction that removes "bitterness" (complexity) to produce the pure essence of logic.

---

## Architect's Closing Note

> "Weld has the 'Tolerance' to wrap any class by force. Espresso has the 'Discipline' to only accept those who sign the
> Interface Contract. This discipline ensures a container that is pure, reflection-based, and devoid of unnecessary
> bloat."
