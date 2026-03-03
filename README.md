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

| Package       | Kandel          | Concepts                                |
|---------------|-----------------|-----------------------------------------|
| `neural`      | Ch.1, 26-27     | Engravable, Area, Neuron, Effector      |
| `signaling`   | Ch.7, 20-21, 23 | Stimulus, Impulse, Transducer, Thalamus |
| `synaptic`    | Ch.8-12         | Encoder, Nucleus                        |
| `cognitive`   | Ch.21-25        | Cortex, Percept                         |
| `homeostatic` | Ch.48, 62       | Drive, Salience                         |
| `learning`    | Ch.63-64        | Plasticity                              |
| `working`     | Ch.65-67        | Memory, Episode, Knowledge, Trace       |

Where implementation diverges from neuroscience, we mark it with `[Engineering]`.

## Architecture

```
specification        Domain interfaces — Kandel-grounded, zero dependencies
runtime              Core engines — Cortex, Thalamus, Plasticity, Drive
anticorruption       ACL — Gemini, filesystem, all external systems
services             Effector implementations (may be runtime-generated)
cli / api / web      Driving adapters — CLI, HTTP+SSE, Vue 3 SPA
```

CDI container: **Weld SE 6** (CDI 4.1 reference implementation).

## Autonomy Levels

| Level  | Name                           | Status               |
|--------|--------------------------------|----------------------|
| **L1** | Operational Closure            | Done                 |
| **L2** | Structural Self-Production     | ~85%                 |
| **L3** | Organizational Self-Production | Intentional boundary |
| **L4** | Structural Coupling Maturity   | Theoretical          |

**L2 note**: ~95% achievable within L3 boundary — prompt/configuration self-modification
(Plasticity meta-learning, Drive self-reconfiguration) does not require code execution.
Only runtime Mode plugin registration requires crossing L3.

**L3 boundary**: `Effector.fire()` authored by humans — system identifies *what*, humans decide *how*.

## Documentation

- [Signal flows](docs/architecture.md) — CEN, DMN, SN
- [Kandel interface reference](docs/kandel.md) — specification packages & interface details
- [Module structure](docs/modules.md) — Maven modules, filesystem, configuration
- [Agentforce comparison](docs/agentforce.md)

## References

1. Maturana & Varela (1980). *Autopoiesis and Cognition*. D. Reidel.
2. Maturana & Varela (1987). *The Tree of Knowledge*. Shambhala.
3. Kandel et al. (2021). *Principles of Neural Science*, 6th ed. McGraw Hill.
4. Evans (2003). *Domain-Driven Design*. Addison-Wesley.

## Disclaimer

Experimental research project. Contributions and feedback welcome.
