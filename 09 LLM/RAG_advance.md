Advance flow: We can add more flow before sending data to LLM
  use full text search BM25 from postgresql to match precise text
  use pgvector to calculate similarity cosine for question and all the vector of chunks

Temperature in LLM:

Problem
  Security:
    Avoid injection prompt by user - sanitize prompt
    How to:
