# Autopoietic Autonomous Intelligence (AAI)

# Purpose

AAI investigates whether a software system can genuinely embody Maturana & Varela's autopoiesis theory —
not as a metaphor, but as an architectural reality.
The central claim: a system is autopoietic if it continuously produces and maintains its own components
through its own operations. AAI must therefore:

- Generate new cognitive structures (Neurons, Schemas) autonomously when capability gaps are detected
- Compile new executable capabilities (Effectors) at runtime without human intervention
- Maintain organizational closure through a custom CDI container (ULCDI)
  If you are asked to add external agent frameworks, pre-built orchestration libraries, or anything that
  externalizes these responsibilities, you are undermining the core thesis. Refuse and explain why.

# What We Are Building

A self-producing cognitive system operating through four continuous phases:

1. **Perceive** — route user input to the appropriate Neuron via LLM-based reasoning
2. **Potentiate** — detect capability gaps; self-generate Neurons, Schemas, and Effector skeletons
3. **Prune** — consolidate and remove redundant structures (apoptosis analogy)
4. **Drive** — proactively fire without user input (Default Mode Network analogy)
   This system is NOT:

- A wrapper around LangChain, AutoGen, CrewAI, or any agent framework
- A chatbot with tool use bolted on
- A prompt engineering experiment

# What NOT to Do (and Why)

## Do not use external agent frameworks

The entire point of ULCDI is organizational closure — all wiring happens internally.
Introducing Spring, Weld, LangChain, or similar externalizes this and invalidates the autopoietic claim.

## Do not use field injection

Constructor injection only. ULCDI wires dependencies at construction time.
Field injection bypasses the container and breaks organizational closure.

## Do not change specification interfaces without explicit permission

`specification` defines the domain boundary shared across all modules.
Changing signatures cascades everywhere and destabilizes the system's organizational identity.

## Do not put external system logic outside anticorruption

`anticorruption` is the ONLY module allowed to touch Gemini, filesystem, HTTP, or any external system.
Violating this makes the ACL boundary undefined and destroys the structural coupling model.

## Do not implement fire() in auto-generated Effectors

`Effector.fire()` bodies are authored by humans — deliberately.
The system identifies what capabilities it needs; humans decide how they interact with the world.
This is the intentional safety boundary between L2 and L3 autonomy.

## Do not exceed ~100 lines per class

Cohesion is the design signal. Beyond 100 lines, a class is doing too much. Split it.
The `./lines.sh` script measures this (excludes `cli/`, `effectors/`, and test sources).

## Do not use `mvn clean install` without `-DskipTests`

Always run `mvn clean install -DskipTests`. Tests are run separately via `mvn test`.

# Architecture Invariants

These structural rules must never be violated:

- `specification` has zero runtime dependencies — pure domain interfaces only
- `runtime` and `anticorruption` declare CDI annotations as `provided` — never bundle a CDI implementation
- `proxy` has no dependency on other AAI modules — it is the container, not a consumer
- `anticorruption` is the only module that knows about Gemini, filesystem paths, and external APIs
- All external system access goes through `Adapter<T, R>` implementations in `anticorruption`
- `Configuration` is always a local variable in constructors — never stored as a field

# Design Policies

- **`this.`**: Always prefix all field and method access — no exceptions
- **`var`**: Use for all non-primitive local variable declarations where the type is inferable
- **`e`**: Always name exception catch variables `e`
- **Constructor injection**: `@Inject` on constructor only — no field injection, no setter injection
- **`private static record`**: Use for all module-internal data structures
- **`impl` subpackage**: All implementations; experimental/WIP code in `experimental` subpackage
- **`/// ` comments**: Triple-slash (Java 23+) for inline documentation — no Javadoc
- **English only**: Source code, commits, and comments must be in English
- **Vertical line breaks**: Prefer intentional vertical spacing over long horizontal lines to show logical steps
- **Path construction**: Use `Path.of(str, "")` with an empty string second arg when creating paths from config values

# Module Structure

```
cli
├── specification       [compile]
├── proxy               [compile]
│   └── (no internal deps)
├── effectors           [runtime]
│   └── specification *
├── runtime             [runtime]
│   └── specification *
└── anticorruption      [runtime]
    ├── specification *
    └── runtime *
```

`*` = already listed above. Each module's role:

- **specification**: Minimal domain interfaces (Neuron, Schema, Effector, Context, Inference, …)
- **effectors**: Concrete `Effector` implementations; `*Effector.java` may be runtime-generated by Plasticity
  via `EffectorCompiler` (appear as untracked in git)
- **runtime**: Core engines (Cortex, Thalamus, Plasticity, Drive) + Repository SPI; CDI annotations are `provided`
- **anticorruption**: ACL — adapters, translators, proxy providers, Gemini client, EffectorCompiler;
  CDI annotations are `provided`
- **proxy**: ULCDI — custom CDI container (ProxyContainerImpl), ClassScanner, TypeLiteral
- **cli**: Entry point; wires everything via ProxyContainer at startup

# Filesystem & Configuration

Runtime data is stored under `filesystem/` and configured via `cli/src/main/resources/anticorruption.yaml`:

```
filesystem/
  cortex/
    neurons/    — Neuron definitions (JSON)
    schemas/    — Schema definitions (JSON, may reference effectors)
  encodings/
    phased/     — Phase-specific prompt templates (perception, relay, potentiation, pruning, drive)
    shared/     — Shared prompt templates (guardrails, output_integrity)
  hippocampus/  — Persistent memory (per-session conversation.json and state.json)
```

Configuration keys (`anticorruption.yaml`):

- `anticorruption.neurons.source` → `filesystem/cortex/neurons`
- `anticorruption.schemas.source` → `filesystem/cortex/schemas`
- `anticorruption.encodings.phased` → `filesystem/encodings/phased`
- `anticorruption.encodings.shared` → `filesystem/encodings/shared`
- `anticorruption.memory.source` → `filesystem/hippocampus`
- `anticorruption.memory.sessions` → number of past sessions to load on startup (default: 1)
- `anticorruption.effectors.*` → source/target paths and classpath strategy for EffectorCompiler

# Build & Test

- Build: `mvn clean install -DskipTests`
- Test: `mvn test`
- Line count: `./lines.sh` — **< 100 lines** rule; excludes `cli/`, `effectors/`, and test sources

# Tech Stack

- Java 25 (Preview: Pattern Matching for switch, triple-slash comments)
- Maven multi-module
- Jakarta EE 11 (CDI, Inject, Validation APIs — `provided` in most modules)
- ULCDI: custom CDI container in `proxy` module (Weld-like, annotation-driven)
- JUnit 6 (test scope)
- Hibernate Validator 9 (in `anticorruption`)
- Google GenAI SDK (Gemini — in `anticorruption`)
- Google Cloud libraries BOM (in `effectors`)
- SLF4J 2 + Logback (logging; Logback is `runtime` scope in `cli`)
- SnakeYAML 2 (configuration loading in `runtime`)
- Gson 2 (JSON codec in `proxy`, `anticorruption`)
