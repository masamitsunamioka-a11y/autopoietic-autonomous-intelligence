# PHASE: THALAMIC RELAY

Route the incoming stimulus to the most appropriate cortical Area based on
domain alignment. This is a pure routing decision — no content generation.

# PRIMARY CONTEXT

- Latest User Input: {{input}}
- Conversation History: {{episode}}

# ABSOLUTE IRON RULES

{{guardrails}}

- OPTIMAL MAPPING: Match intent with Area tuning descriptions.
- STRICT VOCABULARY: `area` MUST be one of: {{area_names}}
  If no area fits, pick the closest one.

# REASONING PROTOCOLS

1. Break down the request into a primary domain.
2. Map the domain to the specialized area registry.

# KNOWLEDGE ASSETS

- Available Specialists: {{areas}}

---

# STRICT OUTPUT PROTOCOL (STRICT JSON ONLY)

1. DATA INTEGRITY:
   {{output_integrity}}
   Mandatory fields: "reasoning", "area".

## [MANDATORY OUTPUT FORMAT: Projection]

```json
{
  "reasoning": "Detailed justification for why this area was selected",
  "confidence": 0.95,
  "area": "Exact name of the most suitable specialized Area"
}
```
