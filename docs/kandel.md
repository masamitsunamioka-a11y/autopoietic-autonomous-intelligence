# Kandel Interface Reference

All interfaces grounded in Kandel, *Principles of Neural Science* (6th ed.).
⚙️ = engineering convention (no direct Kandel structural equivalent).

---

## `neural`

### `Engravable` — Ch.63

Base for structures modifiable by synaptic plasticity. Area, Neuron, Effector extend it.

| Method             | Basis                      |
|--------------------|----------------------------|
| `name()`           | ⚙️ System identifier       |
| `encode(Function)` | ⚙️ Java functional pattern |

### `Area` — Ch.1, 26-27

Functional cortical area (V1, M1, Broca's). Unit of cortical organization.

| Method        | Basis                                                                    |
|---------------|--------------------------------------------------------------------------|
| `tuning()`    | Ch.26, 30 — area-level selectivity; routing criterion + behavioral layer |
| `neurons()`   | Ch.2, 26 — area composed of individually tuned neurons                   |
| `effectors()` | Ch.35-36 — motor areas project to effectors via corticospinal tract      |

### `Neuron` — Ch.2, 26

Fundamental unit. Hubel & Wiesel: individual V1 neurons have orientation selectivity.

| Method     | Basis                                                                            |
|------------|----------------------------------------------------------------------------------|
| `tuning()` | Ch.26 — neuron-level feature selectivity (orientation, frequency, face identity) |

### `Effector` — Ch.35-36

Motor output structures (muscles, glands). In AAI: compiled action capabilities.

| Method      | Basis                                                            |
|-------------|------------------------------------------------------------------|
| `tuning()`  | Ch.35-36, 38-40 — motor tuning (Georgopoulos population vector)  |
| `fire(Map)` | "neurons fire action potentials" — execute action, return result |

---

## `synaptic` — Ch.8-12

Universal neural computation substrate shared by all processing layers.

### `Encoder` — Ch.21

Neural coding: transforms signal into representable form.

| Method                      | Basis                                                                                 |
|-----------------------------|---------------------------------------------------------------------------------------|
| `encode(Impulse, Class<?>)` | Encodes Impulse into cortical representation for Nucleus. ⚙️ Caller dispatches phase. |

### `Nucleus` — Ch.12, 46

Synaptic integration at soma. Nuclei are brain-wide integrative clusters.

| Method                         | Basis                                     |
|--------------------------------|-------------------------------------------|
| `integrate(Impulse, Class<T>)` | Integrates encoded Impulse → typed output |

---

## `signaling`

### `Stimulus` — Ch.21

| Method    | Basis               |
|-----------|---------------------|
| `input()` | Raw sensory content |

### `Impulse` — Ch.7

| Method     | Basis                                                            |
|------------|------------------------------------------------------------------|
| `signal()` | Content carried by the signal                                    |
| `area()`   | ⚙️ Target Area for routing. null before relay(), non-null after. |

### `Transducer` — Ch.21

| Method                | Basis                                                            |
|-----------------------|------------------------------------------------------------------|
| `transduce(Stimulus)` | Stimulus → Impulse(area=null). Peripheral receptor transduction. |

### `Thalamus` — Ch.23, 26, 46

| Method           | Basis                                                   |
|------------------|---------------------------------------------------------|
| `relay(Impulse)` | Routes unrouted Impulse to appropriate Area via tuning. |

---

## `cognitive`

### `Cortex` — Part VII

| Method             | Basis                                                   |
|--------------------|---------------------------------------------------------|
| `respond(Impulse)` | Integrate via Encoder+Nucleus → Mode dispatch → Percept |

### `Percept` — Ch.21, 25

Four psychophysical properties:

| Method        | Basis                             |
|---------------|-----------------------------------|
| `content()`   | Qualitative content (the 'what')  |
| `location()`  | Spatial location in sensory field |
| `intensity()` | Stevens's power law               |
| `duration()`  | Temporal persistence              |

---

## `learning` — Ch.63-64

### `Plasticity` — Ch.63

| Method                | Basis                                                                                             |
|-----------------------|---------------------------------------------------------------------------------------------------|
| `potentiate(Impulse)` | LTP — synaptic strengthening. ⚙️ Single-Impulse trigger (Kandel requires repeated co-activation). |
| `prune()`             | Ch.55-56, 65 — synaptic pruning/elimination                                                       |

---

## `homeostatic`

### `Salience` — Ch.63, Seeley 2007

Orienting response + Salience Network (anterior insula + ACC). Gates DMN↔CEN switch.

| Method         | Basis                              |
|----------------|------------------------------------|
| `orient()`     | Orient attention; suppress DMN     |
| `release()`    | Return to rest; DMN re-engages     |
| `isOriented()` | ⚙️ State query for DMN suppression |

### `Drive` — Ch.48, 62

Drive states (subcortical) + DMN (spontaneous internal activity; Raichle 2001).

| Method         | Basis                     |
|----------------|---------------------------|
| `activate()`   | DMN/drive activation      |
| `deactivate()` | Task-induced deactivation |

---

## `working`

### `Trace` — Ch.63-67

Fundamental unit of memory encoding. One `encode()` = one Trace.

| Method      | Basis                                          |
|-------------|-------------------------------------------------|
| `cue()`     | Ch.65-67 — retrieval cue (recall trigger)        |
| `content()` | Encoded content (the stored memory trace itself) |

### `Memory` — Ch.63-67

Declarative memory: encoding, retrieval, forgetting.

| Method             | Basis                            |
|--------------------|----------------------------------|
| `encode(Trace)`    | Commit to memory                 |
| `retrieve(String)` | Cue-based retrieval              |
| `retrieve()`       | All stored traces                |
| `decay()`          | Forgetting without reinforcement |

### `Episode` — Ch.65-67

Episodic memory (Tulving): autobiographical, time-stamped, per-session.
Marker interface. `@Episodic` qualifier.

### `Knowledge` — Ch.65-67

Semantic memory (Tulving): general world knowledge, cross-session.
Marker interface. `@Semantic` qualifier.
