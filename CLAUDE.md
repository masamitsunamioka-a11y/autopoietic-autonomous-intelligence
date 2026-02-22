# Autopoietic Autonomous Intelligence (AAI)

## Build & Test Commands

- Build: `mvn clean install`
- Test: `mvn test`
- Line count check: `./lines.sh` — counts non-blank, non-comment lines; excludes `cli/`, `effectors/`, and test sources.
  The **< 100 lines** rule applies to this count only.

## Module Dependency Graph

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

`*` = already listed above
Each module's role:

- **specification**: Minimal domain interfaces (Neuron, Receptor, Effector, Context, Inference, …)
- **effectors**: Concrete `Effector` implementations (e.g., Google Cloud search); may contain **runtime-generated files
  ** —
  `*Effector.java` can be produced by Plasticity via `EffectorCompiler` at runtime (these appear as untracked in
  git)
- **runtime**: Core engines (Cortex, Thalamus, Plasticity) + Repository SPI; CDI annotations are
  `provided`
- **anticorruption**: ACL — adapters, translators, proxy providers, Gemini client, EffectorCompiler; CDI annotations are
  `provided`
- **proxy**: ULCDI — custom CDI container (PureJavaProxyContainer), ClassScanner, TypeLiteral
- **cli**: Entry point; wires everything via ProxyContainer at startup

## Filesystem & Configuration

Runtime data is stored under `filesystem/` and configured via `cli/src/main/resources/anticorruption.yaml`:

```
filesystem/
  prompts/    — Markdown prompt templates (inference, routing, upgrade, consolidation, guardrails)
  neurons/    — Neuron definitions (JSON)
  receptors/  — Receptor definitions (JSON, may reference effectors)
```

Configuration keys (`anticorruption.yaml`):

- `anticorruption.prompts.source` → `filesystem/prompts`
- `anticorruption.neurons.source` → `filesystem/neurons`
- `anticorruption.receptors.source` → `filesystem/receptors`
- `anticorruption.effectors.*` → source/target paths and classpath strategy for EffectorCompiler

## Autopoietic Design Principles

This system embodies Maturana & Varela's autopoiesis theory at two distinct levels:
| Autopoietic Concept | Realization in this system |
|---|---|
| **Self-production** (structural level) | `EffectorCompiler` compiles new `Effector` classes at runtime — the system
produces new executable capabilities |
| **Self-production** (organizational level) | `Plasticity.potentiate()` generates new Neurons and Receptors;
`consolidate()` merges and removes redundant ones |
| **Organizational closure** | The ULCDI container wires all components internally without external framework
intervention |
| **Structure determines behavior** | The state of `filesystem/neurons` and `filesystem/receptors` fully determines
inference behavior |
| **Boundary with environment** | The `anticorruption` layer (ACL) acts as the selective boundary between the domain and
external systems (Gemini, FileSystem) |
The consolidation of Neurons and Receptors is analogous to **apoptosis** in biology — the system can autonomously reduce
its
own complexity, not merely grow it. This two-level self-production (structural + organizational) is what makes the
autopoietic claim architecturally genuine.

## Code Style & Rules

- **Purity**: Always use `this.` for field/method access.
- **Compactness**: Keep classes highly cohesive (aim for < 100 lines).
- **Architecture**: Strictly follow the ULCDI and ACL patterns.
- **Naming**: Use `e` for exception variables.
- **Language**: Source code and commits must be in English.
- **Constraint**: Do not change method signatures without permission.
- **var**: Use `var` for all non-primitive local variable declarations where the type is inferable.
- **Injection**: Constructor injection only (`@Inject` on constructor); no field injection.
- **Internal data**: Use `private static record` for module-internal data structures.
- **Packages**: Place implementations in `impl` subpackage; experimental/WIP code in `experimental` subpackage.
- **Comments**: Use `/// ` (triple-slash, Java 23+) for inline documentation comments.

## Tech Stack

- Java 25 (Preview features: Pattern Matching for switch, triple-slash comments)
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
