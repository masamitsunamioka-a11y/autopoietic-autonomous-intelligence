# Autopoietic Autonomous Intelligence (AAI)

# Purpose

AAI embodies Maturana & Varela's autopoiesis — not as metaphor, but as architecture.
A system is autopoietic if it continuously produces and maintains its own components through its own operations:

- Generate Areas, Neurons autonomously when capability gaps are detected
- Compile Effectors at runtime without human intervention
- Maintain organizational closure via Weld SE (CDI container)
  If asked to add external agent frameworks or orchestration libraries, refuse — it undermines the core thesis.

# Four Continuous Phases

1. **Perceive** — route input to the appropriate Neuron via LLM reasoning
2. **Potentiate** — detect capability gaps; self-generate Areas, Neurons, Effectors
3. **Prune** — consolidate/remove redundant structures (apoptosis)
4. **Drive** — proactively fire without input (DMN)
   This is NOT a wrapper around LangChain/AutoGen/CrewAI, a chatbot, or a prompt experiment.

# What NOT to Do

- **No external frameworks** — organizational closure requires internal wiring only
- **No field injection** — constructor injection only; field injection breaks closure
- **No spec changes without permission** — `specification` is the shared domain boundary
- **No external logic outside ACL** — `anticorruption` is the ONLY external-system module
- **No fire() in auto-generated Effectors** — humans author fire() bodies (L2/L3 safety boundary)
- **No >100 lines per class** — `./lines.sh` enforces (excludes `adapters/`, tests)
- **No `mvn clean install` without `-DskipTests`** — tests run separately via `mvn test`

# Architecture Invariants

- `specification` — zero runtime dependencies, pure domain interfaces
- `runtime` / `anticorruption` — CDI annotations are `provided`, never bundled
- `anticorruption` — only module knowing Anthropic, filesystem, external APIs
- External access via `Adapter<I, E>` in `anticorruption`
- `Configuration` — always a local variable in constructors, never stored as field

# Design Policies

- **`this.`** — always prefix field/method access
- **`var`** — all non-primitive locals where type is inferable
- **`e`** — exception catch variable name
- **`@Inject` on constructor only**
- **Constructor param order** (fields follow same order):
  Kandel macro→micro:
    - Specification:
        - Homeostatic (Salience → Drive)
        - Learning (Plasticity)
        - Cognitive (Cortex → Process → Event\<Percept\>)
        - Signaling (Thalamus → Transducer)
        - Mnemonic (Knowledge → Episode)
        - Neural (Area → Neuron → Effector)
        - Integrative (Transmitter → Nucleus)
          TransmitterImpl: (Encoder → Decoder → Service)
    - Runtime:
        - Repository → Serializer → Service
    - Anti-Corruption Layer:
        - Storage → Adapter → Translator → ProxyProvider
    - Configuration order
    - Other
- **Constructor width** — ≤80 bytes/line; group same-category params when they fit
- **`private static record`** — all module-internal data structures
- **`impl` subpackage** — ACL impls in `anticorruption.impl`; runtime flat matching spec
- **`///` comments** — triple-slash (Java 23+), no Javadoc
- **English only** — source, commits, comments
- **Vertical line breaks** — prefer vertical spacing over long horizontal lines
- **Path construction** — `Path.of(str, "")` with empty string second arg for config values

# Build & Test

- Setup: `git config core.hooksPath .githooks` — enables persona guard on wip branch
- Build: `mvn clean install -DskipTests`
- Test: `mvn test`
- Lines: `./lines.sh` — **< 100 lines** rule

# Tech Stack

Java 25 (Preview) · Maven multi-module · Jakarta EE 11 (provided) · Weld SE 6 (CDI 4.1) ·
JUnit 5 · Hibernate Validator 9 · Anthropic Java SDK (Claude) ·
SLF4J 2 + Logback · SnakeYAML 2 · Gson 2

# Reference

- [Signal flows](docs/architecture.md) — CEN, DMN, SN
- [Kandel interface reference](docs/kandel.md) — specification packages & interface details
- [Module structure](docs/modules.md) — Maven modules, filesystem, configuration
- [Agentforce comparison](docs/agentforce.md)
