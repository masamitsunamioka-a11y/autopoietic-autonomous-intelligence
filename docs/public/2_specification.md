# Specification

- [Naming Policy](#naming-policy)
    - [Package](#package)
    - [Interface](#interface)
    - [Method](#method)
    - [Provenance](#provenance)
- [Interface Classification - Neuron Anatomy Mapping](#interface-classification---neuron-anatomy-mapping)
- [Specification Packages](#specification-packages)
- [Specification Details](#specification-details)
    - [Root package](#root-package)
    - [`autopoietic` package](#autopoietic-package---maturana--varela)
    - [`cognitive` package](#cognitive-package---part-vii)
    - [`homeostatic` package](#homeostatic-package---ch48-51)
    - [`mnemonic` package](#mnemonic-package---ch65-67)
    - [`networking` package](#networking-package---ch23-46-62-63)
    - [`neural` package](#neural-package---ch1-26-27)
    - [`signaling` package](#signaling-package---ch7-21)
    - [`synaptic` package](#synaptic-package---ch8-12)

All interfaces grounded in Kandel, *Principles of Neural Science* (5th ed., 2013).
Chapter references throughout this document are based on the 5th edition.

Engineering convention (no direct Kandel structural equivalent) is marked with one of:

- `[Engineering]` - in code comments and review
- `[E#]` - numbered reference in [4_review.md](4_review.md)

## Naming Policy

### Package

An **adjective** derived from a Kandel concept that groups related interfaces.
The name represents a functional role - NOT derived from the book's Part/Chapter structure.
Packages reflect how the brain organizes function, not how the textbook organizes explanation.

Examples: `synaptic`, `cognitive`, `neural`, `networking`

### Interface

A **noun** (or noun phrase) that appears in Kandel 5th edition.

| Category       | Suffix       | Examples                        | Meaning                           |
|----------------|--------------|---------------------------------|-----------------------------------|
| Structure      | proper noun  | Cortex, Thalamus, Nucleus, Area | A brain structure itself          |
| Agent          | -er / -or    | Effector                        | An entity that performs X         |
| Process        | -tion / -ity | Compensation, Conservation      | A phenomenon or mechanism         |
| Product/Signal | common noun  | Percept, Trace, Impulse         | An output or carrier              |
| System         | common noun  | Episode, Knowledge              | A memory or functional system     |
| State/Mode     | common noun  | Default, Salience, Drive, Sleep | A network state or operating mode |

When Kandel uses the concept only as a verb (e.g., "effect"),
the agent-noun form (-er/-or) is permitted and marked as engineering convention.

### Method

A **verb** describing the primary action of the interface.
Derived from Kandel's functional descriptions where possible
(integrate, compensate, conserve, relay, orient, transduce, diffuse).
Methods with no direct Kandel equivalent are marked `[Engineering]`.
Accessor methods use the property name as a noun (e.g., `content()`, `signal()`, `tuning()`).

### Provenance

Every package, interface, and method records its Kandel chapter reference in this document.
When no chapter applies, cite the originating paper or mark as `[Engineering]`:

- Raichle (2001) - Default Mode Network
- Seeley (2007) - Salience Network
- Evans (2003) - Entity, AggregateRoot (DDD)

---

## Interface Classification - Neuron Anatomy Mapping

All specification interfaces map to three categories derived from neuron anatomy
(Kandel Ch.2).

| Category                           | Description                                   | Interfaces                                                                                                             |
|------------------------------------|-----------------------------------------------|------------------------------------------------------------------------------------------------------------------------|
| **Neural cell (collective) roles** | Neuron populations with specialized functions | Cortex, Thalamus, Default, Salience, Sleep, Arousal, Autopoiesis, Knowledge, Episode, Receptor, Area, Neuron, Effector |
| **Signals / Data**                 | Information carried between neurons           | Stimulus, Impulse, Trace, Percept, Transmitter, Modulator, Potential                                                   |
| **Synaptic functions**             | Signal integration at synapses                | Synapse, Nucleus                                                                                                       |

---

## Specification Packages

| Package       | Kandel             | Interfaces                                                                                                                                                           |
|---------------|--------------------|----------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| *(root)*      | Evans DDD          | [Entity](#entity-interface), [AggregateRoot](#aggregateroot-interface)                                                                                               |
| `autopoietic` | M&V                | [Autopoiesis](#autopoiesis-interface--mv)                                                                                                                            |
| `cognitive`   | Part VII           | [Cortex](#cortex-interface--part-vii), [Percept](#percept-interface--ch21-25)                                                                                        |
| `homeostatic` | Ch.48, 51          | [Drive](#drive-interface--ch48), [Sleep](#sleep-interface--ch51-65-67), [Arousal](#arousal-interface--ch51), [Modulator](#modulator-interface--ch51)                 |
| `mnemonic`    | Ch.65-67           | [Trace](#trace-interface--ch63-67), [Episode](#episode-interface--ch65-67), [Knowledge](#knowledge-interface--ch65-67)                                               |
| `networking`  | Ch.23, 46, 62, 63  | [Thalamus](#thalamus-interface--ch23-26-46), [Default](#default-interface--ch62), [Salience](#salience-interface--ch63-seeley-2007)                                  |
| `neural`      | Ch.1, 2, 21, 26-27 | [Receptor](#receptor-interface--ch21), [Area](#area-interface--ch1-26-27), [Neuron](#neuron-interface--ch2-26), [Effector](#effector-interface--ch35-36)             |
| `signaling`   | Ch.7, 21           | [Stimulus](#stimulus-interface--ch21), [Impulse](#impulse-interface--ch7)                                                                                            |
| `synaptic`    | Ch.8-12            | [Synapse](#synapse-interface--ch8), [Transmitter](#transmitter-interface--part-iii), [Potential](#potential-interface--ch12), [Nucleus](#nucleus-interface--ch12-46) |

Runtime packages mirror spec 1:1.

---

## Specification Details

### Root package

Evans DDD shared tools. All domain objects with identity extend AggregateRoot.
The root package permits minimal engineering interfaces/classes that have no Kandel equivalent
but are required by the domain model (e.g., Entity, AggregateRoot).

#### `Entity` interface

| Method | Basis                                                                                      |
|--------|--------------------------------------------------------------------------------------------|
| `id()` | Evans DDD identity. Biological neurons have no names - identified by topographic position. |

#### `AggregateRoot` interface

Extends Entity. Marker for Repository-managed domain objects.
Area, Neuron, Effector, Trace are AggregateRoots.

---

### `autopoietic` package - Maturana & Varela

Grounded in autopoiesis theory, not Kandel. Component self-production
follows M&V: compensate perturbations, conserve organizational identity.

#### `Autopoiesis` interface - M&V

| Method                | Basis                                                                                       |
|-----------------------|---------------------------------------------------------------------------------------------|
| `compensate(Impulse)` | M&V perturbation response - detect capability gaps, generate new structures                 |
| `conserve()`          | M&V adaptation preservation - consolidate redundant structures, eliminate unused components |

---

### `cognitive` package - Part VII

#### `Cortex` interface - Part VII

| Method             | Basis                                                      |
|--------------------|------------------------------------------------------------|
| `respond(Impulse)` | Void. Integrate via Nucleus -> Process dispatch -> Percept |

#### `Percept` interface - Ch.21, 25

Four psychophysical properties:

| Method        | Basis                             |
|---------------|-----------------------------------|
| `content()`   | Qualitative content (the 'what')  |
| `location()`  | Spatial location in sensory field |
| `intensity()` | Stevens's power law               |
| `duration()`  | Temporal persistence              |

---

### `homeostatic` package - Ch.48, 51

#### `Drive` interface - Ch.48

Homeostatic drive states (hunger, thirst, and other physiological needs
that motivate behavior). Marker interface.

#### `Sleep` interface - Ch.51, 65-67

Sleep and dreaming. Memory consolidation during sleep (Ch.65-67).
Lifecycle managed by `@PostConstruct`/`@PreDestroy` in runtime.

#### `Arousal` interface - Ch.51

Ascending arousal system (LC, Raphe, TMN, basal forebrain).
Maintains wakefulness; inhibited by adenosine accumulation.
Part of Saper flip-flop switch with Sleep (VLPO).

| Method           | Basis                                                                                  |
|------------------|----------------------------------------------------------------------------------------|
| `project()`      | Ch.51 ascending arousal tonic projection - activates wake-promoting neural populations |
| `isProjecting()` | Ch.51 tonic projection state - other neurons detect its presence/absence               |

#### `Modulator` interface - Ch.51

Neuromodulator (Ch.51). Chemical substance with diffuse, volume-transmission signaling.
Marker interface. Distinct from point-to-point Transmitter (neurotransmitter).

---

### `mnemonic` package - Ch.65-67

Kandel Ch.65 classifies memory into two major systems:

- **Declarative** (Ch.65 Tulving) - Episode, Knowledge. Explicit memory.
- **Nondeclarative** (Ch.66) - Not yet implemented.

#### `Trace` interface - Ch.63-67

Fundamental unit of memory encoding. One `encode()` = one Trace.

| Method      | Basis                                                                           |
|-------------|---------------------------------------------------------------------------------|
| `id()`      | Retrieval cue (Ch.65-67). `id` is the Evans DDD identifier; semantically = cue. |
| `content()` | Encoded content (the stored memory trace itself)                                |

#### `Episode` interface - Ch.65-67

Episodic memory (Tulving): autobiographical, time-stamped, per-session.
`@Episodic` qualifier.

| Method             | Basis                                                                     |
|--------------------|---------------------------------------------------------------------------|
| `encode(Trace)`    | Commit to memory                                                          |
| `retrieve(String)` | Cue-based retrieval                                                       |
| `retrieve()`       | All stored traces                                                         |
| `decay()`          | Forgetting without reinforcement                                          |
| `promote()`        | Hippocampal replay -> episodic->semantic transfer during sleep (Ch.65-67) |

#### `Knowledge` interface - Ch.65-67

Semantic memory (Tulving): general world knowledge, cross-session.
`@Semantic` qualifier.

| Method             | Basis                                                                                                          |
|--------------------|----------------------------------------------------------------------------------------------------------------|
| `encode(Trace)`    | Commit to memory                                                                                               |
| `retrieve(String)` | Cue-based retrieval                                                                                            |
| `retrieve()`       | Associative recall as Map (cue->content). Ch.65: semantic memory is cue-based, unlike episodic (temporal List) |
| `decay()`          | Forgetting without reinforcement                                                                               |

---

### `networking` package - Ch.23, 46, 62, 63

Large-scale brain networks (DMN, SN) that regulate network state transitions.

#### `Thalamus` interface - Ch.23, 26, 46, 51

| Method           | Basis                                                                         |
|------------------|-------------------------------------------------------------------------------|
| `relay(Impulse)` | Void. Tonic mode - routes unrouted Impulse to appropriate Area, fires Cortex. |
| `burst()`        | Void. Burst mode - sleep spindle firing drives conserve, promote, decay.      |

#### `Default` interface - Ch.62

DMN - Default Mode Network (spontaneous internal activity; Raichle 2001).
Marker interface. Lifecycle managed by `@PostConstruct`/`@PreDestroy` in runtime.

#### `Salience` interface - Ch.63, Seeley 2007

Orienting response + Salience Network (anterior insula + ACC). Gates DMN<->CEN switch.

| Method         | Basis                                                                     |
|----------------|---------------------------------------------------------------------------|
| `orient()`     | Orient attention; suppress DMN                                            |
| `inhibiting()` | Ch.63 SN inhibitory output to DMN. DMN checks this to know if suppressed. |

---

### `neural` package - Ch.1, 26-27

Kandel Ch.2 classifies neurons into three functional types: sensory, interneuron,
and motoneuron. The `neural` package contains two classifications:

- **Sensory neuron** (Ch.2) - Receptor. Transduction of external stimuli.
- **Interneuron** (Ch.2) - Area, Neuron. Integrative processing components.
- **Motoneuron** (Ch.2) - Effector. Motor output components.

All three extend AggregateRoot. ACL mirrors this classification in its subpackage structure.

#### `Area` interface - Ch.1, 26-27

Functional cortical area (V1, M1, Broca's). Unit of cortical organization.

| Method        | Basis                                                                    |
|---------------|--------------------------------------------------------------------------|
| `tuning()`    | Ch.26, 30 - area-level selectivity; routing criterion + behavioral layer |
| `neurons()`   | Ch.2, 26 - area composed of individually tuned neurons                   |
| `effectors()` | Ch.35-36 - motor areas project to effectors via corticospinal tract      |

#### `Neuron` interface - Ch.2, 26

Fundamental unit. Hubel & Wiesel: individual V1 neurons have orientation selectivity.

| Method     | Basis                                                                            |
|------------|----------------------------------------------------------------------------------|
| `tuning()` | Ch.26 - neuron-level feature selectivity (orientation, frequency, face identity) |

#### `Effector` interface - Ch.35-36

Motor output structures (muscles, glands). In AAI: compiled action capabilities.

| Method      | Basis                                                                                     |
|-------------|-------------------------------------------------------------------------------------------|
| `tuning()`  | Ch.35-36, 38-40 - motor tuning (Georgopoulos population vector)                           |
| `fire(Map)` | "neurons fire action potentials" - execute action, return result. [E1] KV map models I/O. |

#### `Receptor` interface - Ch.21

Sensory neuron receptor. Transduces external stimulus energy into neural impulse.

| Method                | Basis                                                                    |
|-----------------------|--------------------------------------------------------------------------|
| `transduce(Stimulus)` | Ch.21 sensory transduction - converts stimulus energy into neural signal |

---

### `signaling` package - Ch.7, 21

#### `Stimulus` interface - Ch.21

External sensory stimulus. The physical energy input to sensory receptors.

| Method     | Basis                                                                                 |
|------------|---------------------------------------------------------------------------------------|
| `energy()` | Ch.21 stimulus energy - the physical energy to be transduced by Receptor. String type |

#### `Impulse` interface - Ch.7

| Method       | Basis                                                                                          |
|--------------|------------------------------------------------------------------------------------------------|
| `signal()`   | Content carried by the signal. String type - neural impulse uniformity (Ch.7)                  |
| `afferent()` | Ch.2, 7 - origin of signal (labeled line principle). String identifier of the source pathway.  |
| `efferent()` | Ch.2, 7 - destination of signal propagation. String (Area id); null before relay(), set after. |

---

### `synaptic` package - Ch.8-12

Universal neural computation substrate shared by all processing layers.

#### `Synapse` interface - Ch.8

Synaptic junction (Ch.8). Marker interface.
The conceptual signal flow through a synapse is:

1. **transduce** - presynaptic terminal converts electrical Impulse to chemical Transmitter (Ch.8)
2. **diffuse** - Transmitter crosses synaptic cleft (Ch.8-9)
3. **transduce** - postsynaptic membrane converts chemical Transmitter back to electrical Impulse (Ch.8)

This flow is documented here for Kandel completeness.
In AAI, the pipeline is internal to TransmissionService (anticorruption).

#### `Transmitter` interface - Part III

Neurotransmitter (Part III). Chemical substance released at synaptic terminals.
Marker interface. Point-to-point synaptic signaling.

#### `Potential` interface - Ch.12

Postsynaptic potential - the graded result of somatic integration (Ch.8, 12).
All integration result records implement this marker interface.

#### `Nucleus` interface - Ch.12, 46

Synaptic integration at soma. Neuroanatomical "nucleus" = cluster of somas
performing integration (Ch.12, 46). Name and function both Kandel-compliant.

| Method                      | Basis                                                                                                         |
|-----------------------------|---------------------------------------------------------------------------------------------------------------|
| `integrate(T, Consumer<T>)` | Ch.12 soma integration + axonal firing. [E3] Consumer\<T\> models downstream projection; T extends Potential. |
