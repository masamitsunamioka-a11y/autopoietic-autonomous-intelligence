# Autopoietic Autonomous Intelligence (AAI)

## Build & Test Commands

- Build: `mvn clean install`
- Test: `mvn test`
- Line count check: `./lines.sh` — counts non-blank, non-comment lines; excludes `cli/`, `actions/`, and test sources.
  The **< 100 lines** rule applies to this count only.

## Module Dependency Graph

```
cli
├── specification       [compile]
├── proxy               [compile]
│   └── (no internal deps)
├── actions             [runtime]
│   └── specification *
├── runtime             [runtime]
│   └── specification *
└── anticorruption      [runtime]
    ├── specification *
    └── runtime *
```

`*` = already listed above
Each module's role:

- **specification**: Minimal domain interfaces (Agent, Topic, Action, Context, Inference, …)
- **actions**: Concrete `Action` implementations (e.g., Google Cloud search); may contain **runtime-generated files** —
  `*Action.java` can be produced by the Evolution Engine via `ActionCompiler` at runtime (these appear as untracked in
  git)
- **runtime**: Core engines (InferenceEngine, RoutingEngine, EvolutionEngine) + Repository SPI; CDI annotations are
  `provided`
- **anticorruption**: ACL — adapters, translators, proxy providers, Gemini client, ActionCompiler; CDI annotations are
  `provided`
- **proxy**: ULCDI — custom CDI container (PureJavaProxyContainer), ClassScanner, TypeLiteral
- **cli**: Entry point; wires everything via ProxyContainer at startup

## Filesystem & Configuration

Runtime data is stored under `filesystem/` and configured via `cli/src/main/resources/anticorruption.yaml`:

```
filesystem/
  prompts/    — Markdown prompt templates (inference, routing, upgrade, consolidation, guardrails)
  agents/     — Agent definitions (JSON)
  topics/     — Topic definitions (JSON, may reference actions)
```

Configuration keys (`anticorruption.yaml`):

- `anticorruption.prompts.source` → `filesystem/prompts`
- `anticorruption.agents.source` → `filesystem/agents`
- `anticorruption.topics.source` → `filesystem/topics`
- `anticorruption.actions.*` → source/target paths and classpath strategy for ActionCompiler

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
- Google Cloud libraries BOM (in `actions`)
- SLF4J 2 + Logback (logging; Logback is `runtime` scope in `cli`)
- SnakeYAML 2 (configuration loading in `runtime`)
- Gson 2 (JSON codec in `proxy`, `anticorruption`)
