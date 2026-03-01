# AAI Specification Architecture

Naming and structure grounded in Kandel, *Principles of Neural Science* (6th ed.).
Where no direct Kandel structural equivalent exists, the interpretation is noted
alongside the closest Kandel concept. Engineering conventions are marked ⚙️.
---

## Signal Flows

### CEN — Central Executive Network (externally driven)

```
Stimulus.input()
  → Transducer.transduce()          Ch.21  sensory transduction
  → Impulse(area = null)
  → Thalamus.relay()                Ch.23,26,46  thalamic relay → Area selection
  → Impulse(area != null)
  → Cortex.respond()               Part VII  cortical perception
      → Encoder.encode()            Ch.21  neural coding (called by each engine)
      → Nucleus.integrate()         Ch.12  synaptic integration
  → Percept
  → Effector.tuning() → select
  → Effector.fire()                 Ch.35-36  motor output execution
```

### DMN — Default Mode Network (internally driven)

```
Drive.activate()                    Ch.48,62  DMN / homeostatic drive activation
  → [DriveImpl generates Impulse(area != null) internally]
  → Cortex.respond()               same cortical pathway as CEN
  → Percept
```

DMN bypasses Thalamus. Area selection is determined internally by Drive
(not by external stimulus routing). Consistent with Ch.62: DMN activates
medial PFC, posterior cingulate, and related areas spontaneously.

### PROJECT — Corticocortical Projection

```
Cortex.respond() → LLM decides PROJECT mode
  → decision.area() → AreaRepository.find()   Ch.20  association cortex direct projection
  → ImpulseImpl(signal, targetArea)
  → Cortex.respond()                          recursive; no second Thalamus relay
  → Percept
```

PROJECT bypasses Thalamus. When Cortex has already determined the target Area
via LLM decision, routing is direct (Ch.20 association fibers — corticocortical
projection). Thalamus is only needed for bottom-up sensory routing (CEN path).
---

## Package: `neural`

### `Engravable`

Base interface for all neural structures capable of being modified by experience
through synaptic plasticity (**Kandel Ch.63**). Area, Neuron, and Effector all
extend Engravable — Plasticity engraves their tuning at runtime.
| Method | Kandel basis |
|--------|-------------|
| `name(): String` | ⚙️ Identifier. No direct Kandel equivalent; required for system addressing. |
| `encode(Function<Engravable,String>): String` | ⚙️ Java functional pattern. No Kandel method equivalent. |
---

### `Area`

