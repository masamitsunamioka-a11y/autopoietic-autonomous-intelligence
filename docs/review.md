# Kandel Compliance Review — specification + runtime

## Summary

**Kandel compliance: 83.8%** — OK / Total = 109 / 130

| Rating            | Count | Description                                 |
|-------------------|-------|---------------------------------------------|
| **OK**            | 109   | Kandel-compliant                            |
| **[Engineering]** | 15    | Resolvable — resolve to increase compliance |
| **Todo**          | 5     | Unimplemented or planned Kandel feature     |
| **Total**         | 130   |                                             |

Infrastructure SPI (4 items, 7 rows) excluded from Total — inherent to any software system.

## Todo

### Unimplemented

| #  | Target                | Description                                                                                                           |
|----|-----------------------|-----------------------------------------------------------------------------------------------------------------------|
| T1 | `Percept.intensity()` | Ch.21 stimulus intensity coding. Declared in spec but unimplemented — throws UnsupportedOperationException (Todo #26) |
| T2 | `Percept.duration()`  | Ch.25 temporal coding. Declared in spec but unimplemented — throws UnsupportedOperationException (Todo #26)           |

### Kandel roadmap

| #  | Target                        | Description                                                                                  |
|----|-------------------------------|----------------------------------------------------------------------------------------------|
| T3 | `Process` spec promotion      | Ch.21-27 cortical process. Move from runtime.cognitive.processual to specification.cognitive |
| T4 | System consolidation pipeline | Ch.67 system consolidation. Episode → Knowledge promotion (hippocampus → neocortex transfer) |
| T5 | Axonal wiring in spec         | Ch.2, 18 axonal wiring determines signal pathways. Resolves E2 and E3                        |

### [Engineering] 22 items

Resolve to increase Kandel compliance. 15 items are resolvable; 7 are inherent infrastructure.

**Resolvable (15):**

| #   | Target                                 | Category                        |
|-----|----------------------------------------|---------------------------------|
| E1  | `Engravable.name()`                    | spec — system addressing        |
| E2  | `Effector.fire(Map)`                   | spec — KV map I/O               |
| E3  | `Impulse.area()`                       | spec — explicit routing         |
| E4  | `Encoder`                              | spec — discrete encoding step   |
| E5  | `Encoder.encode(Impulse, Class<?>)`    | spec — caller dispatch          |
| E6  | `Nucleus.integrate(Impulse, Class<T>)` | spec — type dispatch            |
| E7  | `Salience.isOriented()`                | spec — boolean state query      |
| E8  | `Decision.reasoning`                   | runtime — debugging rationale   |
| E9  | `PerceptImpl.location`                 | runtime — Area name as location |
| E10 | `DriveImpl.schedule()`                 | runtime — timing approximation  |
| E11 | `EncoderImpl.assemble()`               | runtime — template composition  |
| E12 | `NucleusImpl.validate()`               | runtime — Bean Validation       |
| E13 | `TraceImpl` constructor                | runtime — timestamp in cue      |
| E14 | `KnowledgeImpl.prefixOf()`             | runtime — cue parsing           |
| E15 | `KnowledgeImpl.timestampOf()`          | runtime — cue parsing           |

**Infrastructure SPI (4 items, 7 rows) — inherent to software, not resolvable:**

| #  | Target             | Description                                                       |
|----|--------------------|-------------------------------------------------------------------|
| I1 | `Repository<I, E>` | Persistence SPI (find, findAll, store, remove, removeAll, exists) |
| I2 | `Serializer`       | JSON conversion                                                   |
| I3 | `Service<I, E>`    | External service call port                                        |
| I4 | `Configuration`    | YAML configuration loading                                        |

---

## specification

### `specification.neural`

| Class / Method | Rating        | Kandel Rationale / Comment                                                                          |
|----------------|---------------|-----------------------------------------------------------------------------------------------------|
| **Engravable** | OK            | Ch.63 engram — capability of being "engraved by experience". Accurate base for Area/Neuron/Effector |
| `.name()`      | [Engineering] | Biological neurons have no names. Required for system addressing. Annotated                         |
| **Area**       | OK            | Ch.1/26/27 cortical area — higher-level structure containing neuron groups + effectors              |
| `.tuning()`    | OK            | Ch.26 feature selectivity (Hubel & Wiesel) — Area's response selectivity                            |
| `.neurons()`   | OK            | Area containing neuron groups is anatomically accurate                                              |
| `.effectors()` | OK            | Ch.35-36 motor cortex projects to effectors. Area-level ownership is valid                          |
| **Neuron**     | OK            | Ch.2 individual nerve cell. Has tuning selectivity for response characteristics                     |
| `.tuning()`    | OK            | Ch.26 — single neuron selectivity                                                                   |
| **Effector**   | OK            | Ch.35-36 motor output — projection from motor cortex to muscles/glands                              |
| `.tuning()`    | OK            | Effector's operational characteristics. Corresponds to motor neuron tuning property                 |
| `.fire(Map)`   | [Engineering] | KV map models effector I/O; biology uses synaptic signals. Annotated                                |

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

### `specification.synaptic`

| Class / Method                  | Rating        | Kandel Rationale / Comment                                                                          |
|---------------------------------|---------------|-----------------------------------------------------------------------------------------------------|
| **Encoder**                     | [Engineering] | Ch.8-12 presynaptic encoding. Separation as a discrete step has no biological equivalent. Annotated |
| `.encode(Impulse, Class<?>)`    | [Engineering] | Phase dispatch by caller is engineering. Annotated                                                  |
| **Nucleus**                     | OK            | Ch.8 cell body (soma) — site of input integration and output generation                             |
| `.integrate(Impulse, Class<T>)` | [Engineering] | Ch.8 synaptic integration. Class<T> response dispatches output type; biology has no type dispatch   |

### `specification.homeostatic`

| Class / Method  | Rating        | Kandel Rationale / Comment                                                                                    |
|-----------------|---------------|---------------------------------------------------------------------------------------------------------------|
| **Drive**       | OK            | Ch.48 motivated behavior / drive states. Marker interface; lifecycle is @PostConstruct/@PreDestroy in runtime |
| **Salience**    | OK            | Ch.62 salience network — attention allocation                                                                 |
| `.orient()`     | OK            | Ch.62 "orienting response" — attention orientation to salient stimuli                                         |
| `.release()`    | OK            | Release of orienting response                                                                                 |
| `.isOriented()` | [Engineering] | State query for DMN suppression. Annotated                                                                    |

### `specification.learning`

| Class / Method         | Rating | Kandel Rationale / Comment                                                                      |
|------------------------|--------|-------------------------------------------------------------------------------------------------|
| **Plasticity**         | OK     | Ch.63-64 synaptic plasticity — structural change via LTP/LTD                                    |
| `.potentiate(Impulse)` | OK     | Single Impulse = NMDA coincidence trigger (Ch.63); accumulated co-activation carried by Episode |
| `.prune()`             | OK     | Ch.64 synaptic elimination / pruning — removal of redundant connections                         |

### `specification.working`

| Class / Method      | Rating | Kandel Rationale / Comment                                                 |
|---------------------|--------|----------------------------------------------------------------------------|
| **Trace**           | OK     | Ch.65 memory trace (engram) — physical substrate of memory                 |
| `.cue()`            | OK     | Ch.65 retrieval cue — cue for memory retrieval                             |
| `.content()`        | OK     | Memory content held by trace                                               |
| **Memory**          | OK     | Ch.65-67 memory system abstraction                                         |
| `.encode(Trace)`    | OK     | Ch.65 memory encoding — encoding of new memories                           |
| `.retrieve(String)` | OK     | Ch.65 cued recall — recall via presented cue                               |
| `.retrieve()`       | OK     | Ch.65 free recall — recall of all memories                                 |
| `.decay()`          | OK     | Ch.65 forgetting / decay theory — memory decay over time                   |
| **Episode**         | OK     | Ch.65-67 episodic memory (Tulving) — autobiographical, time-stamped memory |
| **Knowledge**       | OK     | Ch.65-67 semantic memory (Tulving) — general world knowledge               |

---

## runtime

### `runtime` (root — SPI)

| Class / Method                                      | Rating        | Kandel Rationale / Comment                      |
|-----------------------------------------------------|---------------|-------------------------------------------------|
| **Repository\<I, E\>**                              | [Engineering] | Pure infra SPI. No Kandel equivalent. Justified |
| `.find()` / `.findAll()` / `.store()` / `.remove()` | [Engineering] | Standard repository operations                  |
| `.removeAll(List<String>)`                          | [Engineering] | Batch deletion. Infra for decay()               |
| `.exists()`                                         | [Engineering] | Existence check                                 |
| **Serializer**                                      | [Engineering] | JSON conversion. Infra                          |
| **Service\<I, E\>**                                 | [Engineering] | External service call port. Infra               |
| **Configuration**                                   | [Engineering] | YAML configuration loading. Infra               |

### `runtime.signaling`

| Class / Method     | Rating | Kandel Rationale / Comment                                                                                                      |
|--------------------|--------|---------------------------------------------------------------------------------------------------------------------------------|
| **StimulusImpl**   | OK     | Record implementation of Stimulus                                                                                               |
| **ImpulseImpl**    | OK     | Record implementation of Impulse                                                                                                |
| **TransducerImpl** | OK     | Ch.21 transduction                                                                                                              |
| `.transduce()`     | OK     | Converts Stimulus.input() to signal, area=null (pre-relay). Accurate                                                            |
| **ThalamusImpl**   | OK     | Ch.21 thalamic relay                                                                                                            |
| `.relay()`         | OK     | Determines Area via Encoder + Nucleus, returns impulse with routing. Corresponds to thalamic relay                              |
| **Projection**     | OK     | Ch.18 "thalamic projection" — output of thalamic-to-cortical projection. reasoning + confidence + area is Nucleus output format |

### `runtime.cognitive`

| Class / Method                      | Rating        | Kandel Rationale / Comment                                                                                 |
|-------------------------------------|---------------|------------------------------------------------------------------------------------------------------------|
| **Decision**                        | OK            | Cortical decision-making output. Ch.57 decision making                                                     |
| `.reasoning`                        | [Engineering] | Reasoning rationale. For debugging. Biologically implicit                                                  |
| `.confidence`                       | OK            | Ch.57 decision confidence / signal detection theory                                                        |
| `.process`                          | OK            | Cortical processing mode (confirmed in prior discussion)                                                   |
| `.response` / `.effector` / `.area` | OK            | Output parameters per process type                                                                         |
| **CortexImpl**                      | OK            | Implementation of Cortex                                                                                   |
| `.respond()`                        | OK            | ReentrantLock (=focus) for exclusive execution. Encoder -> Nucleus -> Process dispatch                     |
| `focus (ReentrantLock)`             | OK            | Ch.25/62 attentional bottleneck — attention focuses on one process at a time. Lock representation is valid |
| `processes (Map)`                   | OK            | Cortical processing mode dispatch table                                                                    |
| **PerceptImpl**                     | [Engineering] | location maps Area name; biology uses somatotopic/retinotopic coordinates (Ch.21, 25). Annotated           |
| `.intensity()` / `.duration()`      | Todo          | UnsupportedOperationException (Todo #26)                                                                   |
| **RefractoryGuard**                 | OK            | Ch.7 refractory period — post-firing refractory period. Suppresses consecutive firing of same Effector     |
| `.observe()`                        | OK            | Refractory period after 3 consecutive firings of same effector                                             |
| `.reset()`                          | OK            | Refractory period release                                                                                  |

### `runtime.cognitive.processual`

| Class / Method               | Rating | Kandel Rationale / Comment                                                                                       |
|------------------------------|--------|------------------------------------------------------------------------------------------------------------------|
| **Process** (interface)      | OK     | Command pattern for cortical processing modes. CDI dispatch via qualifier annotations                            |
| `.handle(Impulse, Decision)` | OK     | Receives impulse and decision, returns percept                                                                   |
| **Vocalize**                 | OK     | Ch.62 Broca's area — language output. Encodes to Episode and returns                                             |
| **Fire**                     | OK     | Ch.35-36 motor execution — Effector firing. Encodes results to Knowledge. RefractoryGuard for refractory control |
| **Potentiate**               | OK     | Ch.63 LTP trigger — calls Plasticity.potentiate() + prune()                                                      |
| **Project**                  | OK     | Ch.18 cortico-cortical projection — projection to another Area. SYSTEM WARNING + re-respond on missing area      |
| **Inhibit**                  | OK     | Ch.24 cortical inhibition / prefrontal inhibitory control — explicit safety boundary termination                 |

### `runtime.homeostatic`

| Class / Method                  | Rating        | Kandel Rationale / Comment                                                                                                           |
|---------------------------------|---------------|--------------------------------------------------------------------------------------------------------------------------------------|
| **Urge**                        | OK            | Ch.48 motivated behavior / urge — structured output of internal drive                                                                |
| `.aroused`                      | OK            | Ch.48 arousal — arousal/activation state                                                                                             |
| `.vocalize`                     | OK            | Broca's area vocalization urge                                                                                                       |
| `.area` / `.signal`             | OK            | Target and signal content of urge                                                                                                    |
| **DriveImpl**                   | OK            | Ch.48/62 DMN (default mode network) implementation                                                                                   |
| `.activate()` / `.deactivate()` | OK            | @PostConstruct/@PreDestroy — standard CDI lifecycle for schedule()                                                                   |
| `.fire()`                       | OK            | salience check -> integrate -> cortex.respond -> Event\<Expression\>. DMN spontaneous firing pattern                                 |
| `Event<Expression>`             | OK            | CDI Event/Observer for Drive output delivery. Replaces direct stdout write. Proper adapter-layer separation                          |
| `.schedule()`                   | [Engineering] | DMN infra-slow oscillation 0.01-0.1 Hz = 10-100s (Ch.62). 10-30s balances Kandel fidelity with interactive responsiveness. Annotated |
| **SalienceImpl**                | OK            | AtomicBoolean for orient/release management. Simple and accurate                                                                     |

### `runtime.learning`

| Class / Method                    | Rating | Kandel Rationale / Comment                                                                  |
|-----------------------------------|--------|---------------------------------------------------------------------------------------------|
| **PlasticityImpl**                | OK     | Ch.63-64 LTP/LTD + structural plasticity                                                    |
| `.potentiate()`                   | OK     | integrate -> reinforce (tuning change) -> sprout (new structure generation)                 |
| `.prune()`                        | OK     | integrate -> eliminate (redundancy removal) -> consolidate (integration) -> episode.decay() |
| `.reinforce()`                    | OK     | Ch.63 synaptic reinforcement — strengthening existing Area's tuning                         |
| `.sprout()`                       | OK     | Ch.63-64 axonal/dendritic sprouting — growth of new connections/structures                  |
| `.eliminate()`                    | OK     | Ch.64 synaptic elimination — removal of unnecessary connections                             |
| `.consolidate()`                  | OK     | Ch.67 memory/synaptic consolidation — persistence of integrated results                     |
| **Potentiation**                  | OK     | Ch.63 long-term potentiation output model                                                   |
| `.newTuning`                      | OK     | New tuning after reinforcement                                                              |
| `.newAreas/Neurons/Effectors`     | OK     | Sprouting results. Inner records (Area, Neuron, Effector) implement Engravable              |
| **Pruning**                       | OK     | Ch.64 synaptic pruning output model                                                         |
| `.mergedAreas` / `.mergedNeurons` | OK     | sources + result for consolidation targets. Pruning + consolidation structure               |

### `runtime.synaptic`

| Class / Method               | Rating        | Kandel Rationale / Comment                                                                                     |
|------------------------------|---------------|----------------------------------------------------------------------------------------------------------------|
| **EncoderImpl**              | OK            | Implements Encoder. Prompt assembly resides in runtime (nearest to cortical processing)                        |
| `.encode(Impulse, Class<?>)` | OK            | Phase-specific template selection via caller dispatch. Spec Encoder already marked [Engineering]               |
| `.perception()`              | OK            | Ch.21 prompt construction for perceptual processing                                                            |
| `.relay()`                   | OK            | Ch.21 prompt construction for thalamic relay                                                                   |
| `.potentiation()`            | OK            | Ch.63 prompt construction for LTP                                                                              |
| `.pruning()`                 | OK            | Ch.64 prompt construction for pruning                                                                          |
| `.drive()`                   | OK            | Ch.48/62 prompt construction for DMN                                                                           |
| `.assemble()`                | [Engineering] | Template composition. `{{guardrails}}` -> `executive_control.md` is engineering but corresponds to PFC (Ch.51) |
| **NucleusImpl**              | OK            | Ch.8 somatic integration                                                                                       |
| `.integrate()`               | OK            | Service (LLM) call -> deserialize -> validate. "input integration -> output generation" pattern                |
| `.validate()`                | [Engineering] | Bean Validation. Not biologically necessary but ensures output quality                                         |

### `runtime.working`

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
| `.prefixOf()`                           | [Engineering] | Prefix before `@` in cue. For timestamp separation. Engineering                                                                            |
| `.timestampOf()`                        | [Engineering] | Parsing of cue-embedded timestamp. Engineering                                                                                             |
