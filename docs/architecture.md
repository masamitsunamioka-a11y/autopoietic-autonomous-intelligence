# Signal Flows

⚙️ = engineering convention (no direct Kandel structural equivalent).

## CEN — Central Executive Network (external)

```
Stimulus.input()
  → Transducer.transduce()          Ch.21
  → Impulse(area=null)
  → Thalamus.relay()                Ch.23,26,46
      → Nucleus.integrate()         Ch.12
  → Cortex.respond()                Part VII
      → Nucleus.integrate()         Ch.12
  → Event<Percept>                  Ch.21,25
  → Effector.fire()                 Ch.35-36
```

All spec interfaces return void — neurons fire downstream, not return values.
Output unified via Event\<Percept\> (CDI observer pattern).

Corticocortical projection (PROJECT process) re-enters Cortex.respond()
recursively via association fibers (Ch.20), bypassing Thalamus.

## DMN — Default Mode Network (internal)

```
@PostConstruct → schedule()          Ch.62
  → [DefaultImpl generates Impulse internally]
      → Nucleus.integrate()         Ch.12
  → Cortex.respond()
      → Nucleus.integrate()         Ch.12
  → Event<Percept>                  Ch.21,25
```

DMN bypasses Thalamus. Area selection is internal (Ch.62: DMN activates
medial PFC, posterior cingulate spontaneously).

## Sleep — Memory Consolidation

```
@PostConstruct → scheduleConsolidation()   Ch.51, 65-67
  → Plasticity.prune()
  → [eliminate + consolidate + Episode.decay()]
```

SleepImpl owns timed consolidation. Biology consolidates during
sleep (Ch.51, 65-67). Separate from DMN spontaneous activity.

## SN — Salience Network (gating)

```
[External stimulus arrives]
  → Salience.orient()               Seeley 2007; Ch.63
  → [DMN suppressed, CEN engaged]
  → [CEN processes stimulus]
  → Salience.release()
  → [CEN disengaged, DMN resumes]
```

Anterior insula + ACC. Gates DMN↔CEN switch (Seeley 2007).
orient() suppresses DMN; release() re-engages DMN.
