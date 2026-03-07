# Kandel Interface Reference

All interfaces grounded in Kandel, *Principles of Neural Science* (6th ed.).
⚙️ = engineering convention (no direct Kandel structural equivalent).

See also: [kandel-building-blocks.yaml](kandel-building-blocks.yaml) — neuron anatomy, synapse structure,
signal flow mapping, and interface classification with bilingual (en/ja) descriptions.

## Interface Classification — Neuron Anatomy Mapping

All specification interfaces map to three categories derived from neuron anatomy
(Kandel Ch.2;
see [Kyoto University, 2013](https://www.kyoto-u.ac.jp/ja/archive/prev/news_data/h/h1/news6/2013_1/131023_1), Fig.1).

| Category                           | Description                                   | Interfaces                                                                                |
|------------------------------------|-----------------------------------------------|-------------------------------------------------------------------------------------------|
| **Neural cell (collective) roles** | Neuron populations with specialized functions | Cortex, Thalamus, Drive, Salience, Plasticity, Knowledge, Episode, Area, Neuron, Effector |
| **Signals / Data**                 | Information carried between neurons           | Stimulus, Impulse, Trace, Percept                                                         |
| **Synaptic functions**             | Signal encoding/integration at synapses       | Encoder, Transmitter, Decoder, Nucleus                                                    |

- **neural** — Most direct correspondence to Fig.1. Neuron = single nerve cell,
  Area = population sharing the same tuning, Effector = motor output driven by motoneurons (Ch.2).
- **integrative** — Abstracts the synapse regions of Fig.1. Encoder = presynaptic signal encoding (Ch.21),
  Nucleus = ⚙️ integration cluster borrowed from neuroanatomical nuclei (Ch.12, 46).
  Named after the role "synaptic integration" (Ch.12), not the structure "synapse".
- **signaling** — Stimulus/Impulse are signals external to and between neurons.
  Transducer (PNS sensory receptor) and Thalamus (routing) operate before signals enter the neuron.
- **cognitive, homeostatic, learning, mnemonic** — Emergent roles of neural populations.
  Percept and Trace are products/units, not populations themselves.

---

## Specification Packages

| Package       | Kandel          | Interfaces                                                                                                                                                           |
|---------------|-----------------|----------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| *(root)*      | ⚙️ Evans DDD    | [Entity](#entity-interface), [AggregateRoot](#aggregateroot-interface)                                                                                               |
| `neural`      | Ch.1, 26-27     | [Area](#area-interface--ch1-26-27), [Neuron](#neuron-interface--ch2-26), [Effector](#effector-interface--ch35-36)                                                    |
| `signaling`   | Ch.7, 20-21, 23 | [Stimulus](#stimulus-interface--ch21), [Impulse](#impulse-interface--ch7), [Transducer](#transducer-interface--ch21), [Thalamus](#thalamus-interface--ch23-26-46)    |
| `cognitive`   | Part VII        | [Cortex](#cortex-interface--part-vii), [Percept](#percept-interface--ch21-25)                                                                                        |
| `integrative` | Ch.8-12         | [Encoder](#encoder-interface--ch21), [Transmitter](#transmitter-interface--part-iii), [Decoder](#decoder-interface--ch10-11), [Nucleus](#nucleus-interface--ch12-46) |
| `homeostatic` | Ch.48, 62       | [Drive](#drive-interface--ch48-62), [Salience](#salience-interface--ch63-seeley-2007)                                                                                |
| `learning`    | Ch.63-64        | [Plasticity](#plasticity-interface--ch63)                                                                                                                            |
| `mnemonic`    | Ch.65-67        | [Trace](#trace-interface--ch63-67), [Episode](#episode-interface--ch65-67), [Knowledge](#knowledge-interface--ch65-67)                                               |

Runtime packages mirror spec 1:1. Exception: `cognitive.processual` exists only in runtime
(Process dispatch — Vocalize, Fire, Potentiate, Project, Inhibit).

---

## Specification Details

### Root package

⚙️ Evans DDD shared tools. All domain objects with identity extend AggregateRoot.
The root package permits minimal engineering interfaces/classes that have no Kandel equivalent
but are required by the domain model (e.g., Entity, AggregateRoot).

#### `Entity` interface

| Method | Basis                                                                                         |
|--------|-----------------------------------------------------------------------------------------------|
| `id()` | ⚙️ Evans DDD identity. Biological neurons have no names — identified by topographic position. |

#### `AggregateRoot` interface

Extends Entity. Marker for Repository-managed domain objects.
Area, Neuron, Effector, Trace are AggregateRoots.

---

### `neural` package

Kandel Ch.2 classifies neurons into three functional types: sensory, interneuron,
and motoneuron. In AAI, sensory transduction is handled by `signaling/Transducer`.
The `neural` package contains the remaining two classifications:

- **Interneuron** (Ch.2) — Area, Neuron. Integrative processing components.
- **Motoneuron** (Ch.2) — Effector. Motor output components.

All three extend AggregateRoot. The package remains flat for code simplicity;
ACL mirrors this classification in its subpackage structure.

#### `Area` interface — Ch.1, 26-27

Functional cortical area (V1, M1, Broca's). Unit of cortical organization.

| Method        | Basis                                                                    |
|---------------|--------------------------------------------------------------------------|
| `tuning()`    | Ch.26, 30 — area-level selectivity; routing criterion + behavioral layer |
| `neurons()`   | Ch.2, 26 — area composed of individually tuned neurons                   |
| `effectors()` | Ch.35-36 — motor areas project to effectors via corticospinal tract      |

#### `Neuron` interface — Ch.2, 26

Fundamental unit. Hubel & Wiesel: individual V1 neurons have orientation selectivity.

| Method     | Basis                                                                            |
|------------|----------------------------------------------------------------------------------|
| `tuning()` | Ch.26 — neuron-level feature selectivity (orientation, frequency, face identity) |

#### `Effector` interface — Ch.35-36

Motor output structures (muscles, glands). In AAI: compiled action capabilities.

| Method      | Basis                                                                                                                             |
|-------------|-----------------------------------------------------------------------------------------------------------------------------------|
| `tuning()`  | Ch.35-36, 38-40 — motor tuning (Georgopoulos population vector)                                                                   |
| `fire(Map)` | "neurons fire action potentials" — execute action, return result. ⚙️ KV map models I/O; biology uses synaptic signals (Ch.35-36). |

---

### `integrative` package — Ch.8-12

Universal neural computation substrate shared by all processing layers.

#### `Encoder` interface — Ch.21

Neural coding (Ch.21): transforms signal into representable form for synaptic transmission.

| Method                      | Basis                                                                                       |
|-----------------------------|---------------------------------------------------------------------------------------------|
| `encode(Impulse, Class<?>)` | Encodes Impulse into cortical representation for Nucleus. `Class<?>` for Java type erasure. |

#### `Transmitter` interface — Part III

Synaptic Transmission (Part III). Orchestrates the complete synaptic transmission cycle:
presynaptic encoding → cleft diffusion → postsynaptic decoding.

| Method                        | Basis                                                          |
|-------------------------------|----------------------------------------------------------------|
| `transmit(Impulse, Class<T>)` | Synaptic transmission cycle. `Class<T>` for Java type erasure. |

#### `Decoder` interface — Ch.10-11

Postsynaptic signal decoding: reconstructs structured response from raw transmission output.
Receptor binding and second-messenger cascades (Ch.10-11).

| Method                     | Basis                                                                         |
|----------------------------|-------------------------------------------------------------------------------|
| `decode(String, Class<T>)` | Deserialize raw signal into typed response. `Class<T>` for Java type erasure. |

#### `Nucleus` interface — Ch.12, 46

Synaptic integration at soma. Neuroanatomical "nucleus" = cluster of somas
performing integration (Ch.12, 46). Name and function both Kandel-compliant.

| Method                   | Basis                                                                                                                                        |
|--------------------------|----------------------------------------------------------------------------------------------------------------------------------------------|
| `integrate(T, Runnable)` | Soma integration + axonal firing. Caller specifies propagation target (axonal projection, Ch.3,4). ⚙️ Runnable models downstream projection. |

---

### `signaling` package

#### `Stimulus` interface — Ch.21

| Method    | Basis               |
|-----------|---------------------|
| `input()` | Raw sensory content |

#### `Impulse` interface — Ch.7

| Method     | Basis                                                                                                                      |
|------------|----------------------------------------------------------------------------------------------------------------------------|
| `signal()` | Content carried by the signal                                                                                              |
| `area()`   | ⚙️ Target Area for explicit routing; null before relay(), non-null after. Biology routes by axonal connections (Ch.2, 18). |

#### `Transducer` interface — Ch.21

| Method                | Basis                                                            |
|-----------------------|------------------------------------------------------------------|
| `transduce(Stimulus)` | Stimulus → Impulse(area=null). Peripheral receptor transduction. |

#### `Thalamus` interface — Ch.23, 26, 46

| Method           | Basis                                                            |
|------------------|------------------------------------------------------------------|
| `relay(Impulse)` | Void. Routes unrouted Impulse to appropriate Area, fires Cortex. |

---

### `cognitive` package

#### `Cortex` interface — Part VII

| Method             | Basis                                                            |
|--------------------|------------------------------------------------------------------|
| `respond(Impulse)` | Void. Integrate via Encoder+Nucleus → Process dispatch → Percept |

#### `Percept` interface — Ch.21, 25

Four psychophysical properties:

| Method        | Basis                             |
|---------------|-----------------------------------|
| `content()`   | Qualitative content (the 'what')  |
| `location()`  | Spatial location in sensory field |
| `intensity()` | Stevens's power law               |
| `duration()`  | Temporal persistence              |

---

### `learning` package — Ch.63-64

#### `Plasticity` interface — Ch.63

| Method                | Basis                                                                                                                                     |
|-----------------------|-------------------------------------------------------------------------------------------------------------------------------------------|
| `potentiate(Impulse)` | LTP — synaptic strengthening. Single Impulse = NMDA coincidence trigger (Ch.63); accumulated co-activation history is carried by Episode. |
| `prune()`             | Ch.55-56, 65 — synaptic pruning/elimination                                                                                               |

---

### `homeostatic` package

#### `Salience` interface — Ch.63, Seeley 2007

Orienting response + Salience Network (anterior insula + ACC). Gates DMN↔CEN switch.

| Method             | Basis                                                                                                          |
|--------------------|----------------------------------------------------------------------------------------------------------------|
| `orient()`         | Orient attention; suppress DMN                                                                                 |
| `release(Percept)` | Return to rest; DMN re-engages. ⚙️ Percept param: CDI @Observes trigger; biology monitors activity indirectly. |
| `isOriented()`     | ⚙️ State query for DMN suppression; boolean polling has no Kandel equivalent.                                  |
| `await()`          | ⚙️ Block caller until CEN completes and Percept is fired.                                                      |

#### `Drive` interface — Ch.48, 62

Drive states (subcortical) + DMN (spontaneous internal activity; Raichle 2001).
Marker interface. Lifecycle managed by `@PostConstruct`/`@PreDestroy` in runtime.

---

### `mnemonic` package

Kandel Ch.65 classifies memory into two major systems:

- **Declarative** (Ch.65 Tulving) — Episode, Knowledge. Explicit memory that can be consciously recalled.
- **Nondeclarative** (Ch.66) — Not yet implemented.

All Traceable types belong to the declarative system. Nondeclarative memory
(procedural skills, priming, conditioning) remains a future extension point.

#### `Trace` interface — Ch.63-67

Fundamental unit of memory encoding. One `encode()` = one Trace.

| Method      | Basis                                                                              |
|-------------|------------------------------------------------------------------------------------|
| `id()`      | ⚙️ Retrieval cue (Ch.65-67). `id` is the Evans DDD identifier; semantically = cue. |
| `content()` | Encoded content (the stored memory trace itself)                                   |

#### `Episode` interface — Ch.65-67

Episodic memory (Tulving): autobiographical, time-stamped, per-session.
`@Episodic` qualifier.

| Method             | Basis                            |
|--------------------|----------------------------------|
| `encode(Trace)`    | Commit to memory                 |
| `retrieve(String)` | Cue-based retrieval              |
| `retrieve()`       | All stored traces                |
| `decay()`          | Forgetting without reinforcement |

#### `Knowledge` interface — Ch.65-67

Semantic memory (Tulving): general world knowledge, cross-session.
`@Semantic` qualifier.

| Method             | Basis                            |
|--------------------|----------------------------------|
| `encode(Trace)`    | Commit to memory                 |
| `retrieve(String)` | Cue-based retrieval              |
| `retrieve()`       | All stored traces                |
| `decay()`          | Forgetting without reinforcement |

---

## Runtime Engineering Compromises

| Class              | Method / Field            | Description                                                                                                                           |
|--------------------|---------------------------|---------------------------------------------------------------------------------------------------------------------------------------|
| `PerceptImpl`      | `location`                | Maps Process name to location (Ch.1, 17: cortical location = function); biology uses somatotopic/retinotopic coordinates (Ch.21, 25). |
| `DriveImpl`        | `schedule()`              | DMN infra-slow oscillation 0.01-0.1 Hz = 10-100s (Ch.62). 10-30s balances Kandel fidelity with interactive responsiveness.            |
| `NucleusImpl`      | `executorService`         | Single-thread ExecutorService models refractory period (Ch.9). Async integration + threshold gating.                                  |
| `TraceImpl`        | constructor               | Embeds `Instant.now()` in id string via `@` separator. Kandel: id is retrieval cue (Ch.65).                                           |
| `DriveImpl`        | `scheduleConsolidation()` | Timed memory consolidation trigger. Biology consolidates during sleep/rest (Ch.67); scheduled as engineering compromise.              |
| `HabituationGuard` | (class)                   | Habituation (Ch.63). Count-based implementation; biology modulates synaptic efficacy.                                                 |
| `HabituationGuard` | `observe()`               | Habituation after 3 consecutive firings of same effector.                                                                             |
