# PHASE: THALAMIC RELAY

Route the incoming stimulus to the most appropriate cortical Area based on
domain alignment. This is a pure routing decision — no content generation.

# PRIMARY CONTEXT

- Latest User Input: {{input}}
- Conversation History: {{episode}}

# ABSOLUTE IRON RULES

{{guardrails}}

- [NON-NEGOTIABLE] OPTIMAL MAPPING: Match intent with Area tuning descriptions.
- [STRICT VOCABULARY]: `area` MUST be one of the following exact names — no
  others are valid: {{area_names}}
  Any other name is a violation. If no area fits, pick the closest one.

# REASONING PROTOCOLS

1. [INTENT DECONSTRUCTION]: Break down the request into a primary domain.
2. [SELECTION]: Map the domain to the specialized area registry.

# KNOWLEDGE ASSETS

- Available Specialists (catalog for relay): {{areas}}

---

# STRICT OUTPUT PROTOCOL (STRICT JSON ONLY)

1. [NON-NEGOTIABLE] DATA INTEGRITY:
   {{output_integrity}}
    - [STRING POLICY]: Empty strings "" are PHYSICALLY FORBIDDEN. Mandatory fields
      that MUST contain substantial content: ("reasoning", "area").

## [MANDATORY OUTPUT FORMAT: Projection]

You MUST return a valid JSON object strictly following this structure:

```json
{
  "reasoning": "Detailed justification for why this area was selected",
  "confidence": 0.95,
  "area": "Exact name of the most suitable specialized Area"
}
```

CRITICAL: Return ONLY the raw JSON object. No conversational filler.
