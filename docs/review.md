# Kandel Compliance Review — specification + runtime

## Summary

**Kandel compliance: 86.7%** — OK / Total = 117 / 135

| Rating            | Count | Description                                 |
|-------------------|-------|---------------------------------------------|
| **OK**            | 117   | Kandel-compliant                            |
| **[Engineering]** | 12    | Resolvable — resolve to increase compliance |
| **Todo**          | 6     | Unimplemented or planned Kandel feature     |
| **Total**         | 135   |                                             |

Infrastructure SPI excluded from Total — inherent to any software system.

**Common prerequisites** (excluded from Engineering count):

- **Evans DDD** — `Entity`, `AggregateRoot` in root package. Domain modeling foundation.
- **LLM output convention** — `reasoning`, `confidence` fields in LLM-generated records (Decision, Projection,
  Fluctuation,
  Potentiation, Pruning). Inherent to LLM structured output; not a Kandel compromise.
- **Java type erasure** — `Class<T>` parameters on `Encoder.encode()`, `Transmitter.transmit()`, `Decoder.decode()`.
  Required for generic type dispatch in Java; no biological equivalent but unavoidable in a statically-typed language.

## Todo

### Unimplemented

| #  | Target                | Description                                                                                                           |
|----|-----------------------|-----------------------------------------------------------------------------------------------------------------------|
| T1 | `Percept.intensity()` | Ch.21 stimulus intensity coding. Declared in spec but unimplemented — throws UnsupportedOperationException (Todo #26) |
| T2 | `Percept.duration()`  | Ch.25 temporal coding. Declared in spec but unimplemented — throws UnsupportedOperationException (Todo #26)           |

### Kandel roadmap

| #  | Target                        | Description                                                                                                                                                                                                                                                                                             |
|----|-------------------------------|---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| T3 | `Process` spec promotion      | Ch.21-27 cortical process. Move from runtime.cognitive.processual to specification.cognitive                                                                                                                                                                                                            |
| T4 | System consolidation pipeline | Ch.67 system consolidation. Episode → Knowledge promotion (hippocampus → neocortex transfer)                                                                                                                                                                                                            |
| T5 | Axonal wiring in spec         | Ch.2, 18 axonal wiring determines signal pathways. Resolves E2                                                                                                                                                                                                                                          |
| T6 | Per-neuron Nucleus            | Ch.9, 12 each neuron has its own soma for synaptic integration. Current NucleusImpl is a singleton summarizing this. Real brain: massively parallel, per-neuron threshold + all-or-nothing firing. AAI Areas are dynamic (agent-like) vs brain's fixed cortical regions — a separate research direction |

### [Engineering]

Resolve to increase Kandel compliance.

**Resolvable:**

| #   | Target                              | Reason                                                                                                              |
|-----|-------------------------------------|---------------------------------------------------------------------------------------------------------------------|
| E1  | `Effector.fire(Map)`                | KV map models I/O; biology uses synaptic signals (Ch.35-36)                                                         |
| E2  | `Impulse.area()`                    | Explicit routing target; biology routes by axonal connections (Ch.2, 18)                                            |
| E3  | `Nucleus.integrate(T, Runnable)`    | Runnable models downstream projection; biology uses axonal propagation (Ch.3, 4)                                    |
| E4  | `Salience.release(Percept)`         | Percept param for CDI @Observes; biology monitors CEN activity indirectly                                           |
| E5  | `Salience.isOriented()`             | Boolean state query; no Kandel equivalent for polling orientation state                                             |
| E6  | `Salience.await()`                  | Block caller until CEN completes and Percept is fired; no biological blocking mechanism                             |
| E7  | `PerceptImpl.location`              | Process name as location (Ch.1, 17: cortical location = function); biology uses somatotopic coordinates (Ch.21, 25) |
| E8  | `DefaultImpl.schedule()`            | DMN infra-slow oscillation 0.01-0.1 Hz = 10-100s (Ch.62); timing approximation for responsiveness                   |
| E9  | `SleepImpl.scheduleConsolidation()` | Timed consolidation trigger; biology consolidates during sleep (Ch.51, 65-67)                                       |
| E10 | `NucleusImpl.executorService`       | Single-thread ExecutorService models refractory period (Ch.9); async integration + threshold                        |
| E11 | `TraceImpl` constructor             | Embeds Instant.now() in id via `@` separator; Kandel: id is retrieval cue (Ch.65)                                   |
| E12 | `HabituationGuard`                  | Count-based implementation; biology modulates synaptic efficacy (Ch.63)                                             |

**Infrastructure SPI — inherent to software, not resolvable:**

| #  | Target                                | Description                                                       |
|----|---------------------------------------|-------------------------------------------------------------------|
| I1 | `Entity`                              | Evans DDD identity                                                |
| I2 | `AggregateRoot`                       | Evans DDD aggregate root. Extends Entity                          |
| I3 | `Repository<T extends AggregateRoot>` | Persistence SPI (find, findAll, store, remove, removeAll, exists) |
| I4 | `Service<I, O>`                       | External service call port                                        |

---

## specification

### `specification.neural`

| Class / Method | Rating        | Kandel Rationale / Comment                                                             |
|----------------|---------------|----------------------------------------------------------------------------------------|
| **Area**       | OK            | Ch.1/26/27 cortical area — higher-level structure containing neuron groups + effectors |
| `.tuning()`    | OK            | Ch.26 feature selectivity (Hubel & Wiesel) — Area's response selectivity               |
| `.neurons()`   | OK            | Area containing neuron groups is anatomically accurate                                 |
| `.effectors()` | OK            | Ch.35-36 motor cortex projects to effectors. Area-level ownership is valid             |
| **Neuron**     | OK            | Ch.2 individual nerve cell. Has tuning selectivity for response characteristics        |
| `.tuning()`    | OK            | Ch.26 — single neuron selectivity                                                      |
| **Effector**   | OK            | Ch.35-36 motor output — projection from motor cortex to muscles/glands                 |
| `.tuning()`    | OK            | Effector's operational characteristics. Corresponds to motor neuron tuning property    |
| `.fire(Map)`   | [Engineering] | KV map models effector I/O; biology uses synaptic signals. Annotated                   |

### `specification.signaling`

| Class / Method         | Rating        | Kandel Rationale / Comment                                                              |
|------------------------|---------------|-----------------------------------------------------------------------------------------|
| **Stimulus**           | OK            | Ch.21 sensory stimulus — external sensory input                                         |
| `.input()`             | OK            | Content of sensory stimulus                                                             |
| **Impulse**            | OK            | Ch.7 action potential / nerve impulse — internal signal transmission                    |
| `.signal()`            | OK            | Signal content carried by action potential                                              |
| `.area()`              | [Engineering] | Biology routes by axonal connections. Explicit routing target is engineering. Annotated |
| **Transducer**         | OK            | Ch.21 sensory transduction — conversion at sensory receptors                            |
| `.transduce(Stimulus)` | OK            | Stimulus -> Impulse conversion is transduction itself                                   |
| **Thalamus**           | OK            | Ch.21 thalamic relay — relay nucleus for all sensory input (except olfaction)           |
| `.relay(Impulse)`      | OK            | Ch.21 thalamic relay nuclei — relays impulse to appropriate cortical area               |

### `specification.cognitive`

| Class / Method      | Rating | Kandel Rationale / Comment                                                                        |
|---------------------|--------|---------------------------------------------------------------------------------------------------|
| **Cortex**          | OK     | Ch.18/26 cerebral cortex — center for higher cognitive processing                                 |
| `.respond(Impulse)` | OK     | "neural response" / "cortical response" — response to afferent impulse (Ch.21, 26)                |
| **Percept**         | OK     | Ch.21 "sensory percept" — conscious perceptual representation resulting from cortical processing  |
| `.content()`        | OK     | Perceptual content                                                                                |
| `.location()`       | OK     | Ch.21, 25 somatotopic/retinotopic map location. Correct Kandel term. [Engineering] on PerceptImpl |
| `.intensity()`      | Todo   | Ch.21 stimulus intensity coding. Declared in spec but unimplemented (Todo #26)                    |
| `.duration()`       | Todo   | Ch.25 temporal coding. Declared in spec but unimplemented (Todo #26)                              |

### `specification.integrative`

| Class / Method               | Rating        | Kandel Rationale / Comment                                                                                |
|------------------------------|---------------|-----------------------------------------------------------------------------------------------------------|
| **Encoder**                  | OK            | Ch.21 neural coding. Presynaptic encoding concept exists in Kandel                                        |
| `.encode(Impulse, Class<?>)` | OK            | `Class<?>` is Java type erasure prerequisite. Phase dispatch by caller                                    |
| **Transmitter**              | OK            | Kandel Part III: Synaptic Transmission. Encode → cleft diffusion → decode cycle                           |
| `.transmit(Impulse, Class)`  | OK            | `Class<T>` is Java type erasure prerequisite. Response type selector                                      |
| **Decoder**                  | OK            | Postsynaptic signal decoding. Receptor binding + second messenger cascades (Ch.10-11)                     |
| `.decode(String, Class)`     | OK            | `Class<T>` is Java type erasure prerequisite. Typed response deserialization                              |
| **Nucleus**                  | OK            | Ch.8 cell body (soma) — site of input integration and output generation                                   |
| `.integrate(T, Runnable)`    | [Engineering] | Ch.8 synaptic integration + Ch.3,4 axonal projection. Runnable models downstream firing target. Annotated |

### `specification.homeostatic`

| Class / Method | Rating | Kandel Rationale / Comment                                                                              |
|----------------|--------|---------------------------------------------------------------------------------------------------------|
| **Drive**      | OK     | Ch.48 homeostatic drive states. Marker interface. Not yet implemented                                   |
| **Sleep**      | OK     | Ch.51, 65-67 sleep and memory consolidation. Lifecycle managed by @PostConstruct/@PreDestroy in runtime |

### `specification.modulatory`

| Class / Method      | Rating        | Kandel Rationale / Comment                                                                              |
|---------------------|---------------|---------------------------------------------------------------------------------------------------------|
| **Default**         | OK            | Ch.62 DMN — default mode network. Marker interface; lifecycle is @PostConstruct/@PreDestroy in runtime  |
| **Salience**        | OK            | Ch.63 salience network — attention allocation (Seeley 2007)                                             |
| `.orient()`         | OK            | Ch.63 "orienting response" — attention orientation to salient stimuli                                   |
| `.release(Percept)` | [Engineering] | Release of orienting response. ⚙️ Percept param for CDI @Observes; biology monitors activity indirectly |
| `.isOriented()`     | [Engineering] | State query for DMN suppression. Annotated                                                              |
| `.await()`          | [Engineering] | Block caller until CEN completes and Percept is fired. Annotated                                        |

### `specification.learning`

| Class / Method         | Rating | Kandel Rationale / Comment                                                                      |
|------------------------|--------|-------------------------------------------------------------------------------------------------|
| **Plasticity**         | OK     | Ch.63-64 synaptic plasticity — structural change via LTP/LTD                                    |
| `.potentiate(Impulse)` | OK     | Single Impulse = NMDA coincidence trigger (Ch.63); accumulated co-activation carried by Episode |
| `.prune()`             | OK     | Ch.64 synaptic elimination / pruning — removal of redundant connections                         |

### `specification.mnemonic`

| Class / Method      | Rating | Kandel Rationale / Comment                                                                |
|---------------------|--------|-------------------------------------------------------------------------------------------|
| **Trace**           | OK     | Ch.65 memory trace (engram) — physical substrate of memory                                |
| `.cue()`            | OK     | Ch.65 retrieval cue — cue for memory retrieval                                            |
| `.content()`        | OK     | Memory content held by trace                                                              |
| `.encode(Trace)`    | OK     | Ch.65 memory encoding — encoding of new memories (declared on Episode/Knowledge directly) |
| `.retrieve(String)` | OK     | Ch.65 cued recall — recall via presented cue                                              |
| `.retrieve()`       | OK     | Ch.65 free recall — recall of all memories                                                |
| `.decay()`          | OK     | Ch.65 forgetting / decay theory — memory decay over time                                  |
| **Episode**         | OK     | Ch.65-67 episodic memory (Tulving) — autobiographical, time-stamped memory                |
| **Knowledge**       | OK     | Ch.65-67 semantic memory (Tulving) — general world knowledge                              |

---

## runtime

### `runtime` (root — SPI)

| Class / Method                                      | Rating        | Kandel Rationale / Comment                      |
|-----------------------------------------------------|---------------|-------------------------------------------------|
| **Repository\<T extends AggregateRoot\>**           | [Engineering] | Pure infra SPI. No Kandel equivalent. Justified |
| `.find()` / `.findAll()` / `.store()` / `.remove()` | [Engineering] | Standard repository operations                  |
| `.removeAll(List<String>)`                          | [Engineering] | Batch deletion. Infra for decay()               |
| `.exists()`                                         | [Engineering] | Existence check                                 |
| **Service\<I, O\>**                                 | [Engineering] | External service call port. Infra               |

### `runtime.signaling`

| Class / Method     | Rating | Kandel Rationale / Comment                                                                                                      |
|--------------------|--------|---------------------------------------------------------------------------------------------------------------------------------|
| **StimulusImpl**   | OK     | Record implementation of Stimulus                                                                                               |
| **ImpulseImpl**    | OK     | Record implementation of Impulse                                                                                                |
| **TransducerImpl** | OK     | Ch.21 transduction                                                                                                              |
| `.transduce()`     | OK     | Converts Stimulus.input() to signal, area=null (pre-relay). Accurate                                                            |
| **ThalamusImpl**   | OK     | Ch.21 thalamic relay                                                                                                            |
| `.relay()`         | OK     | Void. Determines Area via Transmitter + Nucleus, fires Cortex as axonal projection. Thalamic relay                              |
| **Projection**     | OK     | Ch.18 "thalamic projection" — output of thalamic-to-cortical projection. reasoning + confidence + area is Nucleus output format |

### `runtime.cognitive`

| Class / Method                      | Rating        | Kandel Rationale / Comment                                                                                                      |
|-------------------------------------|---------------|---------------------------------------------------------------------------------------------------------------------------------|
| **Decision**                        | OK            | Cortical decision-making output. Ch.57 decision making                                                                          |
| `.reasoning`                        | OK            | LLM output convention — common prerequisite for all LLM-generated records                                                       |
| `.confidence`                       | OK            | Ch.57 decision confidence / signal detection theory                                                                             |
| `.process`                          | OK            | Cortical processing mode (confirmed in prior discussion)                                                                        |
| `.response` / `.effector` / `.area` | OK            | Output parameters per process type                                                                                              |
| **CortexImpl**                      | OK            | Implementation of Cortex. Owns Event\<Percept\> for unified CEN/DMN output                                                      |
| `.respond()`                        | OK            | Void. Transmitter -> Nucleus -> Process dispatch -> Event\<Percept\>                                                            |
| `focus (ReentrantLock)`             | OK            | Ch.25/62 attentional bottleneck — attention focuses on one process at a time. Lock representation is valid                      |
| `processes (Map)`                   | OK            | Cortical processing mode dispatch table                                                                                         |
| **PerceptImpl**                     | [Engineering] | location maps Process name (Ch.1,17: cortical location = function); biology uses somatotopic/retinotopic coordinates. Annotated |
| `.intensity()` / `.duration()`      | Todo          | UnsupportedOperationException (Todo #26)                                                                                        |
| **HabituationGuard**                | [Engineering] | Habituation (Ch.63); count-based implementation — biology modulates synaptic efficacy                                           |
| `.observe()`                        | [Engineering] | Habituation after 3 consecutive firings of same effector                                                                        |
| `.reset()`                          | OK            | Habituation release                                                                                                             |

### `runtime.cognitive.processual`

| Class / Method               | Rating | Kandel Rationale / Comment                                                                                         |
|------------------------------|--------|--------------------------------------------------------------------------------------------------------------------|
| **Process** (interface)      | OK     | Command pattern for cortical processing modes. CDI dispatch via qualifier annotations                              |
| `.handle(Impulse, Decision)` | OK     | Receives impulse and decision, returns percept                                                                     |
| **Vocalize**                 | OK     | Ch.62 Broca's area — language output. Encodes to Episode and returns                                               |
| **Fire**                     | OK     | Ch.35-36 motor execution — Effector firing. Encodes results to Knowledge. HabituationGuard for habituation control |
| **Potentiate**               | OK     | Ch.63 LTP trigger — calls Plasticity.potentiate() + prune()                                                        |
| **Project**                  | OK     | Ch.18 cortico-cortical projection — projection to another Area. SYSTEM WARNING + re-respond on missing area        |
| **Inhibit**                  | OK     | Ch.24 cortical inhibition / prefrontal inhibitory control — explicit safety boundary termination                   |

### `runtime.homeostatic`

| Class / Method                  | Rating        | Kandel Rationale / Comment                                                                                                                    |
|---------------------------------|---------------|-----------------------------------------------------------------------------------------------------------------------------------------------|
| **SleepImpl**                   | OK            | Ch.51, 65-67 sleep — memory consolidation during sleep                                                                                        |
| `.activate()` / `.deactivate()` | OK            | @PostConstruct/@PreDestroy — standard CDI lifecycle for scheduleConsolidation()                                                               |
| `.consolidate()`                | OK            | Calls Plasticity.prune() for synaptic pruning and memory consolidation (Ch.65-67)                                                             |
| `.scheduleConsolidation()`      | [Engineering] | Timed memory consolidation trigger. Biology consolidates during sleep (Ch.51, 65-67); scheduled interval is engineering compromise. Annotated |

### `runtime.modulatory`

| Class / Method                  | Rating        | Kandel Rationale / Comment                                                                                                           |
|---------------------------------|---------------|--------------------------------------------------------------------------------------------------------------------------------------|
| **Fluctuation**                 | OK            | Ch.62 infra-slow fluctuation — structured output of DMN spontaneous activity                                                         |
| `.aroused`                      | OK            | Ch.62 fluctuation exceeds threshold — activation state                                                                               |
| `.vocalize`                     | OK            | Broca's area vocalization threshold                                                                                                  |
| `.area` / `.signal`             | OK            | Target and signal content of fluctuation                                                                                             |
| **DefaultImpl**                 | OK            | Ch.62 DMN (default mode network) implementation                                                                                      |
| `.activate()` / `.deactivate()` | OK            | @PostConstruct/@PreDestroy — standard CDI lifecycle for schedule()                                                                   |
| `.fire()`                       | OK            | salience check -> transmit -> nucleus.integrate -> cortex.respond (void). DMN spontaneous firing                                     |
| `.schedule()`                   | [Engineering] | DMN infra-slow oscillation 0.01-0.1 Hz = 10-100s (Ch.62). 10-30s balances Kandel fidelity with interactive responsiveness. Annotated |
| **SalienceImpl**                | OK            | AtomicBoolean + CountDownLatch. @Observes Percept on release() — isOriented() guard ignores DMN Percepts                             |

### `runtime.learning`

| Class / Method                    | Rating | Kandel Rationale / Comment                                                                        |
|-----------------------------------|--------|---------------------------------------------------------------------------------------------------|
| **PlasticityImpl**                | OK     | Ch.63-64 LTP/LTD + structural plasticity                                                          |
| `.potentiate()`                   | OK     | transmit -> nucleus.integrate -> reinforce (tuning change) -> sprout (new structure generation)   |
| `.prune()`                        | OK     | transmit -> nucleus.integrate -> eliminate (redundancy removal) -> consolidate -> episode.decay() |
| `.reinforce()`                    | OK     | Ch.63 synaptic reinforcement — strengthening existing Area's tuning                               |
| `.sprout()`                       | OK     | Ch.63-64 axonal/dendritic sprouting — growth of new connections/structures                        |
| `.eliminate()`                    | OK     | Ch.64 synaptic elimination — removal of unnecessary connections                                   |
| `.consolidate()`                  | OK     | Ch.67 memory/synaptic consolidation — persistence of integrated results                           |
| `.sanitize()`                     | OK     | Reference integrity validation before store. Subordinate to potentiate/prune Engineering          |
| **Potentiation**                  | OK     | Ch.63 long-term potentiation output model                                                         |
| `.newTuning`                      | OK     | New tuning after reinforcement                                                                    |
| `.newAreas/Neurons/Effectors`     | OK     | Sprouting results. Inner records (NewArea, NewNeuron, NewEffector) implement spec interfaces      |
| **Pruning**                       | OK     | Ch.64 synaptic pruning output model                                                               |
| `.mergedAreas` / `.mergedNeurons` | OK     | sources + result for consolidation targets. Pruning + consolidation structure                     |

### `runtime.integrative`

| Class / Method               | Rating        | Kandel Rationale / Comment                                                                         |
|------------------------------|---------------|----------------------------------------------------------------------------------------------------|
| **EncoderImpl**              | OK            | Implements Encoder. Prompt assembly resides in runtime (nearest to cortical processing)            |
| `.encode(Impulse, Class<?>)` | OK            | Phase-specific template selection via caller dispatch. Spec Encoder already marked [Engineering]   |
| `.perception()`              | OK            | Ch.21 prompt construction for perceptual processing                                                |
| `.relay()`                   | OK            | Ch.21 prompt construction for thalamic relay                                                       |
| `.potentiation()`            | OK            | Ch.63 prompt construction for LTP                                                                  |
| `.pruning()`                 | OK            | Ch.64 prompt construction for pruning                                                              |
| `.defaultMode()`             | OK            | Ch.62 prompt construction for DMN                                                                  |
| **NucleusImpl**              | OK            | Ch.8 somatic integration — async, own thread (refractory period Ch.9)                              |
| `.integrate()`               | OK            | Accumulates signals, fires propagation when threshold reached. Single-thread executor              |
| `executorService`            | [Engineering] | Single-thread ExecutorService. Models refractory period (Ch.9) via sequential execution. Annotated |
| **TransmitterImpl**          | OK            | Implements Transmitter (Part III). Encoder → @Diffusic Service (cleft) → Decoder                   |

### `runtime.mnemonic`

| Class / Method                          | Rating        | Kandel Rationale / Comment                                                                                                                 |
|-----------------------------------------|---------------|--------------------------------------------------------------------------------------------------------------------------------------------|
| **TraceImpl**                           | OK            | Record implementation of Trace                                                                                                             |
| constructor `cue + "@" + Instant.now()` | [Engineering] | Timestamp embedded in cue. Kandel: semantic memory has no temporal context (Ch.65). Accepted as engineering compromise                     |
| **@Episodic**                           | OK            | CDI qualifier. Identifies episodic memory Repository                                                                                       |
| **@Semantic**                           | OK            | CDI qualifier. Identifies semantic memory Repository                                                                                       |
| **EpisodeImpl**                         | OK            | Ch.65-67 episodic memory                                                                                                                   |
| `.encode()`                             | OK            | Memory encoding                                                                                                                            |
| `.retrieve(String)`                     | OK            | Cued recall via repository lookup                                                                                                          |
| `.retrieve()`                           | OK            | Free recall — sorted by timestamp                                                                                                          |
| `.decay()`                              | OK            | Ch.67 sleep consolidation. removeAll for traces exceeding area-proportional capacity. Called from Plasticity.prune()                       |
| `CAPACITY_PER_AREA=10`                  | OK            | Ch.65-67 area-proportional capacity. Hippocampal encoding integrates multiple cortical association areas — capacity scales with area count |
| **KnowledgeImpl**                       | OK            | Ch.65-67 semantic memory                                                                                                                   |
| `.encode()`                             | OK            | Memory encoding + decay(). Ch.66 retroactive interference                                                                                  |
| `.retrieve(String)`                     | OK            | Cued recall via repository lookup                                                                                                          |
| `.retrieve()`                           | OK            | Free recall — sorted by timestamp                                                                                                          |
| `.decay()`                              | OK            | Prefix deduplication. Retains only latest trace per prefix. Corresponds to Ch.66 interference theory                                       |
| `.prefixOf()`                           | OK            | Prefix before `@` in cue. Subordinate to TraceImpl constructor (E11)                                                                       |
| `.timestampOf()`                        | OK            | Parsing of cue-embedded timestamp. Subordinate to TraceImpl constructor (E11)                                                              |
