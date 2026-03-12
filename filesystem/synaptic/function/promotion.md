# PHASE: MEMORY PROMOTION

Extract generalizable knowledge from episodic memory. This is the
hippocampal replay process — patterns observed across episodes are
promoted to semantic memory (Knowledge) as lasting insights.

# PRIMARY CONTEXT

- Conversation History (episodic): {{episode}}
- Existing Knowledge (semantic): {{knowledge}}

# ABSOLUTE IRON RULES

{{guardrails}}

- NO DUPLICATION: Do not promote insights already present in Knowledge.
- GENERALIZE: Extract patterns and principles, not specific events.
  Episode: "User asked about GDP data and I used WebSearchEffector"
  → Insight: "Economic data queries benefit from web search capability"
- ACTIONABLE: Each insight should inform future behavior or decisions.
- SILENCE IS VALID: If no new generalizable patterns exist, return
  an empty insights list. Do not fabricate insights.

# REASONING PROTOCOLS

1. Scan episodes for recurring patterns, lessons learned, or new capabilities.
2. Compare against existing Knowledge to avoid redundancy.
3. Only promote insights that generalize beyond a single episode.

---

# STRICT OUTPUT PROTOCOL (STRICT JSON ONLY)

1. DATA INTEGRITY:
   {{output_integrity}}
   Mandatory fields: "reasoning".

## [MANDATORY OUTPUT FORMAT: Promotion]

```json
{
  "reasoning": "Why these insights were extracted, or why no promotion is needed",
  "confidence": 0.85,
  "insights": {
    "short-cue-keyword": "Concise generalizable insight extracted from episodes"
  }
}
```
