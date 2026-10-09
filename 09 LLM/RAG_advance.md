Advance flow: We can add more flow before sending data to LLM
  use full text search BM25 from postgresql to match precise text
  use pgvector to calculate similarity cosine for question and all the vector of chunks
  => pick up 50 or 100 chunks
  use reciprocal rank fusion to merge the results and get 30 or 50 chunks
  use lightweight ranking model to rescore and re-rank the chunks again
  Then select top k (3-5 chunks)
  Send to LLM for generating answer
  
Temperature in LLM:

Problem
  Security:
    Avoid injection prompt by user - sanitize prompt
    How to:
