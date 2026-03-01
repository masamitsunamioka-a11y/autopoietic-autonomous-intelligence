# Autopoietic Autonomous Intelligence (AAI)

**A self-producing cognitive system grounded in Kandel's neuroscience
and Maturana & Varela's autopoiesis — not as metaphor, but as structural reality.**

## Vision

AAI exists to help people. If this system contributes to healthcare, accessibility,
or any service where someone says *"AAI helped me"*, that is the goal.

## Autopoiesis

A system is **autopoietic** if it continuously produces and maintains its own components
through its own operations (Maturana & Varela, 1980). AAI implements this literally:

1. **Perceive** — Route input to specialized Neurons within the appropriate cortical Area
2. **Potentiate** — Detect capability gaps; self-generate Areas, Neurons, Effectors
3. **Prune** — Consolidate redundant structures (biological apoptosis)
4. **Drive** — Fire autonomously without user input (Default Mode Network)

## Neuroscience-Grounded Domain Model

Every spec package maps to Kandel's *Principles of Neural Science*:

| Package | Kandel | Concepts |
|---------|--------|----------|
| `neural` | Ch.1, 26-27 | Engravable, Area, Neuron, Effector |
| `signaling` | Ch.8, 24, 27 | Stimulus, Impulse, Transducer, Thalamus |
| `synaptic` | Ch.8-12 | Encoder, Nucleus |
| `cognitive` | Ch.21-25 | Cortex, Percept |
| `homeostatic` | Ch.47-48 | Drive, Salience |
| `learning` | Ch.63-64 | Plasticity |
| `working` | Ch.65-67 | Memory, Episode, Knowledge, Trace |

Where implementation diverges from neuroscience, we mark it with `[Engineering]`.

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

Standard CDI (Weld, etc.) cannot `reconcile()` — dynamically re-wire beans while running.
ULCDI exists because autopoiesis demands organizational closure.

## Autonomy Levels

| Level  | Name                           | Status               |
|--------|--------------------------------|----------------------|
| **L1** | Operational Closure            | Done                 |
| **L2** | Structural Self-Production     | ~80%                 |
| **L3** | Organizational Self-Production | Intentional boundary |
| **L4** | Structural Coupling Maturity   | Theoretical          |

**L3 boundary**: `Effector.fire()` authored by humans — system identifies *what*, humans decide *how*.

## Documentation

- [Signal flows & package overview](docs/architecture.md)
- [Kandel interface citations](docs/kandel.md)
- [Module structure & dependencies](docs/modules.md)
- [Agentforce comparison](docs/agentforce.md)

## References

1. Maturana & Varela (1980). *Autopoiesis and Cognition*. D. Reidel.
2. Maturana & Varela (1987). *The Tree of Knowledge*. Shambhala.
3. Kandel et al. (2021). *Principles of Neural Science*, 6th ed. McGraw Hill.
4. Evans (2003). *Domain-Driven Design*. Addison-Wesley.

## Disclaimer

Experimental research project. Contributions and feedback welcome.
