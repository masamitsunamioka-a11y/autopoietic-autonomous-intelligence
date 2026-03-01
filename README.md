# Autopoietic Autonomous Intelligence (AAI)

**A self-producing cognitive system whose architecture is grounded in Kandel's neuroscience
and Maturana & Varela's autopoiesis — not as metaphor, but as structural reality.**

## Vision

AAI exists to help people. If this system can contribute to healthcare, accessibility, or any service
where someone says *"AAI helped me — thank you"*, that is the goal. The technology is the means,
not the end.

## What Is Autopoiesis?

A system is **autopoietic** if it continuously produces and maintains its own components
through its own operations (Maturana & Varela, 1980). AAI implements this literally:

1. **Perceive** — Route input to specialized Neurons within the appropriate cortical Area
2. **Potentiate** — Detect capability gaps; self-generate new Areas, Neurons, and Effectors
3. **Prune** — Consolidate redundant structures (biological apoptosis analog)
4. **Drive** — Fire autonomously without user input (Default Mode Network analog)

The system identifies *what capabilities it needs* and generates them at runtime.

## Neuroscience-Grounded Domain Model

Every package in the specification module maps to Kandel's *Principles of Neural Science*:

| Package | Kandel Reference | Domain Concepts |
|---------|-----------------|-----------------|
| `neural` | Ch.1, 26-27 — Cortical structure | Engravable, Area, Neuron, Effector |
| `signaling` | Ch.8, 24, 27 — Sensory pathways | Stimulus, Impulse, Transducer, Thalamus |
| `synaptic` | Ch.8-12 — Synaptic transmission | Encoder, Nucleus |
| `cognitive` | Ch.21-25 — Cortical response | Cortex, Percept |
| `homeostatic` | Ch.47-48 — Arousal & motivation | Drive, Salience |
| `learning` | Ch.63-64 — Synaptic plasticity | Plasticity |
| `working` | Ch.65-67 — Memory systems | Memory, Episode, Knowledge, Trace |

This is not cosmetic naming. Each interface's semantics, method signatures, and inheritance
relationships have been verified against the corresponding Kandel chapters.

### The `[Engineering]` Convention

Where the implementation necessarily diverges from neuroscience, we mark it explicitly:

```java
/// [Engineering] Explicit timestamp for chronological retrieval;
/// biology infers temporality from decay/context.
Instant timestamp();
```

This convention ensures intellectual honesty: the domain model declares where it faithfully
represents Kandel and where it introduces engineering constructs that have no biological equivalent.

### Engravable — A Case Study in Domain Discovery

`Engravable` is the base type for Area, Neuron, and Effector. The name emerged through
Evans-style Knowledge Crunching:

- **Engram** (Kandel Ch.65-67) = a memory trace left by experience
- Area and Neuron are **not** engrams — they are neural structures
- But they **are** structures capable of being *engraved* by experience through synaptic plasticity (Ch.63)
- Therefore: `Area extends Engravable` — a cortical area is engravable, not an engram

This single naming decision resolved an IS-A category error and unified three separate
base types (Engram + Organ) into one concept that is both Kandel-accurate and architecturally clean.

## Architecture

```
specification        Domain interfaces — Kandel-grounded, zero dependencies
runtime              Core engines — Cortex, Thalamus, Plasticity, Drive
anticorruption       ACL — Gemini, filesystem, all external systems
services             Effector implementations (may be runtime-generated)
proxy (ULCDI)        Custom CDI container — organizational closure
cli / api / web      Driving adapters — CLI, HTTP+SSE, Vue 3 SPA
```

### Why a Custom CDI Container?

Organizational closure is the defining property of autopoiesis. The system must wire
its own components through its own operations. Standard CDI containers (Weld, etc.)
cannot `reconcile()` — dynamically re-wire beans while the system is running.
ULCDI exists because autopoiesis demands it.

### Design Principles

- **DDD (Evans)**: Specification is the domain model; runtime and ACL serve it, never the reverse
- **Constructor injection only**: ULCDI wires at construction time; field injection breaks closure
- **Anticorruption Layer**: The only module that touches Gemini, filesystem, or HTTP
- **< 100 lines per class**: Cohesion is the design signal
- **Pure Java 25**: No external agent frameworks — the entire point is self-production

## Autonomy Level Framework

Grounded in autopoiesis theory, we define four levels by asking
*"what kind of entity is the system?"* — not *"what can it do?"*:

| Level | Name | Status |
|-------|------|--------|
| **L1** | Operational Closure | ✅ Complete |
| **L2** | Structural Self-Production | 🟡 ~80% |
| **L3** | Organizational Self-Production | 🔴 Intentional boundary |
| **L4** | Structural Coupling Maturity | ⬜ Theoretical |

### L2 Highlights

- **Plasticity** autonomously generates Areas, Neurons, and Effectors at runtime
- **EffectorCompiler** dynamically compiles Java classes via `javax.tools` without restart
- **Drive** fires proactively on schedule, independent of user input
- **Guardian-type Neurons** emerged spontaneously during unattended overnight operation

### Intentional Boundary at L3

`Effector.fire()` implementations are authored by humans. The system identifies
*what capabilities it needs*; humans decide *how those capabilities interact with the world*.
This is the safety boundary between L2 and L3.

## Theoretical Foundations

| Source | Contribution |
|--------|-------------|
| Maturana & Varela (1980) [1] | Autopoiesis — self-production as the definition of life |
| Maturana & Varela (1987) [2] | Structural coupling — system-environment co-evolution |
| Kandel et al. (2021) [3] | Neuroscience ground truth for all domain modeling |
| Evans (2003) [4] | Domain-Driven Design — Knowledge Crunching as design method |

## References

[1] Maturana, H.R. & Varela, F.J. (1980). *Autopoiesis and Cognition: The Realization of the Living*.
D. Reidel Publishing.

[2] Maturana, H.R. & Varela, F.J. (1987). *The Tree of Knowledge: The Biological Roots of Human Understanding*.
Shambhala.

[3] Kandel, E.R. et al. (2021). *Principles of Neural Science*, 6th Edition. McGraw Hill.

[4] Evans, E. (2003). *Domain-Driven Design: Tackling Complexity in the Heart of Software*. Addison-Wesley.

## Disclaimer

This is an experimental research project. Contributions and feedback are welcome.