**Kandel Ch.1** "The Brain and Behavior" — functional areas of the cerebral cortex
(V1, M1, Broca's area, etc.) are the unit of cortical organization. **Ch.26, 27** —
cortical areas have area-level tuning properties (orientation columns, tonotopic maps).
| Method | Kandel basis |
|--------|-------------|
| `tuning(): String` | Ch.26, 30 — area-level feature selectivity. V1 is tuned to visual orientation; A1 is tuned to
sound frequency. Serves as both routing criterion (Thalamus selects Area) and base behavioral layer (Encoder includes in
prompt). |
| `neurons(): List<Neuron>` | Ch.2, 26 — a cortical area is composed of individual neurons with specific tuning
properties. |
| `effectors(): List<Effector>` | Ch.35-36 — motor areas (M1) project directly to effectors via corticospinal tract.
Non-motor Areas return an empty list. |
---

### `Neuron`

**Kandel Ch.2** "Nerve Cells and Behavior" — the neuron as the fundamental unit.
**Ch.26** — Hubel & Wiesel: individual neurons in V1 have orientation selectivity
("simple cells are tuned to specific orientations"). The most direct Kandel
grounding in the specification.
| Method | Kandel basis |
|--------|-------------|
| `tuning(): String` | Ch.26 **direct citation** — individual neurons are tuned to specific stimulus features (
orientation, frequency, face identity, etc.). Serves as both the routing criterion within an Area and the
neuron-specific behavioral layer in the prompt. |
---

### `Effector`

**Kandel Ch.35-36** "The Motor System" — effectors are the peripheral structures that
execute motor commands (muscles, glands). Extends `Engravable`. In AAI, Effectors are
compiled capabilities that execute actions in the external environment.
| Method | Kandel basis |
|--------|-------------|
| `tuning(): String` | Ch.35-36, 38-40 — motor cortex neurons are tuned to specific movement directions (Georgopoulos
population vector; Ch.38). Used for selection by Nucleus. |
| `fire(Map<String,Object>): Map<String,Object>` | Kandel throughout — "neurons fire action potentials." In AAI,
executing the Effector's action and returning its result. |
---

## Package: `synaptic`

Synaptic transmission — the universal neural computation substrate shared by all
processing layers (Kandel Part II Ch.8-12). Used by Cortex, Thalamus, Plasticity,
and Drive implementations alike.

- **Encoder**: presynaptic encoding (signal → transmittable representation). Each engine calls Encoder directly.
- **Nucleus**: postsynaptic integration (encoded signal → structured output). Receives already-encoded Impulse.

### `Encoder`

**Kandel Ch.21** "Sensory Coding" — neural coding / cortical encoding. Primary
sensory cortices encode sensory signals into cortical representations (orientation
selectivity in V1, frequency tuning in A1, etc.).
No direct Kandel structural equivalent. Grounded in **neural coding / cortical
encoding** as the transformation of a neural signal into a representational form.
| Method | Kandel basis |
|--------|-------------|
| `encode(Impulse, Class<?>): String` | Encodes the Impulse (signal + Area + Neuron context) into a cortical
representation (LLM prompt) for Nucleus to integrate. Caller class dispatches encoding phase (⚙️). |
---

### `Nucleus`

**Kandel Ch.12** "Modulation of Synaptic Transmission" — synaptic integration
at the soma. **Ch.46** — nuclei (thalamic, basal ganglia, cochlear) are integrative
processing clusters distributed throughout the brain, not exclusively cortical.
In AAI, Nucleus is the universal LLM integration point — used by all processing
layers, mirroring the brain-wide distribution of integrative nuclei.
| Method | Kandel basis |
|--------|-------------|
| `integrate(Impulse, Class<T>): T` | Ch.12 synaptic integration — integrates the encoded Impulse and produces a typed
output. |
---

## Package: `signaling`

Sensory pathway — from external stimulus through thalamic relay to routed Impulse.

### `Stimulus`

**Kandel Ch.21** "Sensory Coding" — "A stimulus is a form of energy detected by
sensory receptors."
| Method | Kandel basis |
|--------|-------------|
| `input(): String` | The raw sensory content entering the system. |
---

### `Impulse`

**Kandel Ch.7** "Propagated Signaling: The Action Potential" — the nerve impulse
(action potential) is the fundamental unit of neural communication.
| Method | Kandel basis |
|--------|-------------|
| `signal(): String` | Content carried by the neural signal. |
| `area(): Area` | ⚙️ Target Area for routing. Biological routing is determined by physical axonal connections (Ch.7),
not by labels on impulses. Included for computational routing; null before `Thalamus.relay()`, non-null after. |
---

### `Transducer`

**Kandel Ch.21** "Sensory Coding" — "Sensory receptors **transduce** stimulus
energy into electrical signals (impulses)."
| Method | Kandel basis |
|--------|-------------|
| `transduce(Stimulus): Impulse` | Converts external Stimulus into internal Impulse (area == null). Mirrors peripheral
receptor transduction before thalamic routing. |
---

### `Thalamus`

**Kandel Ch.23, 26, 46** — the thalamus is the subcortical relay station
(diencephalon). All sensory modalities except olfaction pass through thalamic
nuclei before reaching the cortex.
| Method | Kandel basis |
|--------|-------------|
| `relay(Impulse): Impulse` | Routes an unrouted Impulse (area == null) to the appropriate Area (area != null). Thalamus
selects the target Area using Area.tuning(). |
---

## Package: `cognitive`

Cortical perception — the conscious experience produced from a routed Impulse
(Kandel Part VII "Higher Cognitive Functions").

### `Cortex`

**Kandel Part VII** — the cerebral cortex is the seat of perception, attention,
and executive function.
| Method | Kandel basis |
|--------|-------------|
| `respond(Impulse): Percept` | Receives a routed Impulse, integrates via Encoder + Nucleus, dispatches to the
appropriate Mode (VOCALIZE/FIRE/POTENTIATE/PROJECT/INHIBIT), and returns a Percept. |
---

### `Percept`

**Kandel Ch.21** — "A percept is the subjective experience that results from
sensory stimulus processing." Ch.25 — visual percept formation.
Four defining properties of a percept (Kandel Ch.21 psychophysics).
| Method | Kandel basis |
|--------|-------------|
| `content(): String` | Ch.21, 25 — the qualitative content of conscious experience; the 'what' of a percept (color,
shape, meaning). |
| `location(): String` | Ch.21 — spatial location of the perceived stimulus in the sensory field. |
| `intensity(): double` | Ch.21 — psychophysical intensity; Stevens's power law — magnitude of sensation proportional to
stimulus intensity. |
| `duration(): double` | Ch.21 — persistence of the percept over time. |
---

## Package: `learning`

Synaptic plasticity — the cellular/molecular mechanisms of learning and memory
(Kandel Part IX Ch.63-64).

### `Plasticity`

**Kandel Ch.63** "Synaptic Plasticity and the Molecular Mechanisms of Memory
Storage" — synaptic plasticity is a cellular/molecular mechanism occurring
throughout the nervous system (Part IX: Learning and Memory).
| Method | Kandel basis |
|--------|-------------|
| `potentiate(Impulse): void` | ⚙️ Long-Term Potentiation (LTP); Ch.63 — persistent strengthening of synaptic
connections.
In AAI, generates new Areas / Neurons / Effectors in response to capability gaps. Single-Impulse trigger is an
Engineering simplification; Kandel LTP requires repeated co-activation (Hebb's rule, Ch.63). |
| `prune(): void` | Synaptic pruning; Ch.55-56 (developmental), Ch.65 (synaptic elimination). In AAI, removes redundant
cognitive structures. |
---

## Package: `homeostatic`

System-level regulation — arousal, motivation, and internally driven activity.
Drive and Salience regulate the system from the subcortical/network level
(Kandel Ch.48/62).

### `Salience`

**Kandel Ch.63** — the orienting response: a shift of attention toward a novel
or significant stimulus, suppressing ongoing background activity.
**Salience Network** (Seeley et al., 2007; incorporated Kandel 6th ed.):
anterior insula + ACC gate the switch from DMN to CEN.
| Method | Kandel basis |
|--------|-------------|
| `orient(): void` | Ch.63 orienting response — orient attention toward an incoming external stimulus. Suppresses DMN
(Drive) while CEN (Cortex) is active. |
| `release(): void` | Ch.63 — return to resting state after orienting; DMN re-engages. |
| `isOriented(): boolean` | ⚙️ State query for DMN suppression logic; no direct Kandel equivalent. |
---

### `Drive`

**Kandel Ch.48** "Hypothalamus: Autonomic, Hormonal, and Behavioral Control" —
drive states are internally motivated behaviors arising without external stimuli
(hunger, thirst, sexual drive; subcortical origin).
**Kandel Ch.62** — Default Mode Network (DMN): spontaneous, internally generated
activity spanning cortical and subcortical areas; active at rest.
| Method | Kandel basis |
|--------|-------------|
| `activate(): void` | DMN / drive state activation. Raichle (2001), incorporated in Kandel 6th ed. |
| `deactivate(): void` | Task-induced deactivation; Raichle (2001) — DMN shows "task-induced deactivations" when CEN
becomes active (Ch.62). |
---

## Package: `working`

Working memory layer — active in-session maintenance of declarative memory
(prefrontal cortex; Kandel Ch.65).

### `Trace`

**Kandel Ch.63-67** — a memory trace is the fundamental unit of memory encoding — one
`encode()` call produces one Trace. Shared by both episodic and semantic memory systems.
| Method | Kandel basis |
|--------|-------------|
| `key(): String` | ⚙️ Identifier for the memory trace. No direct Kandel equivalent; required for retrieval
addressing. |
| `value(): Object` | The content of the memory trace — the encoded information. |
| `timestamp(): Instant` | ⚙️ Explicit timestamp for chronological retrieval; biology infers temporality from
decay/context. |
| `of(String, Object): Trace` | ⚙️ Factory method (auto-stamps with `Instant.now()`). |
| `of(String, Object, Instant): Trace` | ⚙️ Factory method with explicit timestamp (for reconstruction from storage). |
| `decode(String, Object): Trace` | ⚙️ Reconstruct from encoded identity (key@timestamp). |
---

### `Memory`

**Kandel Ch.63-67** — declarative (explicit) memory: encoding, retrieval,
and forgetting. Base interface for all in-session memory stores. All operations
use `Trace` as the fundamental unit.
| Method | Kandel basis |
|--------|-------------|
| `encode(Trace): void` | Ch.65-67 — "encoding is the process by which a perceived item is committed to
memory." One Trace = one encoding event. |
| `retrieve(String): Trace` | Ch.65 — memory retrieval by cue. "Retrieve" is the general umbrella term (vs "recall" =
specific free-recall subtype). |
| `retrieve(): List<Trace>` | Retrieval of all stored traces. |
| `decay(): void` | Ch.65 "Forgetting" — memories weaken over time without reinforcement or consolidation. |
---

### `Episode`

**Kandel Ch.65-67** — episodic memory (Tulving; cited by Kandel): autobiographical,
time-stamped, context-rich memory of specific events. In AAI, holds the per-session
conversational history. Stored as `{session}/episode.json`.
Marker interface — inherits all `Memory` capabilities. Distinguished from Knowledge
via `@Episodic` CDI qualifier on the `Repository<Trace, Trace>` injection point.
---

### `Knowledge`

**Kandel Ch.65-67** — semantic memory (Tulving; cited by Kandel): general world
knowledge and facts, not tied to a specific time or context. In AAI, holds the
agent's persistent self-model, goals, and accumulated knowledge across sessions.
Stored as `knowledge.json` (single persistent file shared across sessions).
Marker interface — inherits all `Memory` capabilities. Distinguished from Episode
via `@Semantic` CDI qualifier on the `Repository<Trace, Trace>` injection point.
---

## Comparison with Agentforce

| Agentforce                             | AAI                          | Difference                                                                                            |
|----------------------------------------|------------------------------|-------------------------------------------------------------------------------------------------------|
| Agent                                  | `Area` (cortical area)       | Name only                                                                                             |
| Topic                                  | `Neuron` (individual neuron) | Name only                                                                                             |
| Action                                 | `Effector` (motor effector)  | Name + ownership (see below)                                                                          |
| Agent.description + Agent.instructions | `Area.tuning()`              | **Merged**: tuning covers both routing and behavior (Kandel: inseparable)                             |
| Topic.description + Topic.instructions | `Neuron.tuning()`            | **Merged**: same reason                                                                               |
| Action owned by Topic                  | Effector owned by **Area**   | **Structural**: Ch.35-36 — motor cortex (Area) projects directly to effectors, not individual neurons |
