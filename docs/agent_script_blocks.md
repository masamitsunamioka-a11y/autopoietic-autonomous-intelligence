# AgentScript Blocks Specification

This document faithfully reproduces the structure and sequence of the official Salesforce Agentforce **AgentScript
Blocks** documentation.
---

## Table of Contents

1. **System Block**
2. **Config Block**
3. **Variables Block**
4. **Language Block**
5. **Connection Block**
6. **Topic Blocks**
7. **Start Agent Block**

---

## 1. System Block

```java
public interface System {
    String instructions();

    Messages messages();
}

interface Messages {
    String welcome();

    String error();
}
```

---

## 2. Config Block

```java
public interface Config {
    String developerName();

    String defaultAgentUser();

    String agentLabel();

    String description();
}
```

---

## 3. Variables Block

```java
public interface Variables {
    List<Variable> variables();
}

interface Variable {
    String name();

    boolean mutable();

    String type();

    Object value();
}
```

---

## 4. Language Block

```java
public interface Language {
    String defaultLocale();

    String additionalLocales();

    boolean allAdditionalLocales();
}
```

---

## 5. Connection Block

```java
public interface Connection {
    String name();

    String type();
}
```

---

## 6. Topic Blocks

```java
public interface Topic {
    String name();

    String label();

    String description();

    Reasoning reasoning();
}

interface Reasoning {
    String instructions();

    List<String> actions();
}

public interface Action {
    String name();

    String label();

    String description();

    String target();

    List<ActionParameter> inputs();

    List<ActionParameter> outputs();
}

interface ActionParameter {
    String name();

    String label();

    String type();

    boolean required();
}
```

---

## 7. Start Agent Block

```java
public interface StartAgent {
    System system();

    Config config();

    Variables variables();

    Language language();

    List<Connection> connections();

    List<Topic> topics();

    Reasoning reasoning();
}
```

---

## References

This specification is directly based on the following official Salesforce documentation:

* **AgentScript Blocks**: https://developer.salesforce.com/docs/ai/agentforce/guide/ascript-blocks.html
* **AgentScript Language**: https://developer.salesforce.com/docs/ai/agentforce/guide/ascript-lang.html
