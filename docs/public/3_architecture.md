# Architecture

- [Signal Flow](#signal-flow)
    - [Synaptic Transmission and Synaptic Integration](#synaptic-transmission-and-synaptic-integration)
    - [CEN - Central Executive Network](#cen---central-executive-network)
    - [DMN - Default Mode Network](#dmn---default-mode-network)
    - [Sleep - Memory Consolidation](#sleep---memory-consolidation)
    - [SN - Salience Network](#sn---salience-network)
    - [Autopoiesis - Compensation](#autopoiesis---compensation)
    - [Autopoiesis - Conservation](#autopoiesis---conservation)
    - [Recursive Flow Summary](#recursive-flow-summary)
- [Module Structure](#module-structure)
    - [Module Roles](#module-roles)
    - [Filesystem Layout](#filesystem-layout)
    - [Configuration](#configuration)

## Signal Flow

Async boundary: All callbacks execute on Nucleus thread pool (Ch.9,12).
Recursive: Lines marked [R1]-[R4] re-enter a prior node in the tree.

### Synaptic Transmission and Synaptic Integration

```
@Releasic @Diffusic @Bindic
Service<Impulse, Potential>.call(Impulse)                   Ch.8
├── PATHWAYS lookup
│   ├── Decision                                            Cortex
│   ├── Projection                                          Thalamus
│   ├── Fluctuation                                         Default
│   ├── Compensation                                        Autopoiesis
│   ├── Conservation                                        Autopoiesis
│   └── Consolidation                                       Knowledge
├── release(Pathway, Impulse)                               Promptifier
├── diffuse(Pathway, String)                                Llm (Claude API)
└── bind(Pathway, String)                                   Potentifier

Nucleus.integrate(Potential, Consumer)                      Ch.9,12
└── [thread pool]
    ├── Temporal summation - to be implemented              T6
    ├── Event<Modulator>.fire()
    └── Consumer.accept(Potential)
```

Spindle bypasses synaptic transmission - Thalamus.burst() passes
Spindle directly to Nucleus.integrate() (Ch.51).

### CEN - Central Executive Network

```
Receptor.transduce(Stimulus)                                Ch.21
├── Salience.orient()                                       Ch.63
└── Thalamus.relay(Impulse)                                 [R4 re-entry]
    ├── @Releasic @Diffusic @Bindic
    │   Service<Impulse, Potential>.call(Impulse)
    │   └── Projection{reasoning, amplitude,
    │                  area}
    │
    └── Nucleus.integrate(Projection, Consumer)             Ch.9,12
        └── [thread pool]
            ├── new Impulse
            └── Cortex.respond(Impulse)                     [R1-R3 re-entry]
                ├── Episode.encode(Trace)                   Ch.65-67
                ├── @Releasic @Diffusic @Bindic
                │   Service<Impulse, Potential>.call(Impulse)
                │   └── Decision{reasoning, amplitude,
                │                process, response?,
                │                effector?, area?}
                │
                └── Nucleus.integrate(Decision, Consumer)                       Ch.9,12
                    └── [thread pool]
                        ├── Event<Potential>.fire()                             Ch.63
                        └── Cortex.process(Decision, Impulse)
                            │
                            ├── POTENTIATE
                            │   ├── Autopoiesis.compensate(Impulse)
                            │   └── Cortex.respond(Impulse)                     [R3]
                            │
                            ├── PROJECT                                         Ch.20
                            │   ├── new Impulse
                            │   └── Cortex.respond(Impulse)                     [R2]
                            │
                            ├── VOCALIZE / INHIBIT
                            │   ├── if Habituation.habituated() -> return       Ch.63
                            │   ├── Effector.fire(Map)                          Ch.35-36
                            │   ├── Episode.encode(Trace)
                            │   └── Event<Percept>.fire()                       Ch.21, 25
                            │
                            └── default (FIRE)
                                ├── if Habituation.habituated() -> return       Ch.63
                                ├── Effector.fire(Map)                          Ch.35-36
                                ├── Episode.encode(Trace)
                                └── Cortex.respond(Impulse)                     [R1] Ch.33
```

FIRE re-enters Cortex.respond() via efference copy (Ch.33) with mode=null
to prevent double episode encode. Only VOCALIZE/INHIBIT generate Percept
(CEN end). PROJECT re-enters via association fibers (Ch.20).
Episode encoding centralized at Cortex.respond() entry: CEN mode -> "user"
trace, DMN mode -> "[INTROSPECTION]" trace, null mode -> skip (recursion).

### DMN - Default Mode Network

```
@PostConstruct
└── activate() -> fluctuate                                 Ch.62
@PreDestroy
└── deactivate()

Default.fluctuate()
├── if Salience.inhibiting() -> return
├── if !Arousal.isProjecting() -> return
└── fire()
    ├── @Releasic @Diffusic @Bindic
    │   Service<Impulse, Potential>.call(Impulse)
    │   └── Fluctuation{reasoning, amplitude,
    │                    aroused, signal?}
    │
    └── Nucleus.integrate(Fluctuation, Consumer)            Ch.9,12
        └── [thread pool]
            ├── if !fluctuation.aroused() -> return
            ├── Salience.orient()
            └── Thalamus.relay(Impulse)                     [R4]
```

DMN flows through Thalamus for Area routing, same as CEN.
Thalamus determines efferent for both modes (Ch.21).

### Sleep - Memory Consolidation

```
// Thalamus
@PostConstruct
└── activate() -> oscillate                                 Ch.51
@PreDestroy
└── deactivate()

// Sleep
@PostConstruct
└── activate() -> sleep                                     Ch.51
@PreDestroy
└── deactivate()

Thalamus.oscillate()                                        Ch.51
├── if Arousal.isProjecting() -> return
└── burst()                                                 Ch.51
    └── Nucleus.integrate(Spindle, Consumer)                Ch.9,12
        └── [thread pool]
            ├── Autopoiesis.conserve()
            ├── Episode.promote()                           Ch.65-67
            │   ├── @Releasic @Diffusic @Bindic
            │   │   Service<Impulse, Potential>.call(Impulse)
            │   │   └── Consolidation{reasoning, amplitude,
            │   │                     insights}
            │   └── Nucleus.integrate(Consolidation, Consumer)
            │       └── Knowledge.encode(Trace)
            ├── Episode.decay()
            └── Knowledge.decay()

Sleep.sleep()                                               Ch.51
├── if Arousal.isProjecting() -> return
└── Arousal.project()
```

ThalamusImpl self-governs burst firing via intrinsic oscillation (Ch.51).
When Arousal is not projecting (!isProjecting()), Thalamus enters burst mode
and drives structural conservation, episodic->semantic promotion (Ch.65-67),
and memory decay. SleepImpl manages the sleep-wake transition: when
modulator pressure exceeds threshold, Arousal stops projecting (Saper
flip-flop, Ch.51). After consolidation, Sleep calls project() which
internally clears adenosine pressure and restarts arousal projection.

### SN - Salience Network

```
@PostConstruct
└── activate() -> monitor                                   Ch.63
@PreDestroy
└── deactivate()

[Stimulus arrived]
└── Salience.orient()                                       Ch.63; Seeley 2007
    └── oriented = true
        └── [suppress DMN]

[Cortex fired]
└── Event<Potential>.fire()                                 Ch.63
    └── SalienceImpl.receive(@Observes Potential)

[Cortex not fired for a period]
└── SalienceImpl.monitor()
    └── oriented = false
        └── [resume DMN]
```

Anterior insula + ACC. Gates DMN<->CEN switch (Seeley 2007).
orient() suppresses DMN; collateral monitoring detects CEN idle -> DMN re-engages.

### Autopoiesis - Compensation

```
Autopoiesis.compensate(Impulse)
├── @Releasic @Diffusic @Bindic
│   Service<Impulse, Potential>.call(Impulse)
│   └── Compensation{reasoning, amplitude,
│                    newTuning, newAreas[],
│                    newNeurons[], newEffectors[]}
│
└── Nucleus.integrate(Compensation, Consumer)               Ch.9,12
    └── [thread pool]
        ├── transform(Compensation, Area)
        └── produce(Compensation)
```

### Autopoiesis - Conservation

```
Autopoiesis.conserve()
├── @Releasic @Diffusic @Bindic
│   Service<Impulse, Potential>.call(Impulse)
│   └── Conservation{reasoning, amplitude,
│                    mergedAreas[], mergedNeurons[]}
│
└── Nucleus.integrate(Conservation, Consumer)               Ch.9,12
    └── [thread pool]
        ├── destroy(Conservation)
        └── produce(Conservation)
```

### Recursive Flow Summary

```
[R1] FIRE       -> Cortex.respond()    Efference copy (Ch.33) - result in Episode, re-evaluate
[R2] PROJECT    -> Cortex.respond()    Cross-area projection (Ch.20 association fibers)
[R3] POTENTIATE -> Cortex.respond()    After neural growth, re-evaluate input
[R4] DMN        -> Thalamus.relay()    Internal impulse enters CEN pathway
```

---

## Module Structure

```
adapters/driving/api        Java HTTP server + SSE (uber-JAR)
adapters/driving/web        Vue 3 + TypeScript + Vite + Pinia SPA
├── specification           [compile]
├── adapters/driven/services  [runtime] (Effector impls)
│   └── specification *
├── runtime                 [runtime]
│   └── specification *
└── adapters/driven/anticorruption  [runtime] (ACL)
    ├── specification *
    └── runtime *
```

### Module Roles

| Module             | Role                                                                          |
|--------------------|-------------------------------------------------------------------------------|
| **specification**  | Kandel-grounded domain interfaces, zero dependencies                          |
| **runtime**        | Core engines + SPIs (Repository/Serializer/Service); packages mirror spec 1:1 |
| **anticorruption** | ACL - adapters, translators, proxy providers, Anthropic client                |
| **services**       | Concrete `*Effector.java`, may be runtime-generated by Autopoiesis            |
| **api**            | JDK HttpServer + SSE; Weld SE bootstrap; maven-shade-plugin                   |
| **web**            | Vue 3 SPA + Hono BFF; `npm run dev` / `npm run serve`                         |

### Filesystem Layout

```
filesystem/
  neural/neurons/          Neuron definitions (JSON)
  neural/areas/            Area definitions (JSON)
  synaptic/function/       Function prompt templates (perception, relay, compensation, conservation, default, promotion)
  synaptic/shared/         Shared templates (executive_control, output_integrity)
  hippocampal/episode/     episode_yyyyMMddHHmmss.json
  neocortical/knowledge/   knowledge.json
```

### Configuration

`adapters/driving/api/src/main/resources/configuration.yaml`

| Key                                                           | Description                              |
|---------------------------------------------------------------|------------------------------------------|
| `anticorruption.neural.areas.target`                          | Area JSON directory                      |
| `anticorruption.neural.neurons.target`                        | Neuron JSON directory                    |
| `anticorruption.synaptic.function.source`                     | Function prompt templates directory      |
| `anticorruption.synaptic.shared.source`                       | Shared prompt templates directory        |
| `anticorruption.hippocampal.episode.target`                   | Episode memory directory                 |
| `anticorruption.hippocampal.episode.limit`                    | Past sessions to load (-1 = all)         |
| `anticorruption.neocortical.knowledge.target`                 | Knowledge memory directory               |
| `anticorruption.neural.effectors.package`                     | Effector Java package name               |
| `anticorruption.neural.effectors.source`                      | Effector .java source directory          |
| `anticorruption.neural.effectors.target`                      | Effector .class target directory         |
| `anticorruption.neural.effectors.compiler.classpath.strategy` | `manual` or system class path            |
| `anticorruption.neural.effectors.compiler.classpath.value`    | Classpath value (when strategy = manual) |
