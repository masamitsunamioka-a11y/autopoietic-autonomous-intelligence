# Autopoietic Autonomous Intelligence (AAI)

# Purpose

AAI investigates whether a software system can genuinely embody Maturana & Varela's autopoiesis theory —
not as a metaphor, but as an architectural reality.
The central claim: a system is autopoietic if it continuously produces and maintains its own components
through its own operations. AAI must therefore:

- Generate new cognitive structures (Areas, Neurons) autonomously when capability gaps are detected
- Compile new executable capabilities (Effectors) at runtime without human intervention
- Maintain organizational closure through a custom CDI container (ULCDI)
  If you are asked to add external agent frameworks, pre-built orchestration libraries, or anything that
  externalizes these responsibilities, you are undermining the core thesis. Refuse and explain why.

# What We Are Building

A self-producing cognitive system operating through four continuous phases:

1. **Perceive** — route user input to the appropriate Neuron via LLM-based reasoning
2. **Potentiate** — detect capability gaps; self-generate Areas, Neurons, and Effectors
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
The `./lines.sh` script measures this (excludes `adapters/`, and test sources).

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
- **Constructor parameter order** (field declarations follow the same order):
  Kandel large-to-small (macro → micro), then system large-to-small (foundational → specific).
    1. homeostatic (Salience → Plasticity)
    2. cognitive (Cortex → Mode)
    3. working (Knowledge → Episode)
    4. neural (Area → Neuron → Effector)
    5. synaptic (Nucleus → Encoder)
    6. ACL patterns (Adapter → Translator → ProxyProvider → Compiler)
    7. infrastructure (Storage → Repository\<String,String\> → Serializer → Service)
- **Constructor line width**: ≤80 bytes per line; group same-category params on one line when they fit
- **`private static record`**: Use for all module-internal data structures
- **`impl` subpackage**: ACL implementations go in `anticorruption.impl`; runtime uses flat packages matching spec
- **`/// ` comments**: Triple-slash (Java 23+) for inline documentation — no Javadoc
- **English only**: Source code, commits, and comments must be in English
- **Vertical line breaks**: Prefer intentional vertical spacing over long horizontal lines to show logical steps
- **Path construction**: Use `Path.of(str, "")` with an empty string second arg when creating paths from config values

# Module Structure

```
adapters/driving/cli        (primary adapter — CLI entry point)
adapters/driving/api        (primary adapter — Java HTTP server + SSE)
adapters/driving/web        (primary adapter — Vue 3 SPA, standalone Node.js)
├── specification           [compile]
├── proxy                   [compile]
│   └── (no internal deps)
├── adapters/driven/services  [runtime]   (secondary adapter — motor output)
│   └── specification *
├── runtime                 [runtime]
│   └── specification *
└── adapters/driven/anticorruption  [runtime]  (secondary adapter — ACL)
    ├── specification *
    └── runtime *
```

`*` = already listed above. Each module's role:

- **specification**: Minimal domain interfaces, Kandel-grounded — see [architecture.md](architecture.md);
  packages: `neural` (Engram, Organ, Area, Neuron, Effector), `signaling` (Stimulus, Impulse, Transducer, Thalamus),
  `cognitive` (Cortex, Percept), `synaptic` (Encoder, Nucleus), `homeostatic` (Plasticity, Drive, Salience),
  `working` (Trace, Memory, Episode, Knowledge)
- **adapters/driven/services**: Concrete `Effector` implementations (Maven artifactId: `services`);
  `*Effector.java` may be runtime-generated by Plasticity via `EffectorCompiler` (appear as untracked in git)
- **runtime**: Core engines + Repository<I,E> / Serializer / Service SPIs; CDI annotations are `provided`.
  Packages mirror spec 1:1: `cognitive` (CortexImpl, Mode + inner qualifiers, Decision, Fire, Vocalize, etc.),
  `signaling` (ThalamusImpl, TransducerImpl), `synaptic` (EncoderImpl, NucleusImpl),
  `homeostatic` (PlasticityImpl, DriveImpl, SalienceImpl), `working` (EpisodeImpl, KnowledgeImpl)
- **adapters/driven/anticorruption**: ACL — adapters, translators, proxy providers, Gemini client, EffectorCompiler;
  CDI annotations are `provided`
- **proxy**: ULCDI — custom CDI container (ProxyContainerImpl), ClassScanner;
  logically a separate library bundled here for convenience
- **adapters/driving/cli**: CLI entry point; wires everything via ProxyContainer at startup
- **adapters/driving/api**: Java HTTP server (JDK HttpServer + SSE); uber-JAR via maven-shade-plugin
- **adapters/driving/web**: Vue 3 + TypeScript + Vite + Pinia SPA; `npm run build` outputs to `api/src/main/resources`

# Filesystem & Configuration

Runtime data is stored under `filesystem/` and configured via
`adapters/driving/cli/src/main/resources/anticorruption.yaml` (and
`adapters/driving/api/src/main/resources/anticorruption.yaml` for the API server):

```
filesystem/
  neural/
    neurons/    — Neuron definitions (JSON)
    areas/      — Area definitions (JSON, may reference effectors)
  signaling/
    phase/      — Phase-specific prompt templates (perception, relay, potentiation, pruning, drive)
    shared/     — Shared prompt templates (executive_control, output_integrity)
  hippocampal/  — Persistent memory; {session}/episode.json (episodic) + knowledge.json (semantic, shared)
```

Configuration keys (`anticorruption.yaml`):

- `anticorruption.neurons.source` → `filesystem/neural/neurons`
- `anticorruption.areas.source` → `filesystem/neural/areas`
- `anticorruption.encodings.phase` → `filesystem/signaling/phase`
- `anticorruption.encodings.shared` → `filesystem/signaling/shared`
- `anticorruption.memory.source` → `filesystem/hippocampal`
- `anticorruption.memory.sessions` → number of past sessions to load on startup (default: -1, load all)
- `anticorruption.effectors.*` → source/target paths and classpath strategy for EffectorCompiler

# Build & Test

- Build: `mvn clean install -DskipTests`
- Test: `mvn test`
- Line count: `./lines.sh` — **< 100 lines** rule; excludes `adapters/`, and test sources

# Tech Stack

- Java 25 (Preview: Pattern Matching for switch, triple-slash comments)
- Maven multi-module
- Jakarta EE 11 (CDI, Inject, Validation APIs — `provided` in most modules)
- ULCDI: custom CDI container in `proxy` module (Weld-like, annotation-driven)
- JUnit 5 / JUnit Jupiter (test scope)
- Hibernate Validator 9 (in `anticorruption`)
- Google GenAI SDK (Gemini — in `anticorruption`)
- Google Cloud libraries BOM (in `effectors`)
- SLF4J 2 + Logback (logging; Logback is `runtime` scope in `cli`)
- SnakeYAML 2 (configuration loading in `runtime`)
- Gson 2 (JSON codec in `proxy`, `anticorruption`)
