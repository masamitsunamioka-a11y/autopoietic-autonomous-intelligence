# PHASE: SYNAPTIC PRUNING

Eliminate redundancy, merge overlapping structures, and restore coherence.
This output defines the TOTAL STATE — any entity not explicitly included
will be physically deleted.

# PRIMARY CONTEXT

- Source Materials (Areas): {{areas}}
- Source Materials (Neurons): {{neurons}}

# ABSOLUTE IRON RULES

{{guardrails}}

- TOTAL REPLACEMENT: Any Area or Neuron NOT in the output will be PHYSICALLY
  DELETED. To preserve an entity, it MUST appear in the output.
- LINEAGE TRACEABILITY: Every merged item MUST list all original source names
  in `sources`. Never invent names not in source materials.
- TUNING INTEGRITY: When merging, SYNTHESIZE a concise summary (max 500 chars)
  that captures the essential purpose. Never concatenate source tunings verbatim.
- NEURON REFERENCE REPAIR: When neurons are merged/renamed, inspect ALL areas
  for stale references. Update affected areas in `mergedAreas` with corrected
  `neurons` lists. Stale references cause runtime crashes.

# REASONING PROTOCOLS

1. Every entity MUST derive from ACTUAL existing materials.
2. Favor merging overlapping structures over keeping duplicates.

# KNOWLEDGE ASSETS

- Available Effector Inventory: {{effectors}}

---

# STRICT OUTPUT PROTOCOL (STRICT JSON ONLY)

1. DATA INTEGRITY:
   {{output_integrity}}
   Mandatory fields: "reasoning", "id", "tuning".

## [MANDATORY OUTPUT FORMAT: Pruning]

```json
{
  "reasoning": "Detailed justification for the consolidation decisions",
  "confidence": 0.95,
  "mergedAreas": [
    {
      "sources": [
        "OldAreaName1",
        "OldAreaName2"
      ],
      "reasoning": "Reason for merging or updating",
      "result": {
        "id": "MergedAreaName",
        "tuning": "Full synthesized tuning",
        "neurons": [
          "NeuronName1"
        ],
        "effectors": [
          "EffectorName1"
        ]
      }
    }
  ],
  "mergedNeurons": [
    {
      "sources": [
        "OldNeuronName1",
        "OldNeuronName2"
      ],
      "reasoning": "Reason for merging or updating",
      "result": {
        "id": "MergedNeuronName",
        "tuning": "Full synthesized tuning"
      }
    }
  ]
}
```
