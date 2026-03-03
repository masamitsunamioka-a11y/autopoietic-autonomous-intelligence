# Kandel Interface Reference

All interfaces grounded in Kandel, *Principles of Neural Science* (6th ed.).
⚙️ = engineering convention (no direct Kandel structural equivalent).

## Specification Packages

| Package       | Kandel          | Interfaces                                                                                                                                                        |
|---------------|-----------------|-------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `neural`      | Ch.1, 26-27     | [Engravable](#engravable-interface--ch63), [Area](#area-interface--ch1-26-27), [Neuron](#neuron-interface--ch2-26), [Effector](#effector-interface--ch35-36)      |
| `signaling`   | Ch.7, 20-21, 23 | [Stimulus](#stimulus-interface--ch21), [Impulse](#impulse-interface--ch7), [Transducer](#transducer-interface--ch21), [Thalamus](#thalamus-interface--ch23-26-46) |
| `cognitive`   | Part VII        | [Cortex](#cortex-interface--part-vii), [Percept](#percept-interface--ch21-25)                                                                                     |
| `synaptic`    | Ch.8-12         | [Encoder](#encoder-interface--ch21), [Nucleus](#nucleus-interface--ch12-46)                                                                                       |
| `homeostatic` | Ch.48, 62       | [Drive](#drive-interface--ch48-62), [Salience](#salience-interface--ch63-seeley-2007)                                                                             |
| `learning`    | Ch.63-64        | [Plasticity](#plasticity-interface--ch63)                                                                                                                         |
| `working`     | Ch.65-67        | [Memory](#memory-interface--ch63-67), [Trace](#trace-interface--ch63-67), [Episode](#episode-interface--ch65-67), [Knowledge](#knowledge-interface--ch65-67)      |

Runtime packages mirror spec 1:1. Exception: `cognitive.processual` exists only in runtime
(Process dispatch — Vocalize, Fire, Potentiate, Project, Inhibit).

---

## Specification Details

### `neural` package

#### `Engravable` interface — Ch.63

Base for structures modifiable by synaptic plasticity. Area, Neuron, Effector extend it.

| Method   | Basis                                                                                                                                      |
|----------|--------------------------------------------------------------------------------------------------------------------------------------------|
| `name()` | ⚙️ System addressing identifier. Biological neurons have no names — identified by topographic position + functional properties (Ch.1, 26). |

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

### `synaptic` package — Ch.8-12

Universal neural computation substrate shared by all processing layers.

#### `Encoder` interface — Ch.21

⚙️ Neural coding: transforms signal into representable form. Presynaptic encoding separated as
a discrete step; biology distributes this across vesicle release patterns (Ch.8-12).

| Method                      | Basis                                                                                 |
|-----------------------------|---------------------------------------------------------------------------------------|
| `encode(Impulse, Class<?>)` | Encodes Impulse into cortical representation for Nucleus. ⚙️ Caller dispatches phase. |

#### `Nucleus` interface — Ch.12, 46

Synaptic integration at soma. Nuclei are brain-wide integrative clusters.

| Method                         | Basis                                                                                                                            |
|--------------------------------|----------------------------------------------------------------------------------------------------------------------------------|
| `integrate(Impulse, Class<T>)` | Integrates encoded Impulse → typed response. ⚙️ Class<T> response dispatches output type; biology has no explicit type dispatch. |

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

| Method           | Basis                                                   |
|------------------|---------------------------------------------------------|
| `relay(Impulse)` | Routes unrouted Impulse to appropriate Area via tuning. |

---

### `cognitive` package

#### `Cortex` interface — Part VII

| Method             | Basis                                                      |
|--------------------|------------------------------------------------------------|
| `respond(Impulse)` | Integrate via Encoder+Nucleus → Process dispatch → Percept |

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

| Method         | Basis                                                                         |
|----------------|-------------------------------------------------------------------------------|
| `orient()`     | Orient attention; suppress DMN                                                |
| `release()`    | Return to rest; DMN re-engages                                                |
| `isOriented()` | ⚙️ State query for DMN suppression; boolean polling has no Kandel equivalent. |

#### `Drive` interface — Ch.48, 62

Drive states (subcortical) + DMN (spontaneous internal activity; Raichle 2001).
Marker interface. Lifecycle managed by `@PostConstruct`/`@PreDestroy` in runtime.

---

### `working` package

#### `Trace` interface — Ch.63-67

Fundamental unit of memory encoding. One `encode()` = one Trace.

| Method      | Basis                                            |
|-------------|--------------------------------------------------|
| `cue()`     | Ch.65-67 — retrieval cue (recall trigger)        |
| `content()` | Encoded content (the stored memory trace itself) |

#### `Memory` interface — Ch.63-67

Declarative memory: encoding, retrieval, forgetting.

| Method             | Basis                            |
|--------------------|----------------------------------|
| `encode(Trace)`    | Commit to memory                 |
| `retrieve(String)` | Cue-based retrieval              |
| `retrieve()`       | All stored traces                |
| `decay()`          | Forgetting without reinforcement |

#### `Episode` interface — Ch.65-67

Episodic memory (Tulving): autobiographical, time-stamped, per-session.
Marker interface. `@Episodic` qualifier.

#### `Knowledge` interface — Ch.65-67

Semantic memory (Tulving): general world knowledge, cross-session.
Marker interface. `@Semantic` qualifier.

---

## Runtime Engineering Compromises

| Class           | Method / Field  | Description                                                                                                                |
|-----------------|-----------------|----------------------------------------------------------------------------------------------------------------------------|
| `Decision`      | `reasoning`     | Deliberation rationale as text. Biology has no separate explanation output — decision IS the neural computation (Ch.57).   |
| `PerceptImpl`   | `location`      | Maps Area name to location; biology uses somatotopic/retinotopic coordinates (Ch.21, 25).                                  |
| `DriveImpl`     | `schedule()`    | DMN infra-slow oscillation 0.01-0.1 Hz = 10-100s (Ch.62). 10-30s balances Kandel fidelity with interactive responsiveness. |
| `EncoderImpl`   | `assemble()`    | Template variable substitution + `{{guardrails}}` → executive_control.md composition. No biological equivalent.            |
| `NucleusImpl`   | `validate()`    | Bean Validation on LLM output. Biology has no explicit output validation step.                                             |
| `TraceImpl`     | constructor     | Embeds `Instant.now()` in cue string via `@` separator. Kandel: cue is pure retrieval trigger (Ch.65).                     |
| `KnowledgeImpl` | `prefixOf()`    | Parses cue prefix before `@` for deduplication. Consequence of TraceImpl cue encoding.                                     |
| `KnowledgeImpl` | `timestampOf()` | Parses timestamp after `@` in cue. Consequence of TraceImpl cue encoding.                                                  |
