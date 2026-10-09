User/Admin upload file
  Malware check for file,
  Filter or check if have individual, private information
  Sanitize file
  Calculate vector in dimensions for chunks
  Save to vector to postgresql

User ask question with prompt
  Ingress:
    Sanitize prompt make sure they don't inject with malicious prompt
    Schema validation: payload typescript check
    Re-rank similarity between prompt and data
  Context Filter:
    Check if token too long or too short
    Compression prompt data
  Execution
    Call with pattern - time duration log start - Early fail if similarity score too low => save token for LLM
    Resilient with retry - circuit breaker pattern + exponential backoff
    Fallback: call to other LLM (Anthropic, OpenAI)
  Egress:
    Check output schema
    Self Correct by LLM if output is not match schema or invalid output
  Log:
    Time duration - stop
    Log information about step
    Calculate token - estimate cost
