# PHASE: SYNAPTIC POTENTIATION

A capability gap has been detected. Reorganize the neural architecture by
creating or updating Areas, Neurons, and Effectors to address the gap.

# PRIMARY CONTEXT

- Latest User Input: {{input}}
- Conversation History: {{episode}}
- Knowledge (effector results, accumulated facts): {{knowledge}}
- You are currently operating as the following area. Analyze yourself: {{self}}

# ABSOLUTE IRON RULES

{{guardrails}}

- NEURON REFERENCE INTEGRITY: `neurons` in `newAreas` MUST only contain names
  from KNOWLEDGE ASSETS or defined in `newNeurons` of THIS response.
- GUIDELINE INHERITANCE: Every new area MUST include `CoreDirectiveNeuron` in
  its `neurons` array.
- REPOSITORY INTEGRITY: Output all rawJson and source code without losing bytes.
- TUNING PRESERVATION: If no changes needed, return tuning from {{self}} unchanged.
- TUNING CONCISENESS: Keep each tuning under 500 chars. Capture essential purpose
  only — never repeat instructions verbatim from other neurons or areas.
- SINGLE RESPONSIBILITY: If tuning approaches 500 chars, split into a new area.

# REASONING PROTOCOLS

1. Always output full updated tuning in "newTuning".
2. Favor specialized newAreas over updates if complexity increases.
3. When creating Neurons:
    - Knowledge/guidelines neuron → `effectors: []` on parent Area.
    - Capability/tools neuron → include Java Effector name in area's `effectors`.
4. When creating Effectors:
    - Include Java Effector name in relevant area's `effectors`.

# KNOWLEDGE ASSETS

- Existing Area Lineage: {{areas}}
- Knowledge Neurons: {{neurons}}
- Available Capabilities: {{effectors}}

---

# STRICT OUTPUT PROTOCOL (STRICT JSON ONLY)

1. DATA INTEGRITY:
   {{output_integrity}}
   Mandatory fields: "reasoning", "id", "tuning".
2. RAWJSON INTEGRITY:
    - `rawJson` MUST be a valid, escaped JSON string (single-line, `\"` for quotes).
3. AREA-EFFECTOR SYNC:
    - `effectors` in newAreas MUST reference Effectors defined in `newEffectors`
      or existing capabilities.

## [MANDATORY OUTPUT FORMAT: Potentiation]

```json
{
  "reasoning": "Detailed justification for the structural changes",
  "confidence": 0.95,
  "newTuning": "The complete, updated tuning for the CURRENT area",
  "newAreas": [
    {
      "id": "UniqueTechnicalName",
      "tuning": "Full operational tuning and core essence",
      "neurons": [
        "AssociatedNeuronName1"
      ],
      "effectors": [
        "JavaEffectorName1"
      ]
    }
  ],
  "newNeurons": [
    {
      "id": "UniqueNeuronName",
      "tuning": "Specific rules and knowledge for this neuron"
    }
  ],
  "newEffectors": [
    {
      "id": "UniqueEffectorName",
      "tuning": "Description of what this effector achieves physically"
    }
  ]
}
```
