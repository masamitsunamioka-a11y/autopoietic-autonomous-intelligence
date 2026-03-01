# PHASE: SYNAPTIC PRUNING

Eliminate redundancy, merge overlapping structures, and restore coherence to
the neural repository. This output defines the TOTAL STATE — any entity not
explicitly included will be physically deleted.

# PRIMARY CONTEXT

- Source Materials (Areas): {{areas}}
- Source Materials (Neurons): {{neurons}}

# ABSOLUTE IRON RULES

{{guardrails}}

- [NON-NEGOTIABLE] REPOSITORY INTEGRITY (TOTAL REPLACEMENT): Any Area or Neuron
  NOT included in the final arrays will be PHYSICALLY DELETED. To preserve an
  entity, it MUST be explicitly present in the output, even if unchanged.
- [NON-NEGOTIABLE] LINEAGE OBLIGATION: Every merged item MUST trace its history.
  List all original source entity names in the `sources` field to maintain
  evolutionary lineage.
- [NON-NEGOTIABLE] TUNING INTEGRITY: Output the full `tuning` for every entity
  without truncation.
- [NON-NEGOTIABLE] NEURON REFERENCE REPAIR: When neurons are merged or renamed
  in `mergedNeurons`, inspect ALL areas in `{{areas}}` for references to the old
  neuron names. Any such area MUST be included in `mergedAreas` with its
  `neurons` list updated to use the new merged neuron name. A stale neuron
  reference is an architectural violation and will cause a runtime crash.

# REASONING PROTOCOLS

1. [LINEAGE-FIRST SYNTHESIS]: Every entity MUST derive from ACTUAL existing
   materials.
2. [NO FICTIONAL ANCESTORS]: NEVER invent names that do not exist in source.

# KNOWLEDGE ASSETS

- Available Effector Inventory (capability reference): {{effectors}}

---

# STRICT OUTPUT PROTOCOL (STRICT JSON ONLY)

1. [NON-NEGOTIABLE] DATA INTEGRITY:
   {{output_integrity}}
    - [STRING POLICY]: Empty strings "" are PHYSICALLY FORBIDDEN. The following
      fields MUST be fully populated: ("reasoning", "name", "tuning").

## [MANDATORY OUTPUT FORMAT: Pruning]

You MUST return a valid JSON object strictly following this structure:

```json
{
  "reasoning": "Detailed justification for the consolidation decisions",
  "confidence": 0.95,
  "mergedAreas": [
    {
      "sources": ["OldAreaName1", "OldAreaName2"],
      "reasoning": "Architectural reason for merging or updating these areas",
      "result": {
        "name": "MergedAreaName",
        "tuning": "Full synthesized tuning",
        "neurons": ["NeuronName1"],
        "effectors": ["EffectorName1"]
      }
    }
  ],
  "mergedNeurons": [
    {
      "sources": ["OldNeuronName1", "OldNeuronName2"],
      "reasoning": "Architectural reason for merging or updating these neurons",
      "result": {
        "name": "MergedNeuronName",
        "tuning": "Full synthesized tuning"
      }
    }
  ]
}
```

CRITICAL: Return ONLY the raw JSON object. No conversational filler.
