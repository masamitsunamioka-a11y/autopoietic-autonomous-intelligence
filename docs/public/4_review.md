# Review

- [Summary](#summary)
    - [Todo](#todo)
    - [Engineering](#engineering)
- [Details](#details)
    - [Specification](#specification)
    - [Runtime](#runtime)

## Summary

**Kandel compliance: 95.4%** - OK / Total = 124 / 130

| Rating            | Count | Description                                  |
|-------------------|-------|----------------------------------------------|
| **OK**            | 124   | Kandel-compliant                             |
| **[Engineering]** | 6     | Kandel deviations with engineering rationale |
| **Total**         | 130   |                                              |

No Kandel Equivalent (I1-I6) excluded from Total.

**Common prerequisites** (excluded from Engineering count):

- **Evans DDD** - `Entity`, `AggregateRoot` in root package. Domain modeling foundation.
- **LLM output convention** - `reasoning` field in LLM-generated records (Decision, Projection,
  Fluctuation, Compensation, Conservation, Consolidation). Inherent to LLM structured output; not a Kandel compromise.
  `confidence` is mapped to `amplitude` (Ch.8-12 postsynaptic potential amplitude) by ACL - now Kandel-compliant.
- **LLM pipeline** - TransmissionService internal encoding/decoding (anticorruption).
  Prompt assembly and JSON deserialization have no Kandel equivalent but are implementation-only.

### Todo

#### Kandel Roadmap

| #  | Target               | Description                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                           |
|----|----------------------|-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| T6 | Per-neuron Nucleus   | Ch.9, 12, 51 each neuron has its own soma for synaptic integration. Current NucleusImpl is a singleton. Includes sleep/waking threshold (burst vs tonic firing). Summation (Ch.9, 12): Nucleus core behavior - amplitude accumulation -> threshold -> fire (shouldFire()). Habituation (Ch.63): separate class - presynaptic amplitude decay, used by NucleusImpl to modulate input before summation. Flow: Potential -> [Habituation: decay] -> [Summation: accumulate] -> shouldFire(). Separate research direction |
| T7 | NREM/REM sleep cycle | Ch.51 NREM/REM ~90min cycle. NREM: faithful hippocampal replay -> consolidate(). REM: recombination of Episode fragments -> novel Knowledge (dreaming). Separate research direction                                                                                                                                                                                                                                                                                                                                   |
| T8 | Temperament          | Ch.48-49 emotion, amygdala, reward system. Individual personality traits as neural architecture. Personality expressed through Neuron tuning (e.g. BabyPersonalityNeuron) + natural accumulation via Knowledge. Separate research direction                                                                                                                                                                                                                                                                           |

### Engineering

Kandel deviations justified by Java / software constraints. Resolve to increase compliance.

#### Kandel Deviations with Engineering Rationale

| #   | Target                              | Reason                                                                                             |
|-----|-------------------------------------|----------------------------------------------------------------------------------------------------|
| E1  | `Effector.fire(Map)`                | `Map<String, Object>` for I/O; biology: synaptic signals (Ch.35-36)                                |
| E3  | `Nucleus.integrate(T, Consumer<T>)` | `Consumer<T>` callback; biology: axonal propagation (Ch.3, 4)                                      |
| E8  | `DefaultImpl.activate()`            | `ScheduledExecutorService` 10-30s timer; biology: infra-slow oscillation 0.01-0.1 Hz (Ch.62)       |
| E9  | `SleepImpl.activate()`              | `ScheduledExecutorService` 60-120s timer; biology: hours-scale transition (Ch.51)                  |
| E11 | `Habituation`                       | Count-based threshold; biology: synaptic efficacy modulation (Ch.63). Migrate to NucleusImpl in T6 |
| E16 | `ThalamusImpl.activate()`           | `ScheduledExecutorService` 10-20s timer; biology: seconds-scale spindle/delta bursts (Ch.51)       |

#### No Kandel Equivalent

| #  | Target                                | Description                                                         |
|----|---------------------------------------|---------------------------------------------------------------------|
| I1 | `Entity`                              | Evans DDD identity                                                  |
| I2 | `AggregateRoot`                       | Evans DDD aggregate root. Extends Entity                            |
| I3 | `Repository<T extends AggregateRoot>` | Read-only query SPI (find, findAll, exists). CQRS                   |
| I4 | `Service<I, O>`                       | External service call port                                          |
| I5 | `Command`                             | CQRS command marker interface (Greg Young)                          |
| I6 | `CommandPublisher`                    | CQRS command bus - publishes commands to handlers                   |
| I7 | `Message`                             | CQRS message root - common parent of Command and Event (Greg Young) |

---

## Details

### Specification

#### `specification.autopoietic`

| Class / Method         | Rating | M&V Rationale / Comment                                                      |
|------------------------|--------|------------------------------------------------------------------------------|
| **Autopoiesis**        | OK     | M&V autopoiesis - self-production and maintenance of organizational identity |
| `.compensate(Impulse)` | OK     | M&V perturbation response - detect capability gaps, generate new structures  |
| `.conserve()`          | OK     | M&V adaptation preservation - consolidate/eliminate redundant structures     |

#### `specification.cognitive`

| Class / Method      | Rating | Kandel Rationale / Comment                                                                       |
|---------------------|--------|--------------------------------------------------------------------------------------------------|
| **Cortex**          | OK     | Ch.18/26 cerebral cortex - center for higher cognitive processing                                |
| `.respond(Impulse)` | OK     | "neural response" / "cortical response" - response to afferent impulse (Ch.21, 26)               |
| **Percept**         | OK     | Ch.21 "sensory percept" - conscious perceptual representation resulting from cortical processing |
| `.content()`        | OK     | Perceptual content                                                                               |
| `.location()`       | OK     | Ch.21, 25 somatotopic/retinotopic map location. [Engineering] on PerceptImpl                     |
| `.intensity()`      | OK     | Ch.21 stimulus intensity coding. Mapped from Potential amplitude                                 |
| `.duration()`       | OK     | Ch.25 temporal coding. Measured from respond() start to Percept creation                         |

#### `specification.homeostatic`

| Class / Method    | Rating | Kandel Rationale / Comment                                                                              |
|-------------------|--------|---------------------------------------------------------------------------------------------------------|
| **Drive**         | OK     | Ch.48 homeostatic drive states. Marker interface                                                        |
| **Sleep**         | OK     | Ch.51, 65-67 sleep and memory consolidation. Lifecycle managed by @PostConstruct/@PreDestroy in runtime |
| **Arousal**       | OK     | Ch.51 ascending arousal system (LC, Raphe, TMN, basal forebrain). Saper flip-flop with Sleep            |
| `.project()`      | OK     | Ch.51 ascending arousal tonic projection - activates wake-promoting neural populations                  |
| `.isProjecting()` | OK     | Ch.51 ascending arousal tonic projection state. Other neurons detect its presence/absence               |
| **Modulator**     | OK     | Ch.51 neuromodulator substance. Marker interface. Diffuse volume-transmission signaling                 |

#### `specification.mnemonic`

| Class / Method      | Rating | Kandel Rationale / Comment                                                                |
|---------------------|--------|-------------------------------------------------------------------------------------------|
| **Trace**           | OK     | Ch.65 memory trace (engram) - physical substrate of memory                                |
| `.id()`             | OK     | Ch.65 retrieval cue - cue for memory retrieval                                            |
| `.content()`        | OK     | Memory content held by trace                                                              |
| `.encode(Trace)`    | OK     | Ch.65 memory encoding - encoding of new memories (declared on Episode/Knowledge directly) |
| `.retrieve(String)` | OK     | Ch.65 cued recall - recall via presented cue                                              |
| `.retrieve()`       | OK     | Ch.65 free recall - recall of all memories                                                |
| `.decay()`          | OK     | Ch.65 forgetting / decay theory - memory decay over time                                  |
| **Episode**         | OK     | Ch.65-67 episodic memory (Tulving) - autobiographical, time-stamped memory                |
| `.consolidate()`    | OK     | Ch.65-67 hippocampal replay - episodic->semantic transfer during sleep                    |
| **Knowledge**       | OK     | Ch.65-67 semantic memory (Tulving) - general world knowledge                              |
| **Engram**          | OK     | Ch.65-67 memory engram - Trace envelope with synaptic weight (strength)                   |
| `.strength()`       | OK     | Ch.65-67 synaptic weight - strength of the memory trace encoding                          |
| `.trace()`          | OK     | Ch.65 memory trace - the underlying Trace wrapped by this engram                          |

#### `specification.networking`

| Class / Method  | Rating | Kandel Rationale / Comment                                                                             |
|-----------------|--------|--------------------------------------------------------------------------------------------------------|
| **Default**     | OK     | Ch.62 DMN - default mode network. Marker interface; lifecycle is @PostConstruct/@PreDestroy in runtime |
| **Salience**    | OK     | Ch.63 salience network - attention allocation (Seeley 2007)                                            |
| `.orient()`     | OK     | Ch.63 "orienting response" - attention orientation to salient stimuli                                  |
| `.inhibiting()` | OK     | Ch.63 SN inhibitory output to DMN. DMN checks this to know if suppressed                               |

#### `specification.neural`

| Class / Method         | Rating        | Kandel Rationale / Comment                                                             |
|------------------------|---------------|----------------------------------------------------------------------------------------|
| **Area**               | OK            | Ch.1/26/27 cortical area - higher-level structure containing neuron groups + effectors |
| `.tuning()`            | OK            | Ch.26 feature selectivity (Hubel & Wiesel) - Area's response selectivity               |
| `.neurons()`           | OK            | Area containing neuron groups is anatomically accurate                                 |
| `.effectors()`         | OK            | Ch.35-36 motor cortex projects to effectors. Area-level ownership is valid             |
| **Neuron**             | OK            | Ch.2 individual nerve cell. Has tuning selectivity for response characteristics        |
| `.tuning()`            | OK            | Ch.26 - single neuron selectivity                                                      |
| **Effector**           | OK            | Ch.35-36 motor output - projection from motor cortex to muscles/glands                 |
| `.program()`           | OK            | Ch.38-39 motor program - coordinated action patterns for motoneurons                   |
| `.fire(Map)`           | [Engineering] | KV map models effector I/O; biology uses synaptic signals. Annotated                   |
| **Receptor**           | OK            | Ch.21 sensory receptor - transduction of external stimuli into neural signals          |
| `.transduce(Stimulus)` | OK            | Ch.21 sensory transduction - converts stimulus energy into neural impulse              |

#### `specification.signaling`

| Class / Method    | Rating | Kandel Rationale / Comment                                                          |
|-------------------|--------|-------------------------------------------------------------------------------------|
| **Stimulus**      | OK     | Ch.21 sensory stimulus - external input to sensory receptor                         |
| `.energy()`       | OK     | Ch.21 stimulus energy - the physical energy to be transduced                        |
| **Impulse**       | OK     | Ch.7 action potential / nerve impulse - internal signal transmission                |
| `.signal()`       | OK     | Signal content carried by action potential. String type - neural impulse uniformity |
| `.afferent()`     | OK     | Ch.2, 7 - origin of signal (labeled line principle). Source pathway identifier      |
| `.efferent()`     | OK     | Ch.2, 7 - destination of signal propagation. String (Area id) set by Thalamus relay |
| **Thalamus**      | OK     | Ch.21 thalamic relay - relay nucleus for all sensory input (except olfaction)       |
| `.relay(Impulse)` | OK     | Ch.21 thalamic relay nuclei - relays impulse to appropriate cortical area           |
| `.burst()`        | OK     | Ch.51 thalamic burst firing - sleep spindles drive consolidation, promotion, decay  |

#### `specification.synaptic`

| Class / Method               | Rating        | Kandel Rationale / Comment                                                                                         |
|------------------------------|---------------|--------------------------------------------------------------------------------------------------------------------|
| **Synapse**                  | OK            | Ch.8 synaptic junction. Marker interface. Conceptual flow: transduce -> diffuse -> transduce                       |
| **Transmitter**              | OK            | Kandel Part III: neurotransmitter substance. Marker interface. Point-to-point synaptic signaling                   |
| **Potential**                | OK            | Ch.8, 12 postsynaptic potential - graded result of somatic integration. Marker interface                           |
| **Nucleus**                  | OK            | Ch.8 cell body (soma) - site of input integration and output generation                                            |
| `.integrate(T, Consumer<T>)` | [Engineering] | Ch.12 synaptic integration + Ch.3,4 axonal projection. Consumer\<T\> models downstream firing. T extends Potential |

### Runtime

#### `runtime` (root - SPI)

| Class / Method                            | Rating | Kandel Rationale / Comment                 |
|-------------------------------------------|--------|--------------------------------------------|
| **Repository\<T extends AggregateRoot\>** | I3     | No Kandel equivalent. Read-only (CQRS)     |
| `.find()` / `.findAll()` / `.exists()`    | I3     | Query-side repository operations           |
| **Command**                               | I5     | CQRS command marker interface              |
| **CommandPublisher**                      | I6     | CQRS command bus                           |
| **Message** *(specification)*             | I7     | CQRS message root. Command extends Message |
| `.publish(T)`                             | I6     | Fire command to CommandHandlers            |
| **Service\<I, O\>**                       | I4     | External service call port                 |

#### `runtime.autopoietic`

| Class / Method                    | Rating | M&V / Kandel Rationale / Comment                                                 |
|-----------------------------------|--------|----------------------------------------------------------------------------------|
| **AutopoiesisImpl**               | OK     | M&V autopoiesis. CQRS: compensate/conserve publish Commands via CommandPublisher |
| `.compensate()`                   | OK     | Transmitter -> Nucleus -> CommandPublisher.publish(CompensateNeural)             |
| `.conserve()`                     | OK     | Transmitter -> Nucleus -> CommandPublisher.publish(ConserveNeural)               |
| **CompensateNeural**              | I5     | CQRS command record. Wraps Compensation for neural structure creation            |
| **ConserveNeural**                | I5     | CQRS command record. Wraps Conservation for neural structure pruning             |
| **Compensation**                  | OK     | M&V perturbation response output model                                           |
| `.newTuning`                      | OK     | New tuning after reinforcement                                                   |
| `.newAreas/Neurons/Effectors`     | OK     | Sprouting results. NewArea, NewNeuron, NewEffector implement spec interfaces     |
| **Conservation**                  | OK     | M&V adaptation preservation output model                                         |
| `.mergedAreas` / `.mergedNeurons` | OK     | sources + result for consolidation targets                                       |

#### `runtime.cognitive`

| Class / Method                      | Rating        | Kandel Rationale / Comment                                                                                          |
|-------------------------------------|---------------|---------------------------------------------------------------------------------------------------------------------|
| **Decision**                        | OK            | Cortical decision-making output. Ch.57 decision making                                                              |
| `.reasoning`                        | OK            | LLM output convention                                                                                               |
| `.amplitude`                        | OK            | Ch.8-12 postsynaptic potential amplitude. LLM `confidence` mapped by ACL                                            |
| `.process`                          | OK            | Cortical processing mode                                                                                            |
| `.response` / `.effector` / `.area` | OK            | Output parameters per process type                                                                                  |
| **CortexImpl**                      | OK            | Implementation of Cortex. Episode encode at entry. Fires Event\<Potential\> collateral to Salience (Ch.63)          |
| `.respond()`                        | OK            | Void. Mode-based Episode encode -> Transmitter -> Nucleus -> switch dispatch (potentiate/project/execute)           |
| `.execute()`                        | OK            | Unified Effector dispatch. VOCALIZE/INHIBIT -> Percept (CEN end). FIRE -> Episode + re-entry (Ch.33 efference copy) |
| `.project()`                        | OK            | Ch.18 cortico-cortical projection - projection to another Area. SYSTEM WARNING + re-respond on missing area         |
| `.potentiate()`                     | OK            | M&V compensation trigger - calls Autopoiesis.compensate()                                                           |
| **PerceptImpl**                     | OK            | location = impulse.efferent() (Area id). Ch.21, 25 - cortical location of processing                                |
| `.intensity()`                      | OK            | Ch.21 amplitude -> intensity mapping                                                                                |
| `.duration()`                       | OK            | Ch.25 respond() start to Percept creation                                                                           |
| **Habituation**                     | [Engineering] | Habituation (Ch.63); count-based threshold - biology modulates synaptic efficacy. T6 で NucleusImpl に移植              |
| `.habituated(Decision)`             | OK            | Check if habituated for this Decision's process/effector                                                            |

#### `runtime.homeostatic`

| Class / Method                  | Rating        | Kandel Rationale / Comment                                                                                                 |
|---------------------------------|---------------|----------------------------------------------------------------------------------------------------------------------------|
| **SleepImpl**                   | OK            | Ch.51, 65-67 sleep - memory consolidation during sleep                                                                     |
| `.activate()` / `.deactivate()` | [Engineering] | @PostConstruct/@PreDestroy - standard CDI lifecycle. Timed wake trigger (60-120s); biology: hours-scale transition (Ch.51) |
| `.sleep()`                      | OK            | Arousal gate: skip when projecting. Delegates to Arousal.project() (internal reset). VLPO sleep-wake transition (Ch.51)    |
| **ArousalImpl**                 | OK            | Ch.51 ascending arousal system. Saper flip-flop: AtomicBoolean projecting + AtomicInteger pressure                         |
| `.project()`                    | OK            | Starts arousal projection (projecting=true). Called by SleepImpl after reset(). Ch.51 ascending arousal activation         |
| `.receive(Modulator)`           | OK            | @Observes Modulator - postsynaptic reception. Increments pressure. At threshold: projecting=false (flip-flop). Ch.51       |
| `.isProjecting()`               | OK            | Returns projecting state. Boolean state query. Spec declares [Engineering]; runtime simply implements                      |
| `.reset()`                      | OK            | Adenosine clearance (pressure=0). Prerequisite for project(). Called by SleepImpl during sleep-wake transition             |
| **ModulatorImpl**               | OK            | Record implementation of Modulator. Fired by NucleusImpl after integrate()                                                 |
| **DriveImpl**                   | OK            | Ch.48 homeostatic drive. Empty implementation (marker)                                                                     |

#### `runtime.mnemonic`

| Class / Method      | Rating | Kandel Rationale / Comment                                                                                                          |
|---------------------|--------|-------------------------------------------------------------------------------------------------------------------------------------|
| **TraceImpl**       | OK     | Pure record box (id + content). Timestamp managed by ACL                                                                            |
| **@Episodic**       | OK     | CDI qualifier. Identifies episodic memory Repository                                                                                |
| **@Semantic**       | OK     | CDI qualifier. Identifies semantic memory Repository                                                                                |
| **EpisodeImpl**     | OK     | Ch.65-67 episodic memory. CQRS: encode/decay publish Commands                                                                       |
| `.encode()`         | OK     | Memory encoding via CommandPublisher.publish(EncodeEpisode)                                                                         |
| `.retrieve(String)` | OK     | Cued recall via read-only Repository                                                                                                |
| `.retrieve()`       | OK     | Free recall - sorted by timestamp                                                                                                   |
| `.decay()`          | OK     | Ch.67 sleep consolidation via CommandPublisher.publish(DecayEpisode). Called from Thalamus.burst()                                  |
| `.consolidate()`    | OK     | Ch.65-67 hippocampal replay. Transmitter -> Nucleus pipeline extracts insights from Episode into Knowledge.encode()                 |
| **Consolidation**   | OK     | Ch.65-67 memory consolidation. LLM output record for consolidate(). reasoning + amplitude + insights (extracted semantic knowledge) |
| **EncodeEpisode**   | I5     | CQRS command record. Wraps Trace for encoding                                                                                       |
| **DecayEpisode**    | I5     | CQRS command record. Triggers episodic memory decay                                                                                 |
| **KnowledgeImpl**   | OK     | Ch.65-67 semantic memory. CQRS: encode/decay publish Commands                                                                       |
| `.encode()`         | OK     | Memory encoding via CommandPublisher.publish(EncodeKnowledge). Ch.66 retroactive interference                                       |
| `.retrieve(String)` | OK     | Cued recall via read-only Repository                                                                                                |
| `.retrieve()`       | OK     | Free recall - sorted by timestamp                                                                                                   |
| `.decay()`          | OK     | Prefix deduplication via CommandPublisher.publish(DecayKnowledge). Ch.66 interference theory                                        |
| **EncodeKnowledge** | I5     | CQRS command record. Wraps Trace for encoding                                                                                       |
| **DecayKnowledge**  | I5     | CQRS command record. Triggers semantic memory decay                                                                                 |

#### `runtime.networking`

| Class / Method                  | Rating        | Kandel Rationale / Comment                                                                                                     |
|---------------------------------|---------------|--------------------------------------------------------------------------------------------------------------------------------|
| **ThalamusImpl**                | OK            | Ch.21 thalamic relay + Ch.51 intrinsic oscillation. Self-governs burst via Arousal state                                       |
| `.relay()`                      | OK            | Void. Determines Area via Transmitter + Nucleus, fires Cortex as axonal projection. Thalamic relay                             |
| `.burst()`                      | OK            | Ch.51 thalamic burst firing. Spindle -> Nucleus -> conserve/consolidate/decay. Sleep consolidation                             |
| `.oscillate()`                  | OK            | Ch.51 intrinsic oscillation. Periodically checks Arousal; bursts when not projecting                                           |
| `.activate()`                   | [Engineering] | Timed oscillation (10-20s). Biology: seconds-scale spindle bursts (Ch.51); interval is engineering compromise                  |
| **Projection**                  | OK            | Ch.18 "thalamic projection" - output of thalamic-to-cortical projection. reasoning + amplitude + area is Nucleus output format |
| **Spindle**                     | OK            | Ch.51 sleep spindle - empty record. Passed directly to Nucleus.integrate() (no transmission needed)                            |
| **Fluctuation**                 | OK            | Ch.62 infra-slow fluctuation - structured output of DMN spontaneous activity                                                   |
| `.aroused`                      | OK            | Ch.62 fluctuation exceeds threshold - activation state                                                                         |
| `.area` / `.signal`             | OK            | Target and signal content of fluctuation                                                                                       |
| **DefaultImpl**                 | OK            | Ch.62 DMN (default mode network) implementation. Arousal gate: skip when sleeping                                              |
| `.activate()` / `.deactivate()` | [Engineering] | @PostConstruct/@PreDestroy - `ScheduledExecutorService` 10-30s timer; biology: infra-slow oscillation 0.01-0.1 Hz (Ch.62)      |
| `.fire()`                       | OK            | Transmitter -> nucleus.integrate -> salience.orient -> thalamus.relay (DMN mode). Thalamus determines routing (Ch.21)          |
| **SalienceImpl**                | OK            | Monitors CEN activity via collateral (@Observes Potential). Timer-based idle detection -> release. inhibiting() for DMN gate   |

#### `runtime.neural`

| Class / Method   | Rating | Kandel Rationale / Comment                                                                                       |
|------------------|--------|------------------------------------------------------------------------------------------------------------------|
| **ReceptorImpl** | OK     | Ch.21 sensory transduction. Salience orient -> Thalamus relay (CEN mode)                                         |
| `.transduce()`   | OK     | Sensory processing pipeline: fire stimulus event, orient salience, relay to thalamus (CEN), await CEN completion |

#### `runtime.signaling`

| Class / Method   | Rating | Kandel Rationale / Comment                                                                                |
|------------------|--------|-----------------------------------------------------------------------------------------------------------|
| **StimulusImpl** | OK     | Record implementation of Stimulus                                                                         |
| **ImpulseImpl**  | OK     | Record(signal, afferent, efferent, mode). Mode(CEN/DMN) is runtime-only (Ch.19); efferent set by Thalamus |

#### `runtime.synaptic`

| Class / Method    | Rating | Kandel Rationale / Comment                                                              |
|-------------------|--------|-----------------------------------------------------------------------------------------|
| **NucleusImpl**   | OK     | Ch.12 somatic integration - cached thread pool for concurrent callers                   |
| `.integrate()`    | OK     | Accumulates signals, fires propagation when threshold reached. Fires Event\<Modulator\> |
| `executorService` | OK     | Cached thread pool for concurrent callers. Per-neuron soma is T6 research direction     |
| **@Releasic**     | OK     | CDI qualifier. Ch.8 vesicle release step. Identifies TransmissionService (ACL)          |
| **@Diffusic**     | OK     | CDI qualifier. Ch.8 synaptic cleft diffusion step. Identifies TransmissionService (ACL) |
| **@Bindic**       | OK     | CDI qualifier. Ch.8 receptor binding step. Identifies TransmissionService (ACL)         |
