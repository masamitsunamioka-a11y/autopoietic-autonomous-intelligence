# Theory

- [Vision](#vision)
- [Hybrid Model](#hybrid-model)
- [Autopoiesis](#autopoiesis)
- [Four Continuous Phases](#four-continuous-phases)
- [Autonomy Levels](#autonomy-levels)
- [References](#references)
- [Disclaimer](#disclaimer)

## Vision

AAI exists to help people. If this system contributes to healthcare, accessibility,
or any service where someone says *"AAI helped me"*, that is the goal.

## Hybrid Model

AAI is grounded in two theoretical foundations:

- **Kandel** - Behavior. All runtime signal processing follows Kandel's
  *Principles of Neural Science* (5th ed., 2013). Interfaces, method names,
  and signal flows map to specific chapters.
- **Maturana & Varela** - Growth. Component self-production follows autopoiesis theory.
  The system continuously generates and maintains its own structures (Areas, Neurons, Effectors)
  through its own operations.

Behavior is neuroscience. Growth is autopoiesis. Neither is metaphor - both are structural.

## Autopoiesis

A system is **autopoietic** if it continuously produces and maintains its own components
through its own operations (Maturana & Varela, 1980). AAI implements this literally:

- Generate Areas, Neurons autonomously when capability gaps are detected
- Compile Effectors at runtime without human intervention
- Maintain organizational closure via Weld SE (CDI container)

This is NOT a wrapper around LangChain/AutoGen/CrewAI, a chatbot, or a prompt experiment.

## Four Continuous Phases

| Phase          | Grounding | Description                                                     |
|----------------|-----------|-----------------------------------------------------------------|
| **Perceive**   | Kandel    | Route input to specialized Neurons within the appropriate Area  |
| **Compensate** | M&V       | Detect capability gaps; self-generate Areas, Neurons, Effectors |
| **Conserve**   | M&V       | Consolidate redundant structures; eliminate unused components   |
| **Default**    | Kandel    | Fire autonomously without user input (Default Mode Network)     |

## Autonomy Levels

| Level  | Name                           | Status               |
|--------|--------------------------------|----------------------|
| **L1** | Operational Closure            | Done                 |
| **L2** | Structural Self-Production     | ~85%                 |
| **L3** | Organizational Self-Production | Intentional boundary |
| **L4** | Structural Coupling Maturity   | Theoretical          |

**L2 note**: ~95% achievable within L3 boundary - prompt/configuration self-modification
(Autopoiesis meta-learning, Default self-reconfiguration) does not require code execution.
Only runtime Mode plugin registration requires crossing L3.

**L3 boundary**: `Effector.fire()` authored by humans - system identifies *what*, humans decide *how*.

## References

1. Maturana & Varela (1980). *Autopoiesis and Cognition*. D. Reidel.
2. Maturana & Varela (1987). *The Tree of Knowledge*. Shambhala.
3. Kandel et al. (2013). *Principles of Neural Science*, 5th ed. McGraw Hill.
4. Evans (2003). *Domain-Driven Design*. Addison-Wesley.

## Disclaimer

Experimental research project. Contributions and feedback welcome.
