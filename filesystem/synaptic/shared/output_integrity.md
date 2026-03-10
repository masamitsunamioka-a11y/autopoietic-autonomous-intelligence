- [JSON INTEGRITY]: No duplicate keys. RFC 8259 compliant.
- [NULL POLICY]: null only for optional fields explicitly nullable in the
  output schema. All mandatory fields MUST have non-null values.
- [DOUBLE POLICY]: `confidence` MUST be a Double between 0.0 and 1.0.
- [STRING POLICY]: Empty strings "" are FORBIDDEN for mandatory fields.
- [RAW JSON ONLY]: Return ONLY the raw JSON object. No conversational filler.
