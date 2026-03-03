# Kandel Compliance Review — specification + runtime

## Summary

**Kandel compliance: 95.6%** — (OK + Eng) / Total = 131 / 137

| Rating    | Count | Description                                    |
|-----------|-------|------------------------------------------------|
| **OK**    | 105   | Kandel-compliant                               |
| **[Eng]** | 26    | Engineering compromise (documented / accepted) |
| **WARN**  | 3     | Divergence from Kandel (needs discussion)      |
| **TODO**  | 3     | Known issue, tracked                           |

## WARN

| #  | Target                    | Description                                                                                                                                    |
|----|---------------------------|------------------------------------------------------------------------------------------------------------------------------------------------|
| W1 | `Percept.location()`      | Kandel's "location" refers to somatotopic/retinotopic maps (Ch.21, 25). AAI uses it for Area name. `origin` or `source` would be more accurate |
| W2 | `DriveImpl.schedule()`    | Random interval (5-11s) lacks documented rationale. DMN oscillation (Ch.62) has specific frequency bands. Documentation recommended            |
| W3 | `EpisodeImpl.CAPACITY=50` | Fixed constant. No dynamic adjustment. Biological episodic memory capacity is context-dependent (Ch.65-67). Acceptable for now, monitoring     |

## TODO

| #  | Target                | Description                                                                                                           |
|----|-----------------------|-----------------------------------------------------------------------------------------------------------------------|
| T1 | `Percept.intensity()` | Ch.21 stimulus intensity coding. Declared in spec but unimplemented — throws UnsupportedOperationException (TODO #26) |
| T2 | `Percept.duration()`  | Ch.25 temporal coding. Declared in spec but unimplemented — throws UnsupportedOperationException (TODO #26)           |

---

## specification

### `specification.neural`

| Class / Method | Rating | Kandel Rationale / Comment                                                                          |
|----------------|--------|-----------------------------------------------------------------------------------------------------|
| **Engravable** | OK     | Ch.63 engram — capability of being "engraved by experience". Accurate base for Area/Neuron/Effector |
| `.name()`      | [Eng]  | Biological neurons have no names. Required for system addressing. Annotated                         |
| **Area**       | OK     | Ch.1/26/27 cortical area — higher-level structure containing neuron groups + effectors              |
| `.tuning()`    | OK     | Ch.26 feature selectivity (Hubel & Wiesel) — Area's response selectivity                            |
| `.neurons()`   | OK     | Area containing neuron groups is anatomically accurate                                              |
| `.effectors()` | OK     | Ch.35-36 motor cortex projects to effectors. Area-level ownership is valid                          |
| **Neuron**     | OK     | Ch.2 individual nerve cell. Has tuning selectivity for response characteristics                     |
| `.tuning()`    | OK     | Ch.26 — single neuron selectivity                                                                   |
| **Effector**   | OK     | Ch.35-36 motor output — projection from motor cortex to muscles/glands                              |
| `.tuning()`    | OK     | Effector's operational characteristics. Corresponds to motor neuron tuning property                 |
| `.fire(Map)`   | [Eng]  | KV map models effector I/O; biology uses synaptic signals. Annotated                                |

### `specification.signaling`

| Class / Method         | Rating | Kandel Rationale / Comment                                                              |
|------------------------|--------|-----------------------------------------------------------------------------------------|
| **Stimulus**           | OK     | Ch.21 sensory stimulus — external sensory input                                         |
| `.input()`             | OK     | Content of sensory stimulus                                                             |
| **Impulse**            | OK     | Ch.7 action potential / nerve impulse — internal signal transmission                    |
| `.signal()`            | OK     | Signal content carried by action potential                                              |
| `.area()`              | [Eng]  | Biology routes by axonal connections. Explicit routing target is engineering. Annotated |
| **Transducer**         | OK     | Ch.21 sensory transduction — conversion at sensory receptors                            |
| `.transduce(Stimulus)` | OK     | Stimulus -> Impulse conversion is transduction itself                                   |
| **Thalamus**           | OK     | Ch.21 thalamic relay — relay nucleus for all sensory input (except olfaction)           |
| `.relay(Impulse)`      | OK     | Ch.21 thalamic relay nuclei — relays impulse to appropriate cortical area               |

### `specification.cognitive`

| Class / Method      | Rating | Kandel Rationale / Comment                                                                                                                      |
|---------------------|--------|-------------------------------------------------------------------------------------------------------------------------------------------------|
| **Cortex**          | OK     | Ch.18/26 cerebral cortex — center for higher cognitive processing                                                                               |
| `.respond(Impulse)` | OK     | "neural response" / "cortical response" — response to afferent impulse (Ch.21, 26)                                                              |
| **Percept**         | OK     | Ch.21 "sensory percept" — conscious perceptual representation resulting from cortical processing                                                |
| `.content()`        | OK     | Perceptual content                                                                                                                              |
| `.location()`       | WARN   | Kandel's location = somatotopic/retinotopic map (Ch.21, 25). AAI uses Area name. Semantic divergence. `origin` or `source` may be more accurate |
| `.intensity()`      | TODO   | Ch.21 stimulus intensity coding. Declared in spec but unimplemented (TODO #26)                                                                  |
| `.duration()`       | TODO   | Ch.25 temporal coding. Declared in spec but unimplemented (TODO #26)                                                                            |

### `specification.synaptic`

| Class / Method                  | Rating | Kandel Rationale / Comment                                                                          |
|---------------------------------|--------|-----------------------------------------------------------------------------------------------------|
| **Encoder**                     | [Eng]  | Ch.8-12 presynaptic encoding. Separation as a discrete step has no biological equivalent. Annotated |
| `.encode(Impulse, Class<?>)`    | [Eng]  | Phase dispatch by caller is engineering. Annotated                                                  |
| **Nucleus**                     | OK     | Ch.8 cell body (soma) — site of input integration and output generation                             |
| `.integrate(Impulse, Class<T>)` | OK     | Ch.8 synaptic integration — EPSP/IPSP integration. Class<T> is engineering (annotated)              |

### `specification.homeostatic`

| Class / Method  | Rating | Kandel Rationale / Comment                                            |
|-----------------|--------|-----------------------------------------------------------------------|
| **Drive**       | OK     | Ch.48 motivated behavior / drive states — internal motivational force |
| `.activate()`   | [Eng]  | DMN has no explicit on/off. Lifecycle hook. Annotated                 |
| `.deactivate()` | [Eng]  | Same as above                                                         |
| **Salience**    | OK     | Ch.62 salience network — attention allocation                         |
| `.orient()`     | OK     | Ch.62 "orienting response" — attention orientation to salient stimuli |
| `.release()`    | OK     | Release of orienting response                                         |
| `.isOriented()` | [Eng]  | State query for DMN suppression. Annotated                            |

### `specification.learning`

| Class / Method         | Rating | Kandel Rationale / Comment                                                                                                        |
|------------------------|--------|-----------------------------------------------------------------------------------------------------------------------------------|
| **Plasticity**         | OK     | Ch.63-64 synaptic plasticity — structural change via LTP/LTD                                                                      |
| `.potentiate(Impulse)` | [Eng]  | Kandel's LTP requires repeated co-activation (Hebb) + NMDA coincidence detection. Simplified to single Impulse trigger. Annotated |
| `.prune()`             | OK     | Ch.64 synaptic elimination / pruning — removal of redundant connections                                                           |

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

| Class / Method                                      | Rating | Kandel Rationale / Comment                      |
|-----------------------------------------------------|--------|-------------------------------------------------|
| **Repository\<I, E\>**                              | [Eng]  | Pure infra SPI. No Kandel equivalent. Justified |
| `.find()` / `.findAll()` / `.store()` / `.remove()` | [Eng]  | Standard repository operations                  |
| `.removeAll(List<String>)`                          | [Eng]  | Batch deletion. Infra for decay()               |
| `.exists()`                                         | [Eng]  | Existence check                                 |
| **Serializer**                                      | [Eng]  | JSON conversion. Infra                          |
| **Service\<I, E\>**                                 | [Eng]  | External service call port. Infra               |
| **Configuration**                                   | [Eng]  | YAML configuration loading. Infra               |

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

| Class / Method                      | Rating | Kandel Rationale / Comment                                                                                 |
|-------------------------------------|--------|------------------------------------------------------------------------------------------------------------|
| **Decision**                        | OK     | Cortical decision-making output. Ch.57 decision making                                                     |
| `.reasoning`                        | [Eng]  | Reasoning rationale. For debugging. Biologically implicit                                                  |
| `.confidence`                       | OK     | Ch.57 decision confidence / signal detection theory                                                        |
| `.process`                          | OK     | Cortical processing mode (confirmed in prior discussion)                                                   |
| `.response` / `.effector` / `.area` | OK     | Output parameters per process type                                                                         |
| **CortexImpl**                      | OK     | Implementation of Cortex                                                                                   |
| `.respond()`                        | OK     | ReentrantLock (=focus) for exclusive execution. Encoder -> Nucleus -> Process dispatch                     |
| `focus (ReentrantLock)`             | OK     | Ch.25/62 attentional bottleneck — attention focuses on one process at a time. Lock representation is valid |
| `processes (Map)`                   | OK     | Cortical processing mode dispatch table                                                                    |
| **PerceptImpl**                     | OK     | Record implementation of Percept                                                                           |
| `.intensity()` / `.duration()`      | TODO   | UnsupportedOperationException (TODO #26)                                                                   |
| **RefractoryGuard**                 | OK     | Ch.7 refractory period — post-firing refractory period. Suppresses consecutive firing of same Effector     |
| `.observe()`                        | OK     | Refractory period after 3 consecutive firings of same effector                                             |
| `.reset()`                          | OK     | Refractory period release                                                                                  |

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

| Class / Method                  | Rating | Kandel Rationale / Comment                                                                                                                               |
|---------------------------------|--------|----------------------------------------------------------------------------------------------------------------------------------------------------------|
| **Urge**                        | OK     | Ch.48 motivated behavior / urge — structured output of internal drive                                                                                    |
| `.aroused`                      | OK     | Ch.48 arousal — arousal/activation state                                                                                                                 |
| `.vocalize`                     | OK     | Broca's area vocalization urge                                                                                                                           |
| `.area` / `.signal`             | OK     | Target and signal content of urge                                                                                                                        |
| **DriveImpl**                   | OK     | Ch.48/62 DMN (default mode network) implementation                                                                                                       |
| `.activate()` / `.deactivate()` | [Eng]  | ScheduledExecutorService lifecycle. Annotated                                                                                                            |
| `.fire()`                       | OK     | salience check -> integrate -> cortex.respond -> Event\<Expression\>. DMN spontaneous firing pattern                                                     |
| `.schedule()`                   | WARN   | 5-11s random interval. Kandel's DMN oscillation (Ch.62) has specific frequency bands. Current implementation is sufficient but documentation recommended |
| **SalienceImpl**                | OK     | AtomicBoolean for orient/release management. Simple and accurate                                                                                         |

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
| `Potentiation.Effector.execution` | [Eng]  | Source code for compilation. Not biologically necessary. For EffectorCompiler               |
| `Potentiation.Effector.areas`     | OK     | List of Areas to which Effector belongs                                                     |
| **Pruning**                       | OK     | Ch.64 synaptic pruning output model                                                         |
| `.mergedAreas` / `.mergedNeurons` | OK     | sources + result for consolidation targets. Pruning + consolidation structure               |

### `runtime.synaptic`

| Class / Method               | Rating | Kandel Rationale / Comment                                                                                     |
|------------------------------|--------|----------------------------------------------------------------------------------------------------------------|
| **EncoderImpl**              | [Eng]  | Prompt assembly — the only case where ACL-adjacent implementation resides in runtime                           |
| `.encode(Impulse, Class<?>)` | [Eng]  | Phase-specific template selection via caller dispatch. Annotated                                               |
| `.perception()`              | OK     | Ch.21 prompt construction for perceptual processing                                                            |
| `.relay()`                   | OK     | Ch.21 prompt construction for thalamic relay                                                                   |
| `.potentiation()`            | OK     | Ch.63 prompt construction for LTP                                                                              |
| `.pruning()`                 | OK     | Ch.64 prompt construction for pruning                                                                          |
| `.drive()`                   | OK     | Ch.48/62 prompt construction for DMN                                                                           |
| `.assemble()`                | [Eng]  | Template composition. `{{guardrails}}` -> `executive_control.md` is engineering but corresponds to PFC (Ch.51) |
| **NucleusImpl**              | OK     | Ch.8 somatic integration                                                                                       |
| `.integrate()`               | OK     | Service (LLM) call -> deserialize -> validate. "input integration -> output generation" pattern                |
| `.validate()`                | [Eng]  | Bean Validation. Not biologically necessary but ensures output quality                                         |

### `runtime.working`

| Class / Method                          | Rating | Kandel Rationale / Comment                                                                                                                 |
|-----------------------------------------|--------|--------------------------------------------------------------------------------------------------------------------------------------------|
| **TraceImpl**                           | OK     | Record implementation of Trace                                                                                                             |
| constructor `cue + "@" + Instant.now()` | [Eng]  | Timestamp embedded in cue. Kandel: semantic memory has no temporal context (Ch.65). Accepted as engineering compromise                     |
| **@Episodic**                           | OK     | CDI qualifier. Identifies episodic memory Repository                                                                                       |
| **@Semantic**                           | OK     | CDI qualifier. Identifies semantic memory Repository                                                                                       |
| **EpisodeImpl**                         | OK     | Ch.65-67 episodic memory                                                                                                                   |
| `.encode()`                             | OK     | Memory encoding                                                                                                                            |
| `.retrieve(String)`                     | OK     | Cued recall — partial cue match + latest timestamp                                                                                         |
| `.retrieve()`                           | OK     | Free recall — sorted by timestamp                                                                                                          |
| `.decay()`                              | OK     | Ch.67 sleep consolidation. removeAll for traces exceeding CAPACITY=50. Called from Plasticity.prune()                                      |
| `CAPACITY=50`                           | WARN   | Fixed constant. No dynamic adjustment. Biological episodic memory capacity is context-dependent (Ch.65-67). Acceptable for now, monitoring |
| **KnowledgeImpl**                       | OK     | Ch.65-67 semantic memory                                                                                                                   |
| `.encode()`                             | OK     | Memory encoding + decay(). Ch.66 retroactive interference                                                                                  |
| `.retrieve(String)`                     | OK     | Cued recall                                                                                                                                |
| `.retrieve()`                           | OK     | Free recall — sorted by timestamp                                                                                                          |
| `.decay()`                              | OK     | Prefix deduplication. Retains only latest trace per prefix. Corresponds to Ch.66 interference theory                                       |
| `.prefixOf()`                           | [Eng]  | Prefix before `@` in cue. For timestamp separation. Engineering                                                                            |
| `.timestampOf()`                        | [Eng]  | Parsing of cue-embedded timestamp. Engineering                                                                                             |
