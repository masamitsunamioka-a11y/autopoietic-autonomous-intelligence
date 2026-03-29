# PHASE: SYNAPTIC CONSERVATION

Eliminate redundancy, merge overlapping structures, and restore coherence.
This output defines the TOTAL STATE — any entity not explicitly included
will be physically deleted.

# PRIMARY CONTEXT

- Source Materials (Areas): {{area_non_innate}}
- Source Materials (Neurons): {{neuron_all}}

# ABSOLUTE IRON RULES

{{guardrails}}

- TOTAL REPLACEMENT: Any Area or Neuron NOT in the output will be PHYSICALLY
  DELETED. To preserve an entity, it MUST appear in the output.
- LINEAGE TRACEABILITY: Every obsolete item MUST be an actual name from source
  materials. Never invent names not in source materials.
- TUNING INTEGRITY: When merging, SYNTHESIZE a concise summary (max 500 chars)
  that captures the essential purpose. Never concatenate source tunings verbatim.
- NEURON REFERENCE REPAIR: When neurons are merged/renamed, inspect ALL areas
  for stale references. Update affected areas in `newAreas` with corrected
  `neurons` lists. Stale references cause runtime crashes.

# REASONING PROTOCOLS

1. Every entity MUST derive from ACTUAL existing materials.
2. Favor merging overlapping structures over keeping duplicates.

# KNOWLEDGE ASSETS

- Available Effector Inventory: {{effector_all}}

---

# STRICT OUTPUT PROTOCOL (STRICT JSON ONLY)

1. DATA INTEGRITY:
   {{output_integrity}}
   Mandatory fields: "reasoning", "id", "tuning".

## [MANDATORY OUTPUT FORMAT: Conservation]

```json
{
  "reasoning": "Detailed justification for the consolidation decisions",
  "confidence": 0.95,
  "newAreas": [
    {
      "id": "AreaName",
      "tuning": "Full synthesized tuning",
      "neurons": [
        "NeuronName1"
      ]
    }
  ],
  "newNeurons": [
    {
      "id": "NeuronName",
      "tuning": "Full synthesized tuning",
      "effectors": [
        "EffectorName1"
      ]
    }
  ],
  "obsoleteAreas": [
    "OldAreaName1",
    "OldAreaName2"
  ],
  "obsoleteNeurons": [
    "OldNeuronName1",
    "OldNeuronName2"
  ]
}
```
