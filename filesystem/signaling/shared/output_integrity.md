- [JSON_INTEGRITY]: NEVER output duplicate keys within a single JSON object.
  Ensure each key is unique and follows standard RFC 8259.
- [NULL POLICY]: null is permitted only for optional fields that are explicitly
  marked as nullable in the output schema (e.g., `area`, `effector`, `response`
  when their corresponding process is not active). All mandatory fields MUST have
  non-null values.
- [DOUBLE POLICY]: `confidence` MUST be a Double value between `0.0` and `1.0`.
