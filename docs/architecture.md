# Signal Flows

⚙️ = engineering convention (no direct Kandel structural equivalent).

## CEN — Central Executive Network (external)

```
Stimulus.input()
  → Transducer.transduce()          Ch.21
  → Impulse(area=null)
  → Thalamus.relay()                Ch.23,26,46
  → Impulse(area!=null)
  → Cortex.respond()                Part VII
      → Encoder.encode()            Ch.21
      → Nucleus.integrate()         Ch.12
  → Percept
  → Effector.fire()                 Ch.35-36
```

## DMN — Default Mode Network (internal)

```
Drive.activate()                    Ch.48,62
  → [DriveImpl generates Impulse internally]
  → Cortex.respond()
  → Percept
```

DMN bypasses Thalamus. Area selection is internal (Ch.62: DMN activates
medial PFC, posterior cingulate spontaneously).

## PROJECT — Corticocortical Projection

```
Cortex.respond() → PROJECT mode
  → AreaRepository.find()           Ch.20 association fibers
  → Impulse(signal, targetArea)
  → Cortex.respond()                recursive, no Thalamus
  → Percept
```

## Package Overview

| Package       | Kandel          | Role                                                     |
|---------------|-----------------|----------------------------------------------------------|
| `neural`      | Ch.1, 26-27     | Cortical structure: Engravable, Area, Neuron, Effector   |
| `signaling`   | Ch.7, 20-21, 23 | Sensory pathway: Stimulus, Impulse, Transducer, Thalamus |
| `synaptic`    | Ch.8-12         | Universal computation: Encoder, Nucleus                  |
| `cognitive`   | Part VII        | Cortical response: Cortex, Percept                       |
| `homeostatic` | Ch.48, 62       | Regulation: Drive, Salience                              |
| `learning`    | Ch.63-64        | Plasticity                                               |
| `working`     | Ch.65-67        | Memory: Trace, Episode, Knowledge                        |

Full interface-level Kandel citations: [kandel.md](kandel.md)
